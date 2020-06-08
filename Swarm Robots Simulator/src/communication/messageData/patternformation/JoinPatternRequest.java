/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.patternformation;

import communication.Data;

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */

public class JoinPatternRequest implements Data {
    private int joiningId;
    private double currentHeading;

    public JoinPatternRequest(int joiningId, double currentHeading) {
        this.joiningId = joiningId;
        this.currentHeading = currentHeading;
    }

    public int getJoiningId() {
        return this.joiningId;
    }
    
    public double getCurrentHeading() {
        return this.currentHeading;
    }

}
