/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Mahendra
 */
public class ChildMap {

    private HashMap<Integer, ChildInfo> childMap;

    public ChildMap(HashMap<Integer, ChildInfo> map) {
        this.childMap = map;
    }

    public int getNextPatternLabel() {
        int label = -1;

        for (Map.Entry<Integer, ChildInfo> entry : childMap.entrySet()) {
            int childLabel = entry.getKey();
            ChildInfo info = (ChildInfo) entry.getValue();
            if (info.getAcquiredStatus() == false) {
                label = childLabel;
                break;
                //System.out.println("Parent :"+ parentLabel + "child "+ childLabel);
            }
            // System.out.println("Key = " + entry.getKey() + ", Value = " + ((TableRow) entry.getValue()).getParentLabel());
        }

        return label;
    }
    
    public void updateMap(int childLabel){
       ChildInfo info = childMap.get(childLabel);
       info.setAcquiredStatus(true);
    }
}
