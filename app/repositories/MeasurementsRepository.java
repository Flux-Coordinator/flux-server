package repositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MeasurementsRepository {

    private final MongoClient mongoClient;

    public MeasurementsRepository() {
        final CodecRegistry codecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        final MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = new MongoClient("localhost", mongoClientOptions);
    }

    public MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId) {
        final MongoDatabase database = mongoClient.getDatabase("flux");
        final MongoCollection<MeasurementReadings> collection
                = database.getCollection("measurements", MeasurementReadings.class);
        return collection.find().first();
    }

    public void addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings) {
        final MongoDatabase database = mongoClient.getDatabase("flux");
        final MongoCollection<MeasurementReadings> collection
                = database.getCollection("measurements", MeasurementReadings.class);

        collection.insertOne(readings);
    }
}
