package controllers;

import com.google.inject.Inject;
import models.Project;
import play.mvc.Controller;
import play.mvc.Result;
import repositories.generator.DataGenerator;
import repositories.measurements.MeasurementsRepository;
import repositories.projects.ProjectsRepository;

import java.util.List;

public class AdminController extends Controller {
    private static final int AMOUNT_OF_PROJECTS = 10;
    private static final int AMOUNT_OF_ROOMS_PER_PROJECT = 5;
    private static final int AMOUNT_OF_MEASUREMENTS_PER_ROOM = 10;
    private static final int AMOUNT_OF_READINGS_PER_MEASUREMENT = 1;

    private final ProjectsRepository projectsRepository;
//    private final MeasurementsRepository measurementsRepository;

    @Inject
    public AdminController(final ProjectsRepository projectsRepository, final MeasurementsRepository measurementsRepository) {
        this.projectsRepository = projectsRepository;
//        this.measurementsRepository = measurementsRepository;
    }

        public Result resetData() {
        final List<Project> projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS, AMOUNT_OF_ROOMS_PER_PROJECT);
        this.projectsRepository.addProjects(projects);
        return ok();

//        this.projectsRepository.resetRepository();
//        final List<Project> projects = DataGenerator.generateProjects(AMOUNT_OF_PROJECTS, AMOUNT_OF_ROOMS_PER_PROJECT);
//        final List<MeasurementReadings> measurementReadings = new ArrayList<>(AMOUNT_OF_PROJECTS * AMOUNT_OF_ROOMS_PER_PROJECT * AMOUNT_OF_MEASUREMENTS_PER_ROOM);
//
//        projects.parallelStream()
//                .forEach(project -> project.getRooms().forEach(room -> {
//                        final List<MeasurementMetadata> roomMeasurements = DataGenerator.generateMeasurements(AMOUNT_OF_MEASUREMENTS_PER_ROOM);
//                        roomMeasurements.forEach(measurementMetadata -> {
//                            room.getMeasurements().add(measurementMetadata);
//                            final MeasurementReadings readings = new MeasurementReadings();
//                            readings.setMeasurementId(measurementMetadata.getMeasurementId());
//                            readings.getReadings().addAll(DataGenerator.generateReadings(AMOUNT_OF_READINGS_PER_MEASUREMENT));
//                            measurementReadings.add(readings);
//                        });
//                    })
//                );
//
//        projectsRepository.addProjects(projects);
//        measurementsRepository.addMeasurements(measurementReadings);
//
//        return ok("Created " + AMOUNT_OF_PROJECTS + "  projects with " + AMOUNT_OF_ROOMS_PER_PROJECT + " rooms each," +
//                " " + AMOUNT_OF_MEASUREMENTS_PER_ROOM + " measurements per room" +
//                " and " + AMOUNT_OF_READINGS_PER_MEASUREMENT + " readings per measurement.");
    }
}
