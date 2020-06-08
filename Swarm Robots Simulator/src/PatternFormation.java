
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
     
                    
                    public int patternPositionId;
                    public int nextJoinId;
                    public int joiningRobotId = -1;
                    
                    double distance = 15;

                    @Override
                    public synchronized void processMessage(Message message, int senderId, double bearing) {
                        Robot sender = message.getSender();
                        if (getCurrentState() == Robot.State.JOINED) {
                            if (message.getType() == MessageType.JoinPatternRequest) {
                                int joiningId = ((JoinPatternRequest) message.getData()).getJoiningId();
                                boolean response = table.checkJoinValidity(getId(), joiningId);
                                if (response) {
                                    double targetBearing = table.getTargetBearingFromParent(joiningId);
                                    double targetDistance = table.getTargetDistanceFromParent(joiningId);

                                    console.log(String.format("Target Bearing %f and Distance %f for joining id "
                                            + "%d", targetBearing, targetDistance, joiningId));
                                    boolean joinFeasibility = joinFeasibility = Utility.checkJoinFeasibility(childMap,
                                            targetBearing, bearing);
                                    if (joinFeasibility) {
                                        joiningRobotId = sender.getId();

                                        MessageHandler.sendJoinPatternResponseMsg(sender, nextJoinId);
                                        //transition to Joined busy state
                                        setCurrentState(Robot.State.NAVIGATING);
                                    }

                                    //This is done to clear the message buffer 
                                    //can be removed in the real implementation 
                                    clearMessageBufferOut();
                                }
                            }else if(message.getType() == MessageType.PositionAcquired){
                                nextJoinId++;
                                joiningRobotId = -1;
                                
                            }
                        } else if (getCurrentState() == Robot.State.NAVIGATING) {
                            if (sender.getId() == joiningRobotId && message.getType() == MessageType.PositionDataReq) {
                                if (bearing != table.getTargetBearingFromParent(joiningId)
                                        && distance != table.getTargetBearingFromParent(joiningId)) {
                                    PositionData data = Utility.calculateTargetPosition(table,
                                            joiningId, bearing, distance);
                                    MessageHandler.sendPositionData();
                                }else if(bearing != table.getTargetBearingFromParent(joiningId)
                                        && distance != table.getTargetBearingFromParent(joiningId)){
                                    MessageHandler.sendPositionAcquiredMsg();
                                }

                            }

                        }
                    }

                    @Override

                    public void loop() {
                        if (getCurrentState() == Robot.State.JOINED) {
                            currentHeading = angle;
                            nextJoinId = 1;
                            patternPositionId = 0;
                            console.log(String.format("Sending Message from %d", this.getId()));
                            MessageHandler.sendJoinBroadcastMsg(this);
                        } else if (getCurrentState() == Robot.State.JOINEDBUSY) {
                            MessageHandler.sendPulseMessage(this);
                        }
                    }
                }
                );

                for (int robotIndex = 0; robotIndex < 2; robotIndex++) {
                    join(new Robot(10, 10) {
                        HashMap<Integer, Ellipse2D.Double> childMap = new HashMap<Integer, Ellipse2D.Double>();
                        PatternTable table = new PatternTable();
                        boolean joinFeasibility = true;
                        boolean response = false;
                        double distance = 15;
                        public int patternPositionId;
                        public int nextJoinId;

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double bearing) {
                            if (getCurrentState() == State.JOINED) {
                                if (message.getType() == MessageType.JoinPatternRequest) {

                                    setCurrentState(State.JOINEDBUSY);
                                }
                                setCurrentState(State.POSITIONING);
                            } else if (getCurrentState() == State.JOINEDBUSY) {

                            } else if (getCurrentState() == State.POSITIONING) {

                            } else if (getCurrentState() == State.FREE) {
                                if (message.getType() == MessageType.JoinPattern) {
                                    nextJoinId = ((JoinPattern) message.getData()).getNextJoinId();
                                    setCurrentState(State.REQUESTING);
                                    MessageHandler.sendJoinPatternReqMsg(this);
                                }
                            } else if (getCurrentState() == State.REQUESTING) {
                                if (message.getType() == MessageType.JoinPatternReqAck) {
                                    //getparents

                                }
                                /*
                                response = requestJoin();
                                if (response == "ok") {
                                    positionRobot();
                                    addjoinIdtoList();
                                    broadcastList();
                                    transitionToJoined();
                                }*/
                            }

                        }

                        @Override
                        public void loop() {
                            if (getCurrentState() == State.JOINED) {
                                MessageHandler.sendJoinBroadcastMsg(this);
                                //informRelevantParents();
                            } else if (getCurrentState() == State.JOINEDBUSY) {

                            } else if (getCurrentState() == State.REQUESTING) {
                                moveStop();
                                /*
                                if
                                response = requestJoin();
                                if (response == "ok") {
                                    positionRobot();
                                    addjoinIdtoList();
                                    broadcastList();
                                    transitionToJoined();
                                }*/
                            } else if (getCurrentState() == State.FREE) {
                                moveRandom();
                                avoidObstacles();
                                if (this.getId() == 1) {
                                    swithOnLedStript(Color.yellow);
                                }
                                if (this.getId() == 2) {
                                    swithOnLedStript(Color.green);
                                }
                            } else if (getCurrentState() == State.POSITIONING) {
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
