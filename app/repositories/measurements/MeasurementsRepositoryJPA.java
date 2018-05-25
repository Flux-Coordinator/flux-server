package repositories.measurements;

import models.Measurement;
import models.MeasurementState;
import models.Reading;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public CompletableFuture<Set<Measurement>> getMeasurements(final int limit) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getMeasurements(em, limit)),
                databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Measurement> getMeasurementbyId(final long measurementId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getMeasurementById(em, measurementId)),
                databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> addMeasurement(final long roomId, final Measurement measurement) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            final Measurement persistedMeasurement = addMeasurement(em, roomId, measurement);
            return persistedMeasurement.getMeasurementId();
        }), databaseExecutionContext);
    }


    @Override
    public CompletableFuture<Void> addReadings(final long measurementId, final List<Reading> readings) {
        return CompletableFuture.runAsync(() -> wrap(jpaApi, em -> {
            addReadings(em, measurementId, readings);
            return null;
        }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> changeMeasurementState(long measurementId, final MeasurementState state) {
        return CompletableFuture
                .runAsync(() -> wrap(jpaApi, em -> {
                        changeMeasurementState(em, measurementId, state);
                        return null;
                    }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Measurement>> getMeasurementsByState(final MeasurementState state) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi,
                        em -> getMeasurementsByState(em, state)), databaseExecutionContext);
    }

    @Override
    public void resetRepository() {

    }

    @Override
    public void addMeasurements(final Set<Measurement> measurements) {
        throw new NotImplementedException();
    }

    private Set<Measurement> getMeasurements(final EntityManager em, final int limit) {
        final TypedQuery<Measurement> query = em.createQuery("SELECT m FROM Measurement m", Measurement.class);

        if(limit > 0) {
            query.setMaxResults(limit);
        }

        return new HashSet<>(query.getResultList());
    }

    private Measurement getMeasurementById(final EntityManager em, final long measurementId) {
        return em.find(Measurement.class, measurementId);
    }

    private Measurement addMeasurement(final EntityManager em, final long roomId, final Measurement measurement) {
        measurement.setRoom(em.getReference(Room.class, roomId));
        em.merge(measurement);
        return measurement;
    }

    private void addReadings(final EntityManager em, final long measurementId, final List<Reading> readings) {
        final Measurement measurement = em.getReference(Measurement.class, measurementId);
        readings.forEach(reading -> {
            reading.setMeasurement(measurement);
        });
        readings.forEach(em::persist);
    }

    private void changeMeasurementState(final EntityManager em, final long measurementId, final MeasurementState state) {
        final Measurement measurement = em.find(Measurement.class, measurementId);
        measurement.setMeasurementState(state);
    }

    private Set<Measurement> getMeasurementsByState(final EntityManager em, final MeasurementState state) {
        final TypedQuery<Measurement> query = em
                .createQuery("SELECT m FROM Measurement m WHERE measurementState = :state",
                        Measurement.class);

        query.setParameter("state", state);

        return new HashSet<>(query.getResultList());
    }
}
