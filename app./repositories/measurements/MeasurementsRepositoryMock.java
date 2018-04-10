package repositories.measurements;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;

import java.util.List;

public class MeasurementsRepositoryMock implements MeasurementsRepository {

    private final List<MeasurementReadings> readingsList;

    public MeasurementsRepositoryMock() {
        readingsList = DataGenerator.generateMeasurements(10);
    }

    @Override
    public MeasurementReadings getMeasurementReadingsById(ObjectId measurementId) {
        return null;
    }

    @Override
    public void addMeasurement(MeasurementMetadata metadata, MeasurementReadings readings) {
        readings.setMeasurementId(new ObjectId());
        readingsList.add(readings);
    }
}
