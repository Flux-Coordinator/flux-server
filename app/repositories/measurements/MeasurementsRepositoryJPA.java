package repositories.measurements;

import models.Measurement;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;
import play.db.jpa.JPAApi;
import repositories.DatabaseExecutionContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
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
    public MeasurementReadings getMeasurementReadingsById(ObjectId measurementId) {
        return null;
    }

    @Override
    public ObjectId addMeasurement(MeasurementReadings readings) {
        return null;
    }

    @Override
    public void addReadings(ObjectId measurementId, List<Reading> readings) {

    }

    @Override
    public void resetRepository() {

    }

    @Override
    public void addMeasurements(List<MeasurementReadings> measurementReadings) {

    }

    private List<Measurement> getMeasurements(final EntityManager em, final int limit) {
        return null;
    }
}
