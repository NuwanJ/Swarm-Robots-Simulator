
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example1_AvoidBoundray {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Avoid Boundray") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {

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
