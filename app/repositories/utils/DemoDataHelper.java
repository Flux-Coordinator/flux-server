package repositories.utils;

import java.util.List;
import java.util.Set;
import models.AnchorPosition;
import models.Measurement;
import models.Project;
import models.Reading;
import repositories.generator.DataGenerator;
import repositories.generator.ValueRange;


public class DemoDataHelper {
    public static final int AMOUNT_OF_PROJECTS = 2;
    public static final int AMOUNT_OF_ROOMS_PER_PROJECT = 2;
    public static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 2;
    public static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1000;
    public static final int AMOUNT_OF_ANCHORS = 4;
    public static final double X_MIN = -100;
    public static final double Y_MIN = -1000;
    public static final double Z_MIN = 0;
    public static final double X_MAX = 8550;
    public static final double Y_MAX = 13000;
    public static final double Z_MAX = 3000;
    public static final double LUX_BASE_VALUE = 400;

    public static List<Project> generateDemoData() {
        ValueRange xValueRange = new ValueRange(X_MIN, X_MAX);
        ValueRange yValueRange = new ValueRange(Y_MIN, Y_MAX);
        ValueRange zValueRange = new ValueRange(Z_MIN, Z_MAX);

        final List<Project> projects = DataGenerator.generateProjectsAndRooms(AMOUNT_OF_PROJECTS,
            AMOUNT_OF_ROOMS_PER_PROJECT);

        projects.forEach(project -> project.getRooms().forEach(room -> {
            final Set<Measurement> roomMeasurements = DataGenerator
                .generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM, room);
            roomMeasurements.forEach(measurement -> {
                final Set<AnchorPosition> anchorPositions = DataGenerator
                    .generateAnchorPositions(AMOUNT_OF_ANCHORS, measurement, xValueRange,
                        yValueRange, zValueRange);
                measurement.setAnchorPositions(anchorPositions);

                final Set<Reading> readings = DataGenerator
                    .generateHeatmap(AMOUNT_OF_READINGS_PER_MEASUREMENT, measurement, xValueRange,
                        yValueRange, zValueRange, LUX_BASE_VALUE);
                measurement.setReadings(readings);
            });
            room.setMeasurements(roomMeasurements);
        }));

        return projects;
    }
}
