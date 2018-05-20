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

    public static List<Project> generateProjects(final int amountOfProjects,
                                                 final int roomsPerProject) {
        try {
            final List<Project> projects = new ArrayList<>(amountOfProjects);

            for(int i = 0; i < amountOfProjects; i++) {
                projects.add(generateProject(roomsPerProject));
            }

            return projects;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of projects", ex);
        }
    }

    public static Project generateProject(final int rooms) {
        try {
            final Project project = new Project();

            project.setName("Project-" + random.nextInt(Integer.MAX_VALUE));
            project.setDescription("This is an example project and was automatically generated on " + getLocalDateTime() + ".");
            final Set<Room> roomList = generateRooms(rooms);
            roomList.forEach(room -> room.setProject(project));
            project.setRooms(roomList);

            return project;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single project", ex);
        }
    }

    public static Set<Room> generateRooms(final int amount) {
        try {
            final Set<Room> rooms = new HashSet<>(amount);

            for(int i = 0; i < amount; i++) {
                rooms.add(generateRoom());
            }

            return rooms;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of rooms", ex);
        }
    }

    public static Room generateRoom() {
        try {
            final Room room = new Room();

            room.setName("Room-" + random.nextInt(Integer.MAX_VALUE));
            room.setDescription("This is an example room and was automatically generated on " + getLocalDateTime() + ".");
            room.setFloorSpace(random.nextInt(1000));
            room.setxOffset(random.nextInt(200));
            room.setyOffset(random.nextInt(200));
            room.setScaleFactor(random.nextDouble());

            return room;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single room", ex);
        }
    }

    public static Set<Measurement> generateMeasurements(final int amount, double xMax, double yMax) {
        try {
            final Set<Measurement> measurements = new HashSet<>();

            for (int i = 0; i < amount; i++) {
                measurements.add(generateMeasurement(xMax, yMax));
            }

            return measurements;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of measurements", ex);
        }
    }

    public static Measurement generateMeasurement(double xMax, double yMax) {
        final int amountAnchorPositions = 3;
        try {
            final Measurement measurement = new Measurement();

            measurement.setCreator("Generated");
            measurement.setDescription("Generated automatically");
            measurement.setName("AutoGenenerated" + random.nextInt());
            measurement.setFactor(random.nextDouble() * 10);
            measurement.setOffset(random.nextDouble() * 100);
            measurement.setMeasurementState(MeasurementState.READY);
            measurement.setStartDate(new Date());
            measurement.setEndDate(new Date());

            measurement.setAnchorPositions(new HashSet<>());
            measurement.setReadings(new HashSet<>());

            final ValueRange xRange = new ValueRange(xMax);
            final ValueRange yRange = new ValueRange(yMax);
            final ValueRange zRange = new ValueRange(0);

            for(int i = 0; i < amountAnchorPositions; i++) {

                measurement.getAnchorPositions().add(generateAnchorPosition(xRange, yRange, zRange));
            }

            return measurement;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single measurement metadata", ex);
        }
    }

    public static Set<Reading> generateReadings(final int amount) {
        try {
            final Set<Reading> readings = new HashSet<>(amount);

            for(int i = 0; i < amount; i++) {
                readings.add(generateReading());
            }

            return readings;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating multiple readings", ex);
        }
    }

    public static Set<Reading> generateHeatmap(final int amount, double xMax, double yMax,
        double luxBaseValue) {
        try {
            int amountOfLightSources = random.nextInt(4) + 2;
            double intensity = 20;
            double radius = xMax < yMax ? xMax / 5 : yMax / 5;
            final Set<SimulatedLightSource> simulatedLightSources = generateSimulatedLightSources(
                amountOfLightSources, xMax, yMax, intensity, radius);

            final Set<Reading> readings = new HashSet<>(amount);

            final ValueRange xRange = new ValueRange(xMax);
            final ValueRange yRange = new ValueRange(yMax);
            final ValueRange zRange = new ValueRange(0);
            for (int i = 0; i < amount; i++) {
                readings
                    .add(generateReading(xRange, yRange, zRange, luxBaseValue, simulatedLightSources));
            }

            return readings;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating heatmap", ex);
        }
    }

    public static Set<SimulatedLightSource> generateSimulatedLightSources(int amount, double xMax,
        double yMax, double intensity, double radius) {
        Set<SimulatedLightSource> simulatedLightSources = new HashSet<>(amount);
        for (int i = 0; i < amount; i++) {
            double randomX = xMax * random.nextDouble();
            double randomY = yMax * random.nextDouble();
            simulatedLightSources
                .add(new SimulatedLightSource(randomX, randomY, intensity, radius));
        }
        return simulatedLightSources;
    }

    private static double getVarianceFromLightSources(double xPosition, double yPosition,
        Set<SimulatedLightSource> lightSources) {
        double variance = 0;
        for (SimulatedLightSource lightSource : lightSources) {
            variance += Math.max(0,
                lightSource.getRadius() - getDistanceToLightSource(xPosition, yPosition,
                    lightSource)) * lightSource.getIntensity();
        }
        return variance;
    }

    private static double getDistanceToLightSource(double xPosition, double yPosition,
        SimulatedLightSource lightSource) {
        return Math.sqrt(Math.pow(xPosition - lightSource.getxPosition(), 2) + Math
            .pow(yPosition - lightSource.getyPosition(), 2));
    }

    public static Reading createReading(double xPosition, double yPosition, double zPosition,
        double luxValue) {
        try {
            final Reading reading = new Reading();

            reading.setLuxValue(luxValue);
            reading.setXPosition(xPosition);
            reading.setYPosition(yPosition);
            reading.setZPosition(zPosition);
            reading.setTimestamp(new Date());

            return reading;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating reading", ex);
        }
    }

    public static Reading generateReading() {
        return generateReading(new ValueRange(10000), new ValueRange(10000), new ValueRange(3000),
            new ValueRange(10000));
    }

    public static Reading generateReading(ValueRange xRange, ValueRange yRange, ValueRange zRange,
        ValueRange luxRange) {
        double randomX = getRandomDoubleFromRange(xRange);
        double randomY = getRandomDoubleFromRange(yRange);
        double randomZ = getRandomDoubleFromRange(zRange);
        double randomLux = getRandomDoubleFromRange(luxRange);

        return createReading(randomX, randomY, randomZ, randomLux);
    }

    public static Reading generateReading(ValueRange xRange, ValueRange yRange, ValueRange zRange,
        double luxBaseValue, Set<SimulatedLightSource> simulatedLightSources) {
        double randomX = getRandomDoubleFromRange(xRange);
        double randomY = getRandomDoubleFromRange(yRange);
        double randomZ = getRandomDoubleFromRange(zRange);
        double randomLux = luxBaseValue + getVarianceFromLightSources(randomX, randomY, simulatedLightSources);

        return createReading(randomX, randomY, randomZ, randomLux);
    }

    private static double getRandomDoubleFromRange(ValueRange range) {
        return range.getMin() + (range.getMax() - range.getMin()) * random.nextDouble();
    }

    public static Anchor generateAnchor() {
        try {
            final Anchor anchor = new Anchor();
            anchor.setNetworkid("networkid" + random.nextDouble());
            return anchor;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating anchor", ex);
        }
    }

    public static AnchorPosition generateAnchorPosition(ValueRange xRange, ValueRange yRange, ValueRange zRange) {
        try {
            final AnchorPosition position = new AnchorPosition();

            double randomX = getRandomDoubleFromRange(xRange);
            double randomY = getRandomDoubleFromRange(yRange);
            double randomZ = getRandomDoubleFromRange(zRange);

            position.setXPosition(randomX);
            position.setYPosition(randomY);
            position.setZPosition(randomZ);
            position.setAnchor(generateAnchor());

            return position;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating anchor positions", ex);
        }
    }


    private static String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
