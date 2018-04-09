package models;

import org.bson.types.ObjectId;

import java.util.Date;

public class Reading {
    private ObjectId readingId;
    private double luxValue;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private Date timestamp;

    public ObjectId getReadingId() {
        return readingId;
    }

    public void setReadingId(ObjectId readingId) {
        this.readingId = readingId;
    }

    public double getLuxValue() {
        return luxValue;
    }

    public void setLuxValue(double luxValue) {
        this.luxValue = luxValue;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
