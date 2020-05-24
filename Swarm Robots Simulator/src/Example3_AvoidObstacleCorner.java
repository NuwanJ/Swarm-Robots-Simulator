
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Obstacle;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example3_AvoidObstacleCorner {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Obstacle arround corner ") {
            @Override
            public void create() {

                for (int i = 0; i < 10; i++) {

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
        
        swarm.addObstacle(new Obstacle(40, 40, 70, 70, Color.RED));

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
