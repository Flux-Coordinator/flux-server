package models;

import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Project {
    @BsonId
    private ObjectId projectId;
    private String name;
    private String description;
    private List<Room> rooms;

    public Project() {
        this.rooms = new ArrayList<>();
    }

    public ObjectId getProjectId() {
        return projectId;
    }

    public void setProjectId(ObjectId projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(getProjectId(), project.getProjectId()) &&
                Objects.equals(getName(), project.getName()) &&
                Objects.equals(getDescription(), project.getDescription()) &&
                Objects.equals(getRooms(), project.getRooms());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getName(), getDescription(), getRooms());
    }
}
