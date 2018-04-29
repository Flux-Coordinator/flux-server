package actors.measurements;

import models.Reading;

public class MeasurementActorProtocol {

    public static class PutReading {
        public final Reading reading;

        public PutReading(final Reading reading) {
            this.reading = reading;
        }
    }
}
