package communication.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class LeaveData implements Data{
    
    private int clusterId;    
    
    public LeaveData(int clusterId) {
        this.clusterId = clusterId;             
    }
    
    public int getClusterID() {
        return this.clusterId;   
    }   
   
}
