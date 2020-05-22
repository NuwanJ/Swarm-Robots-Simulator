
import communication.messageData.general.ColorData;
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import utility.Settings;
import view.Obstacle;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Example4_FindRedObject {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Detect Red Object") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {

                    join(new Robot() {

                        boolean done = false;

                        @Override
                        public void loop() {

                            if (!done) {
                                moveForward();
                            }
                            avoidObstacles();
                            Color c = findColor();
                            //System.out.println(c);
                            if (c.equals(Color.RED)) {
                                Message message = new Message(MessageType.ColorExchange, this);
                                message.setData(new ColorData(Color.RED));
                                broadcastMessage(message);
                                swithOnLedStript(Color.RED);
                                done = true;
                                moveStop();
                            } else {
                                broadcastMessage(MessageType.Pulse);
                            }

                            Message recieveMessage = null;
                            int n = Settings.NUM_OF_IR_SENSORS;
                            for (int j = 0; j < n; j++) {
                                if(recieveMessage(j) != null) {
                                    recieveMessage = recieveMessage(j);
                                }
                            }

                            if (recieveMessage != null && recieveMessage.getType() == MessageType.ColorExchange) {
                                Message message = new Message(MessageType.ColorExchange, this);
                                message.setData(recieveMessage.getData());
                                broadcastMessage(message);
                                swithOnLedStript(Color.RED);
                                done = true;
                                moveStop();
                            }

                        }

                    });
                }
            }
        };

        swarm.addObstacle(new Obstacle(450, 200, 70, 70, Color.RED));

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
