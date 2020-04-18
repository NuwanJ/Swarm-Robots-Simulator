package view;


import utility.Constants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Nadun
 */
public class Object extends JPanel {
    
    private Color color;
    
    public Object(int x, int y, int width, int height, Color color) {
        setLocation(x, y);
        setSize(width, height);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics g2d = (Graphics2D)g;
        g2d.setColor(color);
        g2d.fillRect(getX() - Constants.OBJECT_WIDTH, getY() - Constants.OBJECT_WIDTH, getWidth(), getHeight());
    }
    
    

}
