package repositories.measurements;

import models.Measurement;
import models.Reading;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface MeasurementsRepository {

    CompletableFuture<Set<Measurement>> getMeasurements(final int limit);

    CompletableFuture<Measurement> getMeasurementbyId(final long measurementId);

    CompletableFuture<Long> addMeasurement(final long roomId, final Measurement measurement);

    CompletableFuture<Void> addReadings(final long measurementId, final List<Reading> readings);

    void resetRepository();

    void addMeasurements(final Set<Measurement> measurements);
}
