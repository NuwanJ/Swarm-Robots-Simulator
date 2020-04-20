
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
public class PairCommunicationTest {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Pair Communication Test") {
            @Override
            public void create() {

                for (int i = 0; i < 4; i++) {

                    join(new Robot() {

                        @Override
                        public void loop() {
                            moveRandom();
                            avoidObstacles();
                            
                            if(getId() == 0) {
                                broadcastMessage(MessageType.FollowMe);
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
