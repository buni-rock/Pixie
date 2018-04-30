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
