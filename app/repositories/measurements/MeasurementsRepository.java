package repositories.measurements;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;

import java.util.Iterator;

public interface MeasurementsRepository {
    Iterator<MeasurementReadings> getMeasurementReadings();
    MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId);
    ObjectId addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings);
    void resetRepository();
}
