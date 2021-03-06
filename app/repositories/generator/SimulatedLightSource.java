package repositories.generator;

import java.util.Set;

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

    private double getxPosition() {
        return xPosition;
    }

    private double getyPosition() {
        return yPosition;
    }

    private double getIntensity() {
        return intensity;
    }

    private double getRadius() {
        return radius;
    }

    static double getVarianceFromLightSources(double xPosition, double yPosition,
        Set<SimulatedLightSource> lightSources) {
        double variance = 0;
        for (SimulatedLightSource lightSource : lightSources) {
            variance += Math.max(0,
                lightSource.getRadius() - getDistanceToLightSource(xPosition, yPosition,
                    lightSource)) * lightSource.getIntensity();
        }
        return variance;
    }

    private static double getDistanceToLightSource(double xPosition, double yPosition,
        SimulatedLightSource lightSource) {
        return Math.sqrt(Math.pow(xPosition - lightSource.getxPosition(), 2) + Math
            .pow(yPosition - lightSource.getyPosition(), 2));
    }
}
