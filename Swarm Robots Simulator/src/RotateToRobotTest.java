
import communication.Message;
import communication.MessageType;
import java.awt.Color;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nadun
 */
public class RotateToRobotTest {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Pair Communication Test") {
            @Override
            public void create() {

                for (int i = 0; i < 3; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {

                            if (getId() == 0) {
                                //broadcastMessage(MessageType.FollowMe);
                            } else {
                                moveRandom();
                                avoidObstacles();
                                Message recieveMessage = recieveMessage();
                                if (recieveMessage != null && recieveMessage.getType() == MessageType.FollowMe) {
                                    double a = getiRSensor().slope;
                                    boolean up = recieveMessage.getSender().getCenterY() > getCenterY();
                                    moveStop();
                                    if (!rotationOff) {
                                        if (a > 0) {
                                            if (up) {
                                                angularTurn((90 - angle % 360 + a) % 360);
                                            } else {

                                            }
                                        } else {
                                            if (up) {
                                                angularTurn((90 - angle % 360 + a) % 360);
                                            } else {

                                            }
                                        }
                                    }
                                    rotationOff = true;
                                }
                            }

                            //System.out.println(getId() + " - " + d);
//                            
//                            if (d > 0 && d < 55) {
//                                swithOnLedStript(Color.yellow);
//                            }
                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
