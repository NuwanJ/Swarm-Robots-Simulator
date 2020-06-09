
import communication.Message;
import communication.MessageHandler;
import communication.MessageType;
import communication.messageData.aggregation.*;
import communication.messageData.general.DistanceData;
import configs.Settings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import robot.Robot;
import robot.console.Console;
import swarm.Swarm;
import view.Simulator;

/**
 *
 * @author Tharuka
 */
public class Aggregation_Working {

    public static void main(String[] args) {

        double[][] pos = {{100, 100}, {350, 450}, {700, 100}, {550, 200}, {150, 400}};

        Swarm swarm = new Swarm("Testing..") {

            @Override
            public void create() {

                for (int i = 0; i < 3; i++) {
                    join(new Robot(pos[i][0], pos[i][1]) {
                        Robot.State myState = Robot.State.SEARCHING;
                        int clusterId = this.getId();
                        int clusterSize = 1;
                        //HashMap<Integer, Long> waitingMap = new HashMap<>();
                        int n = Settings.NUM_OF_IR_SENSORS;
                        PulseFBData joiningArray[] = new PulseFBData[n];
                        int noOfRobots = 3; // change this
                        boolean moveHoldFlag = true;
                        boolean amIJoining = false;
                        boolean checkMsgFlag = true;
                        int count = 0;
                        double joiningProbArray[] = new double[n];
                        int probSendersArray[] = new int[n];
                        int clusterIdArray[] = new int[n];
                        int clusterSizeArray[] = new int[n];
                        HashMap<Integer, Boolean> clusterMembers = new HashMap<>();
                        boolean pulseFlag = true;
                        boolean pulseFBFlag = true;
                        boolean joinFlag = true;
                        boolean infoFlag = true;
                        boolean goAwayFlag = true;
                        boolean aggreeCounterFlag = true;
                        boolean inClusterCounterFlag = true;
                        boolean leaveFlag = true;

                        //time flags
//                        long joinTime = 0;
//                        long infoTime = 0;
                        public void wait(int miliSec) {
                            long referenceTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - referenceTime) < miliSec) {
                                //do nothing
                            }
                        }

                        public void printRobotStatus() {
                            this.console.log(String.format("Robot:%d State:%s ClusterId:%d ClusterSize: %d",
                                    getId(), myState, clusterId, clusterSize));
                        }

                        public void receiverReset() {
                            for (int i = 0; i < n; i++) {
                                resetReceivers(i);
                            }
                        }

                        public void freeMove(int miliSec) {
                            long leavedTime = System.currentTimeMillis();
                            pulseFlag = false;
                            while ((System.currentTimeMillis() - leavedTime) < miliSec) {
                                moveRandom();
                                avoidObstacles();
                            }
                            pulseFlag = true;
                            receiverReset();
                            moveHoldFlag = true;
                        }

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double bearing) {
                            //super.processMessage(message, sensorId);

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case Pulse:
                                    if (pulseFlag) {
                                        moveStop();
                                        moveHoldFlag = false;

                                        goAwayFlag = true;
                                        pulseFlag = false;
                                        pulseFBFlag = true;
                                        joinFlag = true;
                                        infoFlag = true;
                                        PulseData pulseData = (PulseData) message.getData();

                                        if (pulseData.getClusterId() != clusterId && !(clusterMembers.containsKey(sender.getId()))
                                                && ((sender.getId() - getId()) > 0 || myState != Robot.State.SEARCHING)) {

                                            if (myState == Robot.State.INCLUSTER) {
                                                leaveFlag = false;
                                            }

                                            console.log(String.format("Received %s Message from %d", type, sender.getId()));

                                            pulseFBFlag = false;
                                            MessageHandler.sendPulseFBMsg(this, clusterId,
                                                    sender.getId(), (double) clusterSize / noOfRobots, clusterSize);
                                        }

                                    }

                                    break;

                                case PulseFeedback:
                                    PulseFBData pulseFBData = (PulseFBData) message.getData();
                                    if (pulseFBFlag && pulseFBData.getReceiverId() == getId()
                                            && myState == Robot.State.SEARCHING) {
                                        //receiverReset();
                                        moveStop();
                                        moveHoldFlag = false;
                                        console.log(String.format("Received %s Message from %d", type,
                                                pulseFBData.getSenderId()));
                                        double joiningProb = pulseFBData.getJoiingProb();
                                        if (joiningProb != 0) {
                                            double randomProb = Math.random();
                                            console.log("PMax: " + joiningProb + "/ Random probability: " + randomProb);
                                            if (randomProb < joiningProb) {
                                                pulseFlag = false;
                                                pulseFBFlag = false;
//                                                infoTime = System.currentTimeMillis();
                                                MessageHandler.sendJoinMsg(this, pulseFBData.getSenderId());
                                            } else {
                                                MessageHandler.sendGoAwayMsg(this, pulseFBData.getSenderId());
                                                freeMove(1500);
                                            }
                                        }

                                    }
                                    break;

                                case Join:
                                    JoinData joinData = (JoinData) message.getData();
                                    if (joinFlag && this.getId() == joinData.getreceiverId()) {
                                        clearMessageBufferOut();
                                        boolean proceedFlag = true;
                                        leaveFlag = true;
                                        receiverReset();
//                                        joinTime = 0;
                                        if (myState == Robot.State.INCLUSTER && clusterMembers.containsKey(sender.getId())) {
                                            proceedFlag = false;
                                        }
                                        if (proceedFlag) {
                                            console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                            if (myState == Robot.State.SEARCHING) {
                                                myState = Robot.State.INCLUSTER;
                                            }
                                            clusterSize = clusterSize + 1;
                                            if (clusterSize == noOfRobots) {
                                                myState = Robot.State.AGGREGATE;
                                            }
                                            console.log(String.format("Sending Info Message to robot:( %d )", sender.getId()));
                                            long referenceTime = System.currentTimeMillis();
                                            while ((System.currentTimeMillis() - referenceTime) < 400) {
                                                MessageHandler.sendInfoMsg(this, sender.getId(), clusterId, clusterSize);
                                            }
                                            //clearMessageBufferOut();
                                            //wait(400);
                                            pulseFlag = true;
                                            pulseFBFlag = true;
                                            //MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize,
                                            //        clusterMembers);
                                            clusterMembers.put(sender.getId(), true);
                                            printRobotStatus();
                                        }
                                        proceedFlag = true;
                                    }
                                    break;

                                case Info:
                                    InfoData infoData = (InfoData) message.getData();
                                    if (infoFlag && infoData.getreceiverId() == this.getId()) {
                                        infoFlag = false;
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        clearMessageBufferOut();
//                                        infoTime = 0;
                                        clusterSize = infoData.getClusterSize();
                                        clusterId = infoData.getClusterId();
                                        clusterMembers.put(sender.getId(), true);
                                        //come closer
//
//                                        double distance = findDistance();
//                                        if (distance > 50 || distance == 0) {
//                                            angularTurn(bearing);
//                                            moveForward();
//                                        } else {
//                                            moveStop();
//                                        }
                                        
                                        if (clusterSize == noOfRobots) {
                                            myState = Robot.State.AGGREGATE;

                                        } else if (myState == Robot.State.SEARCHING) {
                                            myState = Robot.State.INCLUSTER;
                                        }
                                        receiverReset();
                                        printRobotStatus();
                                    }
                                    break;

                                case GoAway:
                                    leaveFlag = true;
                                    GoAwayData goAwayData = (GoAwayData) message.getData();
                                    if (myState == Robot.State.SEARCHING && goAwayFlag && goAwayData.getreceiverId() == this.getId()) {
                                        clearMessageBufferOut();
//                                        joinTime = 0;
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
//                                        moveHoldFlag = true;
//                                        pulseFlag = true;
                                        pulseFBFlag = true;
//                                        receiverReset();
                                        goAwayFlag = false;
                                        freeMove(1500);
                                    }
                                    break;

                                case Update:
                                    ClusterUpdateData updateData = (ClusterUpdateData) message.getData();
                                    if (myState == Robot.State.INCLUSTER && updateData.getClusterID() == clusterId
                                            && sender.getId() != getId()) {
                                        receiverReset();
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        int updatedClusterSize = updateData.getNewClusterSize();
                                        if (updatedClusterSize < clusterSize && clusterMembers.containsKey(sender.getId())) {
                                            clusterMembers.remove(sender.getId());
                                        }
                                        if (updatedClusterSize == noOfRobots) {
                                            myState = Robot.State.AGGREGATE;
                                        } else if (updatedClusterSize == 1) {
                                            myState = Robot.State.SEARCHING;
                                            freeMove(2000);
                                        }
                                        clusterSize = updatedClusterSize;
                                        printRobotStatus();

                                    }
                                    break;

                                case Leave:
                                    LeaveData leaveData = (LeaveData) message.getData();
                                    if (clusterId == leaveData.getClusterID()) {
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        clusterId = this.getId();
                                        clusterSize = 1;
                                        myState = Robot.State.SEARCHING;
                                        printRobotStatus();
                                        freeMove(1500);
                                    }

                                    break;

                                default:
                                    break;
                            }

                        }

                        @Override

                        public void loop() {
                            if (myState == Robot.State.SEARCHING) {
                                if (moveHoldFlag) {
                                    moveRandom();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                }
//                                long referenceTime = System.currentTimeMillis();
//                                if (joinTime != 0 || infoTime != 0) {
//                                    if( (referenceTime-joinTime)>2000 || (referenceTime-infoTime)>2000 ) {
//                                        moveHoldFlag = true;
//                                    }
//                                }

                            } else if (myState == Robot.State.INCLUSTER) {
                                if (inClusterCounterFlag) {
                                    clearMessageBufferOut();
                                    pulseFlag = true;
                                    pulseFBFlag = false;
                                    joinFlag = false;
                                    infoFlag = false;
                                    inClusterCounterFlag = false;
                                }

                                if (leaveFlag) {
                                    double leavingFactor = 0.001;
                                    if (clusterId == getId()) {
                                        leavingFactor = 0.0001;
                                    }
                                    double leavingProb = (1 - ((double) clusterSize / (noOfRobots))) * leavingFactor;
                                    double randomProb = Math.random();
                                    //System.out.printf("Robot:{%d} Leaving_Prob-%f   random_Prob-%f \n", getId(), leavingProb, randomProb);
                                    if (randomProb < leavingProb) {
                                        receiverReset();
                                        pulseFlag = false;
                                        pulseFBFlag = false;
                                        joinFlag = false;
                                        infoFlag = false;
                                        clusterSize = clusterSize - 1;
                                        long referenceTime = System.currentTimeMillis();
                                        while ((System.currentTimeMillis() - referenceTime) < 400) {
                                            if (clusterId == this.getId()) {
                                                MessageHandler.sendLeaveMsg(this, clusterId);
                                            } else {
                                                console.log("Me leaving message");
                                                MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize,
                                                        clusterMembers);
                                            }
                                        }

                                        clusterSize = 1;
                                        clusterMembers = new HashMap<>();
                                        myState = Robot.State.SEARCHING;
                                        clusterId = getId();
                                        freeMove(2500);
                                        inClusterCounterFlag = true;
                                        printRobotStatus();
                                    }
                                }

                            } else if (myState == Robot.State.AGGREGATE) {
                                receiverReset();
                                pulseFlag = false;
                                pulseFBFlag = false;
                                joinFlag = false;
                                infoFlag = false;
                                if (aggreeCounterFlag) {
                                    MessageHandler.sendClusterUpdateMsg(this, clusterId, clusterSize,
                                            clusterMembers);
                                }
                                aggreeCounterFlag = false;

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
