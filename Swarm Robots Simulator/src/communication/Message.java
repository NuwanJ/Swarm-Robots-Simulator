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
public class Message {
    
    private MessageType type;
    private Robot sender;
    private Data data;

    public Message(MessageType type, Robot sender) {
        this.type = type;
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public Robot getSender() {
        return sender;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("%d : %s", sender.getId(), type.toString());
    }

    
    
}
