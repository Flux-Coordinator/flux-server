package models;

import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Objects;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return Double.compare(reading.getLuxValue(), getLuxValue()) == 0 &&
                Double.compare(reading.xPosition, xPosition) == 0 &&
                Double.compare(reading.yPosition, yPosition) == 0 &&
                Double.compare(reading.zPosition, zPosition) == 0 &&
                Objects.equals(getReadingId(), reading.getReadingId()) &&
                Objects.equals(getTimestamp(), reading.getTimestamp());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getReadingId(), getLuxValue(), xPosition, yPosition, zPosition, getTimestamp());
    }
}