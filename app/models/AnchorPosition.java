package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity(name = "AnchorPosition")
@Table(name = "anchorposition")
public class AnchorPosition {
    @Id @GeneratedValue @Column(name="id")
    private long anchorId;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    @ManyToOne
    @JoinColumn(name = "measurementId")
    @JsonBackReference
    private Measurement measurement;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "anchorid")
    private Anchor anchor;

    public long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(final long anchorId) {
        this.anchorId = anchorId;
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

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }
}
