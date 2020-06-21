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
import configs.Settings;
import helper.Utility;
import java.util.HashMap;
import robot.datastructures.PatternTable;
import robot.datastructures.Point;

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

    double moveDist = -1;

    Robot receiver;

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

                //See if the pattern join request is for me
                if (parentLabel == myPatternPositionLabel) {

                    double targetBearing = table.getTargetBearingFromParent(nextPatternLabel, getAngle());
                    double targetDistance = table.getTargetDistanceFromParent(nextPatternLabel);

                    console.log(String.format("Target Bearing %f and Distance %f for joining id "
                            + "%d", targetBearing, targetDistance, nextPatternLabel));

                    //check for any obstacles in positioning the robot
                    boolean joinFeasibility = true;

                    //joinFeasibility = Utility.checkJoinFeasibility(childMap, bearing, targetBearing);
                    //if it is possible to position the robot
                    if (joinFeasibility) {
                        //transition the state to navigating
                        setCurrentState(Robot.State.NAVIGATING);

                        receiver = message.getSender();

                        //save the joining robot id for future communication
                        joiningRobotId = receiver.getId();

                        //send join ok in response
                        MessageHandler.sendJoinPatternResMsg(this, receiver, joinFeasibility);

                    } else {
                        //send join reject in response
                        MessageHandler.sendJoinPatternResMsg(this, receiver, joinFeasibility);
                    }
                }
            } 
        } else if (getCurrentState() == Robot.State.NAVIGATING) {

            if (message.getType() == MessageType.PositionDataReq) {

                receiver = message.getSender();
                console.log(String.format("Received %s from %d", message.getType(), receiver.getId()));

                if (bearing < 0) {
                    bearing = bearing + 360;
                }

                //validate the sender if it is the one who was sent join response
                if (receiver.getId() == joiningRobotId) {

                    //calculate the virtual coordinate of the sender in reference to leader robot
                    PositionData virtualCoordiante = Utility.calculateRobotVirtualCoordinates(table, nextPatternLabel,
                            bearing, distance);

                    MessageHandler.sendPositionDataMsg(this, receiver, virtualCoordiante);
                }
            } else if (message.getType() == MessageType.PositionAcquired) {
                nextPatternLabel = ((PositionAcquired)message.getData()).getLabel();
                joiningRobotId = -1;
                setCurrentState(State.JOINED);
            }
        } else if (getCurrentState() == State.FREE) {
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

                }

            }
        } else if (getCurrentState() == State.REQUESTING) {

            //listens for join pattern request
            if (message.getType() == MessageType.JoinPatternResponse) {

                receiver = message.getSender();

                console.log(String.format("Received joinPatternRes Message"));

                if (message.getReceiver().getId() == this.getId()) {
                    angularTurn(bearing);

                    console.log(String.format("Head set to Leader Robot"));

                    setCurrentState(State.JOININGPATTERN);

                    MessageHandler.sendPositionDataReqMsg(this, receiver);
                }
            }
        } else if (getCurrentState() == State.ESCAPE) {
            System.out.println("escape");
            moveStop();

            //set the heading reverse
            angularTurn(180);

            //move away from the ir range of the sender
            moveForwardDistance(Settings.IR_MAX_DISTANCE);

            setCurrentState(State.FREE);

        } else if (getCurrentState() == State.JOININGPATTERN) {

            receiver = message.getSender();

            if (message.getType() == MessageType.PositionData
                    && message.getReceiver().getId() == this.getId()) {

                PositionData data = (PositionData) message.getData();

                double moveDist = table.getTargetDistance(nextPatternLabel, data);

                double turnAngle = table.getTargetRotation(nextPatternLabel,
                        getAngle(), data);

//                double dist = Utility.distanceBetweenTwoPoints(new Point(this.getCenterX(), this.getCenterY()),
//                new Point(550, 300));
                boolean isPositioned = table.positionValidation(nextPatternLabel, data);

                if (!isPositioned) {
                    console.log(String.format("X %f Y %f", data.getX(), data.getY()));
                    console.log(String.format("Distance %f turn %f", moveDist, turnAngle));

                    angularTurn(turnAngle);
                    moveForwardDistance((int) moveDist);
                    MessageHandler.sendPositionDataReqMsg(this, receiver);
                } else {
                    myPatternPositionLabel = nextPatternLabel;
                    joiningRobotId = -1;
                    nextPatternLabel++;
                    MessageHandler.sendPositionAcquiredMsg(this, receiver, nextPatternLabel);
                    angularTurn(-(getAngle() % 360));
                    setCurrentState(State.JOINED);
                }
            }
        }
    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {
            console.log(String.format("JoinBroadcast for label %d", myPatternPositionLabel));
            MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, nextPatternLabel);
        } else if (getCurrentState() == State.NAVIGATING) {

        } else if (getCurrentState() == State.REQUESTING) {
            moveStop();
        } else if (getCurrentState() == State.FREE) {
            moveRandom();
            avoidObstacles();
        }
    }

}
