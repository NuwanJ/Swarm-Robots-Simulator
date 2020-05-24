
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
    private int receiverId;

    public ClusterUpdateData(int clusterId, int newClusterSize, int receiverId) {   
        this.clusterId = clusterId;
        this.newClusterSize = newClusterSize;
        this.receiverId = receiverId;
    } 

    public int getClusterID() {
        return this.clusterId;
    }

    public int getNewClusterSize() {
        return this.newClusterSize;
    }

    public int getReceiverId() {
        return this.receiverId;
    }
}
