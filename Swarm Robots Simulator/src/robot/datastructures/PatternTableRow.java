/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.datastructures;

import communication.Data;

/**
 *
 * @author Mahendra, Nadun, Tharuka
 */
public class PatternTableRow implements Data{
    private int[] patternNeighbourInfo = new int[4];
             
    
    public PatternTableRow(int parentOneId, int parentTwoId, int distantToParentOne,
    int distantToParentTwo){
        this.patternNeighbourInfo[0] =  parentOneId;
        this.patternNeighbourInfo[1] = distantToParentOne;
        this.patternNeighbourInfo[2] = parentTwoId;
        this.patternNeighbourInfo[3] = distantToParentTwo;
    }
}
