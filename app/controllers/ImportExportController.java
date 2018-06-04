package controllers;

import authentication.JWTAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Measurement;
import models.Project;
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
                .thenApplyAsync(projects -> ok(Json.toJson(projects)), httpExecutionContext.current());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> importMeasurements() {
        final JsonNode jsonNode = request().body().asJson();
        final List<Project> importedProjects = Arrays.asList(Json.fromJson(jsonNode, Project[].class));

        return this.importExportRepository
                .importData(importedProjects)
                .thenApplyAsync(aVoid -> ok(""), httpExecutionContext.current());
    }
}
