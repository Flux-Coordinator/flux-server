package controllers;

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
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;
import static play.inject.Bindings.bind;

public class MeasurementsControllerTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .overrides(bind(MeasurementsRepository.class).to(MeasurementsRepositoryMock.class))
                .build();
    }

    @Test
    public void testAddMeasurement() {
        final MeasurementReadings measurementReadings = DataGenerator.generateMeasurement();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(measurementReadings))
                .uri("/measurements");

        final Result result = route(app, request);
        assertEquals(OK, result.status());
    }
}
