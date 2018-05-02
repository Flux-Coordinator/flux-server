package models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    private double offset;
    private double factor;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "measurementid")
    private List<Reading> readings;

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
}
