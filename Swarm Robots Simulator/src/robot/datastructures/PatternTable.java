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

    public double getDistancetoTargetFromJoiningRobot(int joinLabel, 
            double bearing, double distance) {
        
        TableRow row = patterntable.get(joinLabel);
        
        //get the x,y coordinates of the pattern label measured from leader robot(0,0)
        double x_target = row.getXCoordinate();
        double y_target = row.getYCoordinate();

        //get the x,y coordinates of the joining robot measured from leader robot(0,0)
        double x_join = distance * Math.cos(Math.toRadians(bearing));
        double y_join = distance * Math.sin(Math.toRadians(bearing));

        //get distance from joining robot to target robot
        double targetDistance = Utility.distanceBetweenTwoPoints(new Point(x_join, y_join),
                new Point(x_target, y_target));

        return targetDistance;
    }

    public double getBearingtoTargetFromJoiningRobot(int joinLabel, double bearing, double distance) {

        //get perpendicular distance from leader robot to navigation path of the joining robot
        double dist_y = getPerpendicDistToNavPath(joinLabel, bearing, distance);

        //get the amount of deviation angle needed for the joining robot to go to the target location 
        double headingDeviation = Math.toDegrees(Math.asin(dist_y / distance));

        return headingDeviation;
    }

    public double getPerpendicDistToNavPath(int joinLabel, double bearing, double distance) {
        TableRow row = patterntable.get(joinLabel);

        //get the x,y coordinates of the pattern label measured from leader robot(0,0)
        double x_target = row.getXCoordinate();
        double y_target = row.getYCoordinate();

        //get the x,y coordinates of the joining robot measured from leader robot(0,0)
        double x_join = distance * Math.cos(Math.toRadians(bearing));
        double y_join = distance * Math.sin(Math.toRadians(bearing));

        //get the mid point of the navigation path
        double midPointNavPath_x = (x_join + x_target) / 2;
        double midPointNavPath_y = (y_join + y_target) / 2;

        //get the perpendicular distance from leader robot(0,0) to mid point of navigation path
        double distToNavPath = Utility.distanceBetweenTwoPoints(new Point(midPointNavPath_x, midPointNavPath_y),
                new Point(0.0, 0.0));

        return distToNavPath;
    }
}
