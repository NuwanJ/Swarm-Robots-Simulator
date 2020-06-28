
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import java.awt.AlphaComposite;
import java.awt.geom.Point2D;
import java.util.Random;
import robot.Robot;
import robot.datastructures.PatternTable;
import robot.datastructures.Point;
import communication.messageData.patternformation.PositionData;
import configs.Settings;

/**
 *
 * @author Nadun
 */
public class Utility {

    public static AlphaComposite alphaCompositeHidden = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0);
    public static AlphaComposite alphaCompositeVisible = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);

    private final static Random RANDOM = new Random();

    /**
     * Returns distance between two 2D points
     *
     * @param point1 first point
     * @param point2 second point
     * @return distance between points
     */
    public static double getDistance(Point2D point1, Point2D point2) {
        return getDistance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    /**
     * Returns distance between two sets of coordinate
     *
     * @param x1 first x coordinate
     * @param y1 first y coordinate
     * @param x2 second x coordinate
     * @param y2 second y coordinate
     * @return distance between sets of coordinate
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        // using long to avoid possible overflows when multiplying
        double dx = x2 - x1;
        double dy = y2 - y1;

        return Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous line
    }

    /**
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomInRange(int min, int max) {

        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static double calculateDistance(Robot from, Robot to) {

        double centerDist = getDistance(from.getCenterX(), from.getCenterY(),
                to.getCenterX(), to.getCenterY());

        double dist = centerDist - 2 * Settings.ROBOT_RADIUS;

        return dist;
    }

    public static double getSlope(double x1, double y1, double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        double toDegrees = Math.toDegrees(Math.atan2(dy, dx));

        return toDegrees;
    }

    public static double calculateBearing(Robot from, Robot to) {

        double slope = getSlope(from.getCenterX(), from.getCenterY(),
                to.getCenterX(), to.getCenterY());

        // robot orientation to north direction (in positive)
        double orientation = 0;

        double angle = from.getAngle();

        if (angle > 0) {
            orientation = angle % 360;
        } else {
            orientation = 360 - (Math.abs(angle) % 360);
        }

        double bearing = 0;

        if (slope == 0) {

            if (to.getX() < from.getX()) {
                bearing = 90 + slope + 180;
            } else {
                bearing = 90 + slope;
            }

        } else if (slope > 0) {

            if (to.getCenterY() < from.getCenterY()) { // 2nd quadrant
                bearing = 90 + slope + 180;
            } else { // 4th quadrant
                bearing = 90 + slope;
            }
        } else {

            if (to.getCenterY() < from.getCenterY()) { // 1st quadrant
                bearing = 90 - Math.abs(slope);
            } else { // 3rd quadrant
                bearing = 90 - Math.abs(slope) + 180;
            }
        }
        
        bearing -= orientation;
        
        if(bearing < 0) {
            bearing = 360 - Math.abs(bearing);
        }

        return bearing % 360;
    }

    public static double calculateBearing(Point ref, Point target, double heading) {

        double bearing = 0;
        double minBearing = 0;

        //get the gradient between two points
        double slope = getSlope(ref.getX(), ref.getY(),
                target.getX(), target.getY());

        // set robot orientation to angle measured clockwise from north direction 
        double orientation = heading % 360;

        if (slope > 0) {
            if (ref.getY() < target.getY()) {
                minBearing = orientation - (90 - slope);
            } else if (ref.getY() > target.getY()) {
                minBearing = orientation - (270 - slope);
            } else if (target.getY() == 0) { //slope 180
                minBearing = orientation - 270;
            } else if (target.getX() == 0) { //slope 90
                minBearing = orientation;
            }
        } else if (slope <= 0) {
            if (ref.getY() < target.getY()) {
                minBearing = orientation - (270 - slope);
            } else if (ref.getY() > target.getY()) {
                minBearing = orientation - (90 - slope);
            } else if (target.getY() == 0) { //slope 0
                minBearing = orientation - 90;
            } else if (target.getX() == 0) { //slope -90
                minBearing = orientation - 180;
            }
        }

        if (minBearing > 0) {
            bearing = 360 - minBearing;
        } else {
            bearing = -minBearing;
        }

        return bearing;
    }

    //---------------------------------aggregation functions------------------------------------------------------------
    public static double getMax(double[] inputArray) {
        double pMax = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] > pMax) {
                pMax = inputArray[i];
            }
        }
        return pMax;
    }

    public static int getMaxProbSendersId(double[] inputArray, double pMax) {
        int maxId = 0;
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] == pMax) {
                maxId = i;
            }
        }
        return maxId;
    }

    public static double getJoiningProb(int clusterSize) {
        double joiningProb;
        double o_max = 80;
        double robot_diameter = 40;
        double o_des = robot_diameter / 4;
        double v = 86;
        double deltaT = 0.1;
        double arenaArea = 600000;
        double r_m = (1.20 * o_des * Math.pow(clusterSize, 0.48)) / 2;

        if (clusterSize == 1) {
            joiningProb = (2 * o_max * v * deltaT) / arenaArea;
        } else {
            joiningProb = (2 * (o_max + r_m - (o_des / 2)) * v * deltaT) / arenaArea;
        }
        return joiningProb;
    }

    public static double getLeavingProb(int clusterSize) {
        double leavingProb;
        double shrinkProb = 0.9; //use testing and adjust the value
        if (clusterSize < 6) {
            leavingProb = clusterSize * shrinkProb;
        } else {
            leavingProb = Math.PI * (1.20 * Math.pow(clusterSize, 0.48) - 1) * shrinkProb;
        }
        return leavingProb;
    }

    //---------------------------------pattern formation functions------------------------------------------------------------
    public static PositionData calculateRobotVirtualCoordinates(PatternTable patternTable, int joiningLabel, double bearing, double distance) {

        int quadrant = (int) bearing / 90;

        double remainderAngle = bearing % 90;

        double angle = -1.0;

        if (quadrant == 0) {
            angle = 90 - remainderAngle;
        } else if (quadrant == 1) {
            angle = 360 - remainderAngle;
        } else if (quadrant == 2) {
            angle = 180 + remainderAngle;
        } else if (quadrant == 3) {
            angle = 180 - remainderAngle;
        }

        //get the x,y coordinates of the joining robot measured from leader robot(0,0)
        double x_Co = distance * Math.cos(Math.toRadians(angle));
        double y_Co = distance * Math.sin(Math.toRadians(angle));

        return new PositionData(x_Co, y_Co);
    }

    /*
    public static void calculateTargetPosition(PatternTable patternTable,
            int joiningLabel) {

        //Amount of heading deviation needed for the joining robot
        double joinRobotHeadingDeviation = 0;

        //distance and bearing from the leader robot
        double targetBearingFromParent = patternTable.getTargetBearingFromParent(joiningLabel, parentHeading);
        double targetDistanceFromParent = patternTable.getTargetDistanceFromParent(joiningLabel);

        //get the distance from joining robot to target point
        double targetDistance = patternTable.getDistancetoTargetFromJoiningRobot(joiningLabel, bearing, distance);

        //get the perpendicular distance from leader to nav path
        double distToNavPathFromLeader = patternTable.getPerpendicDistToNavPath(joiningLabel, bearing, distance);

        //bearing to the target location when parent set head to head to the joining robot
        double alpha = patternTable.getTargetBearingFromParent(joiningLabel, bearing);

        //leader robot intersect the path
        if (distToNavPathFromLeader < (Settings.ROBOT_RADIUS + 5)) {
            double roatation = bearing % 90;
            if (alpha < 180) {
                joinRobotHeadingDeviation = -roatation;
            } else {
                joinRobotHeadingDeviation = roatation;
            }
        } else {
            double beta = patternTable.getHeadingtoTargetFromJoiningRobot(joiningLabel, bearing, distance);
            if (alpha < 180) {
                joinRobotHeadingDeviation = -beta;
            } else {
                joinRobotHeadingDeviation = beta;
            }
        }
    }
     */
    public static double distanceBetweenTwoPoints(Point a, Point b) {
        double x_diff_squared = Math.pow(Math.abs(a.getX() - b.getX()), 2);
        double y_diff_squared = Math.pow(Math.abs(a.getY() - b.getY()), 2);

        return Math.sqrt(x_diff_squared + y_diff_squared);
    }

    /*
     public static boolean checkJoinFeasibility(HashMap childrenMap,
     double currBearing, double trgBearing) {
     boolean status = true;
     Iterator it = childrenMap.entrySet().iterator();
     while (it.hasNext()) {
     Map.Entry data = (Map.Entry) it.next();
     double bearing = (Double) data.getValue();
     double bearingDiff = Math.abs(trgBearing-currBearing);
     double angle = min(trgBearing,360-bearingDiff);
     if (bearing >= currBearing && bearing <= trgBearing) {
     status = false;
     }

     }
     return status;
     }
     */
    public static void main(String[] args) {
        //bearing should be 180
        System.out.println(calculateBearing(new Point(0, 0), new Point(-10, 0), 45));

//       System.out.println(getSlope(0, 0, 0, -10));
//        System.out.println(getSlope(0, 0, 0, 10));
//        System.out.println(getSlope(0, 0, 10, 0));
//        System.out.println(getSlope(0, 0, -10, 0));
    }

}
