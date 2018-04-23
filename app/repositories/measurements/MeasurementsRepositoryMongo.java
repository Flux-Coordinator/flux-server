package repositories.measurements;

import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.MeasurementReadings;
import models.Reading;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Singleton
public class MeasurementsRepositoryMongo implements MeasurementsRepository {
    private final static String DATABASE_NAME = "flux";
    private final static String COLLECTION_NAME = "measurements";

    private final MongoClient mongoClient;

    @Inject
    public MeasurementsRepositoryMongo(final MongoClient mongoClient) {
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
    public ObjectId addMeasurement(final MeasurementReadings readings) {
        if(readings.getMeasurementId() != null) {
            readings.setMeasurementId(new ObjectId());
        }
        getCollection().insertOne(readings);
        return readings.getMeasurementId();
    }

    @Override
    public void addReadings(final ObjectId measurementId, final List<Reading> readings) {
        getCollection().updateOne(eq("_id", measurementId), new Document("$set", new Document("readings", readings)));
    }

    @Override
    public void resetRepository() {
        getCollection().drop();
        this.mongoClient.getDatabase(DATABASE_NAME).createCollection(COLLECTION_NAME);
    }
}
