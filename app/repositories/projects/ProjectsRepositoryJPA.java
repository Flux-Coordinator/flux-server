package repositories.projects;

import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.exceptions.AlreadyExistsException;
import repositories.utils.SqlNativeHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Set<Project>> getProjectsByIds(final List<Long> projectIds) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi,
                em -> getProjectsByIds(em, projectIds)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Project>> getProjectsByName(final List<String> projectNames) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi,
                em -> getProjectsByName(em, projectNames)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Room>> getProjectRooms(long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getProjectRooms(entityManager, projectId)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> countProjectsByName(final String projectName) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> countProjectsByName(em, projectName)), databaseExecutionContext);
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
        if(project.getProjectId() != null) {
            final EntityGraph graph = em.createEntityGraph(Room.class);
            final Subgraph measurementsGraph = graph.addSubgraph("measurements");
            measurementsGraph.addSubgraph("readings");

            final TypedQuery<Room> roomTypedQuery = em.createQuery("SELECT r FROM Room r WHERE r.project.projectId = :projectId", Room.class);
            roomTypedQuery.setHint("javax.persistence.loadgraph", graph);
            roomTypedQuery.setParameter("projectId", project.getProjectId());
            final HashSet<Room> rooms = new HashSet<>(roomTypedQuery.getResultList());
            project.setRooms(rooms);
        }
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

    private Set<Project> getProjectsByIds(final EntityManager em, final List<Long> projectIds) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p WHERE p.projectId in (:projectIds)", Project.class);
        typedQuery.setParameter("projectIds", projectIds);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Set<Project> getProjectsByName(final EntityManager em, final List<String> projectNames) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p WHERE p.name in (:projectNames)", Project.class);
        typedQuery.setParameter("projectNames", projectNames);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Set<Room> getProjectRooms(final EntityManager em, final long projectId) {
        final TypedQuery<Room> typedQuery = em.createQuery("SELECT r FROM Room r WHERE r.project.projectId = " + projectId, Room.class);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Long countProjectsByName(final EntityManager em, final String projectName) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT COUNT(p) from Project p WHERE p.name LIKE (:projectName)", Long.class);
        typedQuery.setParameter("projectName", projectName);
        return typedQuery.getSingleResult();
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