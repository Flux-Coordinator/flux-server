package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity(name="Reading")
@Table(name="reading")
public class Reading {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reading_seq_generator") @Column(name = "id")
    @SequenceGenerator(name = "reading_seq_generator", sequenceName = "reading_id_seq", allocationSize = 1)
    private Long readingId;
    private double luxValue;
    private Date timestamp;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    @ManyToOne
    @JoinColumn(name = "measurementid")
    @JsonBackReference
    private Measurement measurement;

    public Long getReadingId() {
        return readingId;
    }

    public void setReadingId(final Long readingId) {
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
    public int hashCode() {
        return Objects.hash(getReadingId(), getLuxValue(), getTimestamp(), getXPosition(), getYPosition(), getZPosition());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return Double.compare(reading.getLuxValue(), getLuxValue()) == 0 &&
                Double.compare(reading.xPosition, xPosition) == 0 &&
                Double.compare(reading.yPosition, yPosition) == 0 &&
                Double.compare(reading.zPosition, zPosition) == 0;
    }
}
