package models;

import javax.persistence.*;

@Entity(name="Project")
@Table(name="project", schema = "public")
public class Project {
    @Id @GeneratedValue @Column(name = "id")
    private long projectId;
    private String name;
    private String description;
//    private List<Room> rooms;

    public Project() {
//        this.rooms = new ArrayList<>();
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

//    public List<Room> getRooms() {
//        return rooms;
//    }
//
//    public void setRooms(final List<Room> rooms) {
//        this.rooms = rooms;
//    }
//
//    @Override
//    public boolean equals(final Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Project project = (Project) o;
//        return Objects.equals(getProjectId(), project.getProjectId()) &&
//                Objects.equals(getName(), project.getName()) &&
//                Objects.equals(getDescription(), project.getDescription()) &&
//                Objects.equals(getRooms(), project.getRooms());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getProjectId(), getName(), getDescription(), getRooms());
//    }
}
