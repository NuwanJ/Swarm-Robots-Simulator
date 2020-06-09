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
    private int parentLabel;
    private double currentHeading;

    public JoinPatternRequest(int parentPatternLable, double currentHeading) {
        this.parentLabel = parentPatternLable;
        this.currentHeading = currentHeading;
    }

    public int getParentLabel() {
        return this.parentLabel;
    }
    
    public double getCurrentHeading() {
        return this.currentHeading;
    }

}
