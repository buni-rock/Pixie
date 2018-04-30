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
 * The type Brush options.
 *
 * @author Olimpia Popica
 */
public class BrushOptions {
    private int brushSize;
    private float brushDensity;

    /**
     * Instantiates a new Brush options.
     */
    public BrushOptions(){
        this.brushSize = 5;
        this.brushDensity = 0.5f;
    }

    /**
     * Instantiates a new Brush options.
     *
     * @param brushSize    the brush size
     * @param brushDensity the brush density
     */
    public BrushOptions(int brushSize, float brushDensity) {
        this.brushSize = brushSize;
        this.brushDensity = brushDensity;
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
    public float getBrushDensity() {
        return brushDensity;
    }

    /**
     * Sets brush density.
     *
     * @param brushDensity the brush density
     */
    public void setBrushDensity(float brushDensity) {
        this.brushDensity = brushDensity;
    }
    
}
