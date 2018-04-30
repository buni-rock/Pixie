/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.editobject;

import common.UserPreferences;
import common.Utils;
import gui.editobject.BoxEdit;
import gui.support.CustomTreeNode;
import gui.support.ObjectScribble;
import gui.support.Objects;
import observers.ObservedActions;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

public class SemanticObjEdit extends BoxEdit {

    /**
     *
     * @param parent the parent component of the dialog
     * @param frameImage the original image, in original size
     * @param currentObj the object being segmented
     * @param objectAttributes the list of object attributes: type, class, value
     * @param actionOwner the scope of the dialog: create new box, edit existing
     * one
     * @param objColorsList the list of already used colors (for other objects)
     * @param userPreferences user preferences regarding application
     * configuration
     */
    public SemanticObjEdit(Frame parent,
            BufferedImage frameImage,
            Objects currentObj,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences) {
        super(parent, frameImage, currentObj, objectAttributes, actionOwner, objColorsList, userPreferences);

        jBFilterObjMap.setVisible(true);
    }

    @Override
    protected void drawObjContour(Graphics2D g2d) {
        // draw the outer box of the object
        Rectangle bBox = drawOuterBox(g2d);

        // display the object map if the object is scribble
        displayObjMap(bBox);
    }

    @Override
    protected void filterObjMap() {
        // return if the object is not scribble
        if (!(currentObject instanceof ObjectScribble)) {
            return;
        }

        Utils.filterObjectMap(((ObjectScribble) currentObject).getObjectMap());

        // show the new image
        showImage();
    }

    /**
     * Displays the object map of the object, as a merge between the color of
     * the object and the color of the object segmentation.
     */
    private void displayObjMap(Rectangle bBox) {
        // return if the object is not scribble
        if (!(currentObject instanceof ObjectScribble)) {
            return;
        }

        // get the object map
        byte[][] objMap = ((ObjectScribble) currentObject).getObjectMap();

        // return if the object map is null
        if (objMap == null) {
            return;
        }

        // The formula for alpha blending: R = (foregroundRed*foregroundAlpha) + (backgroundRed*(1-foregroundAlpha))
        int[] bkg;  // backgroung
        int[] fg;   // foreground
        int[] rgb = new int[3];
        float alpha = 120.0f / 255.0f;

        // apply background/object color on the image
        for (int y = bBox.y; y < (bBox.y + bBox.height); y++) {
            for (int x = bBox.x; x < (bBox.x + bBox.width); x++) {

                // compute the position in the original object map matrix
                Point transformedPoint = new Point(x - bBox.x, y - bBox.y);
                int posX = transformedPoint.x;
                int posY = transformedPoint.y;

                if (objMap[posX][posY] > 0) {
                    bkg = Utils.getRGB(origImg.getRGB(x, y));
                    fg = Utils.getRGB(objectColor.getRGB());

                    rgb[0] = (int) ((fg[0] * alpha) + (bkg[0] * (1 - alpha)));
                    rgb[1] = (int) ((fg[1] * alpha) + (bkg[1] * (1 - alpha)));
                    rgb[2] = (int) ((fg[2] * alpha) + (bkg[2] * (1 - alpha)));

                    workImg.setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
                }
            }
        }
    }

}
