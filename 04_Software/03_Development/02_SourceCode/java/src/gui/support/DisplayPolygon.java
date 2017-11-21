/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import java.awt.Color;
import java.awt.Polygon;

/**
 * The type Display polygon.
 *
 * @author Olimpia Popica
 */
public class DisplayPolygon {

    /**
     * The color of the polygon.
     */
    private final Color color;

    /**
     * The polygon to be displayed.
     */
    private final Polygon panelPolygon;

    /**
     * Shows how the representation of the polygon shall be: true = the line
     * drawn is dashed, false = the line drawn is solid.
     */
    private final boolean useDashedLine;    // true = the line drawn is dashed

    /**
     * Instantiates a new Display polygon.
     *
     * @param color         the color
     * @param panelPolygon  the panel polygon
     * @param useDashedLine the use dashed line
     */
    public DisplayPolygon(Color color, Polygon panelPolygon, boolean useDashedLine) {
        this.color = color;
        this.panelPolygon = panelPolygon;
        this.useDashedLine = useDashedLine;
    }

    /**
     * The color of the polygon.
     *
     * @return the color used to draw the polygon
     */
    public Color getColor() {
        return color;
    }

    /**
     * The polygon to be displayed.
     *
     * @return the polygon containing the drawn points by the user
     */
    public Polygon getPanelPolygon() {
        return panelPolygon;
    }

    /**
     * Shows how the representation of the polygon shall be.
     *
     * @return true = the line drawn is dashed, false = the line drawn is solid
     */
    public boolean isUseDashedLine() {
        return useDashedLine;
    }

}
