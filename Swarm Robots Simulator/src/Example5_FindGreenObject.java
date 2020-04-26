
import communication.ColorData;
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Obstacle;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example5_FindGreenObject {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Detect Green Object") {
            @Override
            public void create() {

                for (int i = 0; i < 5; i++) {

                    join(new Robot() {

                        boolean done = false;

                        @Override
                        public void loop() {

                            if (!done) {
                                moveForward();
                            }
                            avoidObstacles();
                            Color c = findColor();
                            System.out.println(c);
                            if (c.equals(Color.GREEN)) {
                                Message message = new Message(MessageType.ColorExchange, this);
                                message.setData(new ColorData(Color.GREEN));
                                broadcastMessage(message);
                                swithOnLedStript(Color.GREEN);
                                done = true;
                                moveStop();
                            } else {
                                broadcastMessage(MessageType.Pulse);
                            }

                            Message recieveMessage = recieveMessage();

                            if (recieveMessage != null && recieveMessage.getType() == MessageType.ColorExchange) {
                                Message message = new Message(MessageType.ColorExchange, this);
                                message.setData(recieveMessage.getData());
                                broadcastMessage(message);
                                swithOnLedStript(Color.GREEN);
                                done = true;
                                moveStop();
                            }

                        }

                    });
                }
            }
        };

        swarm.addObstacle(new Obstacle(450, 200, 70, 70, Color.GREEN));

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
