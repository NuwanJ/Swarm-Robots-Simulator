/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.sensors;

import communication.Message;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import robot.Robot;
import utility.Settings;
import utility.Utility;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class IRSensor extends Ellipse2D.Double {

    private double maxDistance; // max radius
    private Message broadcastMsg;
    private Message recieveMsg;
    private Robot robot;
    private double slope;
    private boolean send, recieve;

    private final Color SHARP_COLOR = new Color(255, 223, 163);

    public IRSensor(Robot robot) {
        this.maxDistance = Settings.IR_MAX_DISTANCE;
        this.robot = robot;
        update();
    }

    public void update() {

        double x_ = robot.getCenterX() - maxDistance;
        double y_ = robot.getCenterY() - maxDistance;

        setFrame(x_, y_, 2 * maxDistance, 2 * maxDistance);

    }

    public void draw(Graphics2D gd) {
        update();

        Graphics2D g2d = (Graphics2D) gd.create();
        
        if (Settings.VISIBLE_IR) {
            g2d.setColor(Color.lightGray);
            g2d.draw(this);
        }

        //g2d.setColor(SHARP_COLOR);
        if (Settings.VISIBLE_IR) {
            g2d.setComposite(Utility.alphaCompositeVisible);
        } else {
            g2d.setComposite(Utility.alphaCompositeHidden);
        }

        for (Robot r : Simulator.field.getRobots()) {

            if (r == robot) {
                continue;
            }

            Area areaShape = new Area(this);
            Area areaRobot = new Area(r.getiRSensor());
            areaShape.intersect(areaRobot);

            if (!areaShape.isEmpty()) {
                if (Settings.VISIBLE_IR_INTERSECTION) {
                    g2d.setColor(Color.yellow);
                    g2d.draw(areaShape);
                }
                recieveMsg = r.getiRSensor().getBroadcastMsg();
                if (recieveMsg != null) {
                    slope = Utility.getSlope(robot.getCenterX(), robot.getCenterY(), r.getCenterX(), r.getCenterY());
                    //System.out.println(robot.getId() + " -> " + recieveMsg + " => " + slope);
                    //r.moveStop();
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
