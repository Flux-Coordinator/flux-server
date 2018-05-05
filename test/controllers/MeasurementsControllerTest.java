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
        final MeasurementMetadata measurementMetadata = DataGenerator.generateMeasurement();
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(Json.toJson(measurementMetadata))
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
            final MeasurementReadings measurementReadings = new MeasurementReadings();
            measurementReadings.setMeasurementId(new ObjectId());
            repository.addMeasurement(measurementReadings);
        }

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
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final int desiredLimitOfMeasurements = 2;

        for(int i = 0; i < desiredLimitOfMeasurements; i++) {
            final MeasurementReadings measurementReadings = new MeasurementReadings();
            measurementReadings.setMeasurementId(new ObjectId());
            repository.addMeasurement(measurementReadings);
        }

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
        final MeasurementReadings measurementReadings = new MeasurementReadings();
        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final ObjectId newMeasurementId = repository.addMeasurement(measurementReadings);
        final MeasurementReadings expectedReading = repository.getMeasurementbyId(newMeasurementId);
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/" + newMeasurementId);
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

    @Test
    public void getMeasurementById_InvalidObjectId_BadRequest() {
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/1234");
        final Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void startMeasurement_StartFirst_IsActive() {
        final ProjectsRepository projectsRepository = app.injector().instanceOf(ProjectsRepository.class);
        final Project project = projectsRepository.getProjects().next();
        final Room room = project.getRooms().get(0);
        final MeasurementMetadata measurementMetadata = room.getMeasurements().get(0);

        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .uri("/measurements/active/" + measurementMetadata.getMeasurementId());
        final Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void addReadings_AddOne_ReadingAdded() {
        final MeasurementMetadata activeMeasurement = getOrSetActiveMeasurement();

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
        final MeasurementReadings measurementReadings = repository.getMeasurementbyId(activeMeasurement.getMeasurementId());
        assertTrue(measurementReadings.getReadings().contains(reading));
    }

    @Test
    public void addReadings_AddMultiple_ReadingAdded() {
        final int amountOfReadings = 10;
        final MeasurementMetadata activeMeasurement = getOrSetActiveMeasurement();

        final List<Reading> readingList = DataGenerator.generateReadings(amountOfReadings);
        final Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .uri("/measurements/active/readings")
                .bodyJson(Json.toJson(readingList));
        final Result result = route(app, request);
        assertEquals(OK, result.status());

        final MeasurementsRepository repository = app.injector().instanceOf(MeasurementsRepository.class);
        final MeasurementReadings measurementReadings = repository.getMeasurementbyId(activeMeasurement.getMeasurementId());
        assertTrue(measurementReadings.getReadings().containsAll(readingList));
    }

    private MeasurementMetadata getOrSetActiveMeasurement() {
        final Http.RequestBuilder getActiveRequest = new Http.RequestBuilder()
                .method(GET)
                .uri("/measurements/active");
        final Result getResult = route(app, getActiveRequest);

        MeasurementMetadata measurementMetadata;
        if(getResult.status() == OK) {
            measurementMetadata = Helpers.convertFromJSON(getResult, MeasurementMetadata.class);
        } else {
            final ProjectsRepository projectsRepository = app.injector().instanceOf(ProjectsRepository.class);
            final Project project = projectsRepository.getProjects().next();
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