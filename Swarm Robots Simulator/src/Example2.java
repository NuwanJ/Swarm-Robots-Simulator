
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example2 {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Example 2") {
            @Override
            public void create() {

                for (int i = 0; i < 3; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveForward();
                            randomTurn(90, 120);
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
