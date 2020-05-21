/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import robot.Robot;

/**
 *
 * @author Nadun
 */
public interface Communication {
    
    public void broadcastMessage(Message message);
    
    public void broadcastMessage(MessageType message);
    
    public void sendMessage(MessageType message, Robot receiver, Data data);
    
    public void sendMessage(MessageType message, Robot receiver);
    
    public Message recieveMessage(int iRIndex);
    
    public void resetReceivers(int index);
    
    public void processMessage(Message message);
    
}
