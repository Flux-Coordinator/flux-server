package repositories.projects;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import models.MeasurementMetadata;
import models.MeasurementReadings;
import models.Project;
import models.Room;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class ProjectsRepositoryMongo implements ProjectsRepository {
    private final static String DATABASE_NAME = "flux";
    private final static String PROJECTS_COLLECTION_NAME = "projects";
    private final static String MEASUREMENT_COLLECTION_NAME = "measurements";

    private final MongoClient mongoClient;

    @Inject
    public ProjectsRepositoryMongo(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    private MongoCollection<Project> getProjectsCollection() {
        final MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        return mongoDatabase.getCollection(PROJECTS_COLLECTION_NAME, Project.class);
    }

    private MongoCollection<MeasurementReadings> getMeasurementsCollection(){
        final MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        return mongoDatabase.getCollection(MEASUREMENT_COLLECTION_NAME, MeasurementReadings.class);
    }

    @Override
    public Iterator<Project> getProjects() {
        return getProjectsCollection().find().iterator();
    }

    @Override
    public ObjectId addProject(final Project project) {
        final ObjectId newId = new ObjectId();
        project.setProjectId(newId);
        getProjectsCollection().insertOne(project);
        return newId;
    }

    @Override
    public Project getProjectById(final ObjectId projectId) {
        final MongoCollection<Project> collection = getProjectsCollection();
        return collection.find(eq("_id", projectId)).first();
    }

    public ObjectId addMeasurement(final ObjectId projectId, final String roomName, final MeasurementMetadata measurementMetadata) {
        final Project project = getProjectById(projectId);
        final Optional<Room> roomOptional = project.getRooms().parallelStream()
                .filter(room -> room.getName().equals(roomName))
                .findAny();

        final ObjectId measurementId = new ObjectId();
        measurementMetadata.setMeasurementId(measurementId);
        //noinspection ConstantConditions (We want to throw here!)
        roomOptional.get().getMeasurements().add(measurementMetadata);

        final MeasurementReadings measurementReadings = new MeasurementReadings();
        measurementReadings.setMeasurementId(measurementId);

        final UpdateResult updateResult = getProjectsCollection().updateOne(eq("_id", projectId), new Document("$set", project));
        final long modifiedProjectsCount = updateResult.getModifiedCount();

        try {
            getMeasurementsCollection().insertOne(measurementReadings);
        } catch(final Exception ex) {
            // Revert possible changes
            if(modifiedProjectsCount > 0) {
                roomOptional.get().getMeasurements().removeIf(m -> m.getMeasurementId().equals(measurementId));
                getProjectsCollection().updateOne(eq("_id", projectId), new Document("$set", project));
            }
            getMeasurementsCollection().deleteOne(eq("_id", measurementId));
            throw ex;
        }
        return measurementId;
    }

    @Override
    public long countProjects() {
        return getProjectsCollection().count();
    }

    @Override
    public void resetRepository() {
        getProjectsCollection().drop();
        this.mongoClient.getDatabase(DATABASE_NAME).createCollection(PROJECTS_COLLECTION_NAME);
    }
}
