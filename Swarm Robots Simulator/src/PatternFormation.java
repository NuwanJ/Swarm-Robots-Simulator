
import communication.Message;
import communication.MessageType;
import communication.messageData.patternformation.JoinPattern;
import configs.Settings;
import robot.Robot;
import swarm.*;
import view.Simulator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
        
/*
public class PatternFormation {
    public static void main(String[] args){
        Swarm swarm = new Swarm("Pattern-Formation"){
            @Override
            public void create(){
                for (int robotIndex = 0; robotIndex < Settings.NUM_OF_ROBOTS; robotIndex++){
                    join(new Robot(){
                        @Override
                        public void loop(){
                            if(getCurrentState() == State.JOINED){
                                Message joinBroadcast = new Message(MessageType.JoinPattern, this);
                                joinBroadcast.setData(new JoinPattern(patternPositionId,nextJoinId));
                                broadcastMessage(joinBroadcast); 
                               // validateIncomingRequests();
                                informRelevantParents();
                                transitionToProcessing();
                            }else if(getCurrentState() == State.POSITIONING){
                                ignore Message();
                            }else if(getCurrentState() == State.REQUESTING){
                                response = requestJoin();
                                if(response == "ok"){
                                    positionRobot();
                                    addjoinIdtoList();
                                    broadcastList();
                                    transitionToJoined();
                                }
                            }else if (getCurrentState() == State.FREE){
                                moveRandom();
                                response = listenForJoinBroadcase();
                                If (response ==true) transitionToRequest;
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
*/