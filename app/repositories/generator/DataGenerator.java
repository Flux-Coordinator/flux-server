package repositories.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import models.Anchor;
import models.AnchorPosition;
import models.Measurement;
import models.MeasurementState;
import models.Project;
import models.Reading;
import models.Room;

/**
 * Helper class to generate data more easily.
 */
public class DataGenerator {
    private static final Random random = new Random();

    private DataGenerator() { }

    public static List<Project> generateProjectsAndRooms(final int amountOfProjects,
                                                 final int roomsPerProject) {
        try {
            final List<Project> projects = new ArrayList<>(amountOfProjects);

            for(int i = 0; i < amountOfProjects; i++) {
                projects.add(generateProjectAndRooms(roomsPerProject));
            }

            return projects;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of projects", ex);
        }
    }

    public static Project generateProjectAndRooms(final int amountOfRooms) {
        try {
            final Project project = new Project();

            project.setName("Project-" + random.nextInt(Integer.MAX_VALUE));
            project.setDescription("This is an example project and was automatically generated on " + getLocalDateTime() + ".");
            project.setRooms(generateRooms(amountOfRooms, project));

            return project;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single project", ex);
        }
    }

    public static Set<Room> generateRooms(final int amountOfRooms, final Project project) {
        try {
            final Set<Room> rooms = new HashSet<>(amountOfRooms);

            for(int i = 0; i < amountOfRooms; i++) {
                rooms.add(generateRoom(project));
            }

            return rooms;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of rooms", ex);
        }
    }

    public static Room generateRoom(final Project project) {
        try {
            final Room room = new Room();

            room.setName("Room-" + random.nextInt(Integer.MAX_VALUE));
            room.setDescription("This is an example room and was automatically generated on " + getLocalDateTime() + ".");
            room.setFloorSpace(random.nextInt(1000));
            room.setxOffset(random.nextInt(200));
            room.setyOffset(random.nextInt(200));
            room.setScaleFactor(random.nextDouble());
            room.setProject(project);

            return room;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single room", ex);
        }
    }

    public static Set<Measurement> generateMeasurements(final int amountOfMeasurements, final Room room) {
        try {
            final Set<Measurement> measurements = new HashSet<>();

            for (int i = 0; i < amountOfMeasurements; i++) {
                measurements.add(generateMeasurement(room));
            }

            return measurements;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of measurements", ex);
        }
    }

    public static Measurement generateMeasurement(final Room room) {
        try {
            final Measurement measurement = new Measurement();

            measurement.setName("Measurement-" + random.nextInt(Integer.MAX_VALUE));
            measurement.setDescription("This is an example measurement and was automatically generated on " + getLocalDateTime() + ".");
            measurement.setCreator("Hans Muster");
            measurement.setFactor(random.nextDouble() * 10);
            measurement.setOffset(random.nextDouble() * 100);
            measurement.setMeasurementState(MeasurementState.READY);
            measurement.setStartDate(new Date());
            measurement.setEndDate(new Date());
            measurement.setRoom(room);

            measurement.setAnchorPositions(new HashSet<>());
            measurement.setReadings(new HashSet<>());

            return measurement;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single measurement metadata", ex);
        }
    }

    public static Set<Reading> generateReadings(final int amount, final Measurement measurement) {
        try {
            final Set<Reading> readings = new HashSet<>(amount);

            for(int i = 0; i < amount; i++) {
                readings.add(generateReading(measurement));
            }

            return readings;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating multiple readings", ex);
        }
    }

    public static Set<Reading> generateHeatmap(final int amount, final Measurement measurement, ValueRange xRange, ValueRange yRange, ValueRange zRange,
        double luxBaseValue) {
        try {
            int amountOfLightSources = random.nextInt(4) + 2;
            double intensity = 20;
            double radius = xRange.getMax() < yRange.getMax() ? xRange.getMax() / 5 : yRange.getMax() / 5;
            final Set<SimulatedLightSource> simulatedLightSources = generateSimulatedLightSources(
                amountOfLightSources, xRange, yRange, intensity, radius);

            final Set<Reading> readings = new HashSet<>(amount);

            for (int i = 0; i < amount; i++) {
                readings
                    .add(generateReading(measurement, xRange, yRange, zRange, luxBaseValue, simulatedLightSources));
            }

            return readings;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating a single heatmap", ex);
        }
    }

    public static Set<SimulatedLightSource> generateSimulatedLightSources(int amount, ValueRange xRange, ValueRange yRange, double intensity, double radius) {
        Set<SimulatedLightSource> simulatedLightSources = new HashSet<>(amount);
        for (int i = 0; i < amount; i++) {
            double randomX = xRange.getRandomValue();
            double randomY = yRange.getRandomValue();
            simulatedLightSources
                .add(new SimulatedLightSource(randomX, randomY, intensity, radius));
        }
        return simulatedLightSources;
    }

    public static Reading generateReading(final Measurement measurement) {
        return generateReading(measurement, new ValueRange(10000), new ValueRange(10000), new ValueRange(3000),
            new ValueRange(10000));
    }

    public static Reading generateReading(final Measurement measurement, ValueRange xRange, ValueRange yRange, ValueRange zRange,
        double luxBaseValue, Set<SimulatedLightSource> simulatedLightSources) {
        double randomX = xRange.getRandomValue();
        double randomY = yRange.getRandomValue();
        double randomZ = zRange.getRandomValue();
        double randomLux = luxBaseValue + SimulatedLightSource.getVarianceFromLightSources(randomX, randomY, simulatedLightSources);

        return generateReading(measurement, randomX, randomY, randomZ, randomLux);
    }

    public static Reading generateReading(final Measurement measurement, ValueRange xRange, ValueRange yRange, ValueRange zRange,
        ValueRange luxRange) {
        double randomX = xRange.getRandomValue();
        double randomY = yRange.getRandomValue();
        double randomZ = zRange.getRandomValue();
        double randomLux = luxRange.getRandomValue();

        return generateReading(measurement, randomX, randomY, randomZ, randomLux);
    }

    public static Reading generateReading(final Measurement measurement, double xPosition, double yPosition, double zPosition,
        double luxValue) {
        try {
            final Reading reading = new Reading();

            reading.setLuxValue(luxValue);
            reading.setXPosition(xPosition);
            reading.setYPosition(yPosition);
            reading.setZPosition(zPosition);
            reading.setTimestamp(new Date());
            reading.setMeasurement(measurement);

            return reading;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating a single reading", ex);
        }
    }

    public static Set<AnchorPosition> generateAnchorPositions(final int amountOfAnchorPositions, final Measurement measurement, ValueRange xRange, ValueRange yRange, ValueRange zRange) {
        try {
            final Set<AnchorPosition> anchorPositions = new HashSet<>(amountOfAnchorPositions);

            for(int i = 0; i < amountOfAnchorPositions; i++) {
                anchorPositions.add(generateAnchorPosition(measurement, xRange, yRange, zRange));
            }

            return anchorPositions;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of anchor positions", ex);
        }
    }

    public static AnchorPosition generateAnchorPosition(final Measurement measurement, ValueRange xRange, ValueRange yRange, ValueRange zRange) {
        try {
            final AnchorPosition anchorPosition = new AnchorPosition();

            anchorPosition.setXPosition(xRange.getRandomValue());
            anchorPosition.setYPosition(yRange.getRandomValue());
            anchorPosition.setZPosition(zRange.getRandomValue());
            anchorPosition.setAnchor(generateAnchor());
            anchorPosition.setMeasurement(measurement);

            return anchorPosition;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single anchor position", ex);
        }
    }

    public static Anchor generateAnchor() {
        try {
            final Anchor anchor = new Anchor();
            anchor.setNetworkid("networkid" + random.nextDouble());
            return anchor;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single anchor", ex);
        }
    }

    private static String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
