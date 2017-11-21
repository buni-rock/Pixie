/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import java.awt.Point;
import java.awt.Color;

/**
 * Encapsulate the needed information to be able to display scribbles. They will
 * not be used in the algorithms; just for display purposes.
 *
 * @author Olimpia Popica
 */
public class DisplayScribbles {

    private final Color color;            // the color of the scribble
    private final Point panelPos;    // the position of the pixel in the panel

    /**
     * Instantiates a new Display scribbles.
     *
     * @param color    the color
     * @param panelPos the panel pos
     */
    public DisplayScribbles(Color color, Point panelPos) {
        this.color = color;
        this.panelPos = panelPos;
    }

    /**
     * Gets color.
     *
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets panel pos.
     *
     * @return the panel pos
     */
    public Point getPanelPos() {
        return panelPos;
    }
}
