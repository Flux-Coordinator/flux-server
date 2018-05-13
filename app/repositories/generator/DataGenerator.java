package repositories.generator;

import models.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            room.setFloorSpace(random.nextInt());
            room.setxOffset(random.nextInt());
            room.setyOffset(random.nextInt());
            room.setScaleFactor(random.nextInt());

            return room;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single room", ex);
        }
    }

    public static Set<Measurement> generateMeasurements(final int amount) {
        try {
            final Set<Measurement> measurements = new HashSet<>();

            for (int i = 0; i < amount; i++) {
                measurements.add(generateMeasurement());
            }

            return measurements;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of measurements", ex);
        }
    }

    public static Measurement generateMeasurement() {
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

            for(int i = 0; i < amountAnchorPositions; i++) {
                measurement.getAnchorPositions().add(generateAnchorPosition());
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

    public static Reading generateReading(double xMax, double yMax, double zMax, double luxMin, double luxMax) {
        double randomX = xMax * random.nextDouble();
        double randomY = yMax * random.nextDouble();
        double randomZ = zMax * random.nextDouble();
        double randomLux = luxMin + (luxMax - luxMin) * random.nextDouble();

        try {
            final Reading reading = new Reading();

            reading.setLuxValue(randomLux);
            reading.setXPosition(randomX);
            reading.setYPosition(randomY);
            reading.setZPosition(randomZ);
            reading.setTimestamp(new Date());

            return reading;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating readings", ex);
        }
    }

    public static Reading generateReading() {
        return generateReading(10000, 10000, 3000, 0, 10000);
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

    public static AnchorPosition generateAnchorPosition() {
        try {
            final AnchorPosition position = new AnchorPosition();

            position.setXPosition(random.nextDouble());
            position.setYPosition(random.nextDouble());
            position.setZPosition(random.nextDouble());
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
