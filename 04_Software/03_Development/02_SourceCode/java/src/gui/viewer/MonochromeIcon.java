/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
