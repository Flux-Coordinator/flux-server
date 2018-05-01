package repositories.generator;

import models.*;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Helper class to generate data more easily.
 */
public class DataGenerator {
    private static final Random random = new Random();

    private DataGenerator() { }

    public static List<Project> generateProjects(final int amountOfProjects, final int roomsPerProject) {
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
//          TODO:  project.setRooms(generateRooms(rooms));

            return project;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single project", ex);
        }
    }

    public static List<Room> generateRooms(final int amount) {
        try {
            final List<Room> rooms = new ArrayList<>(amount);

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
            room.setLength(random.nextDouble() * 100);
            room.setWidth(random.nextDouble() * 100);

            return room;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single room", ex);
        }
    }

    public static List<MeasurementMetadata> generateMeasurements(final int amount) {
        try {
            final List<MeasurementMetadata> measurements = new ArrayList<>();

            for (int i = 0; i < amount; i++) {
                measurements.add(generateMeasurementMetadata());
            }

            return measurements;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a batch of measurements", ex);
        }
    }

    public static List<Reading> generateReadings(final int amount) {
        try {
            final List<Reading> readings = new ArrayList<>(amount);

            for(int i = 0; i < amount; i++) {
                readings.add(generateReading());
            }

            return readings;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating multiple readings", ex);
        }
    }

    public static Reading generateReading() {
        try {
            final Reading reading = new Reading();

            reading.setLuxValue(random.nextDouble());
            reading.setXPosition(random.nextDouble());
            reading.setYPosition(random.nextDouble());
            reading.setZPosition(random.nextDouble());

            reading.setTimestamp(new Date());

            return reading;
        } catch (final Exception ex) {
            throw new DataGeneratorException("Failed generating readings", ex);
        }
    }

    public static AnchorPosition generateAnchorPosition() {
        try {
            final AnchorPosition position = new AnchorPosition();

            position.setName("Anker-" + random.nextInt(Integer.MAX_VALUE));
            position.setXPosition(random.nextDouble());
            position.setYPosition(random.nextDouble());
            position.setZPosition(random.nextDouble());

            return position;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating anchor positions", ex);
        }
    }

    public static MeasurementMetadata generateMeasurementMetadata() {
        final int amountAnchorPositions = 3;
        try {
            final MeasurementMetadata measurementMetadata = new MeasurementMetadata();

            measurementMetadata.setMeasurementId(new ObjectId());
            measurementMetadata.setCreator("Generated");
            measurementMetadata.setDescription("Generated automatically");
            measurementMetadata.setName("AutoGenenerated" + random.nextInt());
            measurementMetadata.setFactor(random.nextDouble() * 10);
            measurementMetadata.setOffset(random.nextDouble() * 100);
            measurementMetadata.setState(MeasurementState.READY);
            measurementMetadata.setStartDate(new Date());
            measurementMetadata.setEndDate(new Date());

            for(int i = 0; i < amountAnchorPositions; i++) {
                measurementMetadata.getAnchorPositions().add(generateAnchorPosition());
            }

            return measurementMetadata;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single measurement metadata", ex);
        }
    }

    private static String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
