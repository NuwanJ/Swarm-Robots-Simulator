package view;

import swarm.Swarm;
import robot.Robot;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import utility.Constants;

/**
 *
 * @author Nadun
 */
public class Field extends JPanel implements ActionListener {

    private ArrayList<Object> objects;
    private Swarm swarm;
    private ArrayList<Robot> robots;
    private Boundary boundary;
    
    private Rectangle2D.Double obstacle = new Rectangle2D.Double(400, 100, 60, 60);

    private final Timer timer = new Timer(10, this);

    long start = 0;
    int collisions = 0;

    public Field() throws HeadlessException {
        this.objects = new ArrayList<>();

        this.boundary = new Boundary(0, 0, Constants.FEILD_WIDTH - 17, Constants.FEILD_HEIGHT - 40);

    }

    public Rectangle2D.Double getObstacle() {
        return obstacle;
    }
    
    

    public Field(Color color) throws HeadlessException, IOException {
        this();
        setBackground(color);
    }

    public void parseSwarm(Swarm swarm) {
        this.swarm = swarm;
        this.robots = swarm.getRobots();
    }

    public void addObject(Object object) {
        objects.add(object);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;
        
        Color color = g2d.getColor();
        
        boundary.draw(g2d);
        
        g2d.setColor(Color.GREEN);
        g2d.fill(obstacle);
        g2d.setColor(color);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Robot r : robots) {
            r.draw(g2d);
        }
        
        

//        long end = System.currentTimeMillis();
//        if (end - start >= 30000) {
//            System.out.println("Number of collisions = " + collisions);
//        } else if ((end - start) % 1000 == 0) {
//            for (Robot r : robots) {
//                for (Robot r2 : robots) {
//                    if (r == r2) {
//                        continue;
//                    }
//                    if (r.getBounds2D().intersects(r2.getBounds2D())) {
//                        collisions++;
//                    }
//                }
//            }
//        }

    }

    public Swarm getSwarm() {
        return swarm;
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public ArrayList<Robot> getRobots() {
        return robots;
    }

    public void start() {
        start = System.currentTimeMillis();
        timer.start();
    }

    public Boundary getBoundary() {
        return boundary;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }

}
