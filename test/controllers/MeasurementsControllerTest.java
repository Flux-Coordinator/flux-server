package controllers;

import helpers.Helpers;
import models.MeasurementReadings;
import org.bson.types.ObjectId;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.measurements.MeasurementsRepositoryMock;
import repositories.projects.ProjectsRepository;
import repositories.projects.ProjectsRepositoryMock;

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class MeasurementsControllerTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(MeasurementsRepository.class).to(MeasurementsRepositoryMock.class))
                .overrides(bind(ProjectsRepository.class).to(ProjectsRepositoryMock.class))
                .build();
    }

    @Test
    public void createMeasurements_BestCase_OK() {
        final MeasurementReadings measurementReadings = DataGenerator.generateMeasurement();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(measurementReadings))
                .uri("/measurements");
        final Result result = route(app, request);
        assertEquals(CREATED, result.status());
    }

    @Test
    public void getMeasurements_GetDefault_OK() {
        final int desiredLimitOfMeasurements = 5;
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements");
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final MeasurementReadings[] measurementReadings = Helpers.convertFromJSON(result, MeasurementReadings[].class);
        assertEquals(desiredLimitOfMeasurements, measurementReadings.length);
    }

    @Test
    public void getMeasurements_Get2_OK() {
        final int desiredLimitOfMeasurements = 2;
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements?limit=" + desiredLimitOfMeasurements);
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final MeasurementReadings[] measurementReadings = Helpers.convertFromJSON(result, MeasurementReadings[].class);
        assertEquals(desiredLimitOfMeasurements, measurementReadings.length);
    }

    @Test
    public void getMeasurementById_GetExisting_OK() {
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final MeasurementReadings expectedReading = repository.getMeasurementReadings().next();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/" + expectedReading.getMeasurementId());
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final MeasurementReadings actualReading = Helpers.convertFromJSON(result, MeasurementReadings.class);
        assertEquals(expectedReading, actualReading);
    }

    @Test
    public void getMeasurementById_GetNotExisting_NoContent() {
        final ObjectId objectId = new ObjectId();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/" + objectId);
        final Result result = route(app, request);
        assertEquals(NO_CONTENT, result.status());
    }

    public void getMeasurementById_InvalidObjectId_BadRequest() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/1234");
        final Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }
}