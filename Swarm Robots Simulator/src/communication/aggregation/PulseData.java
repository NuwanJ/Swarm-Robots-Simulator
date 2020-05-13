/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.aggregation;

import communication.Data;

/**
 *
 * @author Tharuka
 */
public class PulseData implements Data {
    
    private int clusterId;
    
    public PulseData(int dummyId) {
        this.clusterId = clusterId;
    }
    
    public int getClusterId() {
        return this.clusterId;   
    }        
        
}
