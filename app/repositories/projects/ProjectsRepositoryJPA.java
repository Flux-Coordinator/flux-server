package repositories.projects;

import models.MeasurementMetadata;
import models.Project;
import org.bson.types.ObjectId;
import org.hibernate.Session;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Iterator;
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
    public Iterator<Project> getProjects() {

        return null;
    }

    @Override
    public long addProject(Project project) {
        return 0;
    }

    @Override
    public void addProjects(List<Project> projects) {

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

    private Long countProjects(final EntityManager em) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT count(p) from Project p", Long.class);
        return typedQuery.getSingleResult();
    }

    private Project getProjectById(final EntityManager em, final long projectId) {
//        final CriteriaBuilder cb = em.getCriteriaBuilder();
//        final CriteriaQuery<Project> criteriaQuery = cb.createQuery(Project.class);
//        final Root<Project> projectRoot = criteriaQuery.from(Project.class);
//        criteriaQuery.select(projectRoot);
//        final TypedQuery<Project> typedQuery = em.createQuery(criteriaQuery);$
        final TypedQuery<Project> typedQuery = em.createQuery("SELECT p FROM Project p where p.projectId = " + projectId, Project.class);
        return typedQuery.getSingleResult();
    }
}