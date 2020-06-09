/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.patternformation;
import communication.Data;

/**
 *
 * @author Mahendra
 */
public class JoinPatternResponse implements Data{

    private boolean response;

    public JoinPatternResponse(boolean status) {
        this.response = status;
    }

    public boolean getResponse() {
        return this.response;
    }
}
