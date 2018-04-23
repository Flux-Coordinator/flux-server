package repositories.projects;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Project;
import models.Room;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ProjectsRepositoryMock implements ProjectsRepository {
    private final static int AMOUNT_OF_PROJECTS = 10;
    private final static int AMOUNT_OF_ROOMS_PER_PROJECT = 5;

    private final List<Project> projects;
    private final MeasurementsRepository measurementsRepository;

    @Inject
    public ProjectsRepositoryMock(final MeasurementsRepository measurementsRepository) {
        this.projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS, AMOUNT_OF_ROOMS_PER_PROJECT);
        this.measurementsRepository = measurementsRepository;
    }

    @Override
    public Iterator<Project> getProjects() {
        return this.projects.iterator();
    }

    @Override
    public ObjectId addProject(final Project project) {
        final ObjectId newId = new ObjectId();
        project.setProjectId(newId);
        this.projects.add(project);
        return newId;
    }

    @Override
    public Project getProjectById(final ObjectId projectId) {
        return this.projects.stream()
                .filter(project -> project.getProjectId().equals(projectId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ObjectId addMeasurement(final ObjectId projectId, final String roomName, final MeasurementMetadata measurementMetadata) {
        final Project project = getProjectById(projectId);
        final Optional<Room> room = project.getRooms().parallelStream()
                .filter(room1 -> room1.getName().equals(roomName))
                .findAny();

        final ObjectId measurementId = new ObjectId();
        measurementMetadata.setMeasurementId(measurementId);
        final MeasurementReadings measurementReadings = new MeasurementReadings();
        measurementReadings.setMeasurementId(measurementId);

        //noinspection ConstantConditions
        room.get().getMeasurements().add(measurementMetadata);
        measurementsRepository.addMeasurement(measurementReadings);
        return measurementId;
    }

    @Override
    public long countProjects() {
        return this.projects.size();
    }

    @Override
    public void resetRepository() {
        this.projects.clear();
    }
}
