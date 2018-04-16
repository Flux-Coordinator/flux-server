package repositories.projects;

import models.Project;
import org.bson.types.ObjectId;

import java.util.Iterator;

public interface ProjectsRepository {
    Iterator<Project> getProjects();

    ObjectId addProject(final Project project);

    Project getProjectById(final ObjectId projectId);

    void resetRepository();
}