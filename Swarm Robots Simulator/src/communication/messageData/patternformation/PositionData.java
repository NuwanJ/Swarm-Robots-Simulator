/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.patternformation;

/**
 *
 * @author Mahendra
 */
public class PositionData {
    private double targetBearing;
    private double targetDistance;
    private int patternLabel;
    
    public PositionData(double bearing, double distance, int label){
        this.targetBearing = bearing;
        this.targetDistance = distance;
        this.patternLabel = label;
    }
    
    public double getTargetBearing(){
        return this.targetBearing;
    }
    public double getTargetDistance() {
        return targetDistance;
    }
    public int getPatternLabel(){
        return this.patternLabel;
    };
}
