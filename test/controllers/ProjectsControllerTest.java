package controllers;

import helpers.Helpers;
import models.Project;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import repositories.generator.DataGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static play.test.Helpers.*;

public class ProjectsControllerTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .build();
    }

    @Before
    public void setUp() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/reset");
        final Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void createProjects_BestCase_OK() {
        final Project project = DataGenerator.generateProject(5);
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(project))
                .uri("/projects");
        final Result result = route(app, request);
        assertEquals(CREATED, result.status());
        assertFalse(contentAsString(result).isEmpty());
    }

    @Test
    public void getProjects_Default_OK() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/projects");

        final Result result = route(app, request);
        final Project[] projects = Helpers.convertFromJSON(result, Project[].class);
        assertEquals(5, projects.length);
    }
}
