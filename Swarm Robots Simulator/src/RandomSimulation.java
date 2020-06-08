
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author mster
 */
public class RandomSimulation {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("LED Stript Test") {
            @Override
            public void create() {

                for (int i = 0; i < 5; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveRandom();
                            avoidObstacles();
                            /*
                            swithOnLedStript(Color.red);
                            moveRandom();
                            avoidObstacles();
                            swithOnLedStript(Color.yellow);
                            moveRandom();
                            avoidObstacles();
                            swithOnLedStript(Color.red);
                            moveRandom();
                            avoidObstacles();
                            swithOnLedStript(Color.yellow);
                            moveRandom();
                            avoidObstacles();
                            */
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
