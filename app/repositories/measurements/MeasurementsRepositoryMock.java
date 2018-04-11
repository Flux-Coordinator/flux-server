package repositories.measurements;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;

import java.util.Iterator;
import java.util.List;

public class MeasurementsRepositoryMock implements MeasurementsRepository {

    private final List<MeasurementReadings> readingsList;

    public MeasurementsRepositoryMock() {
        readingsList = DataGenerator.generateMeasurements(10);
    }

    @Override
    public Iterator<MeasurementReadings> getMeasurementReadings() {
        return this.readingsList.iterator();
    }

    @Override
    public MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId) {
        return readingsList.stream()
                .filter(measurementReadings -> measurementReadings.getMeasurementId().equals(measurementId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings) {
        readings.setMeasurementId(new ObjectId());
        readingsList.add(readings);
    }
}
