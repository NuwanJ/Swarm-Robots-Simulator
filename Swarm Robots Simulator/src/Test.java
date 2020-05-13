
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
                        int state = 0;

                        @Override
                        public void loop() {
                            avoidObstacles();
                            if (state == 0) {
                                moveForwardDistance(100);
                                state = 1;
                            } else {
                                moveStop();
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
