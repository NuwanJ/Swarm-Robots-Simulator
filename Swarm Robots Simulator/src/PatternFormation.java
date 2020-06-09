
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

        // line points
        Point[] points = new Point[]{
            new Point(400, 300),
            new Point(600, 300)
        };

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
                                int parentLabel = ((JoinPatternRequest) message.getData()).getParentLabel();

                                console.log(String.format("Join Pattern request received from %d",
                                         message.getSender().getId()));

                                if (parentLabel == myPatternPositionLabel) {
                                    double targetBearing = table.getTargetBearingFromParent(nextPatternLabel);
                                    double targetDistance = table.getTargetDistanceFromParent(nextPatternLabel);

                                    console.log(String.format("Target Bearing %f and Distance %f for joining id "
                                            + "%d", targetBearing, targetDistance, nextPatternLabel));

                                    //check for any obstacles in positioning the robot
                                    boolean joinFeasibility = true; //joinFeasibility = Utility.checkJoinFeasibility(childMap,
                                    //bearing, targetBearing);
                                    if (joinFeasibility) {
                                        Robot sender = message.getSender();
                                        joiningRobotId = sender.getId();

                                        console.log(String.format("Sending join response to %d",sender.getId()));
                                        MessageHandler.sendJoinPatternResMsg(this, sender, joinFeasibility);
                                        //transition to Joined busy state
                                        setCurrentState(Robot.State.NAVIGATING);
                                    }
                                }
                                //This is done to clear the message buffer 
                                //can be removed in the real implementation 
                                clearMessageBufferOut();

                            }
                        } else if (getCurrentState() == Robot.State.NAVIGATING) {
                            Robot sender = message.getSender();
                            console.log(String.format("Received %s from %d",message.getType(),sender.getId()));
                            if (sender.getId() == joiningRobotId && message.getType() == MessageType.PositionDataReq) {
                                double bearing_lower_bound = table.getTargetBearingFromParent(nextPatternLabel)
                                        - Settings.BEARING_ERROR_THRESHOLD;
                                double bearing_upper_bound = table.getTargetBearingFromParent(nextPatternLabel)
                                        + Settings.BEARING_ERROR_THRESHOLD;
                                double distance_lower_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                                        - Settings.DISTANCE_ERROR_THRESHOLD;
                                double distance_upper_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                                        + Settings.DISTANCE_ERROR_THRESHOLD;
                                if (bearing <= bearing_upper_bound && bearing >= bearing_lower_bound
                                        && distance <= distance_upper_bound && distance >= distance_lower_bound) {
                                    PositionData data = Utility.calculateTargetPosition(table,
                                            bearing, distance, nextPatternLabel);
                                    MessageHandler.sendPositionDataMsg(this, sender, data);
                                } else {
                                    nextPatternLabel++;
                                    joiningRobotId = -1;
                                    MessageHandler.sendPositionAcquiredMsg(this, nextPatternLabel - 1);
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

                            if (getCurrentState() == Robot.State.JOINED) {
                                if (message.getType() == MessageType.JoinPatternRequest) {
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
                                            Robot sender = message.getSender();
                                            joiningRobotId = sender.getId();

                                            MessageHandler.sendJoinPatternResMsg(this, sender, joinFeasibility);
                                            //transition to Joined busy state
                                            setCurrentState(Robot.State.NAVIGATING);
                                        }
                                    }
                                    //This is done to clear the message buffer 
                                    //can be removed in the real implementation 
                                    clearMessageBufferOut();

                                }
                            } else if (getCurrentState() == Robot.State.NAVIGATING) {
                                Robot sender = message.getReceiver();
                                if (sender.getId() == joiningRobotId && message.getType() == MessageType.PositionDataReq) {
                                    double bearing_lower_bound = table.getTargetBearingFromParent(nextPatternLabel)
                                            - Settings.BEARING_ERROR_THRESHOLD;
                                    double bearing_upper_bound = table.getTargetBearingFromParent(nextPatternLabel)
                                            + Settings.BEARING_ERROR_THRESHOLD;
                                    double distance_lower_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                                            - Settings.DISTANCE_ERROR_THRESHOLD;
                                    double distance_upper_bound = table.getTargetDistanceFromParent(nextPatternLabel)
                                            + Settings.DISTANCE_ERROR_THRESHOLD;

                                    if (bearing <= bearing_upper_bound && bearing >= bearing_lower_bound
                                            && distance <= distance_upper_bound && distance >= distance_lower_bound) {
                                        PositionData data = Utility.calculateTargetPosition(table,
                                                bearing, distance, nextPatternLabel);
                                        MessageHandler.sendPositionDataMsg(this, sender, data);
                                    } else {
                                        nextPatternLabel++;
                                        joiningRobotId = -1;
                                        MessageHandler.sendPositionAcquiredMsg(this, nextPatternLabel - 1);
                                    }

                                }
                            } else if (getCurrentState() == State.FREE) {
                                if (message.getType() == MessageType.JoinPattern) {
                                    nextPatternLabel = ((JoinPattern) message.getData()).getNextPatternLabel();
                                    int senderPatternLabel = ((JoinPattern) message.getData()).getMyPatternLabel();

                                    //check whether the sender is the parent for the requested joining Label
                                    boolean response = table.checkJoinValidity(senderPatternLabel, nextPatternLabel);

                                    if (response) {
                                        MessageHandler.sendJoinPatternReqMsg(this, senderPatternLabel);
                                        setCurrentState(State.REQUESTING);
                                    } else {
                                        setCurrentState(State.ESCAPE);
                                    }

                                }
                            } else if (getCurrentState() == State.REQUESTING) {
                                if (message.getType() == MessageType.JoinPatternResponse) {
                                    if (message.getReceiver().getId() == this.getId()) {
                                        setCurrentState(State.JOININGPATTERN);
                                        MessageHandler.sendPositionDataReqMsg(this);
                                    }
                                }
                            } else if (getCurrentState() == State.ESCAPE) {
                                angle = Math.abs(180 - angle);
                                moveForwardDistance(Settings.IR_MAX_DISTANCE);
                            } else if (getCurrentState() == State.JOININGPATTERN) {
                                if (message.getType() == MessageType.PositionData
                                        && message.getReceiver().getId() == this.getId()) {
                                    PositionData data = (PositionData) message.getData();
                                    angle = data.getTargetBearing();
                                    moveForwardDistance((int) data.getTargetDistance());
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
                            } else if (getCurrentState() == State.NAVIGATING) {

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
