package controllers;

import com.google.inject.Inject;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Project;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;

import java.util.List;

public class AdminController extends Controller {
    private final ProjectsRepository projectsRepository;
    private final MeasurementsRepository measurementsRepository;

    @Inject
    public AdminController(final ProjectsRepository projectsRepository, final MeasurementsRepository measurementsRepository) {
        this.projectsRepository = projectsRepository;
        this.measurementsRepository = measurementsRepository;
    }

    public Result resetData() {
        final int amountOfProjects = 10;
        final int amountOfRoomsPerProject = 5;
        final int amountOfMeasurementsPerRoom = 10;
        this.projectsRepository.resetRepository();
        this.measurementsRepository.resetRepository();

        final List<Project> projects = DataGenerator.generateProjects(amountOfProjects, amountOfRoomsPerProject);

        projects.forEach(this.projectsRepository::addProject);
        this.projectsRepository.getProjects().forEachRemaining(project -> {
            project.getRooms().forEach(room -> {
                final List<MeasurementMetadata> roomMeasurements = DataGenerator.generateMeasurements(amountOfMeasurementsPerRoom);
                roomMeasurements.forEach(measurementMetadata -> this.projectsRepository.createMeasurement(project.getProjectId(), room.getName(), measurementMetadata));
            });
        });

        return ok("Created " + amountOfProjects + "  projects with " + amountOfRoomsPerProject + " rooms each and " + amountOfMeasurementsPerRoom + " measurements per room.");
    }
}
