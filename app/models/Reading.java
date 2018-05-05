package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

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
    private Date timestamp;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "positionid")
    @JsonManagedReference
    private Position position;

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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reading reading = (Reading) o;
        return getReadingId() == reading.getReadingId() &&
                Double.compare(reading.getLuxValue(), getLuxValue()) == 0 &&
                Objects.equals(getTimestamp(), reading.getTimestamp()) &&
                Objects.equals(getPosition(), reading.getPosition()) &&
                Objects.equals(getMeasurement(), reading.getMeasurement());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getReadingId(), getLuxValue(), getTimestamp(), getPosition(), getMeasurement());
    }
}
