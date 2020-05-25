
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

                        boolean done = false;

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double bearing) {

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case ComeCloser:
                                    done = true;
                                    moveStop();
                                    //angularTurn(bearing);
                                    //turnRightAngle(90);
                                    rotationStop();
                                    break;
                                case PulseFeedback:

                                    break;
                                default:
                                    break;
                            }
                        }

                        @Override
                        public void loop() {

                            // first robot send come closer msg 
                            if (getId() == 0) {
                                Message comeCloser = new Message(MessageType.ComeCloser, this);
                                //broadcastMessage(comeCloser);
                            } else {
                                if (!done) {
                                    moveRandom();
                                    avoidObstacles();
                                }
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
