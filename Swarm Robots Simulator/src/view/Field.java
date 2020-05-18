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
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import utility.Settings;

/**
 *
 * @author Nadun
 */
public class Field extends JPanel implements ActionListener {

    private ArrayList<Obstacle> obstacles;
    private Swarm swarm;
    private ArrayList<Robot> robots;
    private Boundary boundary;

    private final Timer timer = new Timer(15, this);

    public static Color color = new Color(240, 240, 240);

    long start = 0;
    int collisions = 0;

    public Field() throws HeadlessException {
        this.obstacles = new ArrayList<>();
        this.boundary = new Boundary(0, 0, Settings.FEILD_WIDTH - 17, Settings.FEILD_HEIGHT - 40);

    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

    public Field(Color color) throws HeadlessException, IOException {
        this();
    }

    public void parseSwarm(Swarm swarm) {
        this.swarm = swarm;
        this.robots = swarm.getRobots();
        this.obstacles = swarm.getObstacles();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        // draw obtacles
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(g2d);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boundary.draw(g2d);

        //robots.get(0).draw(g2d);
        //robots.get(1).draw(g2d);
        
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
        //revalidate();
    }

}
