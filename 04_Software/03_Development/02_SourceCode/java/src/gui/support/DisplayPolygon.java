/*
 * The MIT License
 *
 * Copyright 2017 Olimpia Popica, Benone Aligica
 *
 * Contact: contact[a(t)]annotate[(d){o}t]zone
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
