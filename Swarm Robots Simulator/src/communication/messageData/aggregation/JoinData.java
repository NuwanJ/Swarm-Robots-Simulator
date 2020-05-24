
package communication.messageData.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class JoinData implements Data{
    private int receiverId;    
    
    public JoinData(int clusterId) {
        this.receiverId = receiverId;             
    }
    
    public int getreceiverId() {
        return this.receiverId;   
    }   
   
}