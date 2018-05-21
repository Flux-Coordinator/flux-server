package repositories.utils;

import models.Measurement;
import models.Project;
import models.Reading;
import repositories.generator.DataGenerator;

import java.util.List;
import java.util.Set;

public class DemoDataHelper {
    public static final int AMOUNT_OF_PROJECTS = 2;
    public static final int AMOUNT_OF_ROOMS_PER_PROJECT = 2;
    public static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 2;
    public static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1000;
    public static final double LENGTH_OF_ROOM = 13000;
    public static final double WIDTH_OF_ROOM = 9000;
    public static final double LUX_BASE_VALUE = 400;

    public static List<Project> generateDemoData() {
        final List<Project> projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS,
                AMOUNT_OF_ROOMS_PER_PROJECT);

        projects.forEach(project -> project.getRooms().forEach(room -> {
            final Set<Measurement> roomMeasurements = DataGenerator.generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM, WIDTH_OF_ROOM, LENGTH_OF_ROOM);
            roomMeasurements.forEach(measurement -> {
                measurement.setRoom(room);
                final Set<Reading> readings = DataGenerator.generateHeatmap(AMOUNT_OF_READINGS_PER_MEASUREMENT, WIDTH_OF_ROOM, LENGTH_OF_ROOM, LUX_BASE_VALUE);
                readings.forEach(reading -> {
                    reading.setMeasurement(measurement);
                });
                measurement.getAnchorPositions().forEach(anchorPosition -> anchorPosition.setMeasurement(measurement));
                measurement.setReadings(readings);
            });
            room.setMeasurements(roomMeasurements);
        }));

        return projects;
    }
}
