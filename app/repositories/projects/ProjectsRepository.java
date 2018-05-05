package repositories.projects;

import models.Measurement;
import models.Project;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProjectsRepository {
    CompletableFuture<List<Project>> getProjects(final int limit);

    CompletableFuture<Long> addProject(final Project project);

    CompletableFuture<Void> addProjects(final List<Project> projects);

    CompletableFuture<Project> getProjectById(final long projectId);

    ObjectId addMeasurement(final long projectId, final String roomName, final Measurement measurement);

    CompletableFuture<Long> countProjects();

    void resetRepository();
}