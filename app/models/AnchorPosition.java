package models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorPosition that = (AnchorPosition) o;
        return Double.compare(that.xPosition, xPosition) == 0 &&
                Double.compare(that.yPosition, yPosition) == 0 &&
                Double.compare(that.zPosition, zPosition) == 0 &&
                Objects.equals(getAnchorId(), that.getAnchorId()) &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getAnchorId(), getName(), xPosition, yPosition, zPosition);
    }
}
