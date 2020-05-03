/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.ledstript;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import robot.Robot;
import utility.Settings;

/**
 *
 * @author Nadun
 */
public class LedStript extends Ellipse2D.Double {

    private Robot robot;
    private Color color;
    private float width; 
    public final Color defaultColor = new Color(63, 72, 204);

    public LedStript(Robot robot) {
        this.robot = robot;
        this.color = robot.getLedColor();
        update();
    }

    public void update() {

        double x_ = robot.getX();
        double y_ = robot.getY();
        
        if(robot.getLedColor() == null) {
            this.color = defaultColor;
        } else {
            this.color = robot.getLedColor();
        }

        setFrame(x_, y_, 2 * Settings.ROBOT_RADIUS, 2 * Settings.ROBOT_RADIUS);
    }

    public void draw(Graphics2D g2d) {
        update();

        Color color1 = g2d.getColor();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(color);
        g2d.draw(this);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(color);
        g2d.setColor(color1);

    }

}
