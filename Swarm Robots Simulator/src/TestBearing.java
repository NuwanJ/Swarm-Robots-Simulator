
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
public class TestBearing {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Bearing Test") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {
                    join(new Robot() {

                        int state = 0;
                        double bearing = 0;
                        int dist = 0;

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double b, double d) {

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case ComeCloser:

                                    if (state == 0) {
                                        state = 1;
                                        bearing = b;
                                        System.out.println(bearing + "\n");
                                        DistanceData data = (DistanceData) message.getData();
                                        dist = data.getDistance();
                                        moveStop();
                                        sendMessage(MessageType.PulseFeedback, sender);
                                    }

                                    break;
                                case PulseFeedback:
                                    if (message.getReceiver().getId() == getId()) {
                                        state = 2;
                                    }
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
                                comeCloser.setData(new DistanceData(50));
                                broadcastMessage(comeCloser);

                            } else {
                                if (state == 0) {
                                    moveRandom();
                                    avoidObstacles();
                                } else if (state == 1) {
                                    angularTurn(bearing);
                                    state = 2;
                                } else if (state == 2) {
                                    double distance = findDistance();
                                    console.log(distance + "");
                                    if (distance > dist || distance == 0) {
                                        moveForward();
                                    } else {
                                        state = 3;
                                        moveStop();
                                    }
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
