package repositories.projects;

import akka.http.scaladsl.model.Uri;
import models.MeasurementMetadata;
import models.Project;
import org.bson.types.ObjectId;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
    public Iterator<Project> getProjects() {

        return null;
    }

    @Override
    public CompletionStage<Long> addProject(final Project project) {
        return CompletableFuture.supplyAsync(() -> {
            final Project persistedProject = wrap(entityManager -> addProject(entityManager, project));
            return persistedProject.getProjectId();
        }, databaseExecutionContext);
    }

    @Override
    public void addProjects(final List<Project> projects) {
        CompletableFuture.runAsync(() -> wrap(entityManager -> {
            addProjects(entityManager, projects);
            return null;
        }));
    }

    @Override
    public CompletionStage<Project> getProjectById(final long projectId) {
        return CompletableFuture.supplyAsync(() -> wrap(entityManager -> getProjectById(entityManager, projectId)), databaseExecutionContext);
    }

    @Override
    public ObjectId addMeasurement(ObjectId projectId, String roomName, MeasurementMetadata measurementMetadata) {
        return null;
    }

    @Override
    public CompletionStage<Long> countProjects() {
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

    private Project getProjectById(final EntityManager em, final long projectId) {
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p where p.projectId = " + projectId, Project.class);
        final List<Project> foundProjects = typedQuery.getResultList();

        return foundProjects.isEmpty() ? null : foundProjects.get(0);
    }

    private Long countProjects(final EntityManager em) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT count(p) from Project p", Long.class);
        return typedQuery.getSingleResult();
    }
}