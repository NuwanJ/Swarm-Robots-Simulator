/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import communication.messageData.aggregation.ClusterUpdateData;
import communication.messageData.aggregation.PulseFBData;
import communication.messageData.aggregation.PulseData;
import communication.messageData.aggregation.LeaveData;
import communication.messageData.aggregation.JoinData;
import communication.messageData.aggregation.GoAwayData;
import communication.*;
import robot.Robot;
import java.util.logging.Logger;

/**
 *
 * @author Tharuka
 */
public class MessageHandler {
    
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());
    
    public static void sendPulseMsg(Robot robot, int clusterId) {        
        //System.out.printf("Robot:{%d}- Sending Pulse Msg\n", robot.getId());
        Message pulse = new Message(MessageType.Pulse, robot);
        pulse.setData(new PulseData(clusterId));
        robot.broadcastMessage(pulse);         
    }
    
    public static void sendGoAwayMsg(Robot robot, int senderId) {   
        robot.console.log("Sending GoAway Msg");
        Message goAway = new Message(MessageType.GoAway, robot);
        goAway.setData(new GoAwayData(senderId));
        robot.broadcastMessage(goAway); 
    }
     
    public static void sendJoinMsg(Robot robot, int senderId) {  
        robot.console.log(String.format("Sending Join Msg   Sender ID: %d", senderId));
        Message joingMsg = new Message(MessageType.Join, robot);
        joingMsg.setData(new JoinData(senderId));
        robot.broadcastMessage(joingMsg);
    }
    
    public static void sendLeaveMsg(Robot robot, int clusterId) {        
//        System.out.printf("Robot:{%d}- Sending Leave Cluster Disclosure Msg\n", robot.getId());
        Message leaveMsg = new Message(MessageType.Leave, robot);
        leaveMsg.setData(new LeaveData(clusterId));
        robot.broadcastMessage(leaveMsg);
    }
    
    public static void sendClusterUpdateMsg(Robot robot, int clusterId, int newClusterSize) {        
//        System.out.printf("Robot:{%d}- Sending Me Leaving and Cluster Update Msg\n", robot.getId());
        Message clusterUpdateMsg = new Message(MessageType.Update, robot);
        clusterUpdateMsg.setData(new ClusterUpdateData(clusterId, newClusterSize));
        robot.broadcastMessage(clusterUpdateMsg);
    }
    
    public static void sendPulseFBMsg(Robot robot, int clusterId, int senderId, double joiningProb, int clusterSize) {        
//        System.out.printf("Robot:{%d}- Sending Pulse FeedBack Msg\n", robot.getId());
        Message pulseFBMsg = new Message(MessageType.PulseFeedback, robot);
        pulseFBMsg.setData(new PulseFBData(clusterId, robot.getId(), senderId, 0.8, clusterSize));
        robot.broadcastMessage(pulseFBMsg);                                  
    }
    
    
}
