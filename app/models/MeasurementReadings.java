package models;

import org.bson.types.ObjectId;

import java.util.List;

public class MeasurementReadings {
    private ObjectId measurementId;
    private List<AnchorPosition> anchorPositions;
    private List<Reading> readings;
}
