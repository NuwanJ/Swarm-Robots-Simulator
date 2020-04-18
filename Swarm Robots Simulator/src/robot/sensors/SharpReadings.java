/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.sensors;

import java.awt.Color;

/**
 *
 * @author Nadun
 */
public interface SharpReadings {
    
    public double measureDistance();
    
    public Color readColor();
    
}
