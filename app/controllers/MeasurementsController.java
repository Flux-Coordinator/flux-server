package controllers;

import actors.measurements.MeasurementActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import models.MeasurementReadings;
import models.Reading;
import org.bson.types.ObjectId;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MeasurementsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final MeasurementsRepository measurementsRepository;
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    private MeasurementReadings activeMeasurement;
    private Flow measurementStreamFlow;

    @Inject
    public MeasurementsController(final HttpExecutionContext httpExecutionContext, final MeasurementsRepository measurementsRepository,
                                  final ActorSystem actorSystem, final Materializer materializer) {
        this.httpExecutionContext = httpExecutionContext;
        this.measurementsRepository = measurementsRepository;
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public CompletionStage<Result> getMeasurementById(final String measurementId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ObjectId objectId = new ObjectId(measurementId);
                final MeasurementReadings readings = measurementsRepository.getMeasurementReadingsById(objectId);

                if (readings == null) {
                    return noContent();
                }

                return ok(Json.toJson(readings));
            }
            catch(final Exception ex) {
                Logger.error("Error when getting measurement with the id: " + measurementId, ex);
                return badRequest("Failed retrieving the measurement.");
            }
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getMeasurements(final int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final List<MeasurementReadings> readings = new ArrayList<>(limit);
                final Iterator<MeasurementReadings> readingsIterator = measurementsRepository.getMeasurementReadings();

                while (readingsIterator.hasNext() && readings.size() < limit) {
                    readings.add(readingsIterator.next());
                }

                return ok(Json.toJson(readings));
            }
            catch(final Exception ex) {
                Logger.error("Error getting measurements", ex);
                return internalServerError();
            }
        }, httpExecutionContext.current());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> createMeasurement() {
        return CompletableFuture.supplyAsync(() -> {
            final JsonNode json = request().body().asJson();
            final MeasurementReadings readings = Json.fromJson(json, MeasurementReadings.class);
            final ObjectId createdId = measurementsRepository.addMeasurement(readings);
            final String absoluteUrl = routes.MeasurementsController.getMeasurementById(createdId.toString()).absoluteURL(request());
            return created(absoluteUrl);
        }, httpExecutionContext.current());
    }

    public CompletableFuture<Result> startMeasurement(final String measurementId) {
        return CompletableFuture.supplyAsync(() -> {
            final ObjectId objectId = new ObjectId(measurementId);
            this.activeMeasurement = measurementsRepository.getMeasurementReadingsById(objectId);
            return ok();
        }, httpExecutionContext.current());
    }

    public Result stopMeasurement() {
        this.activeMeasurement = null;
        return ok();
    }

    public Result getActiveMeasurement() {
        if(this.activeMeasurement != null){
            return Results.ok(Json.toJson(this.activeMeasurement));
        }
        return Results.noContent();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> addReading() {
        return CompletableFuture.supplyAsync(() -> {
            if(this.activeMeasurement == null) {
                return Results.badRequest("No active measurement");
            }
            try {
                final JsonNode json = request().body().asJson();
                final Reading[] array = Json.fromJson(json, Reading[].class);
                final List<Reading> readings = new ArrayList<>(Arrays.asList(array));
                measurementsRepository.addReadings(activeMeasurement.getMeasurementId(), readings);
                if(measurementStreamFlow != null && MeasurementActor.out != null) {
                    MeasurementActor.out.tell(Json.toJson(readings), ActorRef.noSender());
                }
                return ok();
            }
            catch (final Exception ex) {
                Logger.error("Error while adding new readings to measurement" + this.activeMeasurement.getMeasurementId(), ex);
                return badRequest("Error while adding new readings.");
            }
        }, httpExecutionContext.current());
    }

    public WebSocket streamMeasurements() {
        measurementStreamFlow = ActorFlow.actorRef(MeasurementActor::props, actorSystem, materializer);
        return WebSocket.Json.accept(request -> measurementStreamFlow);
    }
}