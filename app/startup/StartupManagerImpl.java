package startup;

import repositories.projects.ProjectsRepository;
import repositories.utils.DemoDataHelper;

import javax.inject.Inject;

public class StartupManagerImpl implements StartupManager {
    private final ProjectsRepository projectsRepository;

    @Inject
    public StartupManagerImpl(final ProjectsRepository projectsRepository) {
        this.projectsRepository = projectsRepository;
        this.init();
    }

    @Override
    public void init() {
        this.projectsRepository.resetRepository();
        this.projectsRepository.addProjects(DemoDataHelper.generateDemoData());
    }
}
