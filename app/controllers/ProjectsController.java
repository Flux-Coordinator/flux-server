package controllers;

import authentication.JWTAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import models.Project;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repositories.exceptions.AlreadyExistsException;
import repositories.projects.ProjectsRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Security.Authenticated(value = JWTAuthenticator.class)
@Singleton
public class ProjectsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final ProjectsRepository projectsRepository;

    @Inject
    public ProjectsController(final HttpExecutionContext httpExecutionContext,
                              final ProjectsRepository projectsRepository) {
        this.httpExecutionContext = httpExecutionContext;
        this.projectsRepository = projectsRepository;
    }

    public CompletionStage<Result> getProjects(final int limit) {
        return this.projectsRepository.getProjects(limit)
                .thenApplyAsync(projects -> ok(Json.toJson(projects)), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Failed getting " + limit + " projects", throwable);
                    return internalServerError("Fehler beim Holen der Projekte aus dem Server.");
                });
    }

    public CompletionStage<Result> getProjectById(final long projectId) {
        return this.projectsRepository.getProjectById(projectId).thenApplyAsync(project -> {
            if (project == null) {
                return noContent();
            }
            return ok(Json.toJson(project));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getProjectRooms(final long projectId) {
        return this.projectsRepository.getProjectRooms(projectId)
                .thenApplyAsync(rooms -> ok(Json.toJson(rooms)), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error while retrieving the rooms for project with the id: " + projectId, throwable);
                    return badRequest("Fehler beim Holen der Räume für das Projekt (ID: " + projectId + ").");
                });
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletionStage<Result> addProject() {
        final JsonNode jsonNode = request().body().asJson();
        final Project project = Json.fromJson(jsonNode, Project.class);
        return this.projectsRepository.addProject(project).thenApplyAsync(createdId -> {
            final String absoluteUrl = routes.ProjectsController.getProjectById(createdId).absoluteURL(request());
            return created(absoluteUrl);
        }, httpExecutionContext.current())
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof AlreadyExistsException) {
                        return badRequest(throwable.getCause().getMessage());
                    } else {
                        Logger.error("Error while creating a new project.", throwable);
                        return badRequest("Fehler beim Erstellen des neuen Projekts");
                    }
                });
    }

    public CompletionStage<Result> removeProject(final int projectId) {
        return this.projectsRepository.removeProject(projectId)
                .thenApplyAsync(aVoid -> ok(""), httpExecutionContext.current())
                .exceptionally(throwable -> {
                    Logger.error("Error while removing the project with the id " + projectId, throwable);
                    return badRequest("Fehler beim Entfernen des Projektes (Projekt ID: " + projectId + ").");
                });
    }
}
