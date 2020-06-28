
package communication.messageData.aggregation;
import communication.Data;

/**
 *
 * @author Tharuka
 */

public class CheckPulseData implements Data{
    private int receiverId;
    private boolean checkFlag;
   
    public CheckPulseData(int receiverId, boolean checkFlag) {
        this.receiverId = receiverId;  
        this.checkFlag = checkFlag;
    }
    
    public int getReceiverId() {
        return this.receiverId; 
    }
       
    public boolean getCheckFlag() {
        return this.checkFlag; 
    }
    
}
