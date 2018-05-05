package repositories.measurements;

import models.Measurement;
import models.Reading;
import org.bson.types.ObjectId;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static repositories.utils.Helper.wrap;

@Singleton
public class MeasurementsRepositoryJPA implements MeasurementsRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public MeasurementsRepositoryJPA(final JPAApi jpaApi, final DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }

    @Override
    public CompletableFuture<List<Measurement>> getMeasurements(final int limit) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getMeasurements(entityManager, limit)),
                databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Measurement> getMeasurementbyId(final long measurementId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> getMeasurementById(entityManager, measurementId)),
                databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> createMeasurement(final Measurement measurement) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, entityManager -> {
            final Measurement persistedMeasurement = createMeasurement(entityManager, measurement);
            return persistedMeasurement.getMeasurementId();
        }), databaseExecutionContext);
    }


    @Override
    public CompletableFuture<Void> addReadings(final long measurementId, final List<Reading> readings) {
        return CompletableFuture.runAsync(() -> wrap(jpaApi, entityManager -> {
            addReadings(entityManager, measurementId, readings);
            return null;
        }), databaseExecutionContext);
    }

    @Override
    public void resetRepository() {

    }

    @Override
    public void addMeasurements(final List<Measurement> measurements) {

    }

    private List<Measurement> getMeasurements(final EntityManager em, final int limit) {
        final TypedQuery<Measurement> query = em.createQuery("SELECT m FROM Measurement m", Measurement.class);

        if(limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

    private Measurement getMeasurementById(final EntityManager em, final long measurementId) {
        return em.find(Measurement.class, measurementId);
    }

    private Measurement createMeasurement(final EntityManager em, final Measurement measurement) {
        em.persist(measurement);
        return measurement;
    }

    private void addReadings(final EntityManager em, final long measurementId, final List<Reading> readings) {

    }
}
