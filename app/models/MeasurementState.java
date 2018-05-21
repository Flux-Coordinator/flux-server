package models;

/**
 * The state of the measurement.
 * Ready: Measurement is ready to be run.
 * Running: The measurement is currently being run.
 * Done: The measurement was run and is now finished.
 */
public enum MeasurementState {
    /**
     * Measurement is ready to be run.
     */
    READY,
    /**
     * The measurement is currently being run (active).
     */
    RUNNING,
    /**
     * The measurement was run and is now finished.
     */
    DONE
}
