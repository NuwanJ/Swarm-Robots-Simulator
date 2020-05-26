/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.general;

import communication.Data;

/**
 *
 * @author Nadun
 */
public class DistanceData implements Data {

    private int distance;

    public DistanceData(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

}
