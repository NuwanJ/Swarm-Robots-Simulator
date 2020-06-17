package robot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import communication.Message;
import communication.MessageHandler;
import communication.MessageType;
import communication.messageData.patternformation.JoinPatternRequest;
import communication.messageData.patternformation.PositionData;
import configs.Settings;
import helper.Utility;
import java.util.HashMap;
import robot.Robot;
import robot.datastructures.PatternTable;

/**
 *
 * @author Mahendra
 */
public class PatternLeaderRobot extends Robot {

    //local data structures
    HashMap<Integer, Double> childMap = new HashMap<Integer, Double>();
    PatternTable table = new PatternTable();

    //local variables
    public int myPatternPositionLabel = -1;
    public int nextPatternLabel = -1;
    public int joiningRobotId = -1;

    public PatternLeaderRobot(double x, double y) {
        super(x, y, 0);
        setCurrentState(State.JOINED);
    }

    @Override
    public synchronized void processMessage(Message message, int senderId, double bearing, double distance) {

        if (getCurrentState() == Robot.State.JOINED) {
            if (message.getType() == MessageType.JoinPatternRequest) {
                console.log(String.format("Join Pattern request received from %d",
                        message.getSender().getId()));

                //This is done to clear the message buffer 
                //can be removed in the real implementation 
                clearMessageBufferOut();

                int parentLabel = ((JoinPatternRequest) message.getData()).getParentLabel();

                if (parentLabel == myPatternPositionLabel) {
                    double targetBearing = table.getTargetBearingFromParent(nextPatternLabel,getAngle());
                    double targetDistance = table.getTargetDistanceFromParent(nextPatternLabel);

                    console.log(String.format("Target Bearing %f and Distance %f for joining id "
                            + "%d", targetBearing, targetDistance, nextPatternLabel));

                    //check for any obstacles in positioning the robot
                    boolean joinFeasibility = true; //joinFeasibility = Utility.checkJoinFeasibility(childMap,
                    //bearing, targetBearing);
                    if (joinFeasibility) {

                        setCurrentState(Robot.State.NAVIGATING);

                        Robot sender = message.getSender();

                        joiningRobotId = sender.getId();

                        MessageHandler.sendJoinPatternResMsg(this, sender, joinFeasibility);

                    }
                }
                //clearMessageBufferOut();

            }
        } else if (getCurrentState() == Robot.State.NAVIGATING) {
            if (message.getType() == MessageType.PositionDataReq) {

                Robot sender = message.getSender();

                console.log(String.format("Received %s from %d", message.getType(), sender.getId()));

                if (sender.getId() == joiningRobotId) {

                    double bearing_lower_bound = table.getTargetBearingFromParent(nextPatternLabel,getAngle())
                            - Settings.BEARING_ERROR_THRESHOLD;
                    double bearing_upper_bound = table.getTargetBearingFromParent(nextPatternLabel,getAngle())
                            + Settings.BEARING_ERROR_THRESHOLD;
                    double distance_lower_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                            - Settings.DISTANCE_ERROR_THRESHOLD;
                    double distance_upper_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                            + Settings.DISTANCE_ERROR_THRESHOLD;
                    
                    //get the bearing as the angle measured clockwise from robot's heading
                    /*
                    if (bearing < 0) {
                        bearing = 360 + bearing;
                    }
                    */
                    if (bearing > bearing_upper_bound || bearing < bearing_lower_bound) {
                        if (distance > distance_upper_bound || distance < distance_lower_bound) {
                            PositionData data = Utility.calculateTargetPosition(table,
                                    bearing, distance, nextPatternLabel,getAngle());

                            MessageHandler.sendPositionDataMsg(this, sender, data);

                        } else {
                            nextPatternLabel++;
                            joiningRobotId = -1;
                            MessageHandler.sendPositionAcquiredMsg(this, sender, nextPatternLabel - 1);
                        }
                    }

                }
            }
        }
    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {
            currentHeading = angle;
            nextPatternLabel = 1;
            myPatternPositionLabel = 0;
            console.log(String.format("Sending JoinBroadcast Message from %d", this.getId()));
            MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, nextPatternLabel);
        } else if (getCurrentState() == Robot.State.NAVIGATING) {

        }
    }

}
