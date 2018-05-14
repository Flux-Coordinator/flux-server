package repositories.generator;

class SimulatedLightSource {

    private double xPosition;
    private double yPosition;
    private double intensity;
    private double radius;

    SimulatedLightSource(double xPosition, double yPosition, double intensity,
        double radius) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.intensity = intensity;
        this.radius = radius;
    }

    double getxPosition() {
        return xPosition;
    }

    double getyPosition() {
        return yPosition;
    }

    double getIntensity() {
        return intensity;
    }

    double getRadius() {
        return radius;
    }
}
