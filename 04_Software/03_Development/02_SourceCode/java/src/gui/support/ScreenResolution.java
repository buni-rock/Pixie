/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;

/**
 * The type Screen resolution.
 *
 * @author Olimpia Popica
 */
public class ScreenResolution {

    /**
     * The resolution of the current screen
     */
    private Dimension resolution;

    /**
     * Constructor where the resolution is computed.
     *
     * @param comp - the frame of the gui - needed to get the resolution of the screen where the app is showed
     */
    public ScreenResolution(Component comp) {
        //set the current screen resolution                
        GraphicsConfiguration graphicsCfg = comp.getGraphicsConfiguration();
        DisplayMode displayMode = graphicsCfg.getDevice().getDisplayMode();
        resolution = new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }

    /**
     * Gets screen resolution.
     *
     * @return Returns the value of the resolution of the screen.
     */
    public Dimension getScreenResolution() {
        return resolution;
    }

    /**
     * Gets screen width.
     *
     * @return the width of the screen
     */
    public int getScreenWidth() {
        return resolution.width;
    }

    /**
     * Gets screen height.
     *
     * @return the height of the screen
     */
    public int getScreenHeight() {
        return resolution.height;
    }

    /**
     * Saves the resolution of the screen where the component is showed.
     *
     * @param comp - the frame for which the resolution of the containing screen is needed
     */
    public void setScreenResolution(Component comp) {
        GraphicsConfiguration graphicsCfg = comp.getGraphicsConfiguration();
        DisplayMode displayMode = graphicsCfg.getDevice().getDisplayMode();
        resolution = new Dimension(displayMode.getWidth(), displayMode.getHeight());
    }
}
