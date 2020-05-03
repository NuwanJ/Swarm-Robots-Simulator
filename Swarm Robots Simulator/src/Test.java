
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Test {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Testing..") {
            @Override
            public void create() {
                for (int i = 0; i < 1; i++) {
                    
                    join(new Robot() {
                        
                        
                        
                        @Override
                        public void loop() {
                            moveRandom();
                            avoidObstacles();
                        }

                    });
                    
                    
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
