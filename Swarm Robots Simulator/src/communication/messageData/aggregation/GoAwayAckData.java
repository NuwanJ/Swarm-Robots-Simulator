
package communication.messageData.aggregation;
import communication.Data;

/**
 *
 * @author Tharuka
 */
public class GoAwayAckData implements Data{
 
    int receiverId;
   
    public GoAwayAckData(int receiverId) {
        this.receiverId = receiverId;              
    }
    
    public int getreceiverId() {
        return this.receiverId;   
    }    
    
}
