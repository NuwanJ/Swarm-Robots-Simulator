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
import java.util.logging.Level;
import java.util.logging.Logger;
import robot.Robot;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class IRSensor extends Ellipse2D.Double {

    private double maxCoverage; // max radius
    private Message broadcastMsg;
    private Message recieveMsg;

    private Robot robot;

    private boolean send, recieve;

    public IRSensor(double maxCoverage, Robot robot) {
        this.maxCoverage = maxCoverage;
        this.robot = robot;
        update();
    }

    public void update() {

        double x_ = robot.getCenterX() - maxCoverage;
        double y_ = robot.getCenterY() - maxCoverage;

        setFrame(x_, y_, 2 * maxCoverage, 2 * maxCoverage);

    }

    public void draw(Graphics2D g2d) {
        update();

        Color color = g2d.getColor();
        g2d.setColor(Color.lightGray);
        g2d.draw(this);

        for (Robot r : Simulator.field.getRobots()) {

            if (r == robot) {
                continue;
            }

            Area areaShape = new Area(this);
            Area areaRobot = new Area(r.getiRSensor());
            areaShape.intersect(areaRobot);

            if (!areaShape.isEmpty()) {
                g2d.setColor(Color.yellow);
                g2d.draw(areaShape);
                recieveMsg = r.getiRSensor().getBroadcastMsg();
                if (recieveMsg != null) {
                    System.out.println(robot.getId() + " -> " + recieveMsg);
                }
            }
        }
        g2d.setColor(color);

    }

    public double getMaxCoverage() {
        return maxCoverage;
    }

    public void setMaxCoverage(double maxCoverage) {
        this.maxCoverage = maxCoverage;
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
