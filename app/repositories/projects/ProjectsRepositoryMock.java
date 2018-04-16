package repositories.projects;

import models.Project;
import org.bson.types.ObjectId;
import repositories.generator.DataGenerator;

import java.util.Iterator;
import java.util.List;

public class ProjectsRepositoryMock implements ProjectsRepository {
    private final static int AMOUNT_OF_PROJECTS = 10;

    private final List<Project> projects;

    public ProjectsRepositoryMock() {
        this.projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS);
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
    public long countProjects() {
        return this.projects.size();
    }

    @Override
    public void resetRepository() {
        this.projects.clear();
    }
}
