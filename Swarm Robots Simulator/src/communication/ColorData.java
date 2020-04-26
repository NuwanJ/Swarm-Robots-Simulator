/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package communication;

import java.awt.Color;

/**
 *
 * @author Nadun
 */
public class ColorData extends Color implements Data {

    public ColorData(Color c) {
        super(c.getRed(), c.getGreen(), c.getBlue());
    }

}
