

import communication.Message;
import communication.MessageType;
import robot.*;
import swarm.Swarm;
import view.Simulator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mster
 */
public class TestPatternFormation {

    public static void main(String[] args) {

        Swarm swarm = new Swarm("Pattern Formation") {
            @Override
            public void create() {
                join(new PatternLeaderRobot(500, 300));

                for (int i = 0; i < 2; i++) {
                    join(new PatternJoiningRobot(10, 10));
                }
            }
        };

        Simulator simulator = new Simulator(swarm);
        simulator.start();
    }
}
