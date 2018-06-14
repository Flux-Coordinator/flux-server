package startup;

import play.Environment;
import play.Logger;
import repositories.projects.ProjectsRepository;
import repositories.utils.DemoDataHelper;

import javax.inject.Inject;

public class StartupManagerImpl implements StartupManager {
    private final ProjectsRepository projectsRepository;
    private final Environment environment;

    @Inject
    public StartupManagerImpl(final ProjectsRepository projectsRepository, final Environment environment) {
        this.projectsRepository = projectsRepository;
        this.environment = environment;
        this.init();
    }

    @Override
    public void init() {
        if(!environment.isProd()) {
            this.projectsRepository.resetRepository()
                    .thenAcceptAsync(aVoid -> {
                        this.projectsRepository.addProjects(DemoDataHelper.generateDemoData())
                                .exceptionally(throwable -> {
                                    Logger.error("Could not add demo data", throwable);
                                    return null;
                                });
                    })
                    .exceptionally(throwable -> {
                        Logger.error("Could not reset the repository", throwable);
                        return null;
                    })
                    .join();
        }
    }
}
