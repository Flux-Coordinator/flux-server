package actors.messages;

import models.Reading;

import java.util.ArrayList;
import java.util.List;

public class ReadingsMessage {
    private Reading[] readings;

    public ReadingsMessage() {
    }

    public ReadingsMessage(final Reading[] readings) {
        this.readings = readings;
    }

    public Reading[] getReadings() {
        return readings;
    }

    public void setReadings(final Reading[] readings) {
        this.readings = readings;
    }
}
