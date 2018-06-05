import com.google.inject.AbstractModule;
import repositories.authentication.AuthenticationRepository;
import repositories.authentication.LocalAuthenticationRepository;
import repositories.importexport.ImportExportRepository;
import repositories.importexport.ImportExportRepositoryJPA;
import repositories.measurements.MeasurementsRepository;
import repositories.measurements.MeasurementsRepositoryJPA;
import repositories.projects.ProjectsRepository;
import repositories.projects.ProjectsRepositoryJPA;
import repositories.rooms.RoomsRepository;
import repositories.rooms.RoomsRepositoryJPA;
import startup.StartupManager;
import startup.StartupManagerImpl;
import utils.json.JacksonCustomObjectMapper;
import utils.jwt.JwtHelper;
import utils.jwt.JwtHelperImpl;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    protected void configure() {
        super.configure();

        bind(RoomsRepository.class).to(RoomsRepositoryJPA.class);
        bind(MeasurementsRepository.class).to(MeasurementsRepositoryJPA.class);
        bind(ProjectsRepository.class).to(ProjectsRepositoryJPA.class);
        bind(ImportExportRepository.class).to(ImportExportRepositoryJPA.class);
        bind(AuthenticationRepository.class).to(LocalAuthenticationRepository.class);

        bind(JwtHelper.class).to(JwtHelperImpl.class);
        bind(JacksonCustomObjectMapper.class).asEagerSingleton();
        bind(StartupManager.class).to(StartupManagerImpl.class).asEagerSingleton();
    }
}
