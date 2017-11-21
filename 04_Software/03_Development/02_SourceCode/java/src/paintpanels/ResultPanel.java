/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintpanels;

import common.ExportImage;
import common.Utils;
import gui.support.Objects;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Result panel.
 *
 * @author Olimpia Popica
 */
public class ResultPanel extends JPanel {

    /**
     * The original image loaded or cropped by the user.
     */
    private transient BufferedImage origImage;

    /**
     * The image drawn on the panel
     */
    private transient BufferedImage workImg;

    /**
     * The dimension of the panel
     */
    private final Dimension panelSize;

    /**
     * The map of objects segmented by the algorithm.
     */
    private byte[][] objMap;

    /**
     * The color of the object.
     */
    private Color objColor;

    /**
     * logger instance
     */
    private final transient Logger log = LoggerFactory.getLogger("paintpanels.ResultPanel");

    /**
     * Create an image panel which will display the image from the specified
     * path.
     *
     * @param origImg  - the image resulted after running the matting algorithm
     * @param panelRes - the maximum resolution of the panel where the image shall be displayed
     * @param objColor - the color of the object being segmented
     */
    public ResultPanel(BufferedImage origImg, Dimension panelRes, Color objColor) {
        origImage = origImg;

        // copy the original image in the work image
        workImg = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
        Utils.copySrcIntoDstAt(origImage, workImg);

        // set the object color
        this.objColor = objColor;

        // init object map
        objMap = new byte[origImg.getWidth()][origImg.getHeight()];

        //panel size
        this.panelSize = panelRes;

        initPanel();
    }

    /**
     * Configure the basic characteristics of the panel.
     */
    private void initPanel() {
        //characteristics of the panel
        this.setPreferredSize(panelSize);
        this.repaint();
        this.setVisible(true);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(workImg, 0, 0, panelSize.width, panelSize.height, this); // see javadoc for more info on the parameters            
    }

    /**
     * Change the displayed image with a new one.
     *
     * @param origImg the new image to be displayed
     */
    public void refreshImage(BufferedImage origImg) {
        origImage = origImg;

        // copy the original image in the work image
        workImg = new BufferedImage(origImage.getWidth(), origImage.getHeight(), origImage.getType());
        Utils.copySrcIntoDstAt(origImage, workImg);

        updateResultImg();

        repaint();
    }

    /**
     * Remove the image by making all the pixels of the specified color.
     *
     * @param color - the color which has to be on shown in the image
     */
    public void resetImage(Color color) {
        Graphics2D g2D = origImage.createGraphics();
        g2D.setColor(color);
        g2D.fillRect(0, 0, origImage.getWidth(), origImage.getHeight());
        g2D.dispose();
    }

    /**
     * Merges the given object map in the image object map, at the specified
     * coordinates.
     *
     * @param newObjMap - the object map that has to be merged in the result map
     * @param pos       - the position where the image has to be copied: info x - the top-left X coordinate where the image has to be copied info; y - the top-left Y coordinate where the image has to be copied info; width - the width of the image to be copied info; height - the height of the image to be copied
     * @param objId     - the object id as byte value (it is obtained by computing the image object id % 255)
     * @param mergeBkg  - true if both the background and object have to overwrite the existent content of the object map; false if only the object should be merged
     */
    public void mergeCrop(byte[][] newObjMap, Rectangle pos, long objId, boolean mergeBkg) {
        byte mapId = getByteObjId(objId);

        // update objectMap
        for (int y = 0; y < pos.getHeight(); y++) {
            for (int x = 0; x < pos.getWidth(); x++) {

                if (mergeBkg) {
                    // merge bakground in object map (overwrites the initial byte, no matter what was storred, with bkg)
                    objMap[x + pos.x][y + pos.y] = (newObjMap[x][y] > 0) ? mapId : (byte) 0;

                } else {
                    // merge just the pure object - when the pixel in the  object map is != 0
                    objMap[x + pos.x][y + pos.y] = ((objMap[x + pos.x][y + pos.y] == mapId) && (newObjMap[x][y] == 0)) ? 0 : objMap[x + pos.x][y + pos.y];
                    objMap[x + pos.x][y + pos.y] = (newObjMap[x][y] > 0) ? mapId : objMap[x + pos.x][y + pos.y];
                }
            }
        }
    }

    /**
     * Apply the object map to the original image in order to obtain the new
     * image with the preview of the segmentation.
     */
    public void updateResultImg() {
        // alpha blending: R = (foregroundRed*foregroundAlpha) + (backgroundRed*(1-foregroundAlpha))
        int[] bkg;  // backgroung
        int[] fg;   // foreground
        int[] RGB = new int[3];
        float alpha = 120.0f / 255.0f;

        if (objMap == null) {
            log.error("OBJECT MAP NULL!!!!!");
            return;
        }

        // apply background/object color on the image
        for (int y = 0; y < origImage.getHeight(); y++) {
            for (int x = 0; x < origImage.getWidth(); x++) {
                bkg = Utils.getRGB(origImage.getRGB(x, y));
                fg = Utils.getRGB(Utils.getDrawingColor((int) objMap[x][y], objColor).getRGB());

                RGB[0] = (int) ((fg[0] * alpha) + (bkg[0] * (1 - alpha)));
                RGB[1] = (int) ((fg[1] * alpha) + (bkg[1] * (1 - alpha)));
                RGB[2] = (int) ((fg[2] * alpha) + (bkg[2] * (1 - alpha)));

                workImg.setRGB(x, y, new Color(RGB[0], RGB[1], RGB[2]).getRGB());
            }
        }
    }

    /**
     * Update the result image based on the list of segmented objects.
     *
     * @param objList the list of segmented objects
     */
    public void updateResultImg(List<Objects> objList) {
        // alpha blending: R = (foregroundRed*foregroundAlpha) + (backgroundRed*(1-foregroundAlpha))
        int[] bkg;  // background
        int[] fg;   // foreground
        int[] RGB = new int[3];
        float alpha = 120.0f / 255.0f;

        // apply background/object color on the image
        for (int y = 0; y < origImage.getHeight(); y++) {
            for (int x = 0; x < origImage.getWidth(); x++) {
                bkg = Utils.getRGB(origImage.getRGB(x, y));

                if (objMap[x][y] != (byte) 0) {
                    fg = Utils.getRGB(getObjColor(objMap[x][y], objList).getRGB());
                } else {
                    fg = Utils.getRGB(Color.red.getRGB());
                }

                RGB[0] = (int) ((fg[0] * alpha) + (bkg[0] * (1 - alpha)));
                RGB[1] = (int) ((fg[1] * alpha) + (bkg[1] * (1 - alpha)));
                RGB[2] = (int) ((fg[2] * alpha) + (bkg[2] * (1 - alpha)));

                workImg.setRGB(x, y, new Color(RGB[0], RGB[1], RGB[2]).getRGB());
            }
        }

        repaint();
    }

    /**
     * Get the color of the segmented object, from the object list. Search the
     * object by id.
     *
     * @param id the object id for which the color is wanted
     * @param objList the list of segmented objects
     * @return the color of the wanted object
     */
    private Color getObjColor(byte id, List<Objects> objList) {
        for (Objects obj : objList) {
            if (getByteObjId(obj.getObjectId()) == id) {
                return obj.getColor();
            }
        }
        return objColor;
    }

    /**
     * Return the dimension of the image displayed on the panel.
     *
     * @return - the size of the image
     */
    public Dimension getImgDimensions() {
        return (new Dimension(origImage.getWidth(), origImage.getHeight()));
    }

    /**
     * Get the original image, not the one drawn.
     *
     * @return - the original image, before the segmentation was done
     */
    public BufferedImage getOriginalImage() {
        return origImage;
    }

    /**
     * Get the modified image, the one drawn on the panel.
     *
     * @return - the image, after the segmentation was done
     */
    public BufferedImage getWorkImg() {
        return workImg;
    }

    /**
     * Returns the map of object indexes, segmented by the algorithm.
     *
     * @return - the matrix of the objects in the image
     */
    public byte[][] getObjMap() {
        return objMap;
    }

    /**
     * Set the map of object indexes, segmented by the algorithm.
     *
     * @param objMap - the matrix representing the mapping of the objects in the image.
     */
    public void setObjMap(byte[][] objMap) {
        this.objMap = objMap;
    }

    /**
     * Export the image saved on the panel at the specified path and name.
     *
     * @param name - the name of the file to be exported
     */
    public void exportImage(String name) {
        ExportImage.exportAsPNG(workImg, name);
    }

    /**
     * Search for the object id in the given area of the object map and remove
     * it. It was cancel.
     *
     * @param objectId - the id of the object to be removed from the object map.
     * @param area     - the area where to search for the object id and remove it from the object map
     */
    public void removeObj(long objectId, Rectangle area) {
        if (area == null) {
            return;
        }

        // map the object id on byte
        byte mapId = getByteObjId(objectId);

        for (int y = 0; y < area.height; y++) {
            for (int x = 0; x < area.width; x++) {
                if (objMap[x + area.x][y + area.y] == mapId) {
                    // erase the object, set as background
                    objMap[x + area.x][y + area.y] = (byte) 0;
                }
            }
        }
    }

    /**
     * Returns the equivalent of the object Id, on byte. It is considered that
     * in the image there are no more than 255 different objects.
     *
     * @param objectId - the image id of the object
     * @return - the equivalent id on byte
     */
    private static byte getByteObjId(long objectId) {
        return ((byte) (1 + (objectId % 255)));
    }

    /**
     * Filter the object map array.
     * <p>
     * The filtering is done by sliding a window over the input array and
     * compute the sum of the values in the window (sum of 1s). If the sum is
     * smaller than a threshold, make the middle value of the window 0, else 1.
     * <p>
     * The filter aims to keep the white pixels which are in a neighbourhood of
     * other white pixels and discard the lonely pixels.
     */
    public void filterObjectMap() {
        Utils.filterObjectMap(objMap);
    }

    /**
     * Set the color of the object being segmented.
     *
     * @param objColor - the color of the object being segmented
     */
    public void setObjColor(Color objColor) {
        this.objColor = objColor;
    }

    /**
     * Search for the object id in the given area of the object map and change
     * it to the new specified one.
     *
     * @param oldObjId the old id of the object to be changed in the object map
     * @param newObjId the new id of the object
     */
    public void changeObjId(long oldObjId, long newObjId) {

        // map the object id on byte
        byte oldMapId = getByteObjId(oldObjId);
        byte newObjMap = getByteObjId(newObjId);

        for (int y = 0; y < origImage.getHeight(); y++) {
            for (int x = 0; x < origImage.getWidth(); x++) {
                if (objMap[x][y] == oldMapId) {
                    // erase the object, set as background
                    objMap[x][y] = newObjMap;
                }
            }
        }
    }
}
