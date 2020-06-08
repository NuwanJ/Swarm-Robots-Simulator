/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.patternformation;
import communication.Data;
/**
 *
 * @author Mahendra
 */
public class PositionData implements Data{

    private double targetBearing;
    private double targetDistance;

    public PositionData(double bearing, double distance) {
        this.targetBearing = bearing;
        this.targetDistance = distance;
    }

    public double getTargetBearing() {
        return this.targetBearing;
    }

    public double getTargetDistance() {
        return targetDistance;
    }
}
