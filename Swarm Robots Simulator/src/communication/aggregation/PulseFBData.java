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
    private int clusterSize;

    public PulseFBData(int clusterId, int senderId, int receiverId, double joiningProb, int clusterSize) {
        this.clusterId = clusterId;
        this.receiverId = receiverId;
        this.joiningProb = joiningProb;
        this.senderId = senderId;
        this.clusterSize = clusterSize;
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

    public int getClusterSize() {
        return this.clusterSize;
    }
}
