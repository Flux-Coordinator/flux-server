package repositories.measurements;

import com.google.inject.Singleton;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;

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
    public ObjectId addMeasurement(final MeasurementReadings readings) {
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
        measurementReadings.orElseThrow(() -> new NullPointerException("Measurement to add the reading was not found.")).getReadings().addAll(readings);
    }

    public void resetRepository() {
        this.readingsList.clear();
    }

    @Override
    public void addMeasurements(final List<MeasurementReadings> measurementReadings) {
        this.readingsList.addAll(measurementReadings);
    }
}
