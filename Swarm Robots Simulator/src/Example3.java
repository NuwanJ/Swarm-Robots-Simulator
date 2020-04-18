
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example3 {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Example 3") {
            @Override
            public void create() {

                for (int i = 0; i < 1; i++) {

                    join(new Robot(200, 100, 90) {

                        @Override
                        public void loop() {
                            
                            moveForward(3000);
                            turnLeftAngle(180);
                            moveForward(3000);
                            turnRightAngle(180);
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
