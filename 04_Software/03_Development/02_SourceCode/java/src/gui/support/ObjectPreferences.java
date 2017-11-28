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
