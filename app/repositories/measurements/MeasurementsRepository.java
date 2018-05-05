package repositories.measurements;

import models.Measurement;
import models.Reading;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MeasurementsRepository {

    CompletableFuture<List<Measurement>> getMeasurements(final int limit);

    CompletableFuture<Measurement> getMeasurementbyId(final long measurementId);

    CompletableFuture<Long> createMeasurement(final Measurement measurement);

    void addReadings(final ObjectId measurementId, final List<Reading> readings);

    void resetRepository();

    void addMeasurements(final List<Measurement> measurements);
}
