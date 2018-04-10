package models;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class MeasurementReadings {
    @BsonId
    private ObjectId measurementId;
    private final List<AnchorPosition> anchorPositions;
    private final List<Reading> readings;

    public MeasurementReadings() {
        this.anchorPositions = new ArrayList<>();
        this.readings = new ArrayList<>();
    }

    public ObjectId getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(final ObjectId measurementId) {
        this.measurementId = measurementId;
    }

    public List<AnchorPosition> getAnchorPositions() {
        return anchorPositions;
    }


    public List<Reading> getReadings() {
        return readings;
    }

}
