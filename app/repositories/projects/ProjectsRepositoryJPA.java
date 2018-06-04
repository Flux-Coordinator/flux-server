package repositories.projects;

import models.Project;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.utils.SqlNativeHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public CompletableFuture<Set<Project>> getProjectsById(final List<Long> projectIds) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi,
                em -> getProjectsById(em, projectIds)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Room>> getProjectRooms(long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getProjectRooms(entityManager, projectId)), databaseExecutionContext);
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

    private Set<Project> getProjectsById(final EntityManager em, final List<Long> projectIds) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p WHERE p.id in (:projectIds)", Project.class);
        typedQuery.setParameter("projectIds", projectIds);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Set<Room> getProjectRooms(final EntityManager em, final long projectId) {
        final TypedQuery<Room> typedQuery = em.createQuery("SELECT r FROM Room r WHERE r.project.id = " + projectId, Room.class);
        return new HashSet<>(typedQuery.getResultList());
    }

    private Long countProjects(final EntityManager em) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT count(p) from Project p", Long.class);
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