package controllers;

import models.Project;
import org.bson.types.ObjectId;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.generator.DataGenerator;
import repositories.projects.ProjectsRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ProjectsController extends Controller {
    private final HttpExecutionContext httpExecutionContext;
    private final ProjectsRepository projectsRepository;

    @Inject
    public ProjectsController(final HttpExecutionContext httpExecutionContext, final ProjectsRepository projectsRepository) {
        this.httpExecutionContext = httpExecutionContext;
        this.projectsRepository = projectsRepository;
    }

    public CompletionStage<Result> getProjects(final int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final List<Project> projects = new ArrayList<>(limit);
                final Iterator<Project> iterator = this.projectsRepository.getProjects();
                for(int i = 0; (i < limit) && iterator.hasNext(); i++) {
                    projects.add(iterator.next());
                }
                return ok(Json.toJson(projects));
            } catch(final Exception ex) {
                Logger.error("Failed getting " + limit + " projects", ex);
                return internalServerError();
            }
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getProjectById(final String projectId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final ObjectId objectId = new ObjectId(projectId);
                final Project project = this.projectsRepository.getProjectById(objectId);

                if(project == null) {
                    throw new NullPointerException("Project was not found or was null");
                }

                return ok(Json.toJson(project));
            } catch(final Exception ex) {
                Logger.error("Error while getting project with the id " + projectId, ex);
                return notFound("Project not found");
            }
        }, httpExecutionContext.current());
    }
}
