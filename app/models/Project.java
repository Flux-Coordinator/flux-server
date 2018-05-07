package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity(name="Project")
@Table(name="project", schema = "public")
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq_generator")
    @Column(name = "id")
    @SequenceGenerator(name = "project_seq_generator", sequenceName = "project_id_seq", allocationSize = 1)
    private long projectId;
    private String name;
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "project", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Room> rooms;

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

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(final Set<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(getName(), project.getName()) &&
                Objects.equals(getDescription(), project.getDescription()) &&
                Objects.equals(getRooms(), project.getRooms());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getName(), getDescription());
    }
}
