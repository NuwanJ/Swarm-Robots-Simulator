/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author Nadun
 */
public class AngleData implements Data {

    private double angle;

    public AngleData(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

}
