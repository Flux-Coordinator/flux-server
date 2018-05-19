package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.Project;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.projects.ProjectsRepository;
import repositories.utils.DemoDataHelper;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static repositories.utils.DemoDataHelper.*;

@Singleton
public class AdminController extends Controller {
    private final ProjectsRepository projectsRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public AdminController(final ProjectsRepository projectsRepository,
                           final HttpExecutionContext httpExecutionContext) {
        this.projectsRepository = projectsRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> resetData() {
        final List<Project> projects = DemoDataHelper.generateDemoData();

        this.projectsRepository.resetRepository();

        return this.projectsRepository.addProjects(projects).thenApplyAsync(aVoid ->
                        ok("Created " + AMOUNT_OF_PROJECTS + " projects with " + AMOUNT_OF_ROOMS_PER_PROJECT + " rooms each," +
                                " " + AMOUNT_OF_MEASUREMENTS_PER_ROOM + " measurements per room" +
                                " and " + AMOUNT_OF_READINGS_PER_MEASUREMENT + " readings per measurement.")
                , httpExecutionContext.current());
    }
}
