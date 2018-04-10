package repositories.measurements;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;

public interface MeasurementsRepository {
    MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId);
    void addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings);
}
