package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity(name = "AnchorPosition")
@Table(name = "anchorposition")
public class AnchorPosition {
    @Id @GeneratedValue @Column(name="id")
    private long anchorId;
    private String name;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    @ManyToOne
    @JoinColumn(name = "measurementId")
    @JsonBackReference
    private Measurement measurement;

    public long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(final long anchorId) {
        this.anchorId = anchorId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(final double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(final double yPosition) {
        this.yPosition = yPosition;
    }

    public double getZPosition() {
        return zPosition;
    }

    public void setZPosition(final double zPosition) {
        this.zPosition = zPosition;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }
}
