
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class LEDStriptTest {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("LED Stript Test") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveRandom();
                            avoidObstacles();
                            
                            double d = findDistance();
                            //System.out.println(getId() + " - " + d);
//                            
                            if(d > 0 && d < 55) {
                                swithOnLedStript(Color.yellow);
                            }
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
