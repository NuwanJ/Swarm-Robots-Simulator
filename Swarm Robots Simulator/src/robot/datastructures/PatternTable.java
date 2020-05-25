/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import communication.Data;
import configs.Settings;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
public class PatternTable implements Data{
    private HashMap<Integer,TableRow> patterntable;
    
    public PatternTable(){
        patterntable = new HashMap<Integer,TableRow>();
        createPatternTable();
    }
    
    public void createPatternTable(){
           TableRow row = new TableRow(0, 10, 0);
           patterntable.put(1, row);
           
           row = new TableRow(1, 10, 0);
           patterntable.put(2, row);
           
           row = new TableRow(2, 10, 0);
           patterntable.put(3, row);
           
           row = new TableRow(3, 10, 0);
           patterntable.put(4, row);
           
           row = new TableRow(4, 10, 0);
           patterntable.put(5, row);
           
           row = new TableRow(5, 10, 0);
           patterntable.put(6, row);
    }
   
}
