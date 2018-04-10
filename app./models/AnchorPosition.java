package models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class AnchorPosition {
    @BsonId
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

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getZPosition() {
        return zPosition;
    }

    public void setZPosition(double zPosition) {
        this.zPosition = zPosition;
    }
}
