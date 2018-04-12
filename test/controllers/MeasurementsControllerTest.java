package controllers;

import helpers.Helpers;
import models.MeasurementReadings;
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

import static org.junit.Assert.assertEquals;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class MeasurementsControllerTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(MeasurementsRepository.class).to(MeasurementsRepositoryMock.class))
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
        assertEquals(OK, result.status());
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
    public void getMeasurementById_GetNotExisting_Error404() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/12345623");
        final Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }
}