/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

/**
 *
 * @author Nadun
 */
public interface Communication {
    
    public void broadcastMessage(MessageType header);
    
    public Message recieveMessage();
    
}
