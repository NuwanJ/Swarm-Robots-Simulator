package view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Nadun
 */
public class Obstacle extends Rectangle2D.Double {

    private Color color;

    public Obstacle(int x, int y, int width, int height, Color color) {
        setFrame(x, y, width, height);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(Graphics2D g2d) {
        Color color1 = g2d.getColor();
        g2d.setColor(color);
        g2d.fill(this);
        g2d.setColor(color1);
    }

}
