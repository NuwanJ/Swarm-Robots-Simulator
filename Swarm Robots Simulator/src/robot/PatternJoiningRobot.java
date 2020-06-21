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
import communication.messageData.patternformation.PositionAcquired;
import communication.messageData.patternformation.PositionData;
import helper.Utility;
import robot.datastructures.ChildMap;
import robot.datastructures.PatternTable;

/**
 *
 * @author mster
 */
public class PatternJoiningRobot extends Robot {

    //local data structures
    private PatternTable table = new PatternTable();
    private ChildMap childMap;

    //local variables
    private int myPatternPositionLabel = -1;
    private int joiningLabel = -1;
    private int joiningRobotId = -1;

    //Robot sender;
    public PatternJoiningRobot() {
        super();
        setCurrentState(State.FREE);
    }

    @Override
    public synchronized void processMessage(Message message, int senderId, double bearing, double distance) {

        if (getCurrentState() == Robot.State.JOINED) {

            if (message.getType() == MessageType.JoinPatternRequest) {

                console.log(String.format("Join Pattern request received from %d",
                        message.getSender().getId()));

                int parentLabel = ((JoinPatternRequest) message.getData()).getParentLabel();

                Robot sender = message.getSender();

                //See if the pattern join request is for me
                if (parentLabel == myPatternPositionLabel) {

                    double targetBearing = table.getTargetBearingFromParent(joiningLabel, getAngle());
                    double targetDistance = table.getTargetDistanceFromParent(joiningLabel);

                    console.log(String.format("Target Bearing %f and Distance %f for joining id "
                            + "%d", targetBearing, targetDistance, joiningLabel));

                    //check for any obstacles in positioning the robot
                    boolean joinFeasibility = true;

                    //joinFeasibility = Utility.checkJoinFeasibility(childMap, bearing, targetBearing);
                    //if it is possible to position the robot
                    if (joinFeasibility) {
                        //transition the state to navigating
                        setCurrentState(Robot.State.NAVIGATING);

                        //save the joining robot id for future communication
                        joiningRobotId = sender.getId();

                        //send join ok in response
                        MessageHandler.sendJoinPatternResMsg(this, sender, joinFeasibility);

                    } else {
                        //send join reject in response
                        MessageHandler.sendJoinPatternResMsg(this, sender, joinFeasibility);
                    }
                }
            }
        } else if (getCurrentState() == Robot.State.NAVIGATING) {

            if (message.getType() == MessageType.PositionDataReq) {

                Robot sender = message.getSender();

                console.log(String.format("Received %s from %d", message.getType(), sender.getId()));

                //convert bearing to a positive value measured clockwise
                if (bearing < 0) {
                    bearing = bearing + 360;
                }

                //validate the sender if it is the one who was sent join response
                if (sender.getId() == joiningRobotId) {

                    //calculate the virtual coordinate of the sender in reference to leader robot
                    PositionData virtualCoordiante = Utility.calculateRobotVirtualCoordinates(table, joiningLabel,
                            bearing, distance);

                    //send the calculated virtual coordinates to the joining robot
                    MessageHandler.sendPositionDataMsg(this, sender, virtualCoordiante);
                }
            } else if (message.getType() == MessageType.PositionAcquired
                    && message.getReceiver().getId() == this.getId()) {
                //get the positioned label
                joiningLabel = ((PositionAcquired) message.getData()).getLabel();
                //reset joining robot id field
                joiningRobotId = -1;
                //set the position acquired status to true for the pattern label
                childMap.updateMap(joiningLabel);
                //transition to Joined state
                setCurrentState(State.JOINED);
            }
        } else if (getCurrentState() == State.FREE) {
            if (message.getType() == MessageType.JoinPattern) {

                console.log(String.format("Received JoinPattern message to %d", getId()));

                //Get next pattern label 
                joiningLabel = ((JoinPattern) message.getData()).getNextPatternLabel();

                //Pattern label of the parent who send the JoinPatternMessage
                int senderPatternLabel = ((JoinPattern) message.getData()).getMyPatternLabel();

                //check whether the sender is the parent for the requested joining Label
                if (joiningLabel > 0) {
                    boolean response = table.checkJoinValidity(senderPatternLabel, joiningLabel);

                    if (response) {
                        //if all ok send the join pattern request message
                        MessageHandler.sendJoinPatternReqMsg(this, senderPatternLabel);

                        //change the state to requesting
                        setCurrentState(State.REQUESTING);

                    }
                }

            }
        } else if (getCurrentState() == State.REQUESTING) {

            //listens for join pattern request
            if (message.getType() == MessageType.JoinPatternResponse) {

                Robot sender = message.getSender();

                console.log(String.format("Received joinPatternRes Message"));

                if (message.getReceiver().getId() == this.getId()) {
                    //turn in the closest angle
                    if (bearing > 180) {
                        bearing = 360 - 180;
                    }

                    angularTurn(bearing);

                    setCurrentState(State.JOININGPATTERN);

                    MessageHandler.sendPositionDataReqMsg(this, sender);
                }
            }
        } else if (getCurrentState() == State.JOININGPATTERN) {

            Robot sender = message.getSender();

            if (message.getType() == MessageType.PositionData
                    && message.getReceiver().getId() == this.getId()) {

                PositionData data = (PositionData) message.getData();

                double moveDist = table.getTargetDistance(joiningLabel, data);

                double turnAngle = table.getTargetRotation(joiningLabel,
                        getAngle(), data);

                boolean isPositioned = table.positionValidation(joiningLabel, data);

                if (!isPositioned) {
                    console.log(String.format("X %f Y %f", data.getX(), data.getY()));
                    console.log(String.format("Distance %f turn %f", moveDist, turnAngle));

                    angularTurn(turnAngle);
                    moveForwardDistance((int) moveDist);
                    MessageHandler.sendPositionDataReqMsg(this, sender);
                } else {
                    myPatternPositionLabel = joiningLabel;
                    joiningRobotId = -1;
                    MessageHandler.sendPositionAcquiredMsg(this, sender, joiningLabel);

                    double turn = getAngle() % 360;

                    angularTurn(-turn);

                    //create the child map from the pattern table for this parent
                    childMap = table.getChildMapForParent(myPatternPositionLabel);

                    setCurrentState(State.JOINED);
                }
            }
        }
    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {

            joiningLabel = childMap.getNextPatternLabel();

            console.log(String.format("JoinBroadcast for label %d", myPatternPositionLabel));

            MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, joiningLabel);

        } else if (getCurrentState() == State.REQUESTING) {
            moveStop();
        } else if (getCurrentState() == State.FREE) {
            moveRandom();
            avoidObstacles();
        }
    }

}
