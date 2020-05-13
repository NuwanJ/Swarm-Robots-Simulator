
import communication.Message;
import communication.MessageType;
import java.util.ArrayList;
import robot.Robot;
import robot.sensors.IRSensor;
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

                for (int i = 0; i < 4; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {

                            if (getId() == 0) {
                                broadcastMessage(MessageType.ComeCloser);
                            } else {
                                moveRandom();
                                avoidObstacles();
                                Message recieveMessage = null;
                                ArrayList<IRSensor> irSensors = getiRSensors();
                                int ir = 0, i = 0;
                                for (IRSensor irSensor : irSensors) {
                                    Message m = irSensor.getRecieveMsg();
                                    if(m != null) {
                                        recieveMessage = m;
                                        ir = i;
                                        break;
                                    }
                                    i++;
                                }
                                
                                if (recieveMessage != null && recieveMessage.getType() == MessageType.ComeCloser) {
//                                    double a = getiRSensor().getSlope();
//                                    System.out.println(getId() + "-> " + a);
//                                    double alpha = angle % 360;
//                                    
//                                    if(alpha < 0 && -alpha > 180) {
//                                        alpha = 360 + alpha;
//                                    }
//                                    
//                                    else if(alpha > 0 && alpha > 180) {
//                                        alpha = alpha - 360;
//                                    }
//                                    
//                                    boolean up = recieveMessage.getSender().getCenterY() > getCenterY();
                                    moveStop();
                                    if (!rotationOff) {
//                                        if (a > 0) {
//                                            double beta = 90 - a;
//                                            if (up) {
//                                                angularTurn(beta - alpha);
//                                            } else {
//                                                angularTurn(beta - alpha);
//                                            }
//                                        } else {
//                                            double beta = -90 - a;
//                                            if (up) {
//                                                angularTurn(beta - alpha);
//                                            } else {
//                                                angularTurn(beta - alpha);
//                                            }
//                                        }
                                        
                                       
                                        
                                        angularTurn(ir * 90);
                                        moveForwardDistance(80);
                                        
                                    }
                                    rotationOff = true;
                               }
                            }

                        }

                    });
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.run();
    }
}
