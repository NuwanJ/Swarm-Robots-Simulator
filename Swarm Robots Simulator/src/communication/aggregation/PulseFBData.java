

package communication.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class PulseFBData implements Data {
    private int clusterId;
    private int senderId;
    private int receiverId;
    private double joiningProb;
    
    public PulseFBData(int clusterId, int senderId, int receiverId, double joiningProb) {
        this.clusterId = clusterId;
        this.receiverId = receiverId;  
        this.joiningProb = joiningProb;
         this.senderId = senderId;
    }
    
    public int getClusterID() {
        return this.clusterId;   
    }
    
    public int getSenderId() {
        return this.senderId;   
    }
    
    
    public int getReceiverId() {
        return this.receiverId;   
    }
     
    public double getJoiingProb() {
        return this.joiningProb;   
    } 
}
