/**
 *
 * @author Tharuka
 */

import communication.aggregation.*;
import communication.Message;
import utility.Utility;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;
import communication.MessageType;
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
                        PulseFBData joiningArray[] = new PulseFBData[4];                        
                        int noOfRobots = 2; // change this
                        
                        public void receiveMessages(Message receiveMsg) {   
                            int i = 0;
                            //for all receivers iterate(i)--> {
                                if (receiveMsg != null) { 
                                    if(receiveMsg.getType() == MessageType.Pulse) {
                                         Message leaveMsg = new Message(MessageType.PulseFeedback, this);
                                         leaveMsg.setData(new PulseFBData(clusterId, this.getId(),
                                                 receiveMsg.getSender().getId(), Utility.getJoiningProb(clusterSize)));
                                         broadcastMessage(leaveMsg);
                                         moveStop();
                                    }
                                    else if (receiveMsg.getType() == MessageType.PulseFeedback) {     
                                        PulseFBData newData = (PulseFBData)receiveMsg.getData();
                                        if(newData.getReceiverId() == this.getId()){
                                            joiningArray[i] = newData;   
                                        }                                                                            
                                    }
                                    else if (receiveMsg.getType() == MessageType.Join) {
                                        JoinData newData = (JoinData)receiveMsg.getData();                                        
                                        if(this.getId() == newData.getreceiverId()) {
                                            clusterSize = clusterSize + 1;
                                            if(clusterSize == noOfRobots) {
                                                myState = State.AGGREGATE;
                                                moveStop();
                                            }
                                            Message infoMsg = new Message(MessageType.Info, this);
                                            infoMsg.setData(new InfoData(receiveMsg.getSender().getId(),
                                                                    clusterId, clusterSize));
                                            broadcastMessage(infoMsg);                                           
                                            if(myState == State.INCLUSTER) {
                                                Message clusterUpdateMsg = new Message(MessageType.Update, this);
                                                clusterUpdateMsg.setData(new ClusterUpdateData(clusterId, clusterSize));
                                                broadcastMessage(clusterUpdateMsg); 
                                            }
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Update && myState == State.INCLUSTER) {
                                        ClusterUpdateData newData = (ClusterUpdateData)receiveMsg.getData();  
                                        if (newData.getClusterID() == clusterId) {
                                            int updatedClusterSize = newData.getNewClusterSize();
                                            if(updatedClusterSize == noOfRobots) {
                                                myState = State.AGGREGATE;
                                                moveStop();
                                            } else if(updatedClusterSize == 1) {
                                                myState = State.SEARCHING;
                                            } else {
                                                clusterSize = updatedClusterSize;
                                            }
                                            broadcastMessage(receiveMsg);
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Info) {
                                        InfoData newData = (InfoData)receiveMsg.getData();
                                        if (this.getId() == newData.getreceiverId()) {
                                            if (myState == State.SEARCHING) {
                                                myState = State.INCLUSTER;
                                            }
                                            clusterSize = newData.getClusterSize();
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Leave) {
                                        LeaveData newData = (LeaveData)receiveMsg.getData();
                                        if (clusterId == newData.getClusterID()) {
                                            clusterId = this.getId();
                                            myState = State.SEARCHING;
                                            moveRandom();
                                            avoidObstacles();
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(Task_Aggregation.class.getName()).
                                                        log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                    
                                }
                             //}   
                        }
                        
                        @Override
                        public void loop() {
                            if(myState == State.SEARCHING) {
                                moveRandom();
                                avoidObstacles();
                                broadcastMessage(new Message(MessageType.Pulse, this));
                                joiningArray = new PulseFBData[4];
                                try {                                    
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(Task_Aggregation.class.getName()).log(Level.SEVERE, null, ex);
                                } 
                                PulseFBData probMaxData;
                                double pMax = 0.00; 
                                int maxClusterId = 0;
                                try {
                                    probMaxData = Utility.getMax(joiningArray);
                                    pMax = probMaxData.getJoiingProb();                             
                                    maxClusterId = probMaxData.getClusterID(); 
                                } catch (NullPointerException ex) {
                                    Logger.getLogger(ex.toString());
                                }
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
                                            Message joingMsg = new Message(MessageType.Join, this);
//                                            joingMsg.setData(new JoinData(probMaxData.getSenderId()));
                                            broadcastMessage(joingMsg);                                           
                                         }
                                     } else {
                                         waitingMap.put(maxClusterId, System.currentTimeMillis());
                                     }                                     
                                }
                                //receiveMessages(aggregate, recieveMessage());
                                
                            } else if(myState == State.INCLUSTER) {
                                if(Math.random() < Utility.getLeavingProb(clusterSize)) {
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




