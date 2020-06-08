/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import communication.Data;
import configs.Settings;
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

    public boolean checkJoinValidity(int myLabel, int requestedPatternLabel) {
        boolean status = false;

        //Get the corresponding table row for the current robot
        TableRow row = patterntable.get(requestedPatternLabel);
        int parent = row.getParentLabel();
        if (parent == myLabel) {
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

    public double getTargetBearingFromParent(int joinLabel) {
        TableRow row = patterntable.get(joinLabel);
        double x = row.getXCoordinate();
        double y = row.getYCoordinate();
        double angle = Math.toDegrees(Math.atan2(y, x));
        if (y < 0) {
            angle = angle + 180;
        }
        return angle;
    }

    public double getPerpendicDistToNavPath(int joinLabel, double bearing, double distance) {
        TableRow row = patterntable.get(joinLabel);
        double x = row.getXCoordinate();
        double y = row.getYCoordinate();
        double x_init = distance * Math.cos(Math.toRadians(bearing));
        double y_init = distance * Math.sin(Math.toRadians(bearing));
        
        double midPointNavPath_x = (x_init + x)/2;
        double midPointNavPath_y = (y_init + y)/2;
        double distToNavPath = Math.sqrt(Math.pow(midPointNavPath_x, 2) + Math.pow(midPointNavPath_y, 2)); 
        
        return distToNavPath;
    }

}
