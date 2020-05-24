/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import communication.*;
import communication.messageData.aggregation.*;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import robot.Robot;

/**
 *
 * @author Tharuka
 */
public class MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    public static void sendPulseMsg(Robot robot, int clusterId) {
        //console.log("Sending pulse message");       
        Message pulse = new Message(MessageType.Pulse, robot);
        pulse.setData(new PulseData(clusterId));
        robot.broadcastMessage(pulse);
    }

    public static void sendGoAwayMsg(Robot robot, int senderId) {
        robot.console.log(String.format("Sending Go Away Message to robot:( %d )", senderId));
        Message goAway = new Message(MessageType.GoAway, robot);
        goAway.setData(new GoAwayData(senderId));
        robot.broadcastMessage(goAway);
    }

    public static void sendJoinMsg(Robot robot, int senderId) {
        robot.console.log(String.format("Sending Join Message to robot:( %d )", senderId));
        Message joingMsg = new Message(MessageType.Join, robot);
        joingMsg.setData(new JoinData(senderId));
        robot.broadcastMessage(joingMsg);
    }

    public static void sendClusterUpdateMsg(Robot robot, int clusterId, int newClusterSize, HashMap<Integer, Boolean> clusterMembers) {
        for (Map.Entry member : clusterMembers.entrySet()) {
            robot.console.log(String.format("Sending Cluster Update Message for member:{%d} in cluster: (%d)", member.getKey(), clusterId));
            Message clusterUpdateMsg = new Message(MessageType.Update, robot);
            clusterUpdateMsg.setData(new ClusterUpdateData(clusterId, newClusterSize, (int) member.getKey()));
            robot.broadcastMessage(clusterUpdateMsg);
        }

    }

    public static void sendLeaveMsg(Robot robot, int clusterId) {
        robot.console.log(String.format("Sending Cluster Disperse Message from cluster:( %d )", clusterId));
        Message leaveMsg = new Message(MessageType.Leave, robot);
        leaveMsg.setData(new LeaveData(clusterId));
        robot.broadcastMessage(leaveMsg);
    }

    public static void sendInfoMsg(Robot robot, int receiver, int clusterId, int clusterSize) {
        robot.console.log(String.format("Sending Info Message to robot:( %d )", receiver));
        Message infoMsg = new Message(MessageType.Info, robot);
        infoMsg.setData(new InfoData(receiver, clusterId, clusterSize));
        robot.broadcastMessage(infoMsg);
    }

    public static void sendPulseFBMsg(Robot robot, int clusterId, int receiverId, double joiningProb, int clusterSize) {
        robot.console.log(String.format("Sending Pulse Feedback Message to robot:( %d )", receiverId));
        Message pulseFBMsg = new Message(MessageType.PulseFeedback, robot);
        pulseFBMsg.setData(new PulseFBData(clusterId, robot.getId(), receiverId, 0.8, clusterSize));
        robot.broadcastMessage(pulseFBMsg);
    }

}
