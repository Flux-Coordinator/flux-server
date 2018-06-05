package repositories.measurements;

import models.Measurement;
import models.MeasurementState;
import models.Reading;
import models.Room;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;
import repositories.exceptions.AlreadyExistsException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static repositories.utils.JpaHelper.flushAndClear;
import static repositories.utils.JpaHelper.wrap;

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
    public CompletableFuture<Set<Measurement>> getMeasurementsById(List<Long> measurementIds) {
        return CompletableFuture
                .supplyAsync(() -> wrap(jpaApi,
                        em -> getMeasurementsById(em, measurementIds)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Set<Measurement>> getMeasurementsByNames(final List<String> measurementNames) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> getMeasurementsByNames(em, measurementNames)), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Long> addMeasurement(final long roomId, final Measurement measurement) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            final Measurement persistedMeasurement = addMeasurement(em, roomId, measurement);
            return persistedMeasurement.getMeasurementId();
        }), databaseExecutionContext);
    }

    @Override
    public CompletableFuture<Void> addMeasurements(final List<Measurement> measurements) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            addMeasurements(em, measurements);
            return null;
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
    public CompletableFuture<Void> removeMeasurement(final long measurementId) {
        return CompletableFuture.supplyAsync(() -> wrap(jpaApi, em -> {
            removeMeasurement(em, measurementId);
            return null;
        }), databaseExecutionContext);
    }

    private static EntityGraph<Measurement> retrieveGraphWithReadings(final EntityManager em) {
        final EntityGraph<Measurement> measurementEntityGraph = em.createEntityGraph(Measurement.class);
        measurementEntityGraph.addAttributeNodes("readings");
        return measurementEntityGraph;
    }

    private static Map<String, Object> retrieveGraphWithReadingsHints(final EntityManager em) {
        final Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph", retrieveGraphWithReadings(em));
        return hints;
    }

    private Set<Measurement> getMeasurements(final EntityManager em, final int limit) {
        final TypedQuery<Measurement> query = em.createQuery("SELECT m FROM Measurement m", Measurement.class);

        if(limit > 0) {
            query.setMaxResults(limit);
        }

        return new HashSet<>(query.getResultList());
    }

    private Measurement getMeasurementById(final EntityManager em, final long measurementId) {
        return em.find(Measurement.class, measurementId, retrieveGraphWithReadingsHints(em));
    }

    private Set<Measurement> getMeasurementsById(final EntityManager em, final List<Long> measurementIds) {
        final TypedQuery<Measurement> query = em
                .createQuery("SELECT m FROM Measurement m WHERE m.measurementId in (:measurementIds)", Measurement.class);
        query.setParameter("measurementIds", measurementIds);
        query.setHint("javax.persistence.loadgraph", retrieveGraphWithReadings(em));
        return new HashSet<>(query.getResultList());
    }

    private Long countMeasurementsByName(final EntityManager em, final String measurementName) {
        final TypedQuery<Long> typedQuery = em.createQuery("SELECT COUNT(m) from Measurement m WHERE m.name LIKE (:measurementName)", Long.class);
        typedQuery.setParameter("measurementName", measurementName);
        return typedQuery.getSingleResult();
    }

    private Set<Measurement> getMeasurementsByNames(final EntityManager em, final List<String> measurementNames) {
        final TypedQuery<Measurement> query = em
                .createQuery("SELECT m FROM Measurement m WHERE m.name in (:measurementNames)", Measurement.class);
        query.setParameter("measurementNames", measurementNames);
        query.setHint("javax.persistence.loadgraph", retrieveGraphWithReadings(em));
        return new HashSet<>(query.getResultList());
    }

    private Measurement addMeasurement(final EntityManager em, final long roomId, final Measurement measurement) {
        measurement.setRoom(em.getReference(Room.class, roomId));
        return em.merge(measurement);
    }

    private void addMeasurements(final EntityManager em, final List<Measurement> measurements) {
        measurements.forEach(em::merge);
    }

    private void addReadings(final EntityManager em, final long measurementId, final List<Reading> readings) {
        final Measurement measurement = em.getReference(Measurement.class, measurementId);
        readings.forEach(reading -> reading.setMeasurement(measurement));
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
        query.setHint("javax.persistence.loadgraph", retrieveGraphWithReadings(em));

        return new HashSet<>(query.getResultList());
    }

    private void removeMeasurement(final EntityManager em, final long measurementId) {
        Measurement measurement = em.find(Measurement.class, measurementId);
        measurement.setRoom(null);
        flushAndClear(em);
        measurement = em.getReference(Measurement.class, measurementId);
        em.remove(measurement);
    }
}
