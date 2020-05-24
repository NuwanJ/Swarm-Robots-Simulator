package view;

import swarm.Swarm;
import robot.Robot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import utility.Settings;

/**
 *
 * @author Nadun
 */
public class Field extends JPanel implements ActionListener, MouseListener {

    private ArrayList<Obstacle> obstacles;
    private Swarm swarm;
    private ArrayList<Robot> robots;
    private Boundary boundary;
    protected JMenuItem itemRun, itemExit;
    private JPopupMenu popupMenu;
    private Simulator simulator;

    private final Timer timer = new Timer(15, this);

    public static Color color = new Color(240, 240, 240);

    long start = 0;
    int collisions = 0;

    public Field(Simulator simulator) throws HeadlessException {
        this.obstacles = new ArrayList<>();
        this.boundary = new Boundary(0, 0, Settings.FEILD_WIDTH - 17, Settings.FEILD_HEIGHT - 40);

        popupMenu = new JPopupMenu();
        
        itemRun = new JMenuItem("Run");
        popupMenu.add(itemRun);
        itemExit = new JMenuItem("Exit");
        popupMenu.add(itemExit);
        
        itemRun.setPreferredSize(new Dimension(100, 20));
        
        itemRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if(itemRun.getText().equalsIgnoreCase("Run")) {
                    simulator.run();
                    itemRun.setText("Stop");
                } else if(itemRun.getText().equalsIgnoreCase("Stop")) {
                    simulator.setRunning(false);
                    itemRun.setText("Run");
                }
            }
        });
        
        itemExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               System.exit(0);
            }
        });
        
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            popupMenu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            popupMenu.show(ev.getComponent(), ev.getX(), ev.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

}
