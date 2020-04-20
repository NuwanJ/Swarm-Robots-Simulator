package view;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utility.Constants;
import javax.swing.JFrame;
import robot.Robot;
import swarm.Swarm;

/**
 *
 * @author Nadun
 */
public class Simulator {

    public static Field field;
    
    private JFrame jf;

    public Simulator(Swarm swarm) {

        field = new Field();
        field.parseSwarm(swarm);
        field.setLocation(0, 0);
        field.setSize(Constants.FEILD_WIDTH, Constants.FEILD_HEIGHT);

        jf = new JFrame();
        jf.setLayout(null);
        
        jf.add(field);
        jf.setSize(Constants.FEILD_WIDTH, Constants.FEILD_HEIGHT);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);

        jf.setTitle(swarm.getName());
    }

    public void run() {
       
        ArrayList<Robot> robots = field.getSwarm().getRobots();

        ArrayList<Thread> threads = new ArrayList<>();
        for (Robot robot : robots) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    while (true) {
                        robot.loop();
                        
                        //Thread wheelThread = robot.getWheelThread();
//                        if (wheelThread != null) {
//                            wheelThread.stop();
//                        } else {
//                            wheelThread = new Thread(new WheelThread());
                        //wheelThread.start();
                        //}
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
//        
//        for (Thread thread : threads) {
//            thread.start();
//           
//        }
      
        field.start();
    }

}
