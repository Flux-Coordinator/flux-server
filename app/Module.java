import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.typesafe.config.Config;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import repositories.measurements.MeasurementsRepository;
import repositories.measurements.MeasurementsRepositoryMongo;
import repositories.projects.ProjectsRepository;
import repositories.projects.ProjectsRepositoryJPA;
import repositories.projects.ProjectsRepositoryMongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

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

        bind(MeasurementsRepository.class).to(MeasurementsRepositoryMongo.class);
        bind(ProjectsRepository.class).to(ProjectsRepositoryJPA.class);
    }

    @Provides @Singleton @Inject
    MongoClient provideMongoClient(final Config config) {
        final CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final MongoClientOptions.Builder mongoClientOptions = MongoClientOptions.builder()
                .codecRegistry(codecRegistry);
        final String uri = config.getString("flux.mongodb.uri");
        final MongoClientURI mongoUri = new MongoClientURI(uri, mongoClientOptions);
        return new MongoClient(mongoUri);
    }
}
