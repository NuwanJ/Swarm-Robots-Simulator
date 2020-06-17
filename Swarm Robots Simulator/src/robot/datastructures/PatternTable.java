/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import communication.Data;
import configs.Settings;
import helper.Utility;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import sun.awt.image.BufferedImageGraphicsConfig;

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
public class PatternTable implements Data {

    /*
        * PatternTable Format
    |   PatternId   |   RespectiveParent    |   X Distance  |   Y Distance  |
        
        * Restriction
        One PatternId can only have one parent
     */
    private HashMap<Integer, TableRow> patterntable;

    public PatternTable() {
        patterntable = new HashMap<Integer, TableRow>();
        createPatternTable();
    }

    public void createPatternTable() {
        TableRow row = new TableRow(0, 10, 0);
        patterntable.put(1, row);

        row = new TableRow(1, 10, 0);
        patterntable.put(2, row);

        row = new TableRow(2, 10, 0);
        patterntable.put(3, row);

        row = new TableRow(3, 10, 0);
        patterntable.put(4, row);

        row = new TableRow(4, 10, 0);
        patterntable.put(5, row);

        row = new TableRow(5, 10, 0);
        patterntable.put(6, row);
        /*  
           for (Map.Entry<Integer,TableRow> entry : patterntable.entrySet()) { 
                System.out.println("Key = " + entry.getKey() + ", Value = " + ((TableRow)entry.getValue()).getParentLabel());
           }
         */
    }

    public boolean checkJoinValidity(int parentLabel, int requestedPatternLabel) {
        boolean status = false;

        //Get the corresponding table row for the current robot
        TableRow row = patterntable.get(requestedPatternLabel);
        int parent = row.getParentLabel();
        if (parent == parentLabel) {
            status = true;
        }
        return status;
    }

    public double getTargetDistanceFromParent(int joinLabel) {
        TableRow row = patterntable.get(joinLabel);
        double x = row.getXCoordinate();
        double y = row.getYCoordinate();
        return (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }

    public double getTargetBearingFromParent(int joinLabel, double heading) {
        TableRow row = patterntable.get(joinLabel);

        double x = row.getXCoordinate();
        double y = row.getYCoordinate();

        double angle = Utility.calculateBearing(new Point(0.0, 0.0),
                new Point(x, y), heading);

        if (y < 0) {
            angle = angle + 180;
        }
        return angle;
    }

    public double getTargetDistance(int joinLabel, double bearing, double distance) {
        TableRow row = patterntable.get(joinLabel);

        double x = row.getXCoordinate();
        double y = row.getYCoordinate();

        double x_init = distance * Math.cos(Math.toRadians(bearing));
        double y_init = distance * Math.sin(Math.toRadians(bearing));

        double targetDistance = Utility.distanceBetweenTwoPoints(new Point(x_init, y_init),
                new Point(x, y));

        return targetDistance;
    }

    public double getTargetBearing(int joinLabel, double bearing, double distance) {

        double dist_y = getPerpendicDistToNavPath(joinLabel, bearing, distance);

        double headingDeviation = Math.toDegrees(Math.asin(dist_y / distance));

        return headingDeviation;
    }

    public double getPerpendicDistToNavPath(int joinLabel, double bearing, double distance) {
        TableRow row = patterntable.get(joinLabel);

        double x = row.getXCoordinate();
        double y = row.getYCoordinate();

        double x_init = distance * Math.cos(Math.toRadians(bearing));
        double y_init = distance * Math.sin(Math.toRadians(bearing));

        double midPointNavPath_x = (x_init + x) / 2;
        double midPointNavPath_y = (y_init + y) / 2;

        double distToNavPath = Utility.distanceBetweenTwoPoints(new Point(midPointNavPath_x, midPointNavPath_y),
                new Point(0.0, 0.0));

        return distToNavPath;
    }

    /*
    public static void main(String[] args) {
         //System.out.println(Math.toDegrees(Math.toDegrees(Math.asin(0.5))));
        System.out.println(Math.toDegrees(Math.asin(0.5)));
    }
     */
}
