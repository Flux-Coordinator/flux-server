package repositories.projects;

import models.Measurement;
import models.Project;
import models.Room;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ProjectsRepository {
    CompletableFuture<Set<Project>> getProjects(final int limit);

    CompletableFuture<Long> addProject(final Project project);

    CompletableFuture<Void> addProjects(final List<Project> projects);

    CompletableFuture<Project> getProjectById(final long projectId);

    CompletableFuture<Set<Room>> getProjectRooms(final long projectId);

    CompletableFuture<Set<Project>> getRelatedProjects(final List<Measurement> measurements);

    CompletableFuture<Long> countProjects();

    CompletableFuture<Void> removeProject(final long projectId);

    void resetRepository();
}