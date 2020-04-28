/**
 *
 * @author Tharuka
 */

import communication.aggregation.*;
import communication.Message;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;
import communication.MessageType;
import java.awt.Color;
import java.util.*; 
import java.util.logging.Level;
import java.util.logging.Logger;

public class Task_Aggregation {  
         
      public static void main(String[] args) {              
        
            Swarm swarm = new Swarm("Aggregate") {
            @Override
            public void create() {
                
                for (int i = 0; i < 2; i++) {                  
                    
                    join(new Robot() {
                        
                        State myState = State.SEARCHING;
                        int clusterId = getId();
                        int clusterSize = 0;
                        HashMap<Integer, Long> waitingMap = new HashMap<>();
                        int receiverArray[] = new int[4]; 
                        
                        public void receiveMessages(Task_Aggregation aggregate, Message recieveMessage) {        
                            if (recieveMessage != null && recieveMessage.getType() == MessageType.ColorExchange) {
                                // receiver messages
                            }
                        }
                        
                        @Override
                        public void loop() {
                            if(myState == State.SEARCHING) {
                                moveRandom();
                                avoidObstacles();
                                broadcastMessage(MessageType.Pulse);
                                Arrays.fill(receiverArray, 0);
                                try {                                    
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Task_Aggregation.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                double pMax = getMax(receiverArray);
                                int maxClusterId = 2; // myID
                                boolean flag = true;
                                if( pMax != 0 ) {
                                     for (Map.Entry waitingElement : waitingMap.entrySet()) { 
                                        int keyId = (int)waitingElement.getKey(); 
                                        int value = (int)waitingElement.getValue();
                                        if(keyId == maxClusterId ) {
                                            flag = false; 
                                        }
                                        if((System.currentTimeMillis()-value) > 20) {
                                            waitingMap.remove(keyId);
                                        } 
                                     }
                                     if( flag == true ) {
                                         if(Math.random() < pMax) {
                                             //comeCloser();
                                            clusterId = maxClusterId;
                                            myState = State.INCLUSTER;     
                                            broadcastMessage(new Message(MessageType.Join, this));
                                         }
                                     } else {
                                         waitingMap.put(maxClusterId, System.currentTimeMillis());
                                     }                                     
                                }
                                //receiveMessages(aggregate, recieveMessage());
                                
                            } else if(myState == State.INCLUSTER) {
                                if(Math.random() < getLeavingProb(clusterSize)) {
                                    if(clusterId == this.getId()) {
                                        try {                                            
                                            Message leaveMsg = new Message(MessageType.Leave, this);
                                            leaveMsg.setData(new LeaveData(clusterId));
                                            broadcastMessage(leaveMsg);
                                            Thread.sleep(500);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(Task_Aggregation.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } else {
                                        Message clusterUpdateMsg = new Message(MessageType.Update, this);
                                        clusterUpdateMsg.setData(new ClusterUpdateData(clusterId, (clusterSize-1)));
                                        broadcastMessage(clusterUpdateMsg); 
                                    }
                                    myState = State.SEARCHING;
                                    clusterId = getId();
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




