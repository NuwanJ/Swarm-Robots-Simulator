
import communication.*;
import communication.messageData.patternformation.*;
import configs.Settings;
import java.awt.Point;
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

                    int nextPosition = 0;
                    PatternTable table = new PatternTable();

                    @Override
                    public synchronized void processMessage(Message message, int sensorId, double bearing) {
                        if (getCurrentState() == Robot.State.JOINED) {
                            if (message.getType() == MessageType.JoinPatternRequest) {
                                PositionData positionData = calculateTargetPosition(sensorId);
                                //MessageHandler.sendPositioningData(this,positionData);
                                setCurrentState(Robot.State.JOINEDBUSY);
                                clearMessageBufferOut();
                            }
                        } else if (getCurrentState() == Robot.State.JOINEDBUSY) {
                            if (message.getType() == MessageType.PositionAcquired) {
                                //calculation
                                //MessageHandler.updatePattern(this);
                                setCurrentState(Robot.State.JOINED);
                            }
                        }
                    }

                    @Override
                    public void loop() {
                        if (getCurrentState() == Robot.State.JOINED) {
                            currentHeading = angle;
                            nextJoinId = 1;
                            patternPositionId = 0;
                            MessageHandler.sendJoinBroadcastMsg(this);
                        } else if (getCurrentState() == Robot.State.JOINEDBUSY) {
                            //pulseToTestConnection

                        }
                    }
                });

                for (int robotIndex = 0; robotIndex < 2; robotIndex++) {
                    join(new Robot() {
                        PatternTable table = new PatternTable();

                        @Override
                        public synchronized void processMessage(Message message, int sensorId, double bearing) {
                            if (getCurrentState() == State.JOINED) {
                                if (message.getType() == MessageType.JoinPatternRequest) {
                                    //calculation
                                    //MessageHandler.sendPositioningData(this);
                                    //increment nextjoinid and broadcast
                                    setCurrentState(State.JOINEDBUSY);
                                }
                                setCurrentState(State.POSITIONING);
                            } else if (getCurrentState() == State.JOINEDBUSY) {

                            } else if (getCurrentState() == State.POSITIONING) {
                                //ignore Message();
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
