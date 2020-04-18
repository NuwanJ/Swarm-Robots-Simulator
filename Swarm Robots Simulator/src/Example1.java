
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example1 {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Example 1") {
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
        simulator.run();
    }
}
