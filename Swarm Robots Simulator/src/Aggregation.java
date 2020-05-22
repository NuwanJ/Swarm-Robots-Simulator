
import communication.Message;
import communication.MessageType;
import communication.aggregation.ClusterUpdateData;
import communication.aggregation.InfoData;
import communication.aggregation.JoinData;
import communication.aggregation.LeaveData;
import communication.aggregation.MessageHandler;
import communication.aggregation.PulseData;
import communication.aggregation.PulseFBData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import robot.Robot;
import robot.console.Console;
import swarm.Swarm;
import utility.Settings;
import view.Simulator;

/**
 *
 * @author Tharuka
 */
public class Aggregation {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Testing..") {
            @Override
            public void create() {

                for (int i = 0; i < 2; i++) {
                    join(new Robot() {
                        Robot.State myState = Robot.State.SEARCHING;
                        int clusterId = this.getId();
                        int clusterSize = 1;
                        //HashMap<Integer, Long> waitingMap = new HashMap<>();
                        int n = Settings.NUM_OF_IR_SENSORS;
                        PulseFBData joiningArray[] = new PulseFBData[n];
                        int noOfRobots = 2; // change this
                        boolean moveHoldFlag = true;
                        boolean amIJoining = false;
                        boolean checkMsgFlag = true;
                        int count = 0;
                        double joiningProbArray[] = new double[n];
                        int probSendersArray[] = new int[n];
                        int clusterIdArray[] = new int[n];
                        int clusterSizeArray[] = new int[n];
                        int updateMsgId = 0;
                        HashMap<Integer, Boolean> clusterMembers = new HashMap<>();
                        boolean pulseFlag = true;
                        boolean pulseFBFlag = true;
                        boolean joinFlag = true;
                        boolean infoFlag = true;

                        public void wait(int miliSec) {
                            long referenceTime = System.currentTimeMillis();
                            while ((System.currentTimeMillis() - referenceTime) < miliSec) {
                                //do nothing
                            }
                        }

                        public void printRobotStatus() {
                            this.console.log(String.format("Robot: %d | State: %s | ClusterId: %d | ClusterSize: %d",
                                    getId(), myState, clusterId, clusterSize));
                        }

                        @Override
                        public synchronized void processMessage(Message message) {
                            super.processMessage(message);

                            Robot receiver = message.getReceiver();
                            Robot sender = message.getSender();
                            MessageType type = message.getType();

                            switch (type) {
                                case Pulse:
                                    moveStop();
                                    moveHoldFlag = false;
                                    PulseData pulseData = (PulseData) message.getData();

                                    if (pulseFlag && pulseData.getClusterId() != clusterId && 
                                            ((sender.getId() - getId()) > 0 || myState != Robot.State.SEARCHING)) {

                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));                                        
                                        MessageHandler.sendPulseFBMsg(this, clusterId,
                                                sender.getId(), (double) clusterSize / noOfRobots, clusterSize);

                                    }

                                    break;

                                case PulseFeedback:
                                    PulseFBData pulseFBData = (PulseFBData) message.getData();
                                    if (pulseFBFlag && pulseFBData.getReceiverId() == getId()) {

                                        console.log(String.format("Received %s Message from %d", type,
                                                pulseFBData.getSenderId()));
                                        double joiningProb = pulseFBData.getJoiingProb();
                                        if (joiningProb != 0) {
                                            double randomProb = Math.random();
                                            console.log("PMax: " + joiningProb + "/ Random probability: " + randomProb);

                                            if (randomProb < joiningProb) {
                                                pulseFlag = false;
                                                pulseFBFlag = false;
                                                MessageHandler.sendJoinMsg(this, pulseFBData.getSenderId());
                                            }

                                        }

                                    }
                                    break;
                                    
                                case Join:
                                    JoinData joinData = (JoinData) message.getData();
                                    if (joinFlag && this.getId() == joinData.getreceiverId()
                                            && !(clusterMembers.containsKey(sender.getId()))) {

                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                         if (myState == State.SEARCHING) {
                                            myState = State.INCLUSTER;
                                        }                                       
                                        clusterSize = clusterSize + 1;
                                        if (clusterSize == noOfRobots) {
                                            myState = Robot.State.AGGREGATE;                                            
                                        }
                                        pulseFlag = true;
                                        MessageHandler.sendInfoMsg(this, sender.getId(), clusterId, clusterSize);
                                        updateMsgId = updateMsgId + 1;
                                        MessageHandler.sendClusterUpdateMsg(this, updateMsgId, clusterId, clusterSize,
                                                clusterMembers);

                                        clusterMembers.put(sender.getId(), true);
                                        printRobotStatus();
                                    }
                                    break;

                                case Info:
                                    InfoData infoData = (InfoData) message.getData();
                                    if (infoFlag && infoData.getreceiverId() == this.getId()
                                            && !(clusterMembers.containsKey(sender.getId()))) {

                                        clusterMembers.put(sender.getId(), true);
                                        clusterSize = infoData.getClusterSize();
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        //come closer
                                        if (clusterSize == noOfRobots) {
                                            myState = Robot.State.AGGREGATE;
                                        } else if (myState == Robot.State.SEARCHING) {
                                            myState = Robot.State.INCLUSTER;
                                        }
                                        pulseFlag = true;
                                        pulseFBFlag = true;
                                        printRobotStatus();
                                    }
                                    break;

                                case Update:
                                    ClusterUpdateData updateData = (ClusterUpdateData) message.getData();
                                    if (myState == Robot.State.INCLUSTER && updateData.getClusterID() == clusterId
                                            && updateData.getUpdateMsgId() > updateMsgId && sender.getId() != getId()) {
                                        updateMsgId = updateData.getUpdateMsgId();
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        int updatedClusterSize = updateData.getNewClusterSize();
                                        if (updatedClusterSize == noOfRobots) {
                                            myState = Robot.State.AGGREGATE;
                                        } else if (updatedClusterSize == 1) {
                                            myState = Robot.State.SEARCHING;
                                            moveHoldFlag = true;
                                        } else {
                                            clusterSize = updatedClusterSize;
                                        }
                                        clusterMembers = new HashMap<>();
                                        console.log(String.format("Forwarding Cluster Update Message "
                                                + "for cluster:( %d )", clusterId));
                                        broadcastMessage(message);
                                        printRobotStatus();

                                    }
                                    break;

                                case Leave:
                                    LeaveData newData = (LeaveData) message.getData();
                                    if (clusterId == newData.getClusterID()) {
                                        console.log(String.format("Received %s Message from %d", type, sender.getId()));
                                        clusterId = this.getId();
                                        myState = Robot.State.SEARCHING;
                                        moveHoldFlag = true;
                                        printRobotStatus();
                                    }

                                    break;

                                default:
                                    break;
                            }
                            type = MessageType.Dummy;
                        }

                        @Override

                        public void loop() {
                            if (myState == Robot.State.SEARCHING) {
                                if (moveHoldFlag) {
                                    moveRandom();
                                    avoidObstacles();
                                    MessageHandler.sendPulseMsg(this, clusterId);
                                } else {
//                                    wait(3500);
//                                    if (myState == State.SEARCHING) {
//                                        moveHoldFlag = true;
//                                        pulseFlag = true;
//                                    }
                                }

                            } else if (myState == Robot.State.INCLUSTER) {
                                double leavingFactor = 0.01;
                                if (clusterId == getId()) {
                                    leavingFactor = 0.001;
                                }
                                double leavingProb = (1 - ((double) clusterSize / (noOfRobots))) * leavingFactor;
                                double randomProb = Math.random();
                                //System.out.printf("Robot:{%d} Leaving_Prob-%f   random_Prob-%f \n", getId(), leavingProb, randomProb);
                                if (randomProb < leavingProb) {
                                    if (clusterId == this.getId()) {
                                        clusterSize = 1;
                                        MessageHandler.sendLeaveMsg(this, clusterId);
                                    } else {
                                        clusterSize = clusterSize - 1;
                                        updateMsgId = updateMsgId + 1;
                                        MessageHandler.sendClusterUpdateMsg(this, updateMsgId, clusterId, clusterSize,
                                                clusterMembers);
                                    }
                                    clusterMembers = new HashMap<>();
                                    moveHoldFlag = true;
                                    myState = Robot.State.SEARCHING;
                                    clusterId = getId();
                                    long leavedTime = System.currentTimeMillis();
                                    while ((System.currentTimeMillis() - leavedTime) < 2500) {
                                        moveRandom();
                                        avoidObstacles();
                                    }
                                    printRobotStatus();
                                }
                                //MessageHandler.sendPulseMsg(this, clusterId);
                            } else if (myState == Robot.State.AGGREGATE) {
                                checkMsgFlag = true;
                                updateMsgId = updateMsgId + 1;
                                MessageHandler.sendClusterUpdateMsg(this, updateMsgId, clusterId, clusterSize,
                                        clusterMembers);
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
