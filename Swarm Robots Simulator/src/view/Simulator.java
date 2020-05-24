package view;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import configs.Settings;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import robot.Robot;
import swarm.Swarm;

/**
 *
 * @author Nadun
 */
public class Simulator {

    public static Field field;
    private JFrame jf;
    private boolean running;

    public Simulator(Swarm swarm) {

        setLookAndFeel();

        field = new Field(this);
        field.parseSwarm(swarm);
        field.setLocation(0, 0);
        field.setSize(Settings.FEILD_WIDTH, Settings.FEILD_HEIGHT);

        jf = new JFrame();
        jf.setLayout(null);

        jf.add(field);
        jf.setSize(Settings.FEILD_WIDTH, Settings.FEILD_HEIGHT);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);

        jf.setTitle(swarm.getName());

        jf.addMouseListener(field);

    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;

        if (!running) {
            ArrayList<Robot> robots = field.getSwarm().getRobots();

            for (Robot robot : robots) {
                robot.moveStop();
            }
            field.stop();
        } else {
            run();
        }
        
    }

    private void run() {

        ArrayList<Robot> robots = field.getSwarm().getRobots();

        for (Robot robot : robots) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    while (running) {
                        robot.loop();
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Robot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            };
            t.start();

        }
        field.start();
    }

    public void start() {
        jf.setVisible(true);
        running = false;
    }

}
