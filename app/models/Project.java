package models;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name="Project")
@Table(name="project", schema = "public")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) @Column(name = "id")
    private long projectId;
    private String name;
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectid")
    private List<Room> rooms;

    public Project() {
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(final long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(final List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return getProjectId() == project.getProjectId() &&
                Objects.equals(getName(), project.getName()) &&
                Objects.equals(getDescription(), project.getDescription()) &&
                Objects.equals(getRooms(), project.getRooms());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getProjectId(), getName(), getDescription(), getRooms());
    }
}
