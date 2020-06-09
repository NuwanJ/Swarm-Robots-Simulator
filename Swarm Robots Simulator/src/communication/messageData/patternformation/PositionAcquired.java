/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.patternformation;
import communication.Data;

/**
 *
 * @author mster
 */

public class PositionAcquired implements Data {

    private int label;

    public PositionAcquired(int label){
        this.label = label;
    }

    public double getLabel() {
        return this.label;
    }

}
