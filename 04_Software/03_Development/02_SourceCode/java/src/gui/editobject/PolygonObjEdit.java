/*
 * The MIT License
 *
 * Copyright 2018 Olimpia Popica, Benone Aligica
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
package gui.editobject;

import common.UserPreferences;
import common.Utils;
import gui.support.CustomTreeNode;
import gui.support.ObjectPolygon;
import gui.support.Objects;
import observers.ObservedActions;
import paintpanels.DrawConstants;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observable;

public class PolygonObjEdit extends BoxEdit {

    private final transient Polygon originalPolygon;

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
    public PolygonObjEdit(Frame parent,
            BufferedImage frameImage,
            Objects currentObj,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences) {
        super(parent, frameImage, currentObj, objectAttributes, actionOwner, objColorsList, userPreferences);

        drawingType = 0;

        jPRBOptions.setVisible(true);

        jRBOption1.setForeground(Color.blue);
        jRBOption1.setText("Edit");

        jRBOption2.setForeground(new Color(0, 153, 51));
        jRBOption2.setText("Add Vertex");

        jRBOption3.setForeground(Color.red);
        jRBOption3.setText("Remove Vertex");

        // keep a copy of the initial object
        originalPolygon = Utils.deepCopyPoly(((ObjectPolygon) currentObject).getPolygon());
    }

    @Override
    protected void drawObjContour(Graphics2D g2d) {
        // draw the polygon object
        setPolygon();
    }

    /**
     * Draw the outer box of the object; a rectangle which is resized to the
     * proper image size.
     *
     * @param g2d the graphics object
     * @return the rectangle drawn on the image
     */
    private void setPolygon() {
        if (dPPreviewImg == null) {
            return;
        }

        Polygon poly = Utils.deepCopyPoly(((ObjectPolygon) currentObject).getPolygon());

        // shift the polygon with the size of the border offset
        for (int index = 0; index < poly.npoints; index++) {
            poly.xpoints[index] -= displayBox.x;
            poly.ypoints[index] -= displayBox.y;
        }

        dPPreviewImg.setCurrentPolygon(poly, ((ObjectPolygon) currentObject).getPolygon());
    }

    @Override
    protected void applyObjProperties() {
        dPPreviewImg.setDrawType(DrawConstants.DrawType.EDIT_POLYGON_VERTICES);

        setPolygon();

        dPPreviewImg.setObjColor(objectColor);
    }

    @Override
    protected void updateObjInfo(int actionType) {
        this.drawingType = actionType;
        dPPreviewImg.setActionType(actionType);
        dPPreviewImg.setHighlightPoints(false);
        dPPreviewImg.resetPolygonIndex();
    }

    @Override
    protected void resetObjCoordinates() {
        // reload the initial state of the polygon
        ((ObjectPolygon) currentObject).setPolygon(Utils.deepCopyPoly(originalPolygon));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ObservedActions.Action) {
            ObservedActions.Action type = ((ObservedActions.Action) arg);

            if (ObservedActions.Action.UPDATE_POLYGON_VERTICES == type) {
                Polygon poly = dPPreviewImg.getCurrentPolygonImg();
                // shift the polygon with the size of the border offset
                for (int index = 0; index < poly.npoints; index++) {
                    poly.xpoints[index] += displayBox.x;
                    poly.ypoints[index] += displayBox.y;
                }

                ((ObjectPolygon) currentObject).setPolygon(Utils.deepCopyPoly(poly));
                ((ObjectPolygon) currentObject).computeOuterBBoxCurObj();
            }
        }

    }

}
