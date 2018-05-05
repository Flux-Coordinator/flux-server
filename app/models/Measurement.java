package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity(name="Measurement")
@Table(name="measurement")
public class Measurement {
    @Id @GeneratedValue @Column(name="id")
    private long measurementId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String creator;
    private double targetHeight;
    private double heightTolerance;
    @Column(name = "measurementoffset")
    private double offset;
    private double factor;

    @Enumerated(EnumType.STRING)
    private MeasurementState measurementState;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measurement", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<Reading> readings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measurement", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<AnchorPosition> anchorPositions;

    @ManyToOne
    @JoinColumn(name = "roomid")
    @JsonBackReference
    private Room room;

    public Measurement() {

    }

    public long getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(long measurementId) {
        this.measurementId = measurementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public double getTargetHeight() {
        return targetHeight;
    }

    public void setTargetHeight(double targetHeight) {
        this.targetHeight = targetHeight;
    }

    public double getHeightTolerance() {
        return heightTolerance;
    }

    public void setHeightTolerance(double heightTolerance) {
        this.heightTolerance = heightTolerance;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public Set<Reading> getReadings() {
        return readings;
    }

    public void setReadings(final Set<Reading> readings) {
        this.readings = readings;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Set<AnchorPosition> getAnchorPositions() {
        return anchorPositions;
    }

    public void setAnchorPositions(final Set<AnchorPosition> anchorPositions) {
        this.anchorPositions = anchorPositions;
    }

    public MeasurementState getMeasurementState() {
        return measurementState;
    }

    public void setMeasurementState(MeasurementState measurementState) {
        this.measurementState = measurementState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return Double.compare(that.getTargetHeight(), getTargetHeight()) == 0 &&
                Double.compare(that.getHeightTolerance(), getHeightTolerance()) == 0 &&
                Double.compare(that.getOffset(), getOffset()) == 0 &&
                Double.compare(that.getFactor(), getFactor()) == 0 &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                getStartDate().compareTo(that.getStartDate()) == 0 && // Dont use the standard equals with dates! The database changes the type!!
                getEndDate().compareTo(that.getEndDate()) == 0 &&
                Objects.equals(getCreator(), that.getCreator()) &&
                getMeasurementState() == that.getMeasurementState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getStartDate(), getEndDate(), getCreator(), getTargetHeight(), getHeightTolerance(), getOffset(), getFactor(), getMeasurementState());
    }
}
