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
import utility.Settings;

public class Task_Aggregation {  
         
      public static void main(String[] args) {              
        
            Swarm swarm = new Swarm("Aggregate") {
            @Override
            public void create() {
                
                for (int i = 0; i < 3; i++) {                  
                    
                    join(new Robot() {
                        
                        Robot.State myState = Robot.State.SEARCHING;
                        int clusterId = getId();
                        int clusterSize = 1;
                        HashMap<Integer, Long> waitingMap = new HashMap<>();
                        int n = Settings.NUM_OF_IR_SENSORS;
                        PulseFBData joiningArray[] = new PulseFBData[n];                        
                        int noOfRobots = 3; // change this
                        boolean moveHoldFlag = true;
                        int count = 0;
                        double joiningProbArray[] = new double[n];
                        int probSendersArray[] = new int[n];
                        int clusterIdArray[] = new int[n];                        
                        
                        public void receiveMessages() {   
                           
                            Message receiveMsg = null;                           
                            
                            for (int i = 0; i < n; i++) {
                                receiveMsg = recieveMessage(i);
                                
                                if (receiveMsg != null) { 
                                    
                                    if(receiveMsg.getType() == MessageType.Pulse) {
                                        moveStop();
                                        moveHoldFlag = false;
                                        PulseData newData = (PulseData)receiveMsg.getData();
                                        if(newData.getClusterId() != clusterId) { 
                                            if(receiveMsg.getSender().getId() > this.getId() || 
                                                myState != Robot.State.SEARCHING) {                                             
                                                MessageHandler.sendPulseFBMsg(this, clusterId, 
                                                receiveMsg.getSender().getId(), (double)clusterSize/noOfRobots);
                                                long waitingTime = System.currentTimeMillis();
//                                                while((System.currentTimeMillis() - waitingTime) < 2) {}
                                                moveHoldFlag = true;
                                            } else {
//                                                while(true) {
//                                                    if(recieveMessage(i).getType() == MessageType.PulseFeedback) {
//                                                        break;
//                                                    }
//                                                }
                                            } 
                                            
                                        }                                                                                                                 
                                    }
                                    else if (receiveMsg.getType() == MessageType.PulseFeedback) {                                       
                                        PulseFBData newData = (PulseFBData)receiveMsg.getData();
                                        if(newData.getReceiverId() == this.getId()){  
                                            moveStop();
                                            moveHoldFlag = false;
                                            System.out.println("Robot: " + this.getId() + " - Received Pulse FB Msg");
                                            joiningProbArray[i] = newData.getJoiingProb();
                                            probSendersArray[i] = newData.getSenderId();
                                            clusterIdArray[i] = newData.getClusterID();                                            
                                        }                                                                            
                                    }
                                    else if (receiveMsg.getType() == MessageType.GoAway) {                                        
                                        GoAwayData newData = (GoAwayData)receiveMsg.getData();
                                        if(newData.getreceiverId() == this.getId()){
                                            System.out.println("Robot: " + this.getId() + " - Received GoAway Msg");
                                            moveHoldFlag = true;
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Join) {   
                                        JoinData newData = (JoinData)receiveMsg.getData(); 
                                        if(this.getId() == newData.getreceiverId()) {
                                             if(myState == State.SEARCHING) {
                                                myState = State.INCLUSTER;
                                             }                                        
                                            System.out.println("Robot: " + this.getId() + " - Received Joining Msg");                                        
                                            clusterSize = clusterSize + 1;
                                            if(clusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                                moveStop();
                                                System.out.println("Robot: " + this.getId() + " - Aggregated");
                                            }
                                            
                                            Message infoMsg = new Message(MessageType.Info, this);
                                            infoMsg.setData(new InfoData(receiveMsg.getSender().getId(),
                                                                    clusterId, clusterSize));
                                            broadcastMessage(infoMsg);                                           
                                            if(myState == Robot.State.INCLUSTER) {
                                                System.out.println("Robot: " + this.getId() + " - Sending update Msg"); 
                                                
                                                MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                            }
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Update &&
                                            myState == Robot.State.INCLUSTER) {                                       
                                        ClusterUpdateData newData = (ClusterUpdateData)receiveMsg.getData();  
                                        if (newData.getClusterID() == clusterId) {
                                            System.out.println("Robot: " + this.getId() + " - Received Update Msg");
                                            int updatedClusterSize = newData.getNewClusterSize();
                                            if(updatedClusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                                moveStop();
                                            } else if(updatedClusterSize == 1) {
                                                myState = Robot.State.SEARCHING;
                                                 moveHoldFlag = true;
                                            } else {
                                                clusterSize = updatedClusterSize;
                                            }
                                            System.out.println("Robot: " + this.getId() + " - Sending Update Msg");
                                            broadcastMessage(receiveMsg);
                                        }
                                        
                                    }
                                    else if (receiveMsg.getType() == MessageType.Info) {                                        
                                        InfoData newData = (InfoData)receiveMsg.getData();
                                        if (this.getId() == newData.getreceiverId()) {
                                            System.out.println("Robot: " + this.getId() + " - Received Info Msg");
                                            if (myState == Robot.State.SEARCHING) {
                                                myState = Robot.State.INCLUSTER;
                                            }
                                            clusterSize = newData.getClusterSize();
                                        }
                                    }
                                    else if (receiveMsg.getType() == MessageType.Leave) {                                       
                                        LeaveData newData = (LeaveData)receiveMsg.getData();
                                        if (clusterId == newData.getClusterID()) {
                                            System.out.println("Robot: " + this.getId() + " - Received Leaving Msg");
                                            clusterId = this.getId();
                                            myState = Robot.State.SEARCHING;
                                            moveHoldFlag = true;
//                                            moveRandom();
//                                            avoidObstacles();
//                                            try {
//                                                Thread.sleep(2000);
//                                            } catch (InterruptedException ex) {
//                                                Logger.getLogger(Task_Aggregation.class.getName()).
//                                                        log(Level.SEVERE, null, ex);
//                                            }
                                        }
                                    }
                                   resetReceivers(i);
                                }
                                
                             }   
                             
                        }
                        
                        @Override
                        public void loop() {    
                            System.out.printf("Robot: %d | State: %s | ClusterId: %d | ClusterSize: %d \n",
                                    getId(), myState, clusterId, clusterSize);
                            if(myState == Robot.State.SEARCHING) {
                                if(moveHoldFlag) {
                                    moveRandom();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                    //for(int j=0; j<10; j++) {
                                        receiveMessages();  
                                    //}
                                    
                                    //joiningArray = new PulseFBData[n]; 
                                    Arrays.fill(joiningProbArray, 0.00);
                                    Arrays.fill(probSendersArray, 0);
                                    Arrays.fill(clusterIdArray, 0);                                                                                                    
                                } else {  
                                    //for(int j=0; j<10; j++) {
                                        receiveMessages();  
                                    //}                                 
                                    double pMax = 0.00;  
                                    int pMaxSenderId = 0;
                                    int maxClusterId = 0;                                
                                    pMax = Utility.getMax(joiningProbArray);   
                                    int pMaxId = Utility.getMaxProbSendersId(joiningProbArray, pMax);
                                    pMaxSenderId = probSendersArray[pMaxId];
                                    maxClusterId = clusterIdArray[pMaxId]; 
                                   
                                    boolean flag = true;
                                    if( pMax != 0 ) {
                                        double randomProb = Math.random();
                                        System.out.println("PMax: " + pMax + "/ Random probability: " + randomProb);
                                        for (Map.Entry waitingElement : waitingMap.entrySet()) { 
                                            int keyId = (int)waitingElement.getKey(); 
                                            long value = (long)waitingElement.getValue();
                                            if((System.currentTimeMillis()-value) > 5) {
                                                waitingMap.remove(keyId);
                                            } 
                                        }
                                        for (Map.Entry waitingElement : waitingMap.entrySet()) { 
                                            int keyId = (int)waitingElement.getKey();                                             
                                            if(keyId == maxClusterId ) {
                                                flag = false; 
                                                MessageHandler.sendGoAwayMsg(this, pMaxSenderId);                                                 
                                                moveHoldFlag = true;
                                            }                                            
                                        }
                                        if( flag == true ) {
                                            if(randomProb < pMax) {
                                                 //comeCloser();
                                                clusterId = maxClusterId;
                                                myState = Robot.State.INCLUSTER; 
                                                clusterSize = clusterSize + 1;
                                                moveStop();
                                                moveHoldFlag = false;
                                                MessageHandler.sendJoinMsg(this, pMaxSenderId);                                                
                                            }
                                        } else {
                                            MessageHandler.sendGoAwayMsg(this, pMaxSenderId);
                                            waitingMap.put(maxClusterId, System.currentTimeMillis());
                                        }                                     
                                    }
                                    
                                }
                            } 
                            
                            else if(myState == Robot.State.INCLUSTER) {
                                //for(int j=0; j<10; j++) {
                                    receiveMessages();  
                                //}
                                
                              
                                double leavingProb = (1-((double)clusterSize/(noOfRobots)))*0.2;
                                if(Math.random() < leavingProb) {  
                                    clusterSize = clusterSize - 1;                                    
                                    if(clusterId == this.getId()) {                                       
                                        MessageHandler.sendLeaveMsg(this, clusterId);
                                    } else {                                        
                                        MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                        waitingMap.put(clusterId, System.currentTimeMillis());                                        
                                    }
                                    long leavedTime = System.currentTimeMillis();
//                                    while((System.currentTimeMillis() - leavedTime) < 3) {
//                                        moveRandom();
//                                        avoidObstacles(); 
//                                    }
                                    moveHoldFlag = true;
                                    myState = Robot.State.SEARCHING;
                                    clusterId = getId();
                                    
                                }
                                MessageHandler.sendPulseMsg(this, clusterId);
                            }  
                            
                            else if(myState == Robot.State.INCLUSTER) {
                                 MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
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




