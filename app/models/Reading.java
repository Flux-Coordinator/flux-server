package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name="Reading")
@Table(name="reading")
public class Reading {
    @Id @GeneratedValue @Column(name = "id")
    private long readingId;
    @JsonSerialize(using = ToStringSerializer.class)
    private double luxValue;
    @CreationTimestamp
    private Date timestamp;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    @ManyToOne
    @JoinColumn(name = "measurementid")
    @JsonBackReference
    private Measurement measurement;

    public long getReadingId() {
        return readingId;
    }

    public void setReadingId(long readingId) {
        this.readingId = readingId;
    }

    public double getLuxValue() {
        return luxValue;
    }

    public void setLuxValue(double luxValue) {
        this.luxValue = luxValue;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
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
        Reading reading = (Reading) o;
        return getReadingId() == reading.getReadingId() &&
                Double.compare(reading.getLuxValue(), getLuxValue()) == 0 &&
                Double.compare(reading.getXPosition(), getXPosition()) == 0 &&
                Double.compare(reading.getYPosition(), getYPosition()) == 0 &&
                Double.compare(reading.getZPosition(), getZPosition()) == 0 &&
                Objects.equals(getTimestamp(), reading.getTimestamp()) &&
                Objects.equals(getMeasurement(), reading.getMeasurement());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getReadingId(), getLuxValue(), getTimestamp(), getXPosition(), getYPosition(), getZPosition(), getMeasurement());
    }
}
