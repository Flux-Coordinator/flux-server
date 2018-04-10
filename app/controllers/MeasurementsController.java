package controllers;

import com.mongodb.MongoClient;
import models.AnchorPosition;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.measurements.MeasurementsRepository;
import repositories.measurements.MeasurementsRepositoryJPA;

import javax.inject.Inject;
import java.util.Random;

public class MeasurementsController extends Controller {
    private static Random random = new Random();

    private final MeasurementsRepository measurementsRepository;

    @Inject
    public MeasurementsController(final MongoClient mongoClient) {
        measurementsRepository = new MeasurementsRepositoryJPA(mongoClient);
    }

    public Result getMeasurementById(final String measurementId) {
        final ObjectId objectId = new ObjectId(measurementId);
        final MeasurementReadings readings = measurementsRepository.getMeasurementReadingsById(objectId);

        if(readings == null) {
            return notFound("Measurement not found");
        }

        return ok(Json.toJson(readings));
    }

    public Result createMeasurement() {
        final MeasurementReadings readings = new MeasurementReadings();
        for(int i = 0; i < 5; i++) {
            readings.getReadings().add(createReading());
        }

        for(int i = 0; i < 3; i++) {
            readings.getAnchorPositions().add(createAnchorPosition());
        }

        measurementsRepository.addMeasurement(null, readings);

        return ok();
    }

    private Reading createReading() {
        final Reading reading = new Reading();
        reading.setLuxValue(random.nextDouble());
        reading.setXPosition(random.nextDouble());
        reading.setYPosition(random.nextDouble());
        reading.setZPosition(random.nextDouble());

        return reading;
    }

    private AnchorPosition createAnchorPosition() {
        final AnchorPosition position = new AnchorPosition();
        position.setName("Anker" + random.nextInt());
        position.setXPosition(random.nextDouble());
        position.setYPosition(random.nextDouble());
        position.setZPosition(random.nextDouble());

        return position;
    }
}