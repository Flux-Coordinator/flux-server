package controllers;

import models.Project;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.measurements.MeasurementsRepositoryMock;
import repositories.projects.ProjectsRepository;
import repositories.projects.ProjectsRepositoryMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

public class ProjectsControllerTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(MeasurementsRepository.class).to(MeasurementsRepositoryMock.class))
                .overrides(bind(ProjectsRepository.class).to(ProjectsRepositoryMock.class))
                .build();
    }

    @Test
    public void createProjects_BestCase_OK() {
        final Project project = DataGenerator.generateProject();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(project))
                .uri("/projects");
        final Result result = route(app, request);
        assertEquals(CREATED, result.status());
        assertFalse(Helpers.contentAsString(result).isEmpty());
    }
}
