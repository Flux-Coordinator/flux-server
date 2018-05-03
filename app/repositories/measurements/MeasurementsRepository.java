package repositories.measurements;

import models.Measurement;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MeasurementsRepository {

    CompletableFuture<List<Measurement>> getMeasurements(final int limit);

    MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId);

    ObjectId addMeasurement(final MeasurementReadings readings);

    void addReadings(final ObjectId measurementId, final List<Reading> readings);

    void resetRepository();

    void addMeasurements(final List<MeasurementReadings> measurementReadings);
}
