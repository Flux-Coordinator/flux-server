package repositories.measurements;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.List;

public interface MeasurementsRepository {

    Iterator<MeasurementReadings> getMeasurementReadings();

    MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId);

    ObjectId addMeasurement(final MeasurementReadings readings);

    void addReadings(final ObjectId measurementId, final List<Reading> readings);

    void resetRepository();
}
