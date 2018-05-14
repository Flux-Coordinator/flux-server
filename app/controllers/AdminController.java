package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.Measurement;
import models.Project;
import models.Reading;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;

@Singleton
public class AdminController extends Controller {
    private static final int AMOUNT_OF_PROJECTS = 3;
    private static final int AMOUNT_OF_ROOMS_PER_PROJECT = 2;
    private static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 2;
    private static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1000;

    private final ProjectsRepository projectsRepository;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public AdminController(final ProjectsRepository projectsRepository,
                           final HttpExecutionContext httpExecutionContext) {
        this.projectsRepository = projectsRepository;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> resetData() {
        final List<Project> projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS,
                AMOUNT_OF_ROOMS_PER_PROJECT);

        projects.forEach(project -> project.getRooms().forEach(room -> {
            final Set<Measurement> roomMeasurements = DataGenerator.generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM);
            roomMeasurements.forEach(measurement -> {
                measurement.setRoom(room);
                final Set<Reading> readings = DataGenerator.generateHeatmap(AMOUNT_OF_READINGS_PER_MEASUREMENT, 9000, 13000, 400);
                readings.forEach(reading -> {
                    reading.setMeasurement(measurement);
                });
                measurement.getAnchorPositions().forEach(anchorPosition -> anchorPosition.setMeasurement(measurement));
                measurement.setReadings(readings);
            });
            room.setMeasurements(roomMeasurements);
        }));

        return this.projectsRepository.addProjects(projects).thenApplyAsync(aVoid ->
                        ok("Created " + AMOUNT_OF_PROJECTS + " projects with " + AMOUNT_OF_ROOMS_PER_PROJECT + " rooms each," +
                                " " + AMOUNT_OF_MEASUREMENTS_PER_ROOM + " measurements per room" +
                                " and " + AMOUNT_OF_READINGS_PER_MEASUREMENT + " readings per measurement.")
                , httpExecutionContext.current());
    }
}
