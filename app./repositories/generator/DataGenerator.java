package repositories.generator;

import models.AnchorPosition;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private static Random random = new Random();

    public static List<MeasurementReadings> generateMeasurements(final int amount) {
        final List<MeasurementReadings> measurementReadings = new ArrayList<>();

        for(int i = 0; i < amount; i++) {
            measurementReadings.add(generateMeasurement());
        }

        return measurementReadings;
    }


    public static Reading generateReading() {
        final Reading reading = new Reading();

        reading.setLuxValue(random.nextDouble());
        reading.setXPosition(random.nextDouble());
        reading.setYPosition(random.nextDouble());
        reading.setZPosition(random.nextDouble());

        return reading;
    }

    public static AnchorPosition generateAnchorPosition() {
        final AnchorPosition position = new AnchorPosition();

        position.setName("Anker" + random.nextInt());
        position.setXPosition(random.nextDouble());
        position.setYPosition(random.nextDouble());
        position.setZPosition(random.nextDouble());

        return position;
    }


    public static MeasurementReadings generateMeasurement() {
        final MeasurementReadings measurementReadings = new MeasurementReadings();

        measurementReadings.getReadings().add(generateReading());
        measurementReadings.getReadings().add(generateReading());
        measurementReadings.getReadings().add(generateReading());

        measurementReadings.getAnchorPositions().add(generateAnchorPosition());
        measurementReadings.getAnchorPositions().add(generateAnchorPosition());
        measurementReadings.getAnchorPositions().add(generateAnchorPosition());

        return measurementReadings;
    }
}
