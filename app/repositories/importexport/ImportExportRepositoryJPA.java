package repositories.importexport;

import models.Measurement;
import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;
import repositories.utils.CollectionHelper;
import utils.multithreading.CompletableFutureHelper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static repositories.utils.CollectionHelper.containsByComparator;
import static repositories.utils.JpaHelper.wrap;

public class ImportExportRepositoryJPA implements ImportExportRepository {
    private final JPAApi jpaApi;
    private final ProjectsRepository projectsRepository;
    private final MeasurementsRepository measurementsRepository;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public ImportExportRepositoryJPA(final JPAApi jpaApi,
                                     final ProjectsRepository projectsRepository,
                                     final MeasurementsRepository measurementsRepository,
                                     final DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.projectsRepository = projectsRepository;
        this.measurementsRepository = measurementsRepository;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletableFuture<Set<Project>> getRelatedProjects(final List<Measurement> measurements) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getRelatedProjects(em, measurements)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> importProjects(List<Project> projects) {
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
            final List<Project> newProjects = projects
                    .stream()
                    .filter(p -> !containsByComparator(existingProjects, ep -> ep.getProjectId() == p.getProjectId()))
                    .collect(Collectors.toList());
            final List<CompletableFuture> futures = new ArrayList<>();
            futures.add(this.projectsRepository.addProjects(newProjects));

            // Handle existing projects
            final List<Project> existingImportedProjects = projects
                    .stream()
                    .filter(p -> containsByComparator(existingProjects, ep -> ep.getProjectId() == p.getProjectId()))
                    .collect(Collectors.toList());

            existingImportedProjects.forEach(importedProject -> {
                final Optional<Project> correspondingExistingProject = existingProjects
                        .stream()
                        .filter(p -> p.getProjectId() == importedProject.getProjectId())
                        .findFirst();

                correspondingExistingProject.ifPresent(existingProject -> {
                    futures.add(importRooms(em, existingProject, importedProject.getRooms()));
                });

            });
            CompletableFutureHelper.waitForAllOf(futures);
        }).join();
    }

    private CompletableFuture<Void> importRooms(final EntityManager em, final Project parentProject, final Set<Room> rooms) {
        return null;
    }
}
