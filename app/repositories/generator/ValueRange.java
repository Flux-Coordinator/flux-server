package repositories.generator;

public class ValueRange {
    private double min;
    private double max;

    public ValueRange(double max) {
        this(0, max);
    }

    public ValueRange(double min, double max) {
        this.min = min;
        this.max = max;
    }

    double getMin() {
        return min;
    }

    double getMax() {
        return max;
    }
}
