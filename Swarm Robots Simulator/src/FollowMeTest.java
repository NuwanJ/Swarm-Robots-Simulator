
import communication.messageData.general.AngleData;
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
public class FollowMeTest {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Follow Me Test") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {

                    join(new Robot() {

                        double a = 0;

                        @Override
                        public void loop() {

                            Message message = new Message(MessageType.FollowMe, this);
                            message.setData(new AngleData(angle % 360));

                            if (getId() == 0) {
                                broadcastMessage(message);
                                moveRandom();
                            } else {
                                
                                moveRandom();
                                Message recieveMessage = null;
                                ArrayList<IRSensor> irSensors = getiRSensors();
                                int ir = 0, i = 0;
                                for (IRSensor irSensor : irSensors) {
                                    Message m = irSensor.getRecieveMsg();
                                    if (m != null) {
                                        recieveMessage = m;
                                        ir = i;
                                        break;
                                    }
                                    i++;
                                }
                                
                                if(recieveMessage != null && recieveMessage.getType() == MessageType.FollowMe) {
                                    
                                    AngleData data = (AngleData) recieveMessage.getData();
                                    angle = data.getAngle();
                                }
                                
                           
                                moveForward();
                            }
                            
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
