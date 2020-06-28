
import communication.Message;
import communication.MessageHandler;
import communication.MessageType;
import communication.messageData.aggregation.*;
import configs.Settings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import robot.Robot;
import helper.Utility;
import robot.console.Console;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Tharuka
 */
public class AggregationTesting {

    public static void main(String[] args) {
        // , {360, 200}, {700, 420}
        double[][] pos = {{100, 100}, {300, 450}, {700, 100}, {780, 460}, {50, 500}, {300, 230}, {550, 350}, {50, 300}, {900, 800}, {1300, 100}, {1100, 50}, {1450, 600}};
        int noOfRobots = 6;
        int n = Settings.NUM_OF_IR_SENSORS;
        

        Swarm swarm = new Swarm("Testing..") {
            int clusterCount = 0;
            @Override
            public void create() {

                for (int i = 0; i < noOfRobots; i++) {
                    join(new Robot() {

                        int clusterId = this.getId();
                        int clusterSize = 1;
                        boolean connectionCheck = true;
                        double joiningProbArray[] = new double[n];
                        Hashtable<Integer, Boolean> clusterMembers = new Hashtable<>();
                        Hashtable<Integer, Long> blackList = new Hashtable<>();
                        State previousState = State.SEARCHING;
                        int tempBLCluster = -1;
                        long previousUpdateID = -1;
                        
                        boolean SizeUpdateFlag = true;
                        
                        public void wait(int miliSec) {
                            long referenceTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - referenceTime) < miliSec) {
                                //do nothing
                            }
                        }

                        public void printRobotStatus() {
                            this.console.log(String.format("Robot:%d State:%s ClusterId:%d ClusterSize: %d",
                                    getId(), state, clusterId, clusterSize));
                        }

                        public void receiverReset() {
                            for (int i = 0; i < n; i++) {
                                resetReceivers(i);
                            }
                        }

                        public void updateBlackListTime() {
                            Set<Integer> keySet = blackList.keySet();
                            for (int keyId : keySet) {
                                long value = blackList.get(keyId).longValue();
                                if ((System.currentTimeMillis() - value) > 6500) {
                                    blackList.remove(keyId);
                                    break;
                                }
                            }
                            if (tempBLCluster != -1 && !blackList.containsKey(tempBLCluster)) {
                                tempBLCluster = -1;
                                state = previousState;
                                printRobotStatus();
                            }
                        }

                        @Override
                        public synchronized void processMessage(Message message, int sensorId) {

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case Pulse:
                                    //console.log(String.format("black list contains key ? %b  :  %b", blackList.containsKey(sender.getId()), !blackList.containsKey(sender.getId())));
                                    PulseData pulseData = (PulseData) message.getData();
                                    if (!isMyState(State.TRANSITION) && !blackList.containsKey(pulseData.getClusterId()) && !clusterMembers.containsKey(sender.getId())) {
                                        super.processMessage(message, sensorId);
                                        int idDif = sender.getId() - getId();
                                        if ((idDif > 0 || isMyState(State.INCLUSTER))) {
                                            moveStop();
                                            previousState = state;
                                            changeStateTo(State.TRANSITION);
                                            tempBLCluster = pulseData.getClusterId();
                                            blackList.put(tempBLCluster, System.currentTimeMillis());
                                            double joinProb = Utility.getJoiningProb(noOfRobots, clusterSize);
                                            MessageHandler.sendPulseFBMsg(this, clusterId,
                                                    sender.getId(), joinProb, clusterSize);
                                            receiverReset();
                                            printRobotStatus();
                                        }
                                        receiverReset();
                                    }
                                    break;

                                case PulseFeedback:
                                    PulseFBData pulseFBData = (PulseFBData) message.getData();
                                    if (isMyState(State.SEARCHING) && pulseFBData.getReceiverId() == getId() && !blackList.containsKey(pulseFBData.getClusterID())
                                            && !clusterMembers.containsKey(sender.getId())) {
                                        moveStop();
                                        changeStateTo(State.TRANSITION);
                                        super.processMessage(message, sensorId);

                                        double joiningProb = pulseFBData.getJoiingProb();
                                        if (joiningProb != 0) {
                                            double randomProb = Math.random();
                                            console.log("PMax: " + joiningProb + "| Random probability: " + randomProb);
                                            if (randomProb < joiningProb) {
                                                MessageHandler.sendJoinMsg(this, pulseFBData.getSenderId());
                                            } else {
                                                MessageHandler.sendGoAwayMsg(this, pulseFBData.getSenderId());
                                                if (pulseFBData.getClusterSize() > 1) {
                                                    tempBLCluster = pulseFBData.getClusterID();
                                                    blackList.put(tempBLCluster, System.currentTimeMillis());
                                                    state = previousState;
                                                } else {
                                                    tempBLCluster = pulseFBData.getClusterID();
                                                    blackList.put(tempBLCluster, System.currentTimeMillis());
                                                }
                                            }
                                        }
                                        receiverReset();
                                        printRobotStatus();
                                    }
                                    break;

                                case Join:
                                    JoinData joinData = (JoinData) message.getData();
                                    if (joinData.getreceiverId() == getId() && isMyState(State.TRANSITION) && !clusterMembers.containsKey(sender.getId())) {
                                        state = previousState;
                                        clearMessageBufferOut();
                                        super.processMessage(message, sensorId);
                                        clusterSize = clusterSize + 1;
                                        clusterMembers.put(sender.getId(), true); // make sure to remove when leaving //
                                        if (isMyState(State.SEARCHING)) {
                                            changeStateTo(State.INCLUSTER);
                                        }

                                        if (clusterSize == noOfRobots) {
                                            changeStateTo(State.AGGREGATE);
                                        }
                                        tempBLCluster = -1;
                                        MessageHandler.sendInfoMsg(this, sender.getId(), clusterId, clusterSize);
                                        //clusterMembers.put(sender.getId(), true);
                                        printRobotStatus();
                                        receiverReset();
                                    }
                                    break;

                                case GoAway:
                                    GoAwayData goAwayData = (GoAwayData) message.getData();
                                    if (goAwayData.getreceiverId() == getId() && isMyState(State.TRANSITION)) {
                                        if (previousState == State.SEARCHING) {
                                            blackList.put(sender.getId(), System.currentTimeMillis());
                                        }
                                        state = previousState;
                                        clearMessageBufferOut();
                                        super.processMessage(message, sensorId);
                                        printRobotStatus();
                                        receiverReset();
                                    }
                                    break;

                                case Info:
                                    InfoData infoData = (InfoData) message.getData();
                                    if (infoData.getreceiverId() == this.getId() && isMyState(State.TRANSITION) && clusterSize == 1) {
                                        state = previousState;
                                        clearMessageBufferOut();
                                        super.processMessage(message, sensorId);
                                        clusterSize = infoData.getClusterSize();
                                        clusterId = infoData.getClusterId();
                                        clusterMembers.put(sender.getId(), true);
                                        //come closer                                        
                                        if (clusterSize == noOfRobots) {
                                            changeStateTo(State.AGGREGATE);
                                        } else {
                                            changeStateTo(State.INCLUSTER);
                                        }
                                        tempBLCluster = -1;
                                        MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize, System.currentTimeMillis());
                                        receiverReset();
                                        printRobotStatus();
                                    }
                                    break;

                                case Update:
                                    ClusterUpdateData updateData = (ClusterUpdateData) message.getData();
                                    if (clusterId == updateData.getClusterID()) {
                                        if (previousUpdateID - updateData.getCurrentTime() == 0) {
                                            clearMessageBufferOut();
                                            receiverReset();
                                        } else {
                                            previousUpdateID = updateData.getCurrentTime();
                                            clearMessageBufferOut();
                                            super.processMessage(message, sensorId);
                                            clusterSize = updateData.getNewClusterSize();
                                            if (clusterSize == noOfRobots) {
                                                changeStateTo(State.AGGREGATE);
                                            } else if (clusterSize == 1) {
                                                changeStateTo(State.SEARCHING);
                                                //freeMove(2000);
                                            }
                                            broadcastMessage(message);
                                            receiverReset();
                                            printRobotStatus();
                                        }

                                    }
                                    break;

                                case Leave:
                                    LeaveData leaveData = (LeaveData) message.getData();
                                    if (clusterId == leaveData.getClusterID() && clusterSize != 1) {
                                        super.processMessage(message, sensorId);
                                        tempBLCluster = clusterId;
                                        blackList.put(tempBLCluster, System.currentTimeMillis());
                                        previousState = State.SEARCHING;
                                        clusterSize = 1;
                                        clusterMembers = new Hashtable<>();
                                        clusterId = getId();
                                        broadcastMessage(message);
                                    }
                                    break;

                                default:
                                    break;
                            }

                        }

                        @Override

                        public void loop() {
                            if (!blackList.isEmpty()) {
                                updateBlackListTime();
                            }
                            if (state.equals(State.SEARCHING) && !state.equals(State.TRANSITION)) {
                                moveRandom();
                                if (!state.equals(State.TRANSITION)) {
                                    // if min distance to an obstacle will met, then don't send pulse messages or receive messages
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                }
                            } else if (state.equals(State.INCLUSTER)) {
                                double leavingFactor;
                                if (clusterId == getId()) {
                                    
                                    if (SizeUpdateFlag) {
                                        clusterCount++;
                                        System.out.println("Cluster count: " + clusterCount);
                                        SizeUpdateFlag = false;
                                    }
                                    
                                    leavingFactor = 0.00085;
                                    double leavingProb = (1 - ((double) clusterSize / (noOfRobots))) * leavingFactor;
                                    double randomProb = Math.random();
                                    //System.out.printf("Robot:{%d} Leaving_Prob-%f   random_Prob-%f \n", getId(), leavingProb, randomProb);
                                    if (randomProb < leavingProb) {
                                        if (clusterId == this.getId()) {
                                            MessageHandler.sendLeaveMsg(this, clusterId);
                                        }
//                                    else {
//                                        clusterSize = clusterSize - 1;
//                                        console.log("I'm leaving this cluster...");
//                                        MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize, System.currentTimeMillis());
//                                    }

                                        tempBLCluster = clusterId;
                                        blackList.put(tempBLCluster, System.currentTimeMillis());
                                        previousState = State.SEARCHING;
                                        clusterSize = 1;
                                        clusterMembers = new Hashtable<>();
                                        clusterId = getId();
                                        SizeUpdateFlag = true;
                                        //printRobotStatus();
                                    }
                                }
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
