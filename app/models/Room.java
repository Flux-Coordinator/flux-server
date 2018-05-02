package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name="Room")
@Table(name="room")
public class Room {
    @Id @GeneratedValue @Column(name = "id")
    private long roomId;
    private String name;
    private String description;
    private String floorPlan;
    private double floorSpace;
    private double xOffset;
    private double yOffset;
    private double scaleFactor;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "roomid")
    private List<Measurement> measurements;

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

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Room room = (Room) o;
//        return Double.compare(room.getWidth(), getWidth()) == 0 &&
//                Double.compare(room.getLength(), getLength()) == 0 &&
//                Objects.equals(getName(), room.getName()) &&
//                Objects.equals(getDescription(), room.getDescription()) &&
//                Objects.equals(getFloorPlan(), room.getFloorPlan()) &&
//                Objects.equals(getMeasurements(), room.getMeasurements());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getName(), getDescription(), getFloorPlan(), getWidth(), getLength(), getMeasurements());
//    }
}
