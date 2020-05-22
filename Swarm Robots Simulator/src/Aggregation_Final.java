
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

public class Aggregation_Final {

    public static void main(String[] args) {

        double[][] pos = {{100, 100}, {300, 450}, {700, 100}};

        Swarm swarm = new Swarm("Aggregate") {

            @Override
            public void create() {

                for (int i = 0; i < 3; i++) {

                    join(new Robot(pos[i][0], pos[i][1]) {

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
                        int clusterSizeArray[] = new int[n];

                        public Message waitForAMsg(MessageType expectingType, int receiverIndex, long milliSec) {
                            long currTime = System.currentTimeMillis();
                            boolean receivedMsg = false;
                            Message msg = null;
                            boolean chechCondition = true;

                            while (chechCondition) {
                                chechCondition = (System.currentTimeMillis() - currTime) < milliSec;
                                msg = recieveMessage(receiverIndex);
                                if (msg != null && msg.getType() == expectingType) {
                                    receivedMsg = true;
                                    chechCondition = false;
                                }
                            }
                            if (!receivedMsg) {
                                msg = null;
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
                                if (myState == Robot.State.SEARCHING) {
                                    myState = Robot.State.INCLUSTER;
                                }
                                System.out.println("Robot: " + this.getId() + " - Received Joining Msg");
                                clusterSize = clusterSize + 1;
                                if (clusterSize == noOfRobots) {
                                    myState = Robot.State.AGGREGATE;
                                    moveStop();
                                }

                                long referenceTime = System.currentTimeMillis();
                                System.out.println("Robot: " + this.getId() + " - Sending update Msg");

                                while ((System.currentTimeMillis() - referenceTime) < 400) {
                                    MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                }

                                printRobotStatus();
                                wait(1000);
                            }
                        }

                        public void executeOnPF(Message receiveMsg, int index) {
                            PulseFBData newData = (PulseFBData) receiveMsg.getData();
                            if (newData.getReceiverId() == this.getId()) {
                                System.out.println("Robot: " + this.getId() + " - Received Pulse FB Msg");
                                joiningProbArray[index] = newData.getJoiingProb();
                                probSendersArray[index] = newData.getSenderId();
                                clusterIdArray[index] = newData.getClusterID();
                                clusterSizeArray[index] = newData.getClusterSize();
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
                                        if (newData.getClusterId() != clusterId) {
                                            System.out.printf("Robot:{%d}- Received Pulse Msg from Robot:{%d}\n", getId(), receiveMsg.getSender().getId());
                                            if ((receiveMsg.getSender().getId() - this.getId()) > 0
                                                    || myState != Robot.State.SEARCHING) {

                                                receiverReset();
                                                long referenceTime = System.currentTimeMillis();
                                                System.out.printf("Robot:{%d}- Sending Pulse FeedBack Msg to Robot:{%d}\n", getId(), receiveMsg.getSender().getId());
                                                while ((System.currentTimeMillis() - referenceTime) < 20) {
                                                    MessageHandler.sendPulseFBMsg(this, clusterId,
                                                            receiveMsg.getSender().getId(), (double) clusterSize / noOfRobots, clusterSize);
                                                }
                                                //System.out.printf("Joining wait reciver index : %d\n", i);
                                                for (int j = 0; j < n; j++) {
                                                    Message rMsg = waitForAMsg(MessageType.Join, j, 300);
                                                    if (rMsg != null) {
                                                        checkMsgFlag = false;
                                                        executeOnJoin(rMsg);
                                                    }
                                                }

                                            } else {
                                                checkMsgFlag = false;
                                                Arrays.fill(joiningProbArray, 0.00);
                                                Arrays.fill(probSendersArray, 0);
                                                Arrays.fill(clusterIdArray, 0);
                                                Arrays.fill(clusterSizeArray, 0);
                                                //receiverReset();
                                                for (int j = 0; j < n; j++) {
                                                    Message rMsg = waitForAMsg(MessageType.PulseFeedback, j, 200);
                                                    if (rMsg != null) {
                                                        executeOnPF(rMsg, j);
                                                    }
                                                }
                                                checkForJoining();
                                            }

                                        }
                                    } else if (receiveMsg.getType() == MessageType.Update
                                            && myState == Robot.State.INCLUSTER) {
                                        ClusterUpdateData newData = (ClusterUpdateData) receiveMsg.getData();
                                        if (newData.getClusterID() == clusterId) {
                                            System.out.println("Robot: " + this.getId() + " - Received Update Msg");
                                            int updatedClusterSize = newData.getNewClusterSize();
                                            if (updatedClusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                                clusterSize = updatedClusterSize;
                                                moveStop();
                                            } else if (updatedClusterSize == 1) {
                                                myState = Robot.State.SEARCHING;
                                                clusterId = this.getId();
                                                clusterSize = updatedClusterSize;
                                                moveHoldFlag = true;
                                            } else {
                                                clusterSize = updatedClusterSize;
                                            }
                                            System.out.println("Robot: " + this.getId() + " - Forwaring Update Msg");
                                            printRobotStatus();
                                            long referenceTime = System.currentTimeMillis();
                                            while ((System.currentTimeMillis() - referenceTime) < 400) {
                                                broadcastMessage(receiveMsg);
                                            }

                                        }
                                    } else if (receiveMsg.getType() == MessageType.Leave) {
                                        LeaveData newData = (LeaveData) receiveMsg.getData();
                                        if (clusterId == newData.getClusterID()) {
                                            System.out.println("Robot: " + this.getId() + " - Received Leaving Msg");
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
                            double pMax = 0.00;
                            int pMaxSenderId = 0;
                            int maxClusterId = 0;
                            int maxClusterSize = 0;
                            pMax = Utility.getMax(joiningProbArray);
                            int pMaxId = Utility.getMaxProbSendersId(joiningProbArray, pMax);
                            pMaxSenderId = probSendersArray[pMaxId];
                            maxClusterId = clusterIdArray[pMaxId];
                            maxClusterSize = clusterSizeArray[pMaxId];

                            if (pMax != 0) {
                                double randomProb = Math.random();
                                System.out.println("PMax: " + pMax + "/ Random probability: " + randomProb);

                                if (randomProb < pMax) {
                                    //comeCloser();      
                                    //long referenceTime = System.currentTimeMillis();
                                    System.out.printf("Robot:{%d}- Sending Join Msg   Sender ID: %d\n", getId(), pMaxSenderId);
                                    //System.out.printf("Joining sender reciver index : %d\n", pMaxId);
                                    //while ((System.currentTimeMillis() - referenceTime) < 600) {
                                    MessageHandler.sendJoinMsg(this, pMaxSenderId);
                                    //}  
                                    wait(2000);
                                    clusterId = maxClusterId;
                                    clusterSize = maxClusterSize + 1;
                                    if (clusterSize == noOfRobots) {
                                        myState = Robot.State.AGGREGATE;
                                    } else {
                                        myState = Robot.State.INCLUSTER;
                                    }
                                    printRobotStatus();
                                }

                            }

                        }

                        @Override
                        public void loop() {

                            if (myState == Robot.State.SEARCHING) {
                                checkMsgFlag = true;
                                if (moveHoldFlag) {
                                    moveRandom();
                                    receiveMessages();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                    receiveMessages();
                                }

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
                                //System.out.printf("Robot:{%d} Leaving_Prob-%f   random_Prob-%f \n", getId(), leavingProb, randomProb);
                                if (randomProb < leavingProb) {
                                    clusterSize = clusterSize - 1;
                                    long referenceTime = System.currentTimeMillis();
                                    if (clusterId == this.getId()) {
                                        System.out.printf("Robot:{%d}- Sending Leave Cluster Disclosure Msg\n", getId());
                                    } else {
                                        System.out.printf("Robot:{%d}- Sending Me Leaving and Cluster Update Msg\n", getId());
                                    }
                                    while ((System.currentTimeMillis() - referenceTime) < 400) {
                                        if (clusterId == this.getId()) {
                                            MessageHandler.sendLeaveMsg(this, clusterId);
                                        } else {
                                            MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize);
                                        }
                                    }

                                    long leavedTime = System.currentTimeMillis();
                                    while ((System.currentTimeMillis() - leavedTime) < 2500) {
                                        moveRandom();
                                        avoidObstacles();
                                    }
                                    moveHoldFlag = true;
                                    myState = Robot.State.SEARCHING;
                                    clusterId = getId();
                                    printRobotStatus();

                                }
                                //MessageHandler.sendPulseMsg(this, clusterId);
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
