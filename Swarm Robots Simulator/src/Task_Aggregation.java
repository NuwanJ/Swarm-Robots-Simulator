
/**
 *
 * @author Tharuka
 */
import communication.messageData.aggregation.PulseFBData;
import communication.messageData.aggregation.ClusterUpdateData;
import communication.messageData.aggregation.JoinData;
import communication.messageData.aggregation.PulseData;
import communication.messageData.aggregation.InfoData;
import communication.messageData.aggregation.LeaveData;
import communication.Message;
import helper.Utility;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;
import communication.MessageType;
import communication.MessageHandler;
import java.util.*;
import configs.Settings;

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
                        boolean amIJoining = false;
                        boolean pulseFlag = true;
                        int count = 0;
                        double joiningProbArray[] = new double[n];
                        int probSendersArray[] = new int[n];
                        int clusterIdArray[] = new int[n];

                        public Message waitForAMsg(MessageType expectingType, int receiverIndex, long milliSec) {
                            long currTime = System.currentTimeMillis();
                            boolean receivedMsg = false;
                            Message msg = null;
                            boolean chechCondition = true;

                            while (chechCondition) {
                                chechCondition = (System.currentTimeMillis() - currTime) < milliSec;
                                //System.out.printf("expecting type: %s: , reveived type: %s\n", expectingType, recieveMessage(receiverIndex).getType());
                                //for (int i = 0; i < n; i++) {

                                msg = recieveMessage(receiverIndex);
//                                    if (msg != null) {
//                                        System.out.println("Robot: " + this.getId() + "  received msg type-" + msg.getType().toString() + " expected msg type-"
//                                                + expectingType.toString());
//                                    }

                                if (msg != null && msg.getType() == expectingType) {
                                    //System.out.println("Robot: " + this.getId() + " - Received " + expectingType.toString() + " Msg -------------- GOT YOU----------------");
                                    receivedMsg = true;
                                    //break;
                                    chechCondition = false;
                                }
                                //}
                            }
                            if (!receivedMsg) {
                                msg = null;
                                //System.out.println("Robot: " + this.getId() + " - Didn't receive " + expectingType.toString() + " Msg -------------- GOTTA GO AGAIN---------------");
                                moveHoldFlag = true;
                            }
                            return msg;
                        }

                        public void goForSomeTime(long time) {
                            long currTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - currTime) < time) {
                                moveRandom();
                                avoidObstacles();
                            }
                        }

                        public void printRobotStatus() {
                            System.out.printf("Robot: %d | State: %s | ClusterId: %d | ClusterSize: %d \n",
                                    getId(), myState, clusterId, clusterSize);
                        }

                        public void receiverReset() {
                            for (int i = 0; i < n; i++) {
                                resetReceivers(i);
                            }
                        }

                        public void wait(int miliSec) {
                            long referenceTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - referenceTime) < miliSec) {
                                //do nothing
                            }
                        }

                        public void executeOnJoin(Message receiveMsg) {
                            JoinData newData = (JoinData) receiveMsg.getData();
                            if (this.getId() == newData.getreceiverId()) {
                                if (myState == State.SEARCHING) {
                                    myState = State.INCLUSTER;
                                }
                                System.out.println("Robot: " + this.getId() + " - Received Joining Msg");
                                clusterSize = clusterSize + 1;
                                if (clusterSize == noOfRobots) {
                                    myState = Robot.State.AGGREGATE;
                                    moveStop();
                                    //System.out.println("Robot: " + this.getId() + " - Aggregated");
                                }

                                long referenceTime = System.currentTimeMillis();
                                System.out.printf("Robot:{%d}- Sending Info Msg to Robot:{%d}\n", getId(), receiveMsg.getSender().getId());
                                while ((System.currentTimeMillis() - referenceTime) < 1000) {
                                    Message infoMsg = new Message(MessageType.Info, this);
                                    infoMsg.setData(new InfoData(receiveMsg.getSender().getId(),
                                            clusterId, clusterSize));
                                    broadcastMessage(infoMsg);
                                }

                                if (myState == Robot.State.INCLUSTER) {
                                    System.out.println("Robot: " + this.getId() + " - Sending update Msg");
                                    MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                }
                                printRobotStatus();
                                wait(1000);
                            }
                        }

                        public void executeOnPF(Message receiveMsg, int index) {
                            PulseFBData newData = (PulseFBData) receiveMsg.getData();
                            if (newData.getReceiverId() == this.getId()) {
                                //amIJoining = true;
                                System.out.println("Robot: " + this.getId() + " - Received Pulse FB Msg");
                                joiningProbArray[index] = newData.getJoiingProb();
                                probSendersArray[index] = newData.getSenderId();
                                clusterIdArray[index] = newData.getClusterID();
                            }
                            checkForJoining();
                        }

                        public void executeOnInfo(Message receiveMsg) {
                            InfoData newData = (InfoData) receiveMsg.getData();
                            if (this.getId() == newData.getreceiverId()) {
                                clusterSize = newData.getClusterSize();
                                System.out.println("Robot: " + this.getId() + " - Received Info Msg");
                                if (clusterSize == noOfRobots) {
                                    myState = Robot.State.AGGREGATE;
                                    moveStop();
                                    //System.out.println("Robot: " + this.getId() + " - Aggregated");
                                } else if (myState == Robot.State.SEARCHING) {
                                    myState = Robot.State.INCLUSTER;
                                }
                                printRobotStatus();
                            }
                        }

                        public void receiveMessages() {

                            Message receiveMsg = null;

                            for (int i = 0; i < n; i++) {
                                receiveMsg = recieveMessage(i);

                                if (receiveMsg != null) {

                                    if (receiveMsg.getType() == MessageType.Pulse) {
                                        moveStop();
                                        moveHoldFlag = false;
                                        PulseData newData = (PulseData) receiveMsg.getData();
                                        resetReceivers(i);
                                        //System.out.printf("Robot:{%d} cluster_id:%d Receiving clusterID:%d \n", getId(), clusterId, newData.getClusterId());
                                        if (newData.getClusterId() != clusterId) {
                                            if ((receiveMsg.getSender().getId() - this.getId()) > 0
                                                    || myState != Robot.State.SEARCHING) {
                                                long referenceTime = System.currentTimeMillis();
                                                System.out.printf("Robot:{%d}- Sending Pulse FeedBack Msg to Robot:{%d}\n", getId(), receiveMsg.getSender().getId());
                                                while ((System.currentTimeMillis() - referenceTime) < 100) {
                                                    MessageHandler.sendPulseFBMsg(this, clusterId,
                                                            receiveMsg.getSender().getId(), (double) clusterSize / noOfRobots, clusterSize);
                                                }

                                                Message rMsg = waitForAMsg(MessageType.Join, i, 2000);
                                                if (rMsg != null) {
                                                    executeOnJoin(rMsg);
                                                }
                                            } else {
                                                Message rMsg = waitForAMsg(MessageType.PulseFeedback, i, 1500);
                                                if (rMsg != null) {
                                                    executeOnPF(rMsg, i);
                                                }
                                            }

                                        }
                                    } //                                        else if (receiveMsg.getType() == MessageType.PulseFeedback) {
                                    //                                        PulseFBData newData = (PulseFBData) receiveMsg.getData();
                                    //                                        if (newData.getReceiverId() == this.getId()) {
                                    //                                            amIJoining = true;
                                    //                                            System.out.println("Robot: " + this.getId() + " - Received Pulse FB Msg");
                                    //                                            joiningProbArray[i] = newData.getJoiingProb();
                                    //                                            probSendersArray[i] = newData.getSenderId();
                                    //                                            clusterIdArray[i] = newData.getClusterID();
                                    //                                        }
                                    //                                    } 
                                    //                                    else if (receiveMsg.getType() == MessageType.GoAway) {
                                    //                                        GoAwayData newData = (GoAwayData) receiveMsg.getData();
                                    //                                        if (newData.getreceiverId() == this.getId()) {
                                    //                                            System.out.println("Robot: " + this.getId() + " - Received GoAway Msg");
                                    //                                            moveHoldFlag = true;
                                    //                                        }
                                    //                                    }
                                    //                                    else if (receiveMsg.getType() == MessageType.Join) {
                                    //                                        JoinData newData = (JoinData) receiveMsg.getData();
                                    //                                        if (this.getId() == newData.getreceiverId()) {
                                    //                                            if (myState == State.SEARCHING) {
                                    //                                                myState = State.INCLUSTER;
                                    //                                            }
                                    //                                            System.out.println("Robot: " + this.getId() + " - Received Joining Msg");
                                    //                                            clusterSize = clusterSize + 1;
                                    //                                            if (clusterSize == noOfRobots) {
                                    //                                                myState = Robot.State.AGGREGATE;
                                    //                                                moveStop();
                                    //                                                System.out.println("Robot: " + this.getId() + " - Aggregated");
                                    //                                            }
                                    //
                                    //                                            Message infoMsg = new Message(MessageType.Info, this);
                                    //                                            infoMsg.setData(new InfoData(receiveMsg.getSender().getId(),
                                    //                                                    clusterId, clusterSize));
                                    //                                            broadcastMessage(infoMsg);
                                    //                                            if (myState == Robot.State.INCLUSTER) {
                                    //                                                System.out.println("Robot: " + this.getId() + " - Sending update Msg");
                                    //
                                    //                                                MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                    //                                            }
                                    //                                            printRobotStatus();
                                    //                                        }
                                    //                                    } 
                                    else if (receiveMsg.getType() == MessageType.Update
                                            && myState == Robot.State.INCLUSTER) {
                                        ClusterUpdateData newData = (ClusterUpdateData) receiveMsg.getData();
                                        if (newData.getClusterID() == clusterId) {
                                            System.out.println("Robot: " + this.getId() + " - Received Update Msg");
                                            int updatedClusterSize = newData.getNewClusterSize();
                                            if (updatedClusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                                moveStop();
                                            } else if (updatedClusterSize == 1) {
                                                myState = Robot.State.SEARCHING;
                                                moveHoldFlag = true;
                                                //amIJoining = false;
                                            } else {
                                                clusterSize = updatedClusterSize;
                                            }
                                            System.out.println("Robot: " + this.getId() + " - Sending Update Msg");
                                            broadcastMessage(receiveMsg);
                                            printRobotStatus();

                                        }
                                    } //                                    else if (receiveMsg.getType() == MessageType.Info) {
                                    //                                        InfoData newData = (InfoData) receiveMsg.getData();
                                    //                                        if (this.getId() == newData.getreceiverId()) {
                                    //                                            System.out.println("Robot: " + this.getId() + " - Received Info Msg");
                                    //                                            if (myState == Robot.State.SEARCHING) {
                                    //                                                myState = Robot.State.INCLUSTER;
                                    //                                            }
                                    //                                            clusterSize = newData.getClusterSize();
                                    //                                            printRobotStatus();
                                    //                                        }
                                    //                                    } 
                                    else if (receiveMsg.getType() == MessageType.Leave) {
                                        LeaveData newData = (LeaveData) receiveMsg.getData();
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
                                            printRobotStatus();
                                        }
                                    }
                                    resetReceivers(i);
                                }

                            }

                        }

                        public void checkForJoining() {
                            //else if (amIJoining) {
                            //receiveMessages();
                            double pMax = 0.00;
                            int pMaxSenderId = 0;
                            int maxClusterId = 0;
                            pMax = Utility.getMax(joiningProbArray);
                            int pMaxId = Utility.getMaxProbSendersId(joiningProbArray, pMax);
                            pMaxSenderId = probSendersArray[pMaxId];
                            maxClusterId = clusterIdArray[pMaxId];

                            boolean flag = true;
                            if (pMax != 0) {
                                double randomProb = Math.random();
                                System.out.println("PMax: " + pMax + "/ Random probability: " + randomProb);
//                                        for (Map.Entry waitingElement : waitingMap.entrySet()) {
//                                            int keyId = (int) waitingElement.getKey();
//                                            long value = (long) waitingElement.getValue();
//                                            if ((System.currentTimeMillis() - value) > 2000) {
//                                                waitingMap.remove(keyId);
//                                            }
//                                        }
//                                        for (Map.Entry waitingElement : waitingMap.entrySet()) {
//                                            int keyId = (int) waitingElement.getKey();
//                                            if (keyId == maxClusterId) {
//                                                flag = false;
//                                                goForSomeTime(2000);
//                                                //MessageHandler.sendGoAwayMsg(this, pMaxSenderId);                                                 
//                                                moveHoldFlag = true;
//                                            }
//                                        }
                                //if (flag == true) {
                                if (randomProb < pMax) {
                                    //comeCloser();
                                    MessageHandler.sendJoinMsg(this, pMaxSenderId);
                                    Message msg = waitForAMsg(MessageType.Info, pMaxId, 2000);
                                    if (msg != null) {
                                        clusterId = maxClusterId;
                                        executeOnInfo(msg);
                                                    //receiveMessages();
                                        //myState = Robot.State.INCLUSTER; 
                                        //clusterSize = clusterSize + 1;      
                                    }
                                } else {
                                    goForSomeTime(1000);
                                    moveHoldFlag = true;
                                    //waitingMap.put(maxClusterId, System.currentTimeMillis());
                                }
                                //}
                            }

                            //}
                        }

                        @Override
                        public void loop() {
//                            System.out.printf("Robot: %d | State: %s | ClusterId: %d | ClusterSize: %d \n",
//                                    getId(), myState, clusterId, clusterSize);
//                            printRobotStatus();
                            if (myState == Robot.State.SEARCHING) {
                                receiveMessages();
                                clusterId = this.getId();
                                if (moveHoldFlag) {
                                    moveRandom();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                    receiveMessages();
                                    //joiningArray = new PulseFBData[n]; 
                                    Arrays.fill(joiningProbArray, 0.00);
                                    Arrays.fill(probSendersArray, 0);
                                    Arrays.fill(clusterIdArray, 0);
                                }

                               //checkForJoining(); 
                            } else if (myState == Robot.State.INCLUSTER) {
                                //for(int j=0; j<10; j++) {
                                receiveMessages();
                                //}
                                double leavingFactor = 0.15;
                                if (clusterId == getId()) {
                                    leavingFactor = 0.01;
                                }
                                double leavingProb = (1 - ((double) clusterSize / (noOfRobots))) * leavingFactor;
                                if (Math.random() < leavingProb) {
                                    clusterSize = clusterSize - 1;
                                    if (clusterId == this.getId()) {
                                        MessageHandler.sendLeaveMsg(this, clusterId);
                                    } else {
                                        MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                        //waitingMap.put(clusterId, System.currentTimeMillis());
                                    }
                                    long leavedTime = System.currentTimeMillis();
                                    while ((System.currentTimeMillis() - leavedTime) < 2500) {
                                        moveRandom();
                                        avoidObstacles();
                                    }
                                    moveHoldFlag = true;
                                    //amIJoining = false;
                                    myState = Robot.State.SEARCHING;
                                    clusterId = getId();

                                }
                                MessageHandler.sendPulseMsg(this, clusterId);
                            } else if (myState == Robot.State.AGGREGATE) {
                                MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                            }
                        }

                    }
                    );
                }
            }
        };

        Simulator simulator = new Simulator(swarm);

        simulator.start();
    }

}
