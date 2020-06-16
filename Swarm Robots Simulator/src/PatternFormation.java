
import communication.*;
import communication.messageData.patternformation.*;
import configs.Settings;
import helper.Utility;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import robot.Robot;
import robot.datastructures.*;
import swarm.*;
import view.Simulator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
public class PatternFormation {

    public static void main(String[] args) {
        Swarm swarm = new Swarm("Pattern-Formation") {
            @Override
            public void create() {

                join(new Robot(500, 300, 0, true) {
                    HashMap<Integer, Double> childMap = new HashMap<Integer, Double>();
                    PatternTable table = new PatternTable();

                    public int myPatternPositionLabel = -1;
                    public int nextPatternLabel = -1;
                    public int joiningRobotId = -1;

                    double distance = 15;

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
                );

                for (int robotIndex = 0; robotIndex < 2; robotIndex++) {
                    join(new Robot(10, 10) {
                        HashMap<Integer, Ellipse2D.Double> childMap = new HashMap<Integer, Ellipse2D.Double>();
                        PatternTable table = new PatternTable();

                        public int myPatternPositionLabel = -1;
                        public int nextPatternLabel = -1;
                        public int joiningRobotId = -1;

                        double distance = 15;

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double bearing) {
                            
                            if (getCurrentState() == State.FREE) {
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
                    });
                }
            }
        };
        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
