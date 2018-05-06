package controllers;

import actors.ReadingsActor;
import actors.WebSocketActor;
import actors.messages.ReadingsMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import models.Measurement;
import models.Reading;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class MeasurementsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final MeasurementsRepository measurementsRepository;
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    private Long activeMeasurementId;
    private final ActorRef readingsActor;

    @Inject
    public MeasurementsController(final HttpExecutionContext httpExecutionContext, final MeasurementsRepository measurementsRepository,
                                  final ActorSystem actorSystem, final Materializer materializer) {
        this.httpExecutionContext = httpExecutionContext;
        this.measurementsRepository = measurementsRepository;
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.readingsActor = actorSystem.actorOf(ReadingsActor.getProps());
    }

    private static Result apply(final List<Measurement> measurements) {
        return ok(Json.toJson(measurements));
    }

    public CompletionStage<Result> getMeasurementById(final long measurementId) {
        return measurementsRepository.getMeasurementbyId(measurementId)
                .thenApplyAsync(measurement -> {
                    if (measurement == null) {
                        return noContent();
                    }
                    return ok(Json.toJson(measurement));
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error while retrieving measurement with the id: " + measurementId, throwable);
                    return badRequest("Failed retrieving the measurement");
                });
    }

    public CompletionStage<Result> getMeasurements(final int limit) {
        return measurementsRepository.getMeasurements(limit)
                .thenApplyAsync(MeasurementsController::apply, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error getting measurements", throwable);
                    return internalServerError();
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> createMeasurement() {
        final JsonNode json = request().body().asJson();
        final Measurement measurement = Json.fromJson(json, Measurement.class);
        return measurementsRepository.createMeasurement(measurement).thenApplyAsync(measurementId -> {
            final String absoluteUrl = routes.MeasurementsController.getMeasurementById(measurementId).absoluteURL(request());
            return created(absoluteUrl);
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> startMeasurement(final long measurementId) {
        return measurementsRepository.getMeasurementbyId(measurementId).thenApplyAsync(measurement -> {
            if(measurement == null) {
                return notFound("Measurement was not found");
            }
            this.activeMeasurementId = measurement.getMeasurementId();
            return ok();
        }, httpExecutionContext.current());
    }

    public Result stopMeasurement() {
        this.activeMeasurementId = null;
        return ok();
    }

    public CompletionStage<Result> getActiveMeasurement() {
        if (this.activeMeasurementId == null) {
            return CompletableFuture.completedFuture(noContent());
        }

        return this.measurementsRepository.getMeasurementbyId(this.activeMeasurementId)
                .thenApplyAsync(measurement -> {
                    if (measurement != null) {
                        return ok(Json.toJson(measurement));
                    }
                    return noContent();
                }, httpExecutionContext.current());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addReading() {
        if (this.activeMeasurementId == null) {
            return CompletableFuture.completedFuture(noContent());
        }

        final JsonNode json = request().body().asJson();
        final Reading[] readings = Json.fromJson(json, Reading[].class);
        return measurementsRepository.addReadings(this.activeMeasurementId, Arrays.asList(readings))
                .thenApplyAsync(aVoid -> {
                    readingsActor.tell(new ReadingsMessage(readings), ActorRef.noSender());
                    return ok("");
                }, httpExecutionContext.current()).exceptionally(throwable -> {
                    Logger.error("Error while adding new readings to measurement " + this.activeMeasurementId, throwable);
                    return badRequest("Error while adding new readings.");
                });
    }

    public WebSocket streamMeasurements() {
        final Flow measurementStreamFlow = ActorFlow.actorRef(actorRef -> WebSocketActor.props(actorRef, readingsActor), actorSystem, materializer);
        //noinspection unchecked
        return WebSocket.Json.accept(request -> measurementStreamFlow);
    }
}