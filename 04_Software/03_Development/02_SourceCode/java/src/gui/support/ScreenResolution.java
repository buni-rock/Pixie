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
