package repositories.measurements;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class MeasurementsRepositoryJPA implements MeasurementsRepository {
    private final static String DATABASE_NAME = "flux";
    private final static String COLLECTION_NAME = "measurements";

    private final MongoClient mongoClient;

    @Inject
    public MeasurementsRepositoryJPA(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    private MongoCollection<MeasurementReadings> getCollection() {
        final MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        return mongoDatabase.getCollection(COLLECTION_NAME, MeasurementReadings.class);
    }

    @Override
    public Iterator<MeasurementReadings> getMeasurementReadings() {
        return getCollection().find().iterator();
    }

    @Override
    public MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId) {
        final MongoCollection<MeasurementReadings> collection = getCollection();
        return collection.find(eq("_id", measurementId)).first();
    }

    @Override
    public void addMeasurement(final MeasurementMetadata metadata, final MeasurementReadings readings) {
        final MongoCollection<MeasurementReadings> collection = getCollection();
        collection.insertOne(readings);
    }
}
