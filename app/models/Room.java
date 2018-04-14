package models;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Room {
    private ObjectId roomId;
    private String name;
    private String description;
    private String floorPlan;
    private double width;
    private double length;
    private List<MeasurementMetadata> measurements;

    public Room() {
        this.measurements = new ArrayList<>();
    }

    public ObjectId getRoomId() {
        return roomId;
    }

    public void setRoomId(ObjectId roomId) {
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

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<MeasurementMetadata> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementMetadata> measurements) {
        this.measurements = measurements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Double.compare(room.getWidth(), getWidth()) == 0 &&
                Double.compare(room.getLength(), getLength()) == 0 &&
                Objects.equals(getRoomId(), room.getRoomId()) &&
                Objects.equals(getName(), room.getName()) &&
                Objects.equals(getDescription(), room.getDescription()) &&
                Objects.equals(getFloorPlan(), room.getFloorPlan()) &&
                Objects.equals(getMeasurements(), room.getMeasurements());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getRoomId(), getName(), getDescription(), getFloorPlan(), getWidth(), getLength(), getMeasurements());
    }
}
