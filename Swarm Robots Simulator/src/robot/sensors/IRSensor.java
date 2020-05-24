/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.sensors;

import communication.Message;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import robot.Robot;
import configs.Settings;
import helper.Utility;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class IRSensor extends Arc2D.Double {

    private double maxDistance; // max radius
    private Message broadcastMsg;
    private Message recieveMsg;
    private final Robot robot;
    private double slope;
    private boolean send, recieve;
    private Shape transformedShape;
    private double angle;
    private int id;

    private final Color SHARP_COLOR = new Color(255, 223, 163);

    public IRSensor(int id, Robot robot, double angle) {
        super(Arc2D.PIE);
        this.robot = robot;
        this.angle = angle;
        this.id = id;
        update();
    }

    public void update() {

        double dx = Settings.ROBOT_RADIUS * Math.sin(angle);
        double dy = Settings.ROBOT_RADIUS * Math.cos(angle);

        double maxDist = Settings.IR_MAX_DISTANCE + Settings.ROBOT_RADIUS;

        double x_ = robot.getX() - maxDist + Settings.ROBOT_RADIUS;
        double y_ = robot.getY() - maxDist + Settings.ROBOT_RADIUS;

        setFrame(x_, y_, 2 * maxDist, 2 * maxDist);

        setAngleStart(90 - Settings.IR_MAX_RANGE - angle);
        setAngleExtent(2 * Settings.IR_MAX_RANGE);
    }

    public Shape getTransformedShape() {
        return transformedShape;
    }

    public void draw(Graphics2D gd) {
        update();

        Graphics2D g2d = (Graphics2D) gd.create();

        g2d.setColor(Color.lightGray);

        if (Settings.VISIBLE_IR) {
            g2d.setComposite(Utility.alphaCompositeVisible);
        } else {
            g2d.setComposite(Utility.alphaCompositeHidden);
        }

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(robot.getAngle()), robot.getCenterX(), robot.getCenterY());

        transformedShape = at.createTransformedShape(this);
        g2d.fill(transformedShape);

        for (Robot r : Simulator.field.getRobots()) {

            if (r == robot) {
                continue;
            }

            ArrayList<IRSensor> irSensors = r.getiRSensors();

            for (IRSensor irSensor : irSensors) {
                
                Area areaShape = new Area(transformedShape);
                
                Shape robotShape = irSensor.getTransformedShape();
                if (robotShape == null) {
                    continue;
                }
                Area areaRobot = new Area(robotShape);
                areaShape.intersect(areaRobot);

                if (!areaShape.isEmpty()) {
                    if (Settings.VISIBLE_IR_INTERSECTION) {
                        g2d.setColor(Color.yellow);
                        g2d.draw(areaShape);
                    }
                    recieveMsg = irSensor.getBroadcastMsg();
                    
                    if (recieveMsg != null) {
                        robot.processMessage(recieveMsg, id);
                        slope = Utility.getSlope(robot.getCenterX(), robot.getCenterY(), r.getCenterX(), r.getCenterY());
                    //System.out.println(robot.getId() + " -> " + recieveMsg + " => " + slope);                        
                    } 
                    
                }
            }

        }
        g2d.dispose();

    }

    private double minDistanceFromSharpTo(Area area) {

        double minDist = java.lang.Double.MAX_VALUE;

        PathIterator iterator = area.getPathIterator(null);
        double[] coords = new double[6];
        while (!iterator.isDone()) {
            int type = iterator.currentSegment(coords);
            double x = coords[0];
            double y = coords[1];
            if (type != PathIterator.SEG_CLOSE) {
                double calculatedDist = Utility.getDistance(robot.getCenterX(), robot.getCenterY(), x, y);
                if (minDist > calculatedDist) {
                    minDist = calculatedDist;
                }
            }
            iterator.next();
        }
        return minDist;
    }

    public double getMaxCoverage() {
        return maxDistance;
    }

    public void setMaxCoverage(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Message getBroadcastMsg() {
        return broadcastMsg;
    }

    public Message getRecieveMsg() {
        return recieveMsg;
    }

    public void setRecieveMsg(Message recieveMsg) {
        this.recieveMsg = recieveMsg;
    }

    public void setBroadcastMsg(Message broadcastMsg) {
        this.broadcastMsg = broadcastMsg;
    }

    public double getSlope() {
        return -slope;
    }

    class SenderThread extends Thread {

        @Override
        public void run() {
            while (send) {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IRSensor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    class ReceiverThread extends Thread {

        @Override
        public void run() {
            while (recieve) {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IRSensor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
