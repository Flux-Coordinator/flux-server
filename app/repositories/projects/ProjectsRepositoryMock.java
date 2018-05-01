package repositories.projects;

import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Project;
import models.Room;
import org.bson.types.ObjectId;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ProjectsRepositoryMock implements ProjectsRepository {
    private final List<Project> projects;
    private final MeasurementsRepository measurementsRepository;

    @Inject
    public ProjectsRepositoryMock(final MeasurementsRepository measurementsRepository) {
        this.projects = new ArrayList<>();
        this.measurementsRepository = measurementsRepository;
    }

    @Override
    public Iterator<Project> getProjects() {
        return this.projects.iterator();
    }

    @Override
    public ObjectId addProject(final Project project) {
        if(project.getProjectId() == null) {
            project.setProjectId(new ObjectId());
        }
        this.projects.add(project);
        return project.getProjectId();
    }

    @Override
    public void addProjects(List<Project> projects) {
        this.projects.addAll(projects);
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

        room.orElseThrow(() -> new NullPointerException("Room to add the measurement into was not found.")).getMeasurements().add(measurementMetadata);
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
        this.measurementsRepository.resetRepository();
    }
}
