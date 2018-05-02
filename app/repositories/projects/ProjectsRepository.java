package repositories.projects;

import models.MeasurementMetadata;
import models.Project;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ProjectsRepository {
    CompletionStage<List<Project>> getProjects(final int limit);

    CompletionStage<Long> addProject(final Project project);

    void addProjects(final List<Project> projects);

    CompletionStage<Project> getProjectById(final long projectId);

    ObjectId addMeasurement(final ObjectId projectId, final String roomName, final MeasurementMetadata measurementMetadata);

    CompletionStage<Long> countProjects();

    void resetRepository();
}