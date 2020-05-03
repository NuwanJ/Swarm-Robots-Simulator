
package communication.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class InfoData implements Data{
    private int receiverId;
    private int clusterId;
    private int clusterSize;
    
    public InfoData(int receiverId, int clusterId, int clusterSize) {
        this.receiverId = receiverId;
        this.clusterId = clusterId;
        this.clusterSize = clusterSize;
    }
    
    public int getreceiverId() {
        return this.receiverId;   
    }
    
    public int getClusterId() {
        return this.clusterId;   
    }
        
    public int getClusterSize() {
        return this.clusterSize;   
    }
}
