/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class GoAwayData implements Data{
    private int receiverId;
   
    public GoAwayData(int receiverId) {
        this.receiverId = receiverId;        
    }
    
    public int getreceiverId() {
        return this.receiverId;   
    }   
}
