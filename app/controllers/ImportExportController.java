package controllers;

import authentication.JWTAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Measurement;
import models.Project;
import models.Room;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.importexport.ImportExportRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Security.Authenticated(value = JWTAuthenticator.class)
@Singleton
public class ImportExportController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final ImportExportRepository importExportRepository;

    @Inject
    public ImportExportController(final HttpExecutionContext httpExecutionContext,
                                  final ImportExportRepository importExportRepository) {
        this.httpExecutionContext = httpExecutionContext;
        this.importExportRepository = importExportRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> exportMeasurements() {
        final JsonNode jsonNode = request().body().asJson();
        final Measurement[] measurementsToExport = Json.fromJson(jsonNode, Measurement[].class);
        return this.importExportRepository
                .getRelatedProjects(Arrays.asList(measurementsToExport))
                .thenApplyAsync(projects -> ok(Json.toJson(projects)), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("The export of the measurements did not work as expected.", throwable);
                    return badRequest("Etwas ist beim Exportieren schief gelaufen. " +
                            "Überprüfen Sie die Datei und testen Sie den Import zuerst an einem nicht produktiven System.");
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> importMeasurements() {
        final JsonNode jsonNode = request().body().asJson();
        final List<Project> importedProjects = Arrays.asList(Json.fromJson(jsonNode, Project[].class));

        return this.importExportRepository
                .importData(importedProjects)
                .thenApplyAsync(aVoid -> {
                    final Optional<Integer> amountOfMeasurements = importedProjects
                            .stream()
                            .map(Project::getRooms)
                            .map(rooms -> rooms.stream().map(Room::getMeasurements))
                            .flatMap(measurementsSetStream -> measurementsSetStream)
                            .map(Set::size)
                            .reduce((integer, integer2) -> integer + integer2);
                    return amountOfMeasurements
                            .map(integer -> ok("Es wurden " + integer + " Messungen importiert."))
                            .orElseGet(() -> ok("Etwas ist merkwürdig. Wir konnten nicht feststellen, ob der Import geglückt ist. Bitte stellen Sie fest, ob die Messungen erfolgreich importiert wurden."));
                }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("There was an error importing measurements.", throwable);
                    return internalServerError("Es gab einen Fehler beim Importieren. Bitte prüfen Sie Ihre Daten auf Verluste.");
                });
    }
}
