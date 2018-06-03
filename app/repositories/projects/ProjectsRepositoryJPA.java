package repositories.projects;

import models.Measurement;
import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.utils.CollectionHelper;
import repositories.utils.SqlNativeHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static repositories.utils.JpaHelper.wrap;

@Singleton
public class ProjectsRepositoryJPA implements ProjectsRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public ProjectsRepositoryJPA(final JPAApi jpaApi, final DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletableFuture<Set<Project>> getProjects(final int limit) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getProjects(entityManager, limit)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> addProject(final Project project) {
        return CompletableFuture.supplyAsync(() -> {
            final Project persistedProject = wrap(jpaApi, entityManager -> addProject(entityManager, project));
            return persistedProject.getProjectId();
        }, databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> addProjects(final List<Project> projects) {
        return CompletableFuture.runAsync(() -> wrap(jpaApi, entityManager -> {
            addProjects(entityManager, projects);
            return null;
        }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Project> getProjectById(final long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getProjectById(entityManager, projectId)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Room>> getProjectRooms(long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getProjectRooms(entityManager, projectId)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Project>> getRelatedProjects(final List<Measurement> measurements) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getRelatedProjects(em, measurements)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> countProjects() {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, this::countProjects), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> removeProject(final long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            removeProject(em, projectId);
            return null;
        }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> resetRepository() {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            this.resetRepository(em);
            return null;
        }), databaseExecutionContext);
    }

    private Project addProject(final EntityManager em, final Project project) {
        return em.merge(project);
    }

    private void addProjects(final EntityManager em, final List<Project> projects) {
        projects.forEach(em::merge);
    }

    private Set<Project> getProjects(final EntityManager em, final int limit) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p", Project.class);
        if(limit > 0) {
            typedQuery.setMaxResults(limit);
        }

        return new HashSet<>(typedQuery.getResultList());
    }

    private Project getProjectById(final EntityManager em, final long projectId) {
        return em.find(Project.class, projectId);
    }

    private Set<Room> getProjectRooms(final EntityManager em, final long projectId) {
        final TypedQuery<Room> typedQuery = em.createQuery("SELECT r FROM Room r WHERE r.project.id = " + projectId, Room.class);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Long countProjects(final EntityManager em) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT count(p) from Project p", Long.class);
        return typedQuery.getSingleResult();
    }

    private Set<Project> getRelatedProjects(final EntityManager em, final List<Measurement> measurements) {
        final List<Long> ids = measurements.stream().map(Measurement::getMeasurementId).collect(Collectors.toList());
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p INNER JOIN p.rooms as r " +
                "INNER JOIN r.measurements as m " +
                "INNER JOIN m.readings as reading " +
                "WHERE m.id in (:measurementIds)", Project.class);
        typedQuery.setParameter("measurementIds", ids);

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
                                -> CollectionHelper.containsByComparator(measurements, measurement
                                -> measurement.getMeasurementId() == questionableMeasurement.getMeasurementId()));

                if (!currentRoom.getMeasurements().isEmpty()) {
                    cleanedRooms.add(currentRoom);
                }
            }
            currentProject.setRooms(cleanedRooms);
        }

        return projects;
    }

    private void removeProject(final EntityManager em, final long projectId) {
        final Project projectReference = em.getReference(Project.class, projectId);
        em.remove(projectReference);
    }

    private void resetRepository(final EntityManager em) {
        final Query q = em.createNativeQuery(SqlNativeHelper.getTruncateAllTables());
        q.executeUpdate();
    }
}