package repositories.generator;

import models.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to generate data more easily.
 */
public class DataGenerator {
    private final static Random random = new Random();

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

            project.setName("Project-" + Math.abs(random.nextInt()));
            project.setDescription("This is an example project and was automatically generated on " + getLocalDateTime() + ".");
            project.setRooms(generateRooms(rooms));

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

            room.setName("Room-" + Math.abs(random.nextInt()));
            room.setDescription("This is an example room and was automatically generated on " + getLocalDateTime() + ".");
            room.setLength(random.nextDouble() * 100);
            room.setWidth(random.nextDouble() * 100);

            //TODO: Set measurements

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

    public static Reading generateReading() {
        try {
            final Reading reading = new Reading();

            reading.setLuxValue(random.nextDouble());
            reading.setXPosition(random.nextDouble());
            reading.setYPosition(random.nextDouble());
            reading.setZPosition(random.nextDouble());

            return reading;
        }
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating readings", ex);
        }
    }

    public static AnchorPosition generateAnchorPosition() {
        try {
            final AnchorPosition position = new AnchorPosition();

            position.setName("Anker" + Math.abs(random.nextInt()));
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
        try {
            final MeasurementMetadata measurementMetadata = new MeasurementMetadata();

            measurementMetadata.setCreator("Generated");
            measurementMetadata.setDescription("Generated automatically");
            measurementMetadata.setName("AutoGen" + random.nextInt());
            measurementMetadata.setFactor(random.nextDouble() * 10);
            measurementMetadata.setOffset(random.nextDouble() * 100);
            measurementMetadata.setState(MeasurementState.READY);

            return measurementMetadata;
        } catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating a single measurement metadata", ex);
        }
    }

    public static MeasurementReadings generateMeasurementReadings() {
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
        catch(final Exception ex) {
            throw new DataGeneratorException("Failed generating single measurement reading", ex);
        }
    }

    private static String getLocalDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
