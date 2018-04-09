package models;

import org.bson.types.ObjectId;

public class AnchorPosition {
    private ObjectId anchorId;
    private String name;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    public ObjectId getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(ObjectId anchorId) {
        this.anchorId = anchorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getxPosition() {
        return xPosition;
    }

    public void setxPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public void setyPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getzPosition() {
        return zPosition;
    }

    public void setzPosition(double zPosition) {
        this.zPosition = zPosition;
    }
}
