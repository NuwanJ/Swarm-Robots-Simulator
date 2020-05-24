
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Obstacle;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example2_AvoidObstacleMiddle {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Obstacle in middle ") {
            @Override
            public void create() {

                for (int i = 0; i < 5; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveForward();
                            avoidObstacles();
                        }

                    });
                }
            }
        };
        
        swarm.addObstacle(new Obstacle(450, 200, 70, 70, Color.RED));

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
