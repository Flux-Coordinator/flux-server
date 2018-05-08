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
import models.MeasurementState;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class MeasurementsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final MeasurementsRepository measurementsRepository;
    private final ActorSystem actorSystem;
    private final Materializer materializer;

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

    private static Result apply(final Set<Measurement> measurements) {
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
    public CompletionStage<Result> addMeasurement(final long roomId) {
        final JsonNode json = request().body().asJson();
        final Measurement measurement = Json.fromJson(json, Measurement.class);
        return measurementsRepository.addMeasurement(roomId, measurement).thenApplyAsync(measurementId -> {
            final String absoluteUrl = routes.MeasurementsController.getMeasurementById(measurementId).absoluteURL(request());
            return created(absoluteUrl);
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> startMeasurement(final long measurementId) {
        return measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING)
                .thenComposeAsync(measurements -> {
                    if(!measurements.isEmpty()) {
                        final Measurement activeMeasurement = measurements.iterator().next();
                        if(activeMeasurement.getMeasurementId() != measurementId) {
                            return CompletableFuture.completedFuture(badRequest("There is arleady an active measurement."));
                        } else {
                            return CompletableFuture.completedFuture(ok("Measurement is already active"));
                        }
                    }

                    return measurementsRepository
                            .changeMeasurementState(measurementId, MeasurementState.RUNNING)
                            .thenApply(aVoid -> ok(""));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> stopMeasurement() {
        return measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING)
                .thenComposeAsync(measurements -> {
                    if(!measurements.isEmpty()) {
                        final CompletableFuture[] array = new CompletableFuture[measurements.size()];
                        final Iterator<Measurement> measurementIterator = measurements.iterator();
                        int i = 0;
                        while(measurementIterator.hasNext()) {
                            array[i] = this.measurementsRepository.changeMeasurementState(measurementIterator.next().getMeasurementId(), MeasurementState.DONE);
                            i++;
                        }

                        return CompletableFuture.allOf(array).thenApply(aVoid -> ok(""));
                    }
                    return CompletableFuture.completedFuture(ok(""));
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error stopping the measurement", throwable);
                    return badRequest("Error stopping the measurement");
                });
    }

    public CompletionStage<Result> getActiveMeasurement() {
        return this.measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING)
                .thenApplyAsync(measurements -> {
                    if(measurements.size() > 1) {
                        return internalServerError("There are multiple currently actively measurements. " +
                                "This is unsupported.");
                    }

                    if(measurements.isEmpty()) {
                        return noContent();
                    }

                    return ok(Json.toJson(measurements.iterator().next()));
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error getting the active measurement.", throwable);
                    return badRequest("Error getting the active measurement.");
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addReadings() {
        final CompletableFuture<Reading[]> futureReadings = CompletableFuture.supplyAsync(() -> {
            final JsonNode json = request().body().asJson();
            return Json.fromJson(json, Reading[].class);
        }, httpExecutionContext.current());

        final CompletableFuture<Set<Measurement>> futureActiveMeasurements =
                this.measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING);

        return CompletableFuture.allOf(futureActiveMeasurements, futureReadings).thenApplyAsync(aVoid -> {
            final Set<Measurement> measurements = futureActiveMeasurements.join();

            if(measurements.size() > 1) {
                return internalServerError("There are multiple currently active measurements. This is unsupported");
            }

            if(measurements.isEmpty()) {
                return notFound("No currently active measurement");
            }

            final Measurement activeMeasurement = measurements.iterator().next();
            final Reading[] readings = futureReadings.join();

            return measurementsRepository.addReadings(activeMeasurement.getMeasurementId(), Arrays.asList(readings))
                    .thenApplyAsync(aVoid1 -> {
                        readingsActor.tell(new ReadingsMessage(readings), ActorRef.noSender());
                        return ok("");
                    }).exceptionally(throwable -> {
                        Logger.error("Error while adding new readings to the active measurement");
                        return badRequest("Error while adding new readings to the active measurement");
                    }).join();
        }, httpExecutionContext.current());
    }

    public WebSocket streamMeasurements() {
        final Flow measurementStreamFlow = ActorFlow.actorRef(actorRef -> WebSocketActor.props(actorRef, readingsActor), actorSystem, materializer);
        //noinspection unchecked
        return WebSocket.Json.accept(request -> measurementStreamFlow);
    }
}