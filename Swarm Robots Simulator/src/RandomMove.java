
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class RandomMove {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Test") {

            @Override
            public void create() {

                for (int i = 0; i < 5; i++) {

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
        simulator.start();
    }
}
