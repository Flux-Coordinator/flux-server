package repositories.generator;

class ValueRange {
    private double min;
    private double max;

    ValueRange(double max) {
        this(0, max);
    }

    ValueRange(double min, double max) {
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
