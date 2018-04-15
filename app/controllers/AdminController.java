package controllers;

import com.google.inject.Inject;
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
        final int amountOfMeasurements = 10;
        this.projectsRepository.resetRepository();
        this.measurementsRepository.resetRepository();

        final List<Project> projects = DataGenerator.generateProjects(amountOfProjects);
        final List<MeasurementReadings> measurementReadingsList = DataGenerator.generateMeasurements(amountOfMeasurements);

        projects.forEach(this.projectsRepository::addProject);
        measurementReadingsList.forEach(measurementReadings -> measurementsRepository.addMeasurement(null, measurementReadings));

        return ok("Created " + amountOfProjects + " projects and " + amountOfMeasurements + " measurements.");
    }
}
