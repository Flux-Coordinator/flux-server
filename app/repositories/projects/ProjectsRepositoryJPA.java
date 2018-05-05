package repositories.projects;

import models.Measurement;
import models.Project;
import org.bson.types.ObjectId;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

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
    public CompletableFuture<List<Project>> getProjects(final int limit) {
        return CompletableFuture.supplyAsync(() -> wrap(entityManager -> getProjects(entityManager, limit)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> addProject(final Project project) {
        return CompletableFuture.supplyAsync(() -> {
            final Project persistedProject = wrap(entityManager -> addProject(entityManager, project));
            return persistedProject.getProjectId();
        }, databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> addProjects(final List<Project> projects) {
        return CompletableFuture.runAsync(() -> wrap(entityManager -> {
            addProjects(entityManager, projects);
            return null;
        }));
    }

    @Override
    public CompletableFuture<Project> getProjectById(final long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(entityManager -> getProjectById(entityManager, projectId)), databaseExecutionContext);
    }

    @Override
    public ObjectId addMeasurement(final long projectId, final String roomName, final Measurement measurement) {
        return null;
    }

    @Override
    public CompletableFuture<Long> countProjects() {
        return CompletableFuture.supplyAsync(() -> wrap(this::countProjects), databaseExecutionContext);
    }

    @Override
    public void resetRepository() {

    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Project addProject(final EntityManager em, final Project project) {
        em.persist(project);
        return project;
    }

    private void addProjects(final EntityManager em, final List<Project> projects) {
        projects.forEach(em::persist);
    }

    private List<Project> getProjects(final EntityManager em, final int limit) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p", Project.class);
        if(limit > 0) {
            typedQuery.setMaxResults(limit);
        }
        return typedQuery.getResultList();
    }

    private Project getProjectById(final EntityManager em, final long projectId) {
        return em.find(Project.class, projectId);
    }

    private Long countProjects(final EntityManager em) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT count(p) from Project p", Long.class);
        return typedQuery.getSingleResult();
    }
}