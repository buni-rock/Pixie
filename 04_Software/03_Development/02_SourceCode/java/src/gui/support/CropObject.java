/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import library.Resize;
import commonsegmentation.ScribbleInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Crop object.
 *
 * @author Olimpia Popica
 */
public class CropObject {

    /**
     * The position of the crop in the original image coordinates.
     */
    private Rectangle positionOrig;

    /**
     * The list of scribbles drawn on the image in order to segment the wanted
     * object.
     */
    private List<ScribbleInfo> scribbleList;

    /**
     * The map of the object after the segmentation algorithm was called.
     */
    private byte[][] objectMap;

    /**
     * Counts how many time the object map was filtered
     */
    private int filterCounter;

    /**
     * Instantiates a new Crop object.
     */
    public CropObject() {
        filterCounter = 0;
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
     * Gets position corner orig.
     *
     * @return the position corner orig
     */
    public Point getPositionCornerOrig() {
        return new Point(positionOrig.x, positionOrig.y);
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
     * Gets scribble list.
     *
     * @return the scribble list
     */
    public List<ScribbleInfo> getScribbleList() {
        return scribbleList;
    }

    /**
     * Create a display scribble list just for displaying purposes.
     *
     * @param cropPos       - the position of the crop in the panel
     * @param scribbleColor - the color of the scribble is known from the object level
     * @param resize        - the resize ratio
     * @return - the list of scribbles to be displayed, in panel coordinates
     */
    public List<DisplayScribbles> getDisplayScribbleList(Rectangle cropPos, Color scribbleColor, Resize resize) {
        List<DisplayScribbles> displayScribbles = new ArrayList<>();

        if (scribbleList != null) {
            for (ScribbleInfo si : scribbleList) {
                Color color = (si.getDrawingType() == 0) ? Color.red : scribbleColor;

                int x = resize.originalToResized(si.getImgPosX()) + cropPos.x;
                int y = resize.originalToResized(si.getImgPosY()) + cropPos.y;
                Point scribblePos = new Point(x, y);
                DisplayScribbles ds = new DisplayScribbles(color, scribblePos);
                displayScribbles.add(ds);
            }
        }
        return displayScribbles;
    }

    /**
     * Sets scribble list.
     *
     * @param scribbleList the scribble list
     */
    public void setScribbleList(List<ScribbleInfo> scribbleList) {
        this.scribbleList = scribbleList;
    }

    /**
     * Get object map byte [ ] [ ].
     *
     * @return the byte [ ] [ ]
     */
    public byte[][] getObjectMap() {
        return objectMap;
    }

    /**
     * Sets object map.
     *
     * @param objectMap the object map
     */
    public void setObjectMap(byte[][] objectMap) {
        this.objectMap = objectMap;
    }

    @Override
    public boolean equals(Object posOrig) {
        if (posOrig instanceof Rectangle) {
            return this.positionOrig.equals((Rectangle) posOrig);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * The filter was used, therefore the counter of how many times was it used
     * shall increase.
     */
    public void incrementFilterCount() {
        filterCounter++;
    }

    /**
     * Resets the filter for keeping the count of the filter usages only for the
     * saved crop.
     */
    public void resetFilterCount() {
        filterCounter = 0;
    }

    /**
     * Gets filter counter.
     *
     * @return - how many times was the filter called for the final output map
     */
    public int getFilterCounter() {
        return filterCounter;
    }

}
