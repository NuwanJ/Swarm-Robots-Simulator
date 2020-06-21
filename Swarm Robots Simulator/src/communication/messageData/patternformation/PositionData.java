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

    private double X_Coordinate;
    private double Y_Coordinate;

    public PositionData(double x_co, double y_co) {
        this.X_Coordinate = x_co;
        this.Y_Coordinate = y_co;
    }

    public double getX() {
        return this.X_Coordinate;
    }

    public double getY() {
        return this.Y_Coordinate;
    }
}
