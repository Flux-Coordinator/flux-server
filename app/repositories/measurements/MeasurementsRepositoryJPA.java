package repositories.measurements;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import javax.inject.Inject;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MeasurementsRepositoryJPA implements MeasurementsRepository {

    private final MongoClient mongoClient;

    @Inject
    public MeasurementsRepositoryJPA(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId) {
        final MongoDatabase database = mongoClient.getDatabase("flux");
        final MongoCollection<MeasurementReadings> collection
                = database.getCollection("measurements", MeasurementReadings.class);
        return collection.find(eq("_id", measurementId)).first();
    }

    @Override
    public void addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings) {
        final MongoDatabase database = mongoClient.getDatabase("flux");
        final MongoCollection<MeasurementReadings> collection
                = database.getCollection("measurements", MeasurementReadings.class);

        collection.insertOne(readings);
    }
}
