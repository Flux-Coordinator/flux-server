package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Measurement;
import models.Project;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.projects.ProjectsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ImportExportController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final ProjectsRepository projectsRepository;

    @Inject
    public ImportExportController(final HttpExecutionContext httpExecutionContext,
                                  final ProjectsRepository projectsRepository) {
        this.httpExecutionContext = httpExecutionContext;
        this.projectsRepository = projectsRepository;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> exportMeasurements() {
        final JsonNode jsonNode = request().body().asJson();
        final Measurement[] measurementsToExport = Json.fromJson(jsonNode, Measurement[].class);
        return this.projectsRepository
                .getRelatedProjects(Arrays.asList(measurementsToExport))
                .thenApplyAsync(projects -> ok(Json.toJson(projects)), httpExecutionContext.current());
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> importMeasurements() {
        final JsonNode jsonNode = request().body().asJson();
        final List<Project> importedProjects = Arrays.asList(Json.fromJson(jsonNode, Project[].class));

        importedProjects.stream().parallel().forEach(project -> {
            project.setProjectId(0);
            project.getRooms().forEach(room -> {
                room.setRoomId(0);
                room.getMeasurements().forEach(measurement -> {
                    measurement.setMeasurementId(0);
                    measurement.getAnchorPositions().forEach(anchorPosition -> {
                        anchorPosition.setAnchorPositionId(0);
                        anchorPosition.getAnchor().setAnchorId(0);
                    });
                    measurement.getReadings().forEach(reading -> reading.setReadingId(0));
                });
            });
        });

        return this.projectsRepository
                .addProjects(importedProjects)
                .thenApplyAsync(aVoid -> ok(""), httpExecutionContext.current());
    }
}
