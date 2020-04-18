/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.sensors;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import robot.Robot;
import utility.Constants;
import utility.Utility;
import view.Boundary;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class SharpSensor extends Arc2D.Double {

    private Robot robot;
    private double distance;
    private Color color;

    public SharpSensor(Robot robot) {
        super(Arc2D.PIE);
        this.robot = robot;
        update();
    }

    public void update() {

        double x_ = robot.getX() - Constants.SHARP_MAX_DISTANCE + Constants.ROBOT_RADIUS;
        double y_ = robot.getY() - Constants.SHARP_MAX_DISTANCE;

        setFrame(x_, y_, 2 * Constants.SHARP_MAX_DISTANCE, 2 * Constants.SHARP_MAX_DISTANCE);

        setAngleStart(360 + 90 - Constants.SHARP_MAX_RANGE);
        setAngleExtent(2 * Constants.SHARP_MAX_RANGE);

        distance = 0;
    }

    public void draw(Graphics2D g2d) {
        update();

        Color color1 = g2d.getColor();
        g2d.setColor(new Color(255, 223, 163));

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(robot.getAngle()), robot.getCenterX(), robot.getCenterY());

        Shape createTransformedShape = at.createTransformedShape(this);
        g2d.fill(createTransformedShape);

        Boundary boundary = Simulator.field.getBoundary();

        boolean borderHit = false;
        for (Rectangle2D.Double line : boundary.getBorders()) {

            Area areaShape = new Area(createTransformedShape);
            Area areaLine = new Area(line);
            areaShape.intersect(areaLine);

            if (!areaShape.isEmpty()) {
                distance = minDistanceFromSharpTo(areaShape) - Constants.ROBOT_RADIUS;
                //System.out.println(distance);
                borderHit = true;
                break;
            } else {
                distance = 0;
            }
        }

        if (!borderHit) {
            Area areaShape = new Area(createTransformedShape);
            Area areaRobot = new Area(Simulator.field.getObstacle().getBounds2D());
            areaShape.intersect(areaRobot);

            if (!areaShape.isEmpty()) {
                distance = minDistanceFromSharpTo(areaShape) - Constants.ROBOT_RADIUS;
                color = Color.green;
                //System.out.println("in");
                borderHit = true;
            } else {
                distance = 0;
                color = Color.green;
            }
        }

        if (!borderHit) {
            for (Robot r : Simulator.field.getRobots()) {

                if (r == robot) {
                    continue;
                }

                Area areaShape = new Area(createTransformedShape);
                Area areaRobot = new Area(r.getBounds2D());
                areaShape.intersect(areaRobot);

                if (!areaShape.isEmpty()) {
                    distance = minDistanceFromSharpTo(areaShape) - Constants.ROBOT_RADIUS;
                    color = Color.green;
                    //System.out.println("in");
                    break;
                } else {
                    distance = 0;
                    color = Color.green;
                }
            }
        }
        g2d.setColor(color1);

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

    public double readDistance() {
        return distance;
    }

}
