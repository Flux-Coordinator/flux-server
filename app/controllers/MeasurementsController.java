package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.MeasurementReadings;
import org.bson.types.ObjectId;
import play.libs.Json;
import play.libs.typedmap.TypedKey;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeasurementsController extends Controller {
    private final MeasurementsRepository measurementsRepository;

    @Inject
    public MeasurementsController(final MeasurementsRepository measurementsRepository) {
        this.measurementsRepository = measurementsRepository;
    }

    public Result getMeasurementById(final String measurementId) {
        final ObjectId objectId = new ObjectId(measurementId);
        final MeasurementReadings readings = measurementsRepository.getMeasurementReadingsById(objectId);

        if(readings == null) {
            return notFound("Measurement not found");
        }

        return ok(Json.toJson(readings));
    }

    public Result getMeasurements(final int limit) {
        final List<MeasurementReadings> readings = new ArrayList<>(limit);
        final Iterator<MeasurementReadings> readingsIterator = measurementsRepository.getMeasurementReadings();

        while(readingsIterator.hasNext() && readings.size() < limit) {
            readings.add(readingsIterator.next());
        }

        return ok(Json.toJson(readings));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result createMeasurement() {
        final JsonNode json = request().body().asJson();
        final MeasurementReadings readings = Json.fromJson(json, MeasurementReadings.class);
        measurementsRepository.addMeasurement(null, readings);

        return ok();
    }
}