/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

/**
 * The type Object preferences.
 *
 * @author Benone Aligica
 */
public class ObjectPreferences {

    /**
     * Zooming index saved by the user.
     */
    private int zoomingIndex;

    /**
     * The size of border, in pixels.
     */
    private int borderSize;

    /**
     * Instantiates a new Object preferences.
     */
    public ObjectPreferences() {
        this.zoomingIndex = Integer.MAX_VALUE;

        this.borderSize = 0;
    }

    /**
     * Gets the zooming index.
     *
     * @return user 's prefer zooming index
     */
    public int getZoomingIndex() {
        return zoomingIndex;
    }

    /**
     * Sets the zooming index.
     *
     * @param zoomingIndex user's prefer zooming index
     */
    public void setZoomingIndex(int zoomingIndex) {
        this.zoomingIndex = zoomingIndex;
    }

    /**
     * Return the size of the border, in pixels.
     *
     * @return the size of the border, in pixels
     */
    public int getBorderSize() {
        return borderSize;
    }

    /**
     * Set the size of the border, in pixels.
     *
     * @param borderSize the size of the border, in pixels
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

}
