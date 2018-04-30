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

import common.Utils;
import gui.actions.GUIController;
import java.awt.Dimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Panel resolution.
 *
 * @author Olimpia Popica
 */
public final class PanelResolution {

    /**
     * logger instance
     */
    private static final Logger log = LoggerFactory.getLogger(GUIController.class);

    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors.
     *
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private PanelResolution() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Computes the optimal size of the panel where the image should be
     * displayed.
     *
     * @param imageSize    the size of the image to be displayed
     * @param maxPanelSize the maximum possible size of the panel where the image can be displayed
     * @return the optimal size of the panel, according to the screen resolution
     */
    public static Dimension computeOptimalPanelSize(Dimension imageSize, Dimension maxPanelSize) {
        int width = 0;
        int height = 0;
        double aspectRatio = (double) imageSize.width / (double) imageSize.height;
        Dimension optimalSize = new Dimension(imageSize);

        if ( // image bigger than panel
                ((imageSize.width > maxPanelSize.width)
                && (imageSize.height > maxPanelSize.height))
                //panel bigger than image
                || ((imageSize.width <= maxPanelSize.width)
                && (imageSize.height <= maxPanelSize.height))) {
            // image bigger than panel

            //fix height, compute width
            int tempWidth = computeWidth(maxPanelSize.height, aspectRatio);

            // check if the image fits in the panel
            if (Utils.checkPlausability(new Dimension(tempWidth, maxPanelSize.height), maxPanelSize)) {
                width = tempWidth;
                height = maxPanelSize.height;
            } else {
                //fix width, compute height
                int tempHeight = computeHeigth(maxPanelSize.width, aspectRatio);

                // check if the image fits in the panel
                if (Utils.checkPlausability(new Dimension(maxPanelSize.width, tempHeight), maxPanelSize)) {
                    width = maxPanelSize.width;
                    height = tempHeight;
                }
            }

        } else if ((imageSize.width > maxPanelSize.width)
                && (imageSize.height <= maxPanelSize.height)) {
            //image width bigger than panel width

            //fix width, compute height
            int tempHeight = computeHeigth(maxPanelSize.width, aspectRatio);

            // check if the image fits in the panel
            if (Utils.checkPlausability(new Dimension(maxPanelSize.width, tempHeight), maxPanelSize)) {
                width = maxPanelSize.width;
                height = tempHeight;
            }

        } else if ((imageSize.width <= maxPanelSize.width)
                && (imageSize.height > maxPanelSize.height)) {
            //image height bigger than panel height

            //fix height, compute width
            int tempWidth = computeWidth(maxPanelSize.height, aspectRatio);

            // check if the image fits in the panel
            if (Utils.checkPlausability(new Dimension(tempWidth, maxPanelSize.height), maxPanelSize)) {
                width = tempWidth;
                height = maxPanelSize.height;
            }
        }

        if (width > 0 && height > 0) {
            optimalSize = new Dimension(width, height);
        } else {
            log.error("The resize of the image to fit the panel failed!!!");
        }

        return optimalSize;
    }

    /**
     * Compute the height, keeping the width and aspect ratio constant.
     */
    private static int computeHeigth(int width, double aspectRatio) {
        return (int) (width / aspectRatio);
    }

    /**
     * Compute the width, keeping the height and aspect ratio constant.
     */
    private static int computeWidth(int height, double aspectRatio) {
        return (int) (height * aspectRatio);
    }
}
