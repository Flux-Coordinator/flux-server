package repositories.utils;

import java.util.List;
import java.util.Set;
import models.AnchorPosition;
import models.Measurement;
import models.Project;
import models.Reading;
import models.Room;
import repositories.generator.DataGenerator;
import repositories.generator.ValueRange;


public class DemoDataHelper {
    public static final int AMOUNT_OF_PROJECTS = 2;
    public static final int AMOUNT_OF_ROOMS_PER_PROJECT = 2;
    public static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 2;
    public static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1000;
    private static final int AMOUNT_OF_ANCHORS = 4;
    private static final double X_MIN = -100;
    private static final double Y_MIN = -1000;
    private static final double Z_MIN = 0;
    private static final double X_MAX = 8550;
    private static final double Y_MAX = 13000;
    private static final double Z_MAX = 3000;
    private static final double LUX_BASE_VALUE = 400;

    public static List<Project> generateDemoData() {
        ValueRange xValueRange = new ValueRange(X_MIN, X_MAX);
        ValueRange yValueRange = new ValueRange(Y_MIN, Y_MAX);
        ValueRange zValueRange = new ValueRange(Z_MIN, Z_MAX);

        final List<Project> projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS);
        projects.forEach(project -> {
            final Set<Room> rooms = DataGenerator
                .generateRooms(AMOUNT_OF_ROOMS_PER_PROJECT, project);
            rooms.forEach(room -> {
                final Set<Measurement> measurements = DataGenerator
                    .generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM, room);
                measurements.forEach(measurement -> {
                    final Set<AnchorPosition> anchorPositions = DataGenerator
                        .generateAnchorPositions(AMOUNT_OF_ANCHORS, measurement, xValueRange,
                            yValueRange, zValueRange);
                    measurement.setAnchorPositions(anchorPositions);

                    final Set<Reading> readings = DataGenerator
                        .generateHeatmap(AMOUNT_OF_READINGS_PER_MEASUREMENT, measurement,
                            xValueRange,
                            yValueRange, zValueRange, LUX_BASE_VALUE);
                    measurement.setReadings(readings);
                });
                room.setMeasurements(measurements);
            });
            project.setRooms(rooms);
        });

        return projects;
    }
}
