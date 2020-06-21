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
import java.util.ArrayList;
import robot.Robot;
import configs.Settings;
import helper.Utility;
import java.awt.geom.PathIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private double bearing;
    private Shape transformedShape;
    private double angle;
    private int id;
    private boolean listening = false;

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

        g2d.dispose();

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

    public void setListening(boolean value) {
        listening = value;
        if (value) {
            IRSensorThread thread = new IRSensorThread();
            thread.start();
        }
    }

    public void clearMessageBufferOut(Robot sender) {
        ArrayList<IRSensor> irSensors = sender.getiRSensors();
        for (IRSensor iRSensor : irSensors) {
            iRSensor.setBroadcastMsg(null);
        }
    }

    // msg receiving thread
    private class IRSensorThread extends Thread {

        @Override
        public void run() {
            while (listening) {

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

                            // get the broadcasting message from other robot
                            Message msg = irSensor.getBroadcastMsg();

                            //if (recieveMsg == null || recieveMsg.getType() != msg.getType()) {
                            recieveMsg = msg;
                            //}

                            if (recieveMsg != null) {

                                bearing = Utility.calculateBearing(robot, r);
                                double distance = Utility.calculateDistance(robot, r);
                                if (msg.getReceiver() != null && 
                                        msg.getReceiver().getId() == robot.getId()) {
                                    clearMessageBufferOut(r);
                                }
                                robot.processMessage(msg, id, bearing, distance);

                            }
                        } else {
                            recieveMsg = null;
                        }
                    }

                }

                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(IRSensor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    // end thread

}
