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
package gui.viewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Generate an icon with just one color, the one specified by the user.
 *
 * @author Olimpia Popica
 */
public class MonochromeIcon implements Icon {

    private final int width;
    private final int height;

    private Color color;
    private Color border;

    /**
     * Create an icon of the specified dimension and color.
     *
     * @param width  - the width of the icon
     * @param height - the height of the icon
     * @param color  - the color of the icon
     */
    public MonochromeIcon(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;

        this.border = color.darker();
    }

    /**
     * Sets color.
     *
     * @param c the c
     */
    public void setColor(Color c) {
        color = c;
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
     * Sets border color.
     *
     * @param c the c
     */
    public void setBorderColor(Color c) {
        border = c;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    /**
     * Sets border.
     *
     * @param border the border
     */
    public void setBorder(Color border) {
        this.border = border;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

        g.setColor(color);
        g.fillRect(x, y, width - 1, height - 1);

        g.setColor(border);
        g.drawRect(x, y, width - 1, height - 1);
    }

}
