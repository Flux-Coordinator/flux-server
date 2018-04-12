package repositories.generator;

import models.AnchorPosition;
import models.MeasurementReadings;
import models.Reading;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to generate data more easily.
 */
public class DataGenerator {
    private final static Random random = new Random();

    public static List<MeasurementReadings> generateMeasurements(final int amount) {
        try {
            final List<MeasurementReadings> measurementReadings = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                measurementReadings.add(generateMeasurement());
            }

            return measurementReadings;
        }
        catch(Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of measurements", ex);
        }
    }

    public static Reading generateReading() {
        try {
            final Reading reading = new Reading();

            reading.setLuxValue(random.nextDouble());
            reading.setXPosition(random.nextDouble());
            reading.setYPosition(random.nextDouble());
            reading.setZPosition(random.nextDouble());

            return reading;
        }
        catch(Exception ex) {
            throw new DataGeneratorException("Failed generating readings", ex);
        }
    }

    public static AnchorPosition generateAnchorPosition() {
        try {
            final AnchorPosition position = new AnchorPosition();

            position.setName("Anker" + random.nextInt());
            position.setXPosition(random.nextDouble());
            position.setYPosition(random.nextDouble());
            position.setZPosition(random.nextDouble());

            return position;
        }
        catch(Exception ex) {
            throw new DataGeneratorException("Failed generating anchor positions", ex);
        }
    }

    public static MeasurementReadings generateMeasurement() {
        try {
            final MeasurementReadings measurementReadings = new MeasurementReadings();

            measurementReadings.getReadings().add(generateReading());
            measurementReadings.getReadings().add(generateReading());
            measurementReadings.getReadings().add(generateReading());

            measurementReadings.getAnchorPositions().add(generateAnchorPosition());
            measurementReadings.getAnchorPositions().add(generateAnchorPosition());
            measurementReadings.getAnchorPositions().add(generateAnchorPosition());

            return measurementReadings;
        }
        catch(Exception ex) {
            throw new DataGeneratorException("Failed generating single measurement", ex);
        }
    }
}
