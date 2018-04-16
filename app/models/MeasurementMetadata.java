package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Objects;

public class MeasurementMetadata {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId measurementId;
    private String name;
    private String description;
    private String creator;
    private Date startDate;
    private Date endDate;
    private MeasurementState state;
    private double offset;
    private double factor;

//region Getters/Setters

    public ObjectId getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(ObjectId measurementId) {
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public MeasurementState getState() {
        return state;
    }

    public void setState(MeasurementState state) {
        this.state = state;
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

//endregion Getters/Setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementMetadata that = (MeasurementMetadata) o;
        return Double.compare(that.getOffset(), getOffset()) == 0 &&
                Double.compare(that.getFactor(), getFactor()) == 0 &&
                Objects.equals(getMeasurementId(), that.getMeasurementId()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getCreator(), that.getCreator()) &&
                Objects.equals(getStartDate(), that.getStartDate()) &&
                Objects.equals(getEndDate(), that.getEndDate()) &&
                getState() == that.getState();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeasurementId(), getName(), getDescription(), getCreator(), getStartDate(), getEndDate(), getState(), getOffset(), getFactor());
    }
}
