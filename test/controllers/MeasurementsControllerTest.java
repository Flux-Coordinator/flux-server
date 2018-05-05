package controllers;

import helpers.Helpers;
import models.*;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class MeasurementsControllerTest extends WithApplication {
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
    public void createMeasurements_BestCase_OK() {
        final Measurement measurement = DataGenerator.generateMeasurement();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(measurement))
                .uri("/measurements");
        final Result result = route(app, request);
        assertEquals(CREATED, result.status());
        assertFalse(play.test.Helpers.contentAsString(result).isEmpty());
    }

    @Test
    public void getMeasurements_GetDefault_OK() {
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final int desiredLimitOfMeasurements = 5;

        for(int i = 0; i < desiredLimitOfMeasurements; i++) {
            final Measurement measurement = new Measurement();
            repository.createMeasurement(measurement);
        }

        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements");
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final Measurement[] measurements = Helpers.convertFromJSON(result, Measurement[].class);
        assertEquals(desiredLimitOfMeasurements, measurements.length);
    }

    @Test
    public void getMeasurements_Get2_OK() {
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final int desiredLimitOfMeasurements = 2;

        for(int i = 0; i < desiredLimitOfMeasurements; i++) {
            final Measurement measurement = new Measurement();
            repository.createMeasurement(measurement);
        }

        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements?limit=" + desiredLimitOfMeasurements);
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final Measurement[] measurements = Helpers.convertFromJSON(result, Measurement[].class);
        assertEquals(desiredLimitOfMeasurements, measurements.length);
    }

    @Test
    public void getMeasurementById_GetExisting_OK() throws ExecutionException, InterruptedException {
        final Measurement measurementReadings = new Measurement();
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final CompletableFuture<Long> newMeasurementId = repository.createMeasurement(measurementReadings);
        final Measurement expectedMeasurement = repository.getMeasurementbyId(newMeasurementId.get()).get();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/" + newMeasurementId);
        final Result result = route(app, request);
        assertEquals(OK, result.status());
        final Measurement actualMeasurement = Helpers.convertFromJSON(result, Measurement.class);
        assertEquals(expectedMeasurement, actualMeasurement);
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

    @Test
    public void getMeasurementById_InvalidObjectId_BadRequest() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/1234");
        final Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void startMeasurement_StartFirst_IsActive() throws ExecutionException, InterruptedException {
        final ProjectsRepository projectsRepository = app.injector().instanceOf(ProjectsRepository.class);
        final Project project = projectsRepository.getProjects(1).get().get(0);
        final Room room = project.getRooms().get(0);
        final Measurement measurement = room.getMeasurements().get(0);

        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/measurements/active/" + measurement.getMeasurementId());
        final Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void addReadings_AddOne_ReadingAdded() throws ExecutionException, InterruptedException {
        final Measurement activeMeasurement = getOrSetActiveMeasurement();

        final Reading reading = DataGenerator.generateReading();
        final List<Reading> readingList = new ArrayList<>(1);
        readingList.add(reading);
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/measurements/active/readings")
                .bodyJson(Json.toJson(readingList));
        final Result result = route(app, request);
        assertEquals(OK, result.status());

        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final Measurement measurement = repository.getMeasurementbyId(activeMeasurement.getMeasurementId()).get();
        assertTrue(measurement.getReadings().contains(reading));
    }

    @Test
    public void addReadings_AddMultiple_ReadingAdded() throws ExecutionException, InterruptedException {
        final int amountOfReadings = 10;
        final Measurement activeMeasurement = getOrSetActiveMeasurement();

        final List<Reading> readingList = DataGenerator.generateReadings(amountOfReadings);
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/measurements/active/readings")
                .bodyJson(Json.toJson(readingList));
        final Result result = route(app, request);
        assertEquals(OK, result.status());

        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final Measurement measurement = repository.getMeasurementbyId(activeMeasurement.getMeasurementId()).get();
        assertTrue(measurement.getReadings().containsAll(readingList));
    }

    private Measurement getOrSetActiveMeasurement() throws ExecutionException, InterruptedException {
        final Http.RequestBuilder getActiveRequest = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/active");
        final Result getResult = route(app, getActiveRequest);

        Measurement measurementMetadata;
        if(getResult.status() == OK) {
            measurementMetadata = Helpers.convertFromJSON(getResult, Measurement.class);
        } else {
            final ProjectsRepository projectsRepository = app.injector().instanceOf(ProjectsRepository.class);
            final Project project = projectsRepository.getProjects(1).get().get(0);
            final Room room = project.getRooms().get(0);
            measurementMetadata = room.getMeasurements().get(0);

            final Http.RequestBuilder setActiveRequest = new Http.RequestBuilder()
                    .method(PUT)
                    .uri("/measurements/active/" + measurementMetadata.getMeasurementId());
            final Result setActiveResult = route(app, setActiveRequest);
            assertEquals(OK, setActiveResult.status());
        }

        return measurementMetadata;
    }
}