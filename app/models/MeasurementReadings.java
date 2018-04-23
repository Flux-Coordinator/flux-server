package models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeasurementReadings {
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId measurementId;
    private List<Reading> readings;

    public MeasurementReadings() {
        this.readings = new ArrayList<>();
    }

    public ObjectId getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(final ObjectId measurementId) {
        this.measurementId = measurementId;
    }


    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(final List<Reading> readings) {
        this.readings = readings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeasurementReadings that = (MeasurementReadings) o;
        return Objects.equals(getMeasurementId(), that.getMeasurementId()) &&
                Objects.equals(getReadings(), that.getReadings());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getMeasurementId(), getReadings());
    }
}
