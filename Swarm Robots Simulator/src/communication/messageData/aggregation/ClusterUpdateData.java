
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class ClusterUpdateData implements Data {

    private int clusterId;
    private int newClusterSize;   
    private long currentTime;

    public ClusterUpdateData(int clusterId, int newClusterSize, long currentTime) {   
        this.clusterId = clusterId;
        this.newClusterSize = newClusterSize;       
        this.currentTime = currentTime;
    } 

    public int getClusterID() {
        return this.clusterId;
    }

    public int getNewClusterSize() {
        return this.newClusterSize;
    }
      
    public long getCurrentTime() {
        return this.currentTime;
    }
}
