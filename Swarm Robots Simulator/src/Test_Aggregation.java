
/**
 *
 * @author Tharuka
 */
/*
import communication.aggregation.*;
import communication.Message;
import utility.Utility;
import robot.Robot;
import swarm.Swarm;
import view.Simulator;
import communication.MessageType;
import java.util.*;
import utility.Settings;

public class Test_Aggregation {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Aggregate") {
            @Override
            public void create() {

                for (int i = 0; i < 2; i++) {

                    join(new Robot() {

                        Robot.State myState = Robot.State.SEARCHING;
                        int clusterId = this.getId();
                        int clusterSize = 1;
                        HashMap<Integer, Long> waitingMap = new HashMap<>();
                        int n = Settings.NUM_OF_IR_SENSORS;
                        PulseFBData joiningArray[] = new PulseFBData[n];
                        int noOfRobots = 2; // change this
                        boolean moveHoldFlag = true;
                        boolean amIJoining = false;
                        boolean pulseFlag = true;
                        boolean checkMsgFlag = true;
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
                            console.log(String.format("Robot: %d | State: %s | ClusterId: %d | ClusterSize: %d \n",
                                    getId(), myState, clusterId, clusterSize));
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
                                if (myState == Robot.State.SEARCHING) {
                                    myState = Robot.State.INCLUSTER;
                                }
                                console.log("Received Joining Msg");
                                clusterSize = clusterSize + 1;
                                if (clusterSize == noOfRobots) {
                                    myState = Robot.State.AGGREGATE;
                                    moveStop();
                                    //System.out.println("Robot: " + this.getId() + " - Aggregated");
                                }

                                long referenceTime = System.currentTimeMillis();
                                console.log(String.format("Sending Info Msg to Robot:{%d}", receiveMsg.getSender().getId()));
                                while ((System.currentTimeMillis() - referenceTime) < 1000) {
                                    Message infoMsg = new Message(MessageType.Info, this);
                                    infoMsg.setData(new InfoData(receiveMsg.getSender().getId(),
                                            clusterId, clusterSize));
                                    broadcastMessage(infoMsg);
                                }

                                if (myState == Robot.State.INCLUSTER) {
                                    console.log("Sending update Msg");
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
                                console.log("Received Pulse FB Msg");
                                joiningProbArray[index] = newData.getJoiingProb();
                                probSendersArray[index] = newData.getSenderId();
                                clusterIdArray[index] = newData.getClusterID();
                            }

                        }

                        public void executeOnInfo(Message receiveMsg) {
                            InfoData newData = (InfoData) receiveMsg.getData();
                            if (this.getId() == newData.getreceiverId()) {
                                clusterId = newData.getClusterId();
                                clusterSize = newData.getClusterSize();
                                console.log("Received Info Msg");
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

                                if (receiveMsg != null && checkMsgFlag) {

                                    if (receiveMsg.getType() == MessageType.Pulse) {
                                        moveStop();
                                        moveHoldFlag = false;
                                        PulseData newData = (PulseData) receiveMsg.getData();
                                        resetReceivers(i);
                                        //System.out.printf("Robot:{%d} cluster_id:%d Receiving clusterID:%d \n", getId(), clusterId, newData.getClusterId());
                                        if (newData.getClusterId() != clusterId) {
                                            console.log(String.format("Received Pulse Msg from Robot:{%d}", receiveMsg.getSender().getId()));
                                            if ((receiveMsg.getSender().getId() - this.getId()) > 0
                                                    || myState != Robot.State.SEARCHING) {

                                                //checkMsgFlag = false;
                                                receiverReset();
                                                long referenceTime = System.currentTimeMillis();
                                                console.log(String.format("Sending Pulse FeedBack Msg to Robot:{%d}", receiveMsg.getSender().getId()));
                                                while ((System.currentTimeMillis() - referenceTime) < 100) {
                                                    MessageHandler.sendPulseFBMsg(this, clusterId,
                                                            receiveMsg.getSender().getId(), (double) clusterSize / noOfRobots, clusterSize);
                                                }

                                                Message rMsg = waitForAMsg(MessageType.Join, i, 2000);
                                                if (rMsg != null) {
                                                    checkMsgFlag = false;
                                                    executeOnJoin(rMsg);
                                                }
                                            } else {
                                                checkMsgFlag = false;
                                                Arrays.fill(joiningProbArray, 0.00);
                                                Arrays.fill(probSendersArray, 0);
                                                Arrays.fill(clusterIdArray, 0);
                                                //receiverReset();
                                                for (int j = 0; j < n; j++) {
                                                    Message rMsg = waitForAMsg(MessageType.PulseFeedback, i, 100);
                                                    if (rMsg != null) {
                                                        executeOnPF(rMsg, i);
                                                    }
                                                }
                                                checkForJoining();
                                            }

                                        }
                                    } else if (receiveMsg.getType() == MessageType.Update
                                            && myState == Robot.State.INCLUSTER) {
                                        ClusterUpdateData newData = (ClusterUpdateData) receiveMsg.getData();
                                        if (newData.getClusterID() == clusterId) {
                                            console.log("Received Update Msg");
                                            int updatedClusterSize = newData.getNewClusterSize();
                                            if (updatedClusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                                moveStop();
                                            } else if (updatedClusterSize == 1) {
                                                myState = Robot.State.SEARCHING;
                                                clusterId = this.getId();
                                                moveHoldFlag = true;
                                                //amIJoining = false;
                                            } else {
                                                clusterSize = updatedClusterSize;
                                            }
                                            console.log("Forwaring Update Msg");
                                            printRobotStatus();
                                            long referenceTime = System.currentTimeMillis();
                                            while ((System.currentTimeMillis() - referenceTime) < 400) {
                                                broadcastMessage(receiveMsg);
                                            }

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
                                            console.log("Received Leaving Msg");
                                            clusterId = this.getId();
                                            myState = Robot.State.SEARCHING;
                                            moveHoldFlag = true;
                                            printRobotStatus();
                                        }
                                    }
                                    //resetReceivers(i);
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
                            //System.out.printf("Robot:{%d} went here MaxProb-%f\n", getId(), pMax);
                            if (pMax != 0) {
                                double randomProb = Math.random();
                                console.log("PMax: " + pMax + "/ Random probability: " + randomProb);
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
                                    Message msg = waitForAMsg(MessageType.Info, pMaxId, 2500);
                                    if (msg != null) {
                                        checkMsgFlag = false;
                                        //clusterId = maxClusterId;
                                        executeOnInfo(msg);
                                        //receiveMessages();
                                        //myState = Robot.State.INCLUSTER; 
                                        //clusterSize = clusterSize + 1;      
                                    }
                                } else {
                                    goForSomeTime(500);
                                    moveHoldFlag = true;
                                    //waitingMap.put(maxClusterId, System.currentTimeMillis());
                                }
                                //}
                            }

                            //}
                        }

                        @Override
                        public void loop() {//                            
//                            printRobotStatus();
                            if (myState == Robot.State.SEARCHING) {
                                checkMsgFlag = true;
                                if (moveHoldFlag) {
                                    moveRandom();
                                    receiveMessages();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                    receiveMessages();
                                }

                                //checkForJoining();                                 
                            } else if (myState == Robot.State.INCLUSTER) {
                                //for(int j=0; j<10; j++) {
                                checkMsgFlag = true;
                                receiveMessages();
                                //}
                                double leavingFactor = 0.01;
                                if (clusterId == getId()) {
                                    leavingFactor = 0.001;
                                }
                                double leavingProb = (1 - ((double) clusterSize / (noOfRobots))) * leavingFactor;
                                double randomProb = Math.random();
                                console.log(String.format("Leaving_Prob-%f   random_Prob-%f \n", leavingProb, randomProb));
                                if (randomProb < leavingProb) {
                                    clusterSize = clusterSize - 1;
                                    long referenceTime = System.currentTimeMillis();
                                    while ((System.currentTimeMillis() - referenceTime) < 400) {
                                        if (clusterId == this.getId()) {
                                            console.log("Sending Leave Cluster Disclosure Msg");
                                            MessageHandler.sendLeaveMsg(this, clusterId);
                                        } else {
                                            console.log("Sending Me Leaving and Cluster Update Msg");
                                            MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                            //waitingMap.put(clusterId, System.currentTimeMillis());
                                        }
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
                                checkMsgFlag = true;
                                MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                            }
                        }

                    }
                    );
                }
            }
        };

        Simulator simulator = new Simulator(swarm);

        simulator.run();
    }

}
*/