/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.editobject;


import common.Constants;
import common.ConstantsLabeling;
import common.UserPreferences;
import common.Utils;
import gui.support.CustomTreeNode;
import gui.support.Objects;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import library.BoxLRTB;
import observers.ObservedActions;
import paintpanels.DrawConstants;

/**
 *
 * @author Olimpia Popica
 */
public class BoxEdit extends EditWindow {

    /**
     * The box which represents the displayed image. It includes the object
     * outer box and the border.
     */
    protected Rectangle displayBox;

    /**
     * The size of the border for the image. The number of extra pixels added to
     * the labeled image, in each direction.
     */
    private transient BoxLRTB borderSize;

    /**
     * The selected object for being edited.
     */
    protected transient Objects currentObject;

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
    public BoxEdit(Frame parent,
            BufferedImage frameImage,
            Objects currentObj,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences) {
        super(parent, frameImage, objectAttributes, actionOwner, objColorsList,
                userPreferences, currentObj.getUserPreference());
        this.currentObject = currentObj;
        // save the original object position in order to be able to reset the changes
        this.origObjPos = new Rectangle(currentObj.getOuterBBox());

        // save the initial color of the object
        this.objectColor = currentObject.getColor();

        // init the border size based on computation or user preferences
        initObjPreviewBorder();

        // get the selected object preview image
        displayImage();

        initOtherVariables();

        enableSemanticOptions(false);
        enableBoxOptions(true);

        prepareFrame();
    }

    private void initOtherVariables() {
        // display a label with the object id
        displayObjId();

        // add the size of the object        
        setFrameTitle();

        // set object type specific properties
        applyObjProperties();
    }

    @Override
    protected void saveObject() {
        if (!isObjectAttributesSet()) {
            // the user wants to correct the data, do not close the application!
            return;
        }

        // save the object attributes
        saveObjInfo();

        // notify the controller that the attributes were saved
        observable.notifyObservers(actionOwner);

        closeWindow();
    }

    @Override
    protected void cancelObject() {
        // notify to track the object if the box is in edit mode
        if (actionOwner != ObservedActions.Action.SAVE_BOUNDING_BOX) {
            // save the object in the ground truth
            observable.notifyObservers(ObservedActions.Action.SAVE_LABEL);
        }

        cancelWindow();
    }

    @Override
    protected void deleteObject() {
        // if the textfield id the focus owner, do not erase the object
        if (jTFBorder.isFocusOwner()) {
            return;
        }

        if (actionOwner == ObservedActions.Action.SAVE_BOUNDING_BOX) {
            // in it is a new object, cancel it
            cancelWindow();

        } else if ((actionOwner == ObservedActions.Action.UPDATE_OBJECT_SCRIBBLE)
                || (actionOwner == ObservedActions.Action.UPDATE_BOUNDING_BOX)) {
            // if it is an existing object, in edit mode, remove it
            removeObject();
        }
    }

    @Override
    protected void moveObject(int offsetX, int offsetY) {
        // move the object in the display window
        currentObject.move(offsetX, offsetY, new Dimension(frameImg.getWidth(), frameImg.getHeight()));

        // refresh the displayed image
        displayImage();

        // refresh also the image on the GUI
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    @Override
    protected void changeSize(int left, int top, int right, int bottom) {
        currentObject.changeSize(left, top, right, bottom, null, null, new Dimension(frameImg.getWidth(), frameImg.getHeight()));

        // refresh the displayed image
        displayImage();

        // change the title of the frame
        setFrameTitle();

        // refresh also the image on the GUI
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    @Override
    protected void restoreObjOrigPos() {
        // reset the object coordinates
        resetObjCoordinates();

        // refresh the displayed image
        displayImage();

        // change the title of the frame
        setFrameTitle();

        // refresh also the image on the GUI
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    @Override
    protected void changeObjColor() {
        // set the color of the object
        objectColor = getNewObjColor(objectColor);

        // set the color on the drawing panel
        dPPreviewImg.setObjColor(objectColor);

        // refresh the image on the panel
        showImage();

        // refresh the color of the object id label
        setObjIdColor();
    }

    @Override
    protected void displayObjId() {
        // add the object id
        // set the text of the object
        jLObjId.setText("Object id: " + currentObject.getObjectId());

        // set the color of the object id label
        setObjIdColor();
    }

    @Override
    protected void setFrameTitle() {
        String title = "";

        // add the size of the object (original)
        title += "Preview " + currentObject.getOuterBBox().width + "x" + currentObject.getOuterBBox().height;

        // if the work image has different size, show it in brackets (for zooming)
        if (!dPPreviewImg.getWorkImgSize().equals(dPPreviewImg.getOrigImgSize())) {
            // compute the size of the box with the zooming factor
            Rectangle resizedOuterBox = dPPreviewImg.getResize().resizeBox(currentObject.getOuterBBox());

            // display the zoomed size
            title += " (" + resizedOuterBox.width + "x" + resizedOuterBox.height + ")";
        }

        setTitle(title);
    }

    @Override
    protected void updateBorderValue(int value) {
        // get the value of the border only if it is valid
        // the border has to be greater than a min value
        value = Math.max(value, Constants.MIN_BORDER);

        // the border cannot be more than the distance from the box to the image border
        value = Math.min(value, getMaxPossibleBorder());

        // set the border size
        borderPX = value;

        // save the user preference
        currentObject.getUserPreference().setBorderSize(borderPX);

        // update text field
        jTFBorder.setText(Integer.toString(borderPX));

        // refresh the displayed image
        displayImage();
    }

    /**
     * Compute the distances from the box to the borders and sort it.
     *
     * @return the value of the max displayable border
     */
    protected int getMaxPossibleBorder() {
        // for simplicity, use a shorter name
        Rectangle objBox = currentObject.getOuterBBox();

        // save the borders inside an array
        int[] maxBorder = new int[4];

        // save the max border values (the distance from box to the image border)
        maxBorder[0] = objBox.x;
        maxBorder[1] = objBox.y;
        maxBorder[2] = frameImg.getWidth() - (objBox.x + objBox.width);
        maxBorder[3] = frameImg.getHeight() - (objBox.y + objBox.height);

        // sort the array into ascending order, according to the natural ordering of its elements
        Arrays.sort(maxBorder);

        // return the greatest value of the array
        return maxBorder[3];
    }

    @Override
    protected final void displayImage() {
        displayBox = getBorderedSize(currentObject.getOuterBBox(), borderPX);

        displayImage(displayBox);
    }

    /**
     * Display the image with the segmented box.
     */
    @Override
    protected void showImage() {
        // get the graphics of the image
        Graphics2D g2d = workImg.createGraphics();
        // draw the outer box of the object
        drawObjContour(g2d);
        g2d.dispose();

        updatePreview(workImg, DrawConstants.DrawType.DO_NOT_DRAW, currentObject.getUserPreference().getZoomingIndex());
    }

    /**
     * Initialise the border size from the user preferences or from the border
     * percentage.
     */
    private void initObjPreviewBorder() {
        // if the user preferences are invalid, compute the border as the percentage of the object size
        if (currentObject.getUserPreference().getBorderSize() < Constants.MIN_BORDER) {
            this.borderPX = Math.max(Math.max((int) (Constants.BORDER_PERCENTAGE * currentObject.getOuterBBox().width),
                    (int) (Constants.BORDER_PERCENTAGE * currentObject.getOuterBBox().height)),
                    Constants.MIN_BORDER);
        } else {
            // if the user border preferences is valid, use them
            this.borderPX = currentObject.getUserPreference().getBorderSize();
        }

        // set the value of the border
        jTFBorder.setText(Integer.toString(borderPX));
    }

    /**
     * Draw the outer shape of the object.
     *
     * @param g2d the graphics object
     */
    protected void drawObjContour(Graphics2D g2d) {
        drawOuterBox(g2d);
    }

    /**
     * Draw the outer box of the object; a rectangle which is resized to the
     * proper image size.
     *
     * @param g2d the graphics object
     * @return the rectangle drawn on the image
     */
    protected final Rectangle drawOuterBox(Graphics2D g2d) {
        // compute the position of the object box, to be displayed
        Rectangle bBox = new Rectangle(borderSize.getxLeft(),
                borderSize.getyTop(),
                currentObject.getOuterBBox().width,
                currentObject.getOuterBBox().height);

        g2d.setColor(objectColor);

        g2d.drawRect(bBox.x - 1, bBox.y - 1, bBox.width + 1, bBox.height + 1);

        return bBox;
    }

    /**
     * Based on the size of the generated box, compute a border size to be added
     * to the displayed image. The purpose is to have a better view of the
     * object. If the tracker cut some parts of the object, the user has to
     * notice and correct it.
     *
     * @param outerBBox - the original segmentation size
     * @param border the number of pixels to be added to the box
     * @return - the new size of the display image
     */
    private Rectangle getBorderedSize(Rectangle outerBBox, int border) {

        // if the border is too small, make it at least 10 pixels
        if (border < Constants.MIN_BORDER) {
            border = Constants.MIN_BORDER;
        }

        // set the border as 20% of the size of the box
        borderSize = new BoxLRTB(border);

        // limit the bordering in such way as to not get out of the image
        if ((outerBBox.x - border) < 0) {
            borderSize.setxLeft(outerBBox.x);
        }

        if ((outerBBox.y - border) < 0) {
            borderSize.setyTop(outerBBox.y);
        }

        if ((outerBBox.x + outerBBox.width + border) > frameImg.getWidth()) {
            borderSize.setxRight(frameImg.getWidth() - (outerBBox.x + outerBBox.width));
        }

        if ((outerBBox.y + outerBBox.height + border) > frameImg.getHeight()) {
            borderSize.setyBottom(frameImg.getHeight() - (outerBBox.y + outerBBox.height));
        }

        // return the new size with the border added
        return (new Rectangle(outerBBox.x - borderSize.getxLeft(),
                outerBBox.y - borderSize.getyTop(),
                outerBBox.width + borderSize.getxLeft() + borderSize.getxRight(),
                outerBBox.height + borderSize.getyTop() + borderSize.getyBottom()));
    }

    /**
     * Sets the color of the label showing the object id and its background.
     */
    private void setObjIdColor() {
        // set the color of the text of the object
        jLObjId.setForeground(objectColor);

        // set the background for better readability
        jLObjId.setBackground(Utils.getContrastColor(objectColor, 190));
    }

    /**
     * Save the object attributes (type, class, value, occlusion) and other
     * object specific characteristics.
     */
    private void saveObjInfo() {
        // set the specified attributes
        if (jCBObjType.getSelectedIndex() > -1) {
            currentObject.setObjectType(jCBObjType.getItemAt(jCBObjType.getSelectedIndex()));
        }

        if (jCBObjClass.getSelectedIndex() > -1) {
            currentObject.setObjectClass(jCBObjClass.getItemAt(jCBObjClass.getSelectedIndex()));
        }

        if (jCBObjValue.getSelectedIndex() > -1) {
            currentObject.setObjectValue(jCBObjValue.getItemAt(jCBObjValue.getSelectedIndex()));
        }

        if (jCBOccluded.getSelectedIndex() > -1) {
            currentObject.setOccluded(jCBOccluded.getItemAt(jCBOccluded.getSelectedIndex()));
        }
        // update the color of the object
        currentObject.setColor(objectColor);

        // set the labeling source: human or machine
        currentObject.setSegmentationSource(ConstantsLabeling.LABEL_SOURCE_MANUAL);
    }

    /**
     * Close the window and cancel the object. The user does not want to save
     * it.
     */
    private void cancelWindow() {
        closeWindow();

        // cancel current object
        observable.notifyObservers(ObservedActions.Action.CANCEL_CURRENT_OBJECT);
    }

    /**
     * Close the window and delete the object. The user does not want to have
     * it.
     */
    private void removeObject() {
        closeWindow();

        // cancel current object
        observable.notifyObservers(ObservedActions.Action.REMOVE_SELECTED_OBJECT);
    }

    @Override
    protected void changeBorderSize(int notches) {
        int tempBorder = borderPX;

        if (notches < 0) {
            // Mouse wheel moved UP = zoom in
            tempBorder += Math.abs(notches);
        } else {
            // Mouse wheel moved DOWN = zoom out
            tempBorder -= notches;
        }

        // update the border value
        updateBorderValue(tempBorder);
    }

    @Override
    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Implement specific to the object actions, like fill in some data for the
     * drawing panel etc.
     */
    protected void applyObjProperties() {
        // do nothing; the classes which need to implement this method; will do so
    }

    /**
     * Reset the position of the object. The user wants to have the status of
     * the object from the beginning of the edit.
     */
    protected void resetObjCoordinates() {
        currentObject.setOuterBBox(new Rectangle(origObjPos));
    }

}
