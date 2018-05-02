package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import javax.persistence.*;
import java.util.Date;

@Entity(name="Reading")
@Table(name="reading")
public class Reading {
    @Id @GeneratedValue @Column(name = "id")
    private long readingId;
    @JsonSerialize(using = ToStringSerializer.class)
    private double luxValue;
    private Date timestamp;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "positionid")
    @JsonManagedReference
    private Position position;

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
}
