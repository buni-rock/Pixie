/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import common.ConstantsLabeling;
import common.Utils;
import library.Resize;
import commonsegmentation.ScribbleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The type Object scribble.
 *
 * @author Olimpia Popica
 */
public class ObjectScribble extends Objects {

    /**
     * The list of crops which together define the object.
     */
    private final List<CropObject> cropList;

    /**
     * The object map built from the crops of the object (for scribble object).
     */
    private byte[][] objectMap;

    /**
     * logger instance
     */
    private final Logger log = LoggerFactory.getLogger("gui.support.ObjectScribble");

    /**
     * Instantiates a new Object scribble.
     */
    public ObjectScribble() {
        super();
        this.cropList = new ArrayList<>();
    }

    @Override
    public void computeOuterBBoxCurObj() {
        generateObjMap();
    }

    /**
     * Returns the array of crop objects which was needed for the segmentation
     * of the object.
     *
     * @return - the list of crops for the object
     */
    public List<CropObject> getCropList() {
        return cropList;
    }

    /**
     * Adds a new crop in the list of crops needed to segment the object.
     *
     * @param cropObj - the crop object which has to be added in the list of crops needed to segment the object
     */
    public void addToCropList(CropObject cropObj) {
        this.cropList.add(cropObj);
    }

    /**
     * Returns the map of the object. It is computed based on the crop maps.
     * <p>
     * 1 = object; 0 = background
     *
     * @return - the map of the object
     */
    public byte[][] getObjectMap() {
        return objectMap;
    }

    /**
     * Generate the object map out of the object parts (crops). Compute and set
     * the outer box of the object.
     */
    public void generateObjMap() {
        byte[][] objMap;

        Rectangle objPos = computeCropsOuterBox();

        /*--------------------------STEP 1------------------------------------*/
        // create the object map with border, by merging all the crops
        byte[][] tempMap = new byte[objPos.width][objPos.height];
        int objBitCounter = 0;

        for (CropObject crop : cropList) {
            for (int y = 0; y < crop.getPositionOrig().height; y++) {
                // compute the y position in outer box coordinates
                int yPos = crop.getPositionCornerOrig().y - objPos.y + y;

                for (int x = 0; x < crop.getPositionOrig().width; x++) {
                    // compute the x position in outer box coordinates
                    int xPos = crop.getPositionCornerOrig().x - objPos.x + x;
                    // merge just the objects, not the background
                    if (crop.getObjectMap()[x][y] > (byte) 0) {
                        tempMap[xPos][yPos] = crop.getObjectMap()[x][y];
                        objBitCounter++;
                    }
                }
            }
        }

        // stop the computation if there are no object parts
        if (objBitCounter == 0) {
            return;
        }

        /*--------------------------STEP 2------------------------------------*/
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        // find the rectangle which contains the exact object
        for (int y = 0; y < objPos.height; y++) {
            for (int x = 0; x < objPos.width; x++) {
                // search for the edges of the object
                if (tempMap[x][y] > 0) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (y < minY) {
                        minY = y;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }
                    if (y > maxY) {
                        maxY = y;
                    }
                }
            }
        }

        /*--------------------------STEP 3------------------------------------*/
        int mapXPos = objPos.x + minX;
        int mapYPos = objPos.y + minY;

        // extract only the object and create its object map
        int mapWidth = Utils.limit(0, objPos.width - (objPos.width - maxX) - minX + 1, objPos.width);
        int mapHeight = Utils.limit(0, objPos.height - (objPos.height - maxY) - minY + 1, objPos.height);
        objMap = new byte[mapWidth][mapHeight];

        // copy the exact object in the obj map
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                objMap[x][y] = tempMap[minX + x][minY + y];
            }
        }

        /*--------------------------STEP TEST--------------------------------*/
        // test
        int sumObjMap = 0;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                sumObjMap += objMap[x][y];
            }
        }

        int sumTempObjMap = 0;
        for (int y = 0; y < objPos.height; y++) {
            for (int x = 0; x < objPos.width; x++) {
                sumTempObjMap += tempMap[x][y];
            }
        }

        if (sumObjMap != sumTempObjMap) {
            log.error("THE OBJECT MAP IS DIFFERENT!!!! {} obj - temp = ", (sumObjMap - sumTempObjMap));
        }

        /*--------------------------STEP 4------------------------------------*/
        // set the object outer box as the borders of the object map
        setOuterBBox(new Rectangle(mapXPos, mapYPos, mapWidth, mapHeight));

        // set the object map of the current object
        objectMap = objMap;
    }

    /**
     * Compute the outer box of the crops; the box which contains all the edges
     * of the crops.
     *
     * @return - the rectangle containing all the crops
     */
    private Rectangle computeCropsOuterBox() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (CropObject cropObj : cropList) {
            Rectangle cropPosImg = cropObj.getPositionOrig();

            int xLeftCrop = cropPosImg.x;
            int xRightCrop = cropPosImg.x + cropPosImg.width;
            int yLeftCrop = cropPosImg.y;
            int yRightCrop = cropPosImg.y + cropPosImg.height;

            if (minX > xLeftCrop) {
                minX = xLeftCrop;
            }

            if (maxX < xRightCrop) {
                maxX = xRightCrop;
            }

            if (minY > yLeftCrop) {
                minY = yLeftCrop;
            }

            if (maxY < yRightCrop) {
                maxY = yRightCrop;
            }
        }

        return (new Rectangle(minX, minY, maxX - minX, maxY - minY));
    }

    @Override
    public void move(int xOffset, int yOffset, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize) {

        // if the outer box is selected, move the whole object; else move only the selected crop
        Rectangle outerBBoxPanel = resizeRate.originalToResized(outerBBox);

        if (outerBBoxPanel.equals(coordPanelBox)) {
            // check to be in the image with the boundaries
            if ((outerBBox.x + xOffset >= 0)
                    && (outerBBox.y + yOffset >= 0)
                    && (outerBBox.x + outerBBox.width + xOffset <= frameSize.width)
                    && (outerBBox.y + outerBBox.height + yOffset <= frameSize.height)) {

                moveObject(xOffset, yOffset);

                // if the label was changed, it means that the user touched it, therefore the segmentation is manual
                segmentationSource = ConstantsLabeling.LABEL_SOURCE_MANUAL;
            }
        } else {
            for (CropObject cropObj : cropList) {
                Rectangle cropPanelPos = resizeRate.originalToResized(cropObj.getPositionOrig());
                if (cropPanelPos.equals(coordPanelBox)) {
                    Rectangle cropPosOrig = cropObj.getPositionOrig();

                    // check to be in the image with the boundaries
                    if ((cropPosOrig.x + xOffset >= 0)
                            && (cropPosOrig.y + yOffset >= 0)
                            && (cropPosOrig.x + cropPosOrig.width + xOffset < frameSize.width)
                            && (cropPosOrig.y + cropPosOrig.height + yOffset < frameSize.height)) {

                        cropObj.setPositionOrig(moveBox(xOffset, yOffset, cropPosOrig));
                    }
                }
            }
        }

    }

    /**
     * Moves the object completely in the specified direction (crops, scribbles,
     * outer box etc.).
     *
     * @param xOffset - how much should the object move on the X axis
     * @param yOffset - how much the object should move on the Y axis
     */
    public void moveObject(int xOffset, int yOffset) {
        // move all the crops
        cropList.stream().forEach((cropObj) -> {
            cropObj.setPositionOrig(moveBox(xOffset, yOffset, cropObj.getPositionOrig()));
        });

        // move the outer bounding box
        outerBBox = moveBox(xOffset, yOffset, outerBBox);
    }

    /**
     * Moves one box with the specified offset and returns the new image
     * coordinates.
     *
     * @param xOffset    - how much should the object move on the X axis
     * @param yOffset    - how much the object should move on the Y axis
     * @param boxPosOrig - the position of the selected box in image coordinates
     * @return - the new image coordinates of the box
     */
    public Rectangle moveBox(int xOffset, int yOffset, Rectangle boxPosOrig) {
        boxPosOrig.setLocation(boxPosOrig.x + xOffset, boxPosOrig.y + yOffset);

        return boxPosOrig;
    }

    @Override
    public boolean remove(Rectangle coordPanelBox, Resize resizeRate) {
        // if the outer box is selected, the whole object shall be erased, therefore it will be done in the parent method; else remove only the selected crop
        Rectangle outerBBoxPanel = resizeRate.originalToResized(outerBBox);
        if (outerBBoxPanel.equals(coordPanelBox)) {
            return false;
        } else {
            for (Iterator<CropObject> it = cropList.iterator(); it.hasNext();) {
                Rectangle cropPanelPos = resizeRate.originalToResized(it.next().getPositionOrig());
                if (cropPanelPos.equals(coordPanelBox)) {
                    it.remove();
                }
            }
            return (!cropList.isEmpty());
        }
    }

    @Override
    public void changeSize(int left, int top, int right, int bottom, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize) {
        for (CropObject cropObj : cropList) {
            Rectangle cropPanelPos = resizeRate.originalToResized(cropObj.getPositionOrig());

            if (cropPanelPos.equals(coordPanelBox)) {
                Rectangle cropPosOrig = cropObj.getPositionOrig();

                // check to be in the image with the boundaries
                int xTopRight = cropPosOrig.x + cropPosOrig.width;
                int xBottomRight = cropPosOrig.y + cropPosOrig.height;

                boolean leftCheck = ((cropPosOrig.x + left >= 0)
                        && (cropPosOrig.x + left < xTopRight));

                boolean topCheck = ((cropPosOrig.y + top >= 0)
                        && (cropPosOrig.y + top < xBottomRight));

                boolean rightCheck = ((xTopRight + right > cropPosOrig.x)
                        && (xTopRight + right < frameSize.width));

                boolean bottomCheck = ((xBottomRight + bottom > cropPosOrig.y)
                        && (xBottomRight + bottom < frameSize.height));

                if (leftCheck && topCheck && rightCheck && bottomCheck) {
                    // change the outer bounding box size; the crop will move as well because it is the same object
                    changeSize(left, top, right, bottom, cropPosOrig);

                    // remove the scribbles which are out of the boundaries
                    removeScribbles(cropObj);

                    // if the label was changed, it means that the user touched it, therefore the segmentation is manual
                    segmentationSource = ConstantsLabeling.LABEL_SOURCE_MANUAL;
                }
            }
        }
    }

    /**
     * Changes the size of the box with the specified offsets.
     *
     * @param left - how much should the object be modified on the left side
     * @param top - how much should the object be modified on the top part
     * @param right - how much should the object be modified on the right side
     * @param bottom - how much should the object be modified on the bottom part
     * @param bboxOrigPos - the initial position of the selected box in image
     * coordinates
     */
    private void changeSize(int left, int top, int right, int bottom, Rectangle bboxOrigPos) {
        bboxOrigPos.setBounds(bboxOrigPos.x + left, bboxOrigPos.y + top, bboxOrigPos.width + right - left, bboxOrigPos.height + bottom - top);
    }

    /**
     * Removes the scribbles which no longer fit in the image; the ones which
     * would generate a coordinates outer box exception.
     *
     * @param cropObj - the object for which the scribbles have to be reviewed
     */
    private void removeScribbles(CropObject cropObj) {
        for (Iterator<ScribbleInfo> it = cropObj.getScribbleList().iterator(); it.hasNext();) {
            ScribbleInfo si = it.next();

            if ((si.getImgPosX() >= cropObj.getPositionOrig().width)
                    || (si.getImgPosY() >= cropObj.getPositionOrig().height)) {
                // remove the scribble
                it.remove();
            }

        }
    }

    @Override
    public boolean contains(Rectangle coordPanelBox, Resize resizeRate) {
        for (CropObject cropObj : cropList) {
            Rectangle cropPanelPos = resizeRate.originalToResized(cropObj.getPositionOrig());
            if (cropPanelPos.equals(coordPanelBox)) {
                return true;
            }
        }

        Rectangle outerBBoxPanel = resizeRate.originalToResized(outerBBox);

        return outerBBoxPanel.equals(coordPanelBox);
    }

    /**
     * Returns the crop which is located at the same coordinates as the
     * specified ones.
     *
     * @param coordPanelBox - the position where the crop is located in panel coordinates
     * @param resizeRate    - the resize ratio between the image and the panel
     * @return - the crop found at the wanted location
     */
    public CropObject getCrop(Rectangle coordPanelBox, Resize resizeRate) {
        for (CropObject cropObj : cropList) {
            Rectangle cropPanelPos = resizeRate.originalToResized(cropObj.getPositionOrig());
            if (cropPanelPos.equals(coordPanelBox)) {
                return cropObj;
            }
        }

        return null;
    }
}
