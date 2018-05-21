package repositories.generator;

import java.util.Random;

public class ValueRange {
    private static final Random random = new Random();

    private double min;
    private double max;
    private boolean isEquals;

    public ValueRange(double max) {
        this(0, max);
    }

    public ValueRange(double min, double max) {
        this.min = min;
        this.max = max;
        this.isEquals = Double.compare(min, max) == 0;
    }

    double getRandomValue() {
        if (this.isEquals) {
            return this.min;
        }
        return this.min + (this.max - this.min) * random.nextDouble();
    }

    double getMin() {
        return min;
    }

    double getMax() {
        return max;
    }
}
