/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
