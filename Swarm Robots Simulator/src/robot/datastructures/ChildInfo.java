/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

/**
 *
 * @author Mahendra
 */
public class ChildInfo {

    private double childXCoordinate;
    private double childYCoordinate;
    private boolean isAcquired;

    public ChildInfo(double x_co, double y_co, boolean isAcquired) {
        this.childXCoordinate = x_co;
        this.childYCoordinate = y_co;
        this.isAcquired = isAcquired;
    }

    public double getXCoordinate() {
        return this.childXCoordinate;
    }

    public double getYCoordinate() {
        return this.childYCoordinate;
    }

    public boolean getAcquiredStatus() {
        return this.isAcquired;
    }

    public void setAcquiredStatus(boolean status) {
       this.isAcquired = status;
    }
}
