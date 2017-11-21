/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
