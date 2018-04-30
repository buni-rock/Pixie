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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * The type Cropped image.
 *
 * @author Olimpia Popica
 */
public class CroppedImage {

    private Rectangle positionOrig;
    private Rectangle positionPanel;
    private BufferedImage cropImg;

    /**
     * Instantiates a new Cropped image.
     *
     * @param positionOrig  the position orig
     * @param positionPanel the position panel
     * @param cropImg       the crop img
     */
    public CroppedImage(Rectangle positionOrig, Rectangle positionPanel, BufferedImage cropImg) {
        this.positionOrig = positionOrig;
        this.positionPanel = positionPanel;
        this.cropImg = cropImg;
    }

    /**
     * Gets position orig.
     *
     * @return the position orig
     */
    public Rectangle getPositionOrig() {
        return positionOrig;
    }

    /**
     * Sets position orig.
     *
     * @param positionOrig the position orig
     */
    public void setPositionOrig(Rectangle positionOrig) {
        this.positionOrig = positionOrig;
    }

    /**
     * Gets position panel.
     *
     * @return the position panel
     */
    public Rectangle getPositionPanel() {
        return positionPanel;
    }

    /**
     * Sets position panel.
     *
     * @param positionPanel the position panel
     */
    public void setPositionPanel(Rectangle positionPanel) {
        this.positionPanel = positionPanel;
    }

    /**
     * Gets crop img.
     *
     * @return the crop img
     */
    public BufferedImage getCropImg() {
        return cropImg;
    }

    /**
     * Sets crop img.
     *
     * @param cropImg the crop img
     */
    public void setCropImg(BufferedImage cropImg) {
        this.cropImg = cropImg;
    }
}
