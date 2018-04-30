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

/**
 * The type Crop window config.
 *
 * @author Olimpia Popica
 */
public class CropWindowConfig {

    /**
     * The Merge bkg.
     */
    boolean mergeBKG;
    /**
     * The Brush size.
     */
    int brushSize = 5;
    /**
     * The Brush density.
     */
    int brushDensity = 20;
    /**
     * The Object color.
     */
    Color objectColor;
    /**
     * The Object id.
     */
    long objectId;

    /**
     * Instantiates a new Crop window config.
     */
    public CropWindowConfig() {
    }

    /**
     * Instantiates a new Crop window config.
     *
     * @param mergeBKG     the merge bkg
     * @param brushSize    the brush size
     * @param brushDensity the brush density
     * @param brushColor   the brush color
     * @param objectId     the object id
     */
    public CropWindowConfig(boolean mergeBKG, int brushSize, int brushDensity, Color brushColor, long objectId) {
        this.mergeBKG = mergeBKG;
        this.brushSize = brushSize;
        this.brushDensity = brushDensity;
        this.objectColor = brushColor;
        this.objectId = objectId;
    }

    /**
     * Is merge bkg boolean.
     *
     * @return the boolean
     */
    public boolean isMergeBKG() {
        return mergeBKG;
    }

    /**
     * Sets merge bkg.
     *
     * @param mergeBKG the merge bkg
     */
    public void setMergeBKG(boolean mergeBKG) {
        this.mergeBKG = mergeBKG;
    }

    /**
     * Gets brush size.
     *
     * @return the brush size
     */
    public int getBrushSize() {
        return brushSize;
    }

    /**
     * Sets brush size.
     *
     * @param brushSize the brush size
     */
    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    /**
     * Gets brush density.
     *
     * @return the brush density
     */
    public int getBrushDensity() {
        return brushDensity;
    }

    /**
     * Sets brush density.
     *
     * @param brushDensity the brush density
     */
    public void setBrushDensity(int brushDensity) {
        this.brushDensity = brushDensity;
    }

    /**
     * Gets object color.
     *
     * @return the object color
     */
    public Color getObjectColor() {
        return objectColor;
    }

    /**
     * Sets object color.
     *
     * @param objectColor the object color
     */
    public void setObjectColor(Color objectColor) {
        this.objectColor = objectColor;
    }

    /**
     * Gets object id.
     *
     * @return the object id
     */
    public long getObjectId() {
        return objectId;
    }

    /**
     * Sets object id.
     *
     * @param objectId the object id
     */
    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

}
