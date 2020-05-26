/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import java.util.Hashtable;

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
public class TableRow {
    private int parentLabel;
    private Hashtable<String,Double> distanceFromParent;
    
    public TableRow(int parent, double x, double y){
        this.parentLabel = parent;
        distanceFromParent = new Hashtable<String,Double>();
        distanceFromParent.put("x", x);
        distanceFromParent.put("y", y);
    }
    
    public int getParentLabel(){
        return this.parentLabel;
    }
    
    public double getXCoordinate(){
        return this.distanceFromParent.get("x");
    }
    
    public double getYCoordinate(){
        return this.distanceFromParent.get("y");
    }

}
