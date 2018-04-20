package repositories.measurements;

import com.google.inject.Singleton;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Singleton
public class MeasurementsRepositoryMock implements MeasurementsRepository {
    private final List<MeasurementReadings> readingsList;

    public MeasurementsRepositoryMock() {
        this.readingsList = new ArrayList<>();
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
    public ObjectId createMeasurement(final MeasurementReadings readings) {
        if(readings.getMeasurementId() == null) {
            readings.setMeasurementId(new ObjectId());
        }
        readingsList.add(readings);
        return readings.getMeasurementId();
    }

    @Override
    public void addReadings(final ObjectId measurementId, final List<Reading> readings) {
        final Optional<MeasurementReadings> measurementReadings = readingsList.parallelStream()
                .filter(m -> m.getMeasurementId().equals(measurementId))
                .findAny();

        measurementReadings.get().getReadings().addAll(readings);
    }

    @Override
    public void resetRepository() {
        this.readingsList.clear();
    }
}
