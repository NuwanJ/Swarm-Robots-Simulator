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
public class JoinPattern implements Data {
    private int myPatternLabel;
    private int nextPatternLabel;
    
    public JoinPattern(int myPatternLabel, int nextPatternLabel){
        this.myPatternLabel = myPatternLabel;
        this.nextPatternLabel = nextPatternLabel;
    }
    
    public int getMyPatternLabel(){
        return this.myPatternLabel;
    }
    
    public int getNextPatternLabel(){
        return this.nextPatternLabel;
    }
    
}
