
import communication.Message;
import communication.MessageType;
import communication.messageData.general.DistanceData;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Nadun
 */
public class Test {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Testing..") {
            @Override
            public void create() {

                for (int i = 0; i < 6; i++) {

                    join(new Robot() {

                        int state = 0;

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double b) {

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case ComeCloser:
                                    if (state == 0) {
                                        moveStop();
                                        state = 1;
                                    }
                                    break;
                                case PulseFeedback:

                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void loop() {

                            if (getId() == 0) {

                                Message comeCloser = new Message(MessageType.ComeCloser, this);
                                comeCloser.setData(new DistanceData(50));
                                broadcastMessage(comeCloser);
                            } else if (state == 0) {
                                moveRandom();
                                avoidObstacles();
                            } else if(state == 1) {
                                
                            }

                        }

                    });

                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
