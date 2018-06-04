package repositories.projects;

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

    CompletableFuture<Set<Project>> getProjectsByIds(final List<Long> projectIds);

    CompletableFuture<Set<Project>> getProjectsByName(final List<String> projectNames);

    CompletableFuture<Set<Room>> getProjectRooms(final long projectId);

    CompletableFuture<Long> countProjects();

    CompletableFuture<Void> removeProject(final long projectId);

    CompletableFuture<Void> resetRepository();
}