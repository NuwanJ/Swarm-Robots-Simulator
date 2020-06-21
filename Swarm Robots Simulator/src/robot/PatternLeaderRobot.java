package robot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import communication.Message;
import communication.MessageHandler;
import communication.MessageType;
import communication.messageData.patternformation.PositionAcquired;
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

    Robot receiver;

    public PatternLeaderRobot(double x, double y) {
        super(x, y, 0);
        setCurrentState(State.JOINED);
        //Set initial parameters for leader robot
        currentHeading = angle;
        nextPatternLabel = 1;
        myPatternPositionLabel = 0;
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
                nextPatternLabel = ((PositionAcquired) message.getData()).getLabel();
                joiningRobotId = -1;
                setCurrentState(State.JOINED);
            }
        }

    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {

            console.log(String.format("JoinBroadcast for label %d", nextPatternLabel));

            //Sending join broadcast message to free robots 
            MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, nextPatternLabel);

        }
    }

}
