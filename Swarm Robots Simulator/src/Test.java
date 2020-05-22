
import communication.Message;
import communication.MessageType;
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

                for (int i = 0; i < 3; i++) {
                    join(new Robot() {

                        @Override
                        public synchronized void processMessage(Message message, int sensorId) {
                            super.processMessage(message, sensorId);

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case Pulse:
                                    /* your code here */
                                    sendMessage(MessageType.PulseFeedback, receiver);
                                    break;
                                case PulseFeedback:
                                    if (receiver != null && receiver.getId() == getId()) {
                                        /* your code here */
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void loop() {
                            Message pulse = new Message(MessageType.Pulse, this);
                            broadcastMessage(pulse);
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
