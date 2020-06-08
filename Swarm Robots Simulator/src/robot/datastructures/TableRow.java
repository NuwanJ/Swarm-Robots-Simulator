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
    private double xDistance;
    private double yDistance;
    
    public TableRow(int parent, double x, double y){
        this.parentLabel = parent;
        xDistance = x;
        yDistance = y;
    }
    
    public int getParentLabel(){
        return this.parentLabel;
    }
    
    public double getXCoordinate(){
        return this.xDistance;
    }
    
    public double getYCoordinate(){
        return this.yDistance;
    }

}
