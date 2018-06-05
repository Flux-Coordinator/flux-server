package controllers;

import actors.ReadingsActor;
import actors.WebSocketActor;
import actors.messages.ReadingsMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import authentication.JWTAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.*;
import repositories.exceptions.AlreadyExistsException;
import repositories.measurements.MeasurementsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Security.Authenticated(value = JWTAuthenticator.class)
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
                    return badRequest("Fehler beim Holen der Messung (ID: " + measurementId + ") aus dem Server.");
                });
    }

    public CompletionStage<Result> getMeasurements(final int limit) {
        return measurementsRepository.getMeasurements(limit)
                .thenApplyAsync(MeasurementsController::apply, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error getting measurements", throwable);
                    return internalServerError("Fehler beim Holen der Messungen aus dem Server.");
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addMeasurement(final long roomId) {
        final JsonNode json = request().body().asJson();
        final Measurement measurement = Json.fromJson(json, Measurement.class);
        return measurementsRepository
                .addMeasurement(roomId, measurement).thenApplyAsync(measurementId -> {
                    final String absoluteUrl = routes.MeasurementsController.getMeasurementById(measurementId).absoluteURL(request());
                    return created(absoluteUrl);
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    if(throwable.getCause() instanceof AlreadyExistsException) {
                        return badRequest(throwable.getCause().getMessage());
                    }
                    Logger.error("There was an error adding a measurement with the ID " + measurement.getMeasurementId() + ".", throwable);
                    return badRequest("Die Messung konnte nicht erstellt werden.");
                });
    }

    public CompletionStage<Result> startMeasurement(final long measurementId) {
        return measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING)
                .thenComposeAsync(measurements -> {
                    if(!measurements.isEmpty()) {
                        final Measurement activeMeasurement = measurements.iterator().next();
                        if(activeMeasurement.getMeasurementId() != measurementId) {
                            final Room room = activeMeasurement.getRoom();
                            final Project project = room.getProject();
                            final String errorMessage = "Die Messung \"" + activeMeasurement.getName() +
                                    "\" im Raum \"" + room.getName() + "\" im Projekt \"" + project.getName() + "\" ist bereits aktiv.";
                            return CompletableFuture.completedFuture(badRequest(errorMessage));
                        } else {
                            return CompletableFuture.completedFuture(ok("Messung ist bereits aktiv."));
                        }
                    }

                    return measurementsRepository
                            .changeMeasurementState(measurementId, MeasurementState.RUNNING)
                            .thenApply(aVoid -> ok("Messung ist jetzt aktiv."));
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
                    return badRequest("Fehler beim Stoppen der Messung");
                });
    }

    public CompletionStage<Result> getActiveMeasurement() {
        return this.measurementsRepository.getMeasurementsByState(MeasurementState.RUNNING)
                .thenApplyAsync(measurements -> {
                    if(measurements.size() > 1) {
                        return internalServerError("Es sind gerade mehrere Messungen aktiv. Dies wird nicht untersützt.");
                    }

                    if(measurements.isEmpty()) {
                        return noContent();
                    }

                    return ok(Json.toJson(measurements.iterator().next()));
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error getting the active measurement.", throwable);
                    return badRequest("Fehler beim Holen der aktiven Messung.");
                });
    }

    public CompletionStage<Result> removeMeasurement(final long measurementId) {
        return measurementsRepository.removeMeasurement(measurementId).thenApplyAsync(aVoid -> ok(""), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Failed removing measurement with the id: " + measurementId, throwable);
                    return badRequest("Die Messung konnte nicht gelöscht werden (Messung ID: " + measurementId  + ").");
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
                return internalServerError("Es sind gerade mehrere Messungen aktiv. Dies wird nicht untersützt.");
            }

            if(measurements.isEmpty()) {
                return notFound("Keine aktive Messung gefunden");
            }

            final Measurement activeMeasurement = measurements.iterator().next();
            final Reading[] readings = futureReadings.join();

            return measurementsRepository.addReadings(activeMeasurement.getMeasurementId(), Arrays.asList(readings))
                    .thenApplyAsync(aVoid1 -> {
                        readingsActor.tell(new ReadingsMessage(readings), ActorRef.noSender());
                        return ok("");
                    }).exceptionally(throwable -> {
                        Logger.error("Error while adding new readings to the active measurement");
                        return badRequest("Fehler beim Hinzufügen neuer Readings zur aktiven Messung.");
                    }).join();
        }, httpExecutionContext.current());
    }

    public WebSocket streamMeasurements() {
        final Flow measurementStreamFlow = ActorFlow.actorRef(actorRef -> WebSocketActor.props(actorRef, readingsActor), actorSystem, materializer);
        //noinspection unchecked
        return WebSocket.Json.accept(request -> measurementStreamFlow);
    }
}
