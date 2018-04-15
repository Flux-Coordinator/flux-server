package repositories.projects;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Project;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.Iterator;

import static com.mongodb.client.model.Filters.eq;

public class ProjectsRepositoryMongo implements ProjectsRepository {
    private final static String DATABASE_NAME = "flux";
    private final static String COLLECTION_NAME = "projects";

    private final MongoClient mongoClient;

    @Inject
    public ProjectsRepositoryMongo(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    private MongoCollection<Project> getCollection() {
        final MongoDatabase mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
        return mongoDatabase.getCollection(COLLECTION_NAME, Project.class);
    }

    @Override
    public Iterator<Project> getProjects() {
        return getCollection().find().iterator();
    }

    @Override
    public void addProject(final Project project) {
        getCollection().insertOne(project);
    }

    @Override
    public Project getProjectById(final ObjectId projectId) {
        final MongoCollection<Project> collection = getCollection();
        return collection.find(eq("_id", projectId)).first();
    }

    @Override
    public void resetRepository() {
        getCollection().drop();
        this.mongoClient.getDatabase(DATABASE_NAME).createCollection(COLLECTION_NAME);
    }
}
