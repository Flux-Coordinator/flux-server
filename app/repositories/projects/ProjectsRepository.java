package repositories.projects;

import models.Measurement;
import models.Project;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ProjectsRepository {
    CompletionStage<List<Project>> getProjects(final int limit);

    CompletionStage<Long> addProject(final Project project);

    CompletionStage<Void> addProjects(final List<Project> projects);

    CompletionStage<Project> getProjectById(final long projectId);

    ObjectId addMeasurement(final long projectId, final String roomName, final Measurement measurement);

    CompletionStage<Long> countProjects();

    void resetRepository();
}