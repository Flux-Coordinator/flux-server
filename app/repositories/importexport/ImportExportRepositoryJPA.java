package repositories.importexport;

import models.Measurement;
import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;
import repositories.rooms.RoomsRepository;
import repositories.utils.CollectionHelper;
import utils.multithreading.CompletableFutureHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static repositories.utils.CollectionHelper.containsByComparator;
import static repositories.utils.CollectionHelper.getContainedItemsByComparator;
import static repositories.utils.JpaHelper.wrap;

public class ImportExportRepositoryJPA implements ImportExportRepository {
    private final JPAApi jpaApi;
    private final ProjectsRepository projectsRepository;
    private final RoomsRepository roomsRepository;
    private final MeasurementsRepository measurementsRepository;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public ImportExportRepositoryJPA(final JPAApi jpaApi,
                                     final ProjectsRepository projectsRepository,
                                     final RoomsRepository roomsRepository,
                                     final MeasurementsRepository measurementsRepository,
                                     final DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.projectsRepository = projectsRepository;
        this.roomsRepository = roomsRepository;
        this.measurementsRepository = measurementsRepository;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletableFuture<Set<Project>> getRelatedProjects(final List<Measurement> measurements) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getRelatedProjects(em, measurements)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> importProjects(final List<Project> projects) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            this.importProjects(em, projects);
            return null;
        }), databaseExecutionContext);
    }

    private Set<Project> getRelatedProjects(final EntityManager em, final List<Measurement> measurements) {
        final List<Long> ids = measurements.stream().map(Measurement::getMeasurementId).collect(Collectors.toList());

        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p " +
                "JOIN p.rooms as r " +
                "JOIN r.measurements as m " +
                "WHERE m.id in (:measurementIds)", Project.class);
        typedQuery.setParameter("measurementIds", ids);
        final List<CompletableFuture> mergeMeasurementsTasks = new ArrayList<>();

        final Set<Project> projects = new HashSet<>(typedQuery.getResultList());

        // Clean up unnecessary rooms in the project that were retrieved from the database.
        for (Project currentProject : projects) {
            em.detach(currentProject);
            final Set<Room> cleanedRooms = new HashSet<>();
            final Iterator<Room> roomsIterator = currentProject.getRooms().iterator();

            //noinspection WhileLoopReplaceableByForEach
            while (roomsIterator.hasNext()) {
                final Room currentRoom = roomsIterator.next();

                // Retain only the measurements that are actually in the list of to-be-exported measurements.
                CollectionHelper
                        .retainAllByComparator(currentRoom.getMeasurements(), questionableMeasurement
                                -> containsByComparator(measurements, measurement
                                -> measurement.getMeasurementId() == questionableMeasurement.getMeasurementId()));

                if (!currentRoom.getMeasurements().isEmpty()) {
                    final List<Long> measurementIds = currentRoom.getMeasurements().stream().map(Measurement::getMeasurementId).collect(Collectors.toList());
                    final CompletableFuture future = this.measurementsRepository
                            .getMeasurementsById(measurementIds)
                            .thenAccept(currentRoom::setMeasurements);
                    mergeMeasurementsTasks.add(future);
                    cleanedRooms.add(currentRoom);
                }
            }
            currentProject.setRooms(cleanedRooms);
        }
        CompletableFutureHelper.waitForAllOf(mergeMeasurementsTasks);
        return projects;
    }

    private void importProjects(final EntityManager em, final List<Project> projects) {
        final List<Long> projectIds = projects.stream().map(Project::getProjectId).collect(Collectors.toList());
        this.projectsRepository.getProjectsById(projectIds).thenAccept(existingProjects -> {
            // Handle non-existing projects
            final List<Project> existingImportedProjects = getContainedItemsByComparator(existingProjects, projects, (p1, p2) -> p1.getProjectId() == p2.getProjectId());
            final List<Project> newProjects = new ArrayList<>(projects);
            newProjects.removeAll(existingImportedProjects);
            final List<CompletableFuture> futures = new ArrayList<>(1 + existingImportedProjects.size());
            futures.add(this.projectsRepository.addProjects(newProjects));

            existingImportedProjects.forEach(importedProject -> {
                final Optional<Project> correspondingExistingProject = existingProjects
                        .stream()
                        .filter(p -> p.getProjectId() == importedProject.getProjectId() && p.getName().equals(importedProject.getName()))
                        .findFirst();

                if(correspondingExistingProject.isPresent()) {
                    futures.add(importRooms(importedProject, importedProject.getRooms()));
                } else {
                    importedProject.setProjectId(-1);
                    futures.add(this.projectsRepository.addProject(importedProject));
                }
            });
            CompletableFutureHelper.waitForAllOf(futures);
        }).join();
    }

    private CompletableFuture<Void> importRooms(final Project parentProject, final Set<Room> rooms) {
        final List<Long> roomIds = rooms.stream().map(Room::getRoomId).collect(Collectors.toList());
        return this.roomsRepository.getRoomsById(roomIds).thenAccept(existingRooms -> {
            final List<Room> existingImportedRooms = getContainedItemsByComparator(existingRooms, rooms, (r1, r2) -> r1.getRoomId() == r2.getRoomId());
            final List<Room> newRooms = new ArrayList<>(rooms);
            newRooms.removeAll(existingImportedRooms);
            final List<CompletableFuture> futures = new ArrayList<>(1 + existingImportedRooms.size());
            futures.add(this.roomsRepository.addRooms(newRooms));

            // Handle existing rooms
            existingImportedRooms.forEach(importedRoom -> {
                final Optional<Room> correspondingExistingRoom = existingRooms
                        .stream()
                        .filter(room -> room.getRoomId() == importedRoom.getRoomId() && room.getName().equals(importedRoom.getName()))
                        .findFirst();

                if(correspondingExistingRoom.isPresent()) {
                    futures.add(importMeasurements(importedRoom.getMeasurements()));
                } else {
                    importedRoom.setRoomId(-1);
                    importedRoom.setProject(null);
                    futures.add(this.roomsRepository.addRoom(parentProject.getProjectId(), importedRoom));
                }
            });

            CompletableFutureHelper.waitForAllOf(futures);
        });
    }

    private CompletableFuture<Void> importMeasurements(final Set<Measurement> measurements) {
        final List<Long> measurementIds = measurements.stream().map(Measurement::getMeasurementId).collect(Collectors.toList());
        return this.measurementsRepository.getMeasurementsById(measurementIds).thenAccept(existingMeasurements -> {
            final List<Measurement> existingImportedMeasurements = getContainedItemsByComparator(existingMeasurements, measurements, (m1, m2) -> m1.getMeasurementId() == m2.getMeasurementId());
            final List<Measurement> newMeasurements = new ArrayList<>(measurements);
            newMeasurements.removeAll(existingImportedMeasurements);
            final List<CompletableFuture> futures = new ArrayList<>(1 + existingImportedMeasurements.size());
            futures.add(this.measurementsRepository.addMeasurements(newMeasurements));

            existingImportedMeasurements.forEach(importedMeasurement -> {
                final Optional<Measurement> correspondingExistingMeasurement = existingMeasurements
                        .stream()
                        .filter(m -> m.getMeasurementId() == importedMeasurement.getMeasurementId() && m.getName().equals(importedMeasurement.getName()))
                        .findFirst();

                importedMeasurement.setMeasurementId(-1);
                if(correspondingExistingMeasurement.isPresent()) {
                    importedMeasurement.setName(importedMeasurement.getName() + " (Duplikat)");
                }
            });
            futures.add(measurementsRepository.addMeasurements(existingImportedMeasurements));

            CompletableFutureHelper.waitForAllOf(futures);
        });
    }
}
