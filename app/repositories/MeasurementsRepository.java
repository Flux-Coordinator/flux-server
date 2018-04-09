package repositories;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import models.MeasurementReadings;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.libs.Json;

public class MeasurementsRepository {

    private final MongoClient mongoClient;

    public MeasurementsRepository() {
        mongoClient = new MongoClient("localhost", 27017);
    }

    public MeasurementReadings getMeasurementReadingsById(final ObjectId measurementId) {
        final MongoDatabase database = mongoClient.getDatabase("flux");
        final Document foundReading = database.getCollection("readings").find(Filters.eq("_id", measurementId)).first();
        return Json.fromJson(Json.parse(foundReading.toJson()), MeasurementReadings.class);
    }
}
