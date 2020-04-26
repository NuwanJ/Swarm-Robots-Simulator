/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Boundary {

    private ArrayList<Rectangle2D.Double> borders;

    public Boundary(double x, double y, double w, double h) {
        this.borders = new ArrayList<>();
        borders.add(new Rectangle2D.Double(x, y, w, 1));
        borders.add(new Rectangle2D.Double(x, y + h, w, 1));
        borders.add(new Rectangle2D.Double(x, y, 1, h));
        borders.add(new Rectangle2D.Double(x + w, y, 1, h));
    }

    public void draw(Graphics2D g2d) {
        Color color = g2d.getColor();
        g2d.setColor(Color.BLACK);
        for (Rectangle2D.Double line : borders) {
            g2d.draw(line);
        }
        g2d.setColor(color);
    }

    public ArrayList<Rectangle2D.Double> getBorders() {
        return borders;
    }
    
    

}
