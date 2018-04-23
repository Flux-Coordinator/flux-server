package repositories.projects;

import models.MeasurementMetadata;
import models.Project;
import models.Reading;
import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.List;

public interface ProjectsRepository {
    Iterator<Project> getProjects();

    ObjectId addProject(final Project project);

    Project getProjectById(final ObjectId projectId);

    ObjectId addMeasurement(final ObjectId projectId, final String roomName, final MeasurementMetadata measurementMetadata);

    long countProjects();

    void resetRepository();
}