package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "AnchorPosition")
@Table(name = "anchorposition")
public class AnchorPosition {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "anchorposition_seq_generator")
    @Column(name="id")
    @SequenceGenerator(name = "anchorposition_seq_generator", sequenceName = "anchorposition_id_seq", allocationSize = 1)
    private long anchorPositionId;
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

    public long getAnchorPositionId() {
        return anchorPositionId;
    }

    public void setAnchorPositionId(final long anchorId) {
        this.anchorPositionId = anchorId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorPosition that = (AnchorPosition) o;
        return Double.compare(that.xPosition, xPosition) == 0 &&
                Double.compare(that.yPosition, yPosition) == 0 &&
                Double.compare(that.zPosition, zPosition) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(xPosition, yPosition, zPosition);
    }
}
