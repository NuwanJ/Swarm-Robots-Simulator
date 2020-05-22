/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication.messageData.general;

import java.awt.Color;
import communication.Data;
/**
 *
 * @author Nadun, Mahendra, Tharuka
 */
public class ColorData extends Color implements Data {

    public ColorData(Color c) {
        super(c.getRed(), c.getGreen(), c.getBlue());
    }

}
