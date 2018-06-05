package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import utils.DateHelper;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity(name="Measurement")
@Table(name="measurement")
public class Measurement {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "measurement_seq_generator")
    @Column(name="id")
    @SequenceGenerator(name = "measurement_seq_generator", sequenceName = "measurement_id_seq", allocationSize = 1)
    private long measurementId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String creator;
    @Column(name = "xoffset")
    private double xOffset;
    @Column(name = "yoffset")
    private double yOffset;
    private double scaleFactor;
    @CreationTimestamp
    private Timestamp createdDate;

    @Enumerated(EnumType.STRING)
    private MeasurementState measurementState;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measurement", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Set<Reading> readings;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "measurement", fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<AnchorPosition> anchorPositions;

    @ManyToOne
    @JoinColumn(name = "roomid")
    @JsonBackReference
    private Room room;

    public Measurement() { }

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

    public double getxOffset() {
        return xOffset;
    }

    public void setxOffset(double xOffset) {
        this.xOffset = xOffset;
    }

    public double getyOffset() {
        return yOffset;
    }

    public void setyOffset(double yOffset) {
        this.yOffset = yOffset;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public Long getRoomId() {
        if(room != null) {
            return room.getRoomId();
        }
        return null;
    }

    public Long getProjectId() {
        if(room != null && room.getProjectId() != null) {
            return room.getProjectId();
        }
        return null;
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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Measurement that = (Measurement) o;
        return Double.compare(that.getxOffset(), getxOffset()) == 0 &&
                Double.compare(that.getyOffset(), getyOffset()) == 0 &&
                Double.compare(that.getScaleFactor(), getScaleFactor()) == 0 &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                DateHelper.dateEquals(getStartDate(), that.getStartDate()) && // Dont use the standard equals with dates! The database changes the type!!
                DateHelper.dateEquals(getEndDate(), that.getEndDate()) &&
                Objects.equals(getCreator(), that.getCreator()) &&
                DateHelper.dateEquals(getCreatedDate(), that.getCreatedDate()) &&
                getMeasurementState() == that.getMeasurementState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getStartDate(), getEndDate(), getCreator(), getxOffset(), getyOffset(), getScaleFactor(), getMeasurementState());
    }

    @PrePersist
    void preInsert() {
        if(this.getMeasurementState() == null) {
            this.setMeasurementState(MeasurementState.READY);
        }
    }

}
