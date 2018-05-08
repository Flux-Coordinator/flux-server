package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity(name="Room")
@Table(name="room")
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "room_seq_generator") @Column(name = "id")
    @SequenceGenerator(name="room_seq_generator", sequenceName = "room_id_seq", allocationSize = 1)
    private long roomId;
    private String name;
    private String description;
    private String floorPlan;
    private double floorSpace;
    @Column(name = "xoffset")
    private double xOffset;
    @Column(name = "yoffset")
    private double yOffset;
    private double scaleFactor;


    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<Measurement> measurements;

    @ManyToOne
    @JoinColumn(name="projectid")
    @JsonBackReference
    private Project project;

    public Room() {
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
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

    public String getFloorPlan() {
        return floorPlan;
    }

    public void setFloorPlan(String floorPlan) {
        this.floorPlan = floorPlan;
    }

    public double getFloorSpace() {
        return floorSpace;
    }

    public void setFloorSpace(double floorSpace) {
        this.floorSpace = floorSpace;
    }

    public double getxOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Set<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<Measurement> measurements) {
        this.measurements = measurements;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Double.compare(room.getFloorSpace(), getFloorSpace()) == 0 &&
                Double.compare(room.getxOffset(), getxOffset()) == 0 &&
                Double.compare(room.getyOffset(), getyOffset()) == 0 &&
                Double.compare(room.getScaleFactor(), getScaleFactor()) == 0 &&
                Objects.equals(getName(), room.getName()) &&
                Objects.equals(getDescription(), room.getDescription()) &&
                Objects.equals(getFloorPlan(), room.getFloorPlan()) &&
                Objects.equals(getMeasurements(), room.getMeasurements());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getDescription(), getFloorPlan(), getFloorSpace(), getxOffset(), getyOffset(), getScaleFactor(), getMeasurements());
    }
}
