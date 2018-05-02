package models;

import javax.persistence.*;

@Entity(name = "Position")
@Table(name = "position")
public class Position {
    @Id @GeneratedValue @Column(name = "id")
    private long positionId;
    private double xPosition;
    private double yPosition;
    private double zPosition;

    @OneToOne(mappedBy = "position")
    private Reading reading;

    public Position() {
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public double getxPosition() {
        return xPosition;
    }

    public void setxPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getyPosition() {
        return yPosition;
    }

    public void setyPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getzPosition() {
        return zPosition;
    }

    public void setzPosition(double zPosition) {
        this.zPosition = zPosition;
    }
}
