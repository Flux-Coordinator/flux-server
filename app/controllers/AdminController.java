package controllers;

import com.google.inject.Inject;
import models.Measurement;
import models.Project;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class AdminController extends Controller {
    private static final int AMOUNT_OF_PROJECTS = 10;
    private static final int AMOUNT_OF_ROOMS_PER_PROJECT = 5;
    private static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 10;
    private static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1;

    private final ProjectsRepository projectsRepository;
    private final HttpExecutionContext httpExecutionContext;
//    private final MeasurementsRepository measurementsRepository;

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
            final List<Measurement> roomMeasurements = DataGenerator.generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM);
            roomMeasurements.forEach(measurement -> {
                measurement.setRoom(room);
                measurement.setReadings(DataGenerator.generateReadings(AMOUNT_OF_READINGS_PER_MEASUREMENT));
            });
            room.setMeasurements(roomMeasurements);
        }));

        return this.projectsRepository.addProjects(projects).thenApplyAsync(aVoid ->
                        ok("Created " + AMOUNT_OF_PROJECTS + "  projects with " + AMOUNT_OF_ROOMS_PER_PROJECT + " rooms each," +
                                " " + AMOUNT_OF_MEASUREMENTS_PER_ROOM + " measurements per room" +
                                " and " + AMOUNT_OF_READINGS_PER_MEASUREMENT + " readings per measurement.")
                , httpExecutionContext.current());
    }
}
