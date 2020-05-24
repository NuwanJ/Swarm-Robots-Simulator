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
    private int senderJoinedId;
    private int nextJoinId;
    
    public JoinPattern(int senderJoinedId, int nextjoinId){
        this.senderJoinedId = senderJoinedId;
        this.nextJoinId = nextjoinId;
    }
    
    public int getSenderId(){
        return this.senderJoinedId;
    }
    
    public int getNextJoinId(){
        return this.nextJoinId;
    }
    
}
