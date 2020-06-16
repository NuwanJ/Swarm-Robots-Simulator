/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot;

import communication.Message;
import communication.MessageHandler;
import communication.MessageType;
import communication.messageData.patternformation.JoinPattern;
import communication.messageData.patternformation.JoinPatternRequest;
import communication.messageData.patternformation.PositionData;
import configs.Settings;
import helper.Utility;
import java.util.HashMap;
import robot.datastructures.PatternTable;

/**
 *
 * @author mster
 */
public class PatternJoiningRobot extends Robot {

    //local data structures
    HashMap<Integer, Double> childMap = new HashMap<Integer, Double>();
    PatternTable table = new PatternTable();

    //local variables
    public int myPatternPositionLabel = -1;
    public int nextPatternLabel = -1;
    public int joiningRobotId = -1;

    double distance = 15;

    public PatternJoiningRobot(double x, double y) {
        super(x, y);
    }

    @Override
    public synchronized void processMessage(Message message, int senderId, double bearing) {

        if (getCurrentState() == Robot.State.JOINED) {
            if (message.getType() == MessageType.JoinPatternRequest) {
                console.log(String.format("Join Pattern request received from %d",
                        message.getSender().getId()));

                //This is done to clear the message buffer 
                //can be removed in the real implementation 
                clearMessageBufferOut();

                int parentLabel = ((JoinPatternRequest) message.getData()).getParentLabel();

                if (parentLabel == myPatternPositionLabel) {
                    double targetBearing = table.getTargetBearingFromParent(nextPatternLabel);
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

                    double bearing_lower_bound = table.getTargetBearingFromParent(nextPatternLabel)
                            - Settings.BEARING_ERROR_THRESHOLD;
                    double bearing_upper_bound = table.getTargetBearingFromParent(nextPatternLabel)
                            + Settings.BEARING_ERROR_THRESHOLD;
                    double distance_lower_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                            - Settings.DISTANCE_ERROR_THRESHOLD;
                    double distance_upper_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                            + Settings.DISTANCE_ERROR_THRESHOLD;

                    if (bearing > bearing_upper_bound && bearing < bearing_lower_bound
                            || distance > distance_upper_bound && distance < distance_lower_bound) {
                        PositionData data = Utility.calculateTargetPosition(table,
                                bearing, distance, nextPatternLabel);

                        MessageHandler.sendPositionDataMsg(this, sender, data);

                    } else {
                        nextPatternLabel++;
                        joiningRobotId = -1;
                        MessageHandler.sendPositionAcquiredMsg(this, sender, nextPatternLabel - 1);
                    }

                }
            }
        }else if (getCurrentState() == State.FREE) {
            if (message.getType() == MessageType.JoinPattern) {

                console.log(String.format("Received JoinPattern message to %d", getId()));

                //Get next pattern label 
                nextPatternLabel = ((JoinPattern) message.getData()).getNextPatternLabel();

                //Pattern label of the parent who send the JoinPatternMessage
                int senderPatternLabel = ((JoinPattern) message.getData()).getMyPatternLabel();

                //check whether the sender is the parent for the requested joining Label
                boolean response = table.checkJoinValidity(senderPatternLabel, nextPatternLabel);

                if (response) {
                    //if all ok send the join pattern request message
                    MessageHandler.sendJoinPatternReqMsg(this, senderPatternLabel);

                    //change the state to requesting
                    setCurrentState(State.REQUESTING);

                    //Unexpected Behavior
                    //clearMessageBufferOut();
                } else {
                    //if not the relevant parent go away from that robot
                    setCurrentState(State.ESCAPE);
                }

            }
        } else if (getCurrentState() == State.REQUESTING) {
            //listens for join pattern request
            if (message.getType() == MessageType.JoinPatternResponse) {

                Robot sender = message.getSender();

                console.log(String.format("Received joinPatternRes Message"));

                if (message.getReceiver().getId() == this.getId()) {

                    setCurrentState(State.JOININGPATTERN);

                    MessageHandler.sendPositionDataReqMsg(this, sender);
                }
            }
        } else if (getCurrentState() == State.ESCAPE) {
            //set the heading reverse
            angle = Math.abs(180 - angle);

            //move away from the ir range of the sender
            moveForwardDistance(Settings.IR_MAX_DISTANCE);

        } else if (getCurrentState() == State.JOININGPATTERN) {

            if (message.getType() == MessageType.PositionData
                    && message.getReceiver().getId() == this.getId()) {

                console.log(String.format("Received PositionData"));

                PositionData data = (PositionData) message.getData();

                angle = data.getTargetBearing();

                console.log(String.format("Bearing %f Distance %f", data.getTargetBearing(), data.getTargetDistance()));
                moveForwardDistance((int) data.getTargetDistance());
            }
        }
    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {
            //MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, nextPatternLabel);
        } else if (getCurrentState() == State.NAVIGATING) {
            clearMessageBufferOut();
        } else if (getCurrentState() == State.REQUESTING) {
            moveStop();
        } else if (getCurrentState() == State.FREE) {
            moveRandom();
            avoidObstacles();
        } else if (getCurrentState() == State.JOININGPATTERN) {
            //positionToRelativeLocation(bearing, distance);
        }
    }

}
