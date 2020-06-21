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
import robot.datastructures.ChildInfo;
import robot.datastructures.ChildMap;
import robot.datastructures.PatternTable;

/**
 *
 * @author Mahendra
 */
public class PatternLeaderRobot extends Robot {

    //local data structures
    private PatternTable table = new PatternTable();
    private ChildMap childMap;

    //local variables
    private int myPatternPositionLabel = -1;
    private int nextPatternLabel = -1;
    private int joiningRobotId = -1;
    
    public PatternLeaderRobot(double x, double y) {
        super(x, y, 0);
        setCurrentState(State.JOINED);
        
        //Set initial parameters for leader robot
        currentHeading = angle;
        myPatternPositionLabel = 0;
        
        //create the child label from the pattern table for this parent
        childMap = table.getChildMapForParent(myPatternPositionLabel);
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
                    PositionData virtualCoordiante = Utility.calculateRobotVirtualCoordinates(table, nextPatternLabel,
                            bearing, distance);
                    
                    //send the calculated virtual coordinates to the joining robot
                    MessageHandler.sendPositionDataMsg(this, sender, virtualCoordiante);
                }
            } else if (message.getType() == MessageType.PositionAcquired) {
                //get the positioned label
                nextPatternLabel = ((PositionAcquired) message.getData()).getLabel();
                //reset joining robot id field
                joiningRobotId = -1;
                //set the position acquired status to true for the pattern label
                childMap.updateMap(nextPatternLabel);
                //transition to Joined state
                setCurrentState(State.JOINED);
            }
        }

    }

    @Override
    public void loop() {
        if (getCurrentState() == Robot.State.JOINED) {

            nextPatternLabel = childMap.getNextPatternLabel();
            
            console.log(String.format("JoinBroadcast for label %d", nextPatternLabel));

            //Sending join broadcast message to free robots 
            MessageHandler.sendJoinBroadcastMsg(this, myPatternPositionLabel, nextPatternLabel);

        }
    }

}
