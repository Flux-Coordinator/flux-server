package repositories.measurements;

import com.google.inject.Singleton;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;

import java.util.Iterator;
import java.util.List;

@Singleton
public class MeasurementsRepositoryMock implements MeasurementsRepository {
    private final static int AMOUNT_OF_MEASUREMENTS = 10;

    private final List<MeasurementReadings> readingsList;

    public MeasurementsRepositoryMock() {
        this.readingsList = DataGenerator.generateMeasurements(AMOUNT_OF_MEASUREMENTS);
        this.readingsList.forEach(measurementReadings -> measurementReadings.setMeasurementId(new ObjectId()));
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
    public ObjectId addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings) {
        final ObjectId newId = new ObjectId();
        readings.setMeasurementId(newId);
        readingsList.add(readings);
        return newId;
    }

    @Override
    public void resetRepository() {
        this.readingsList.clear();
    }
}
