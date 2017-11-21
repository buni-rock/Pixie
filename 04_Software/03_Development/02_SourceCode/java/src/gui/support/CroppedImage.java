/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
