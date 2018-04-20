package repositories.projects;

import models.MeasurementMetadata;
import models.Project;
import org.bson.types.ObjectId;

import java.util.Iterator;

public interface ProjectsRepository {
    Iterator<Project> getProjects();

    ObjectId addProject(final Project project);

    Project getProjectById(final ObjectId projectId);

    MeasurementMetadata createMeasurement(final ObjectId projectId, final String roomName, final MeasurementMetadata measurementMetadata);

    long countProjects();

    void resetRepository();
}