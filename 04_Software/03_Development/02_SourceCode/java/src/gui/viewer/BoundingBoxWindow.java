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
package gui.viewer;

import common.Constants;
import common.ConstantsLabeling;
import common.UserPreferences;
import common.Utils;
import gui.support.ObjectBBox;
import gui.support.ObjectPolygon;
import gui.support.ObjectScribble;
import gui.support.Objects;
import gui.support.ScreenResolution;
import library.BoxLRTB;
import java.awt.Point;
import java.awt.Rectangle;
import library.Resize;
import observers.NotifyObservers;
import observers.ObservedActions;
import gui.support.CustomTreeNode;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * The type Bounding box window.
 *
 * @author Olimpia Popica
 */
public final class BoundingBoxWindow extends javax.swing.JDialog {

    /**
     * The event dispatcher for the keyboard in order to execute specific tasks
     * for the crop window.
     */
    private transient BBoxWinKeyEventDispatcher boxKeyEventDispatch;

    /**
     * The resolution of the current screen where the application is showed.
     */
    private transient ScreenResolution screenRes;

    /**
     * The original image of the object in the original size.
     */
    private transient BufferedImage origImg;

    /**
     * The original image in the frame; the whole image - original size.
     */
    private final transient BufferedImage frameImg;

    /**
     * The selected object for being edited.
     */
    private final transient Objects currentObject;

    /**
     * The original position of the object in the image; before being modified.
     */
    private final Rectangle origObjPos;

    /**
     * The image used to be displayed. It is computed out of the original image
     * in order to keep the details and not loose data.
     */
    private transient BufferedImage workImg;

    /**
     * Used to resize the image for better understanding.
     */
    private transient Resize resize;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * Shows who called the crop window: the normal crop or the edit crop. Class
     * ObservedActions shows the possible actions.
     */
    private final ObservedActions.Action actionOwner;

    /**
     * The size of the border for the image. The number of extra pixels added to
     * the labeled image, in each direction.
     */
    private transient BoxLRTB borderSize;

    /**
     * The color of the object, used in the bounding box window. It will not be
     * saved for the object if the object is cancel. It will be saved for the
     * object if the object is saved.
     */
    private Color objectColor;

    /**
     * The list of colors which were used for the segmented objects in the
     * current frame. It helps to prevent the segmentation of two objects with
     * the same color.
     */
    private final List<Color> objColorsList;

    /**
     * The size of the border, in pixels, for displaying purposes (the user has
     * to see some of the environment to be able to label correctly).
     */
    private int borderPX;

    /**
     * The box which represents the displayed image. It includes the object
     * outer box and the border.
     */
    private Rectangle displayBox;

    /**
     * The text being displayed on the histogram button when the image has been
     * enhanced and the user can go to the original one.
     */
    private static final String HISTO_ORIGINAL = "Original";

    /**
     * The text being displayed on the histogram button when the image shown is
     * original, but can be enhanced.
     */
    private static final String HISTO_HIGHLIGHT = "Highlight";

    /**
     * The list of object attributes: type, class, value.
     */
    private CustomTreeNode objectAttributes;

    /**
     * User preferences related to the application.
     */
    private final UserPreferences userPrefs;

    /**
     * Creates new form BoundingBoxWindow
     *
     * @param parent           the parent component of the dialog
     * @param frameImage       the original image, in original size
     * @param currentObj       the object being segmented
     * @param objectAttributes the list of object attributes: type, class, value
     * @param actionOwner      the scope of the dialog: create new box, edit existing one
     * @param objColorsList    the list of already used colors (for other objects)
     * @param userPreferences user preferences regarding application
     * configuration
     */
    public BoundingBoxWindow(java.awt.Frame parent,
            BufferedImage frameImage,
            Objects currentObj,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences) {
        super(parent, true);

        initComponents();

        this.actionOwner = actionOwner;
        this.objColorsList = objColorsList;

        // save the original image
        this.frameImg = frameImage;
        this.currentObject = currentObj;

        // save the original object position in order to be able to reset the changes
        this.origObjPos = new Rectangle(currentObj.getOuterBBox());

        // save the initial color of the object
        this.objectColor = currentObject.getColor();

        // save the user prederences
        this.userPrefs = userPreferences;

        // get the tree in the form to be displayed (add if needed the invalid branch)
        this.objectAttributes = objectAttributes;

        initAttributesComponents();

        // init the border size based on computation or user preferences
        initObjPreviewBorder();

        // get the selected object preview image
        displayImage();

        initOtherVariables();

        initResolutions();

        prepareFrame();
    }

    /**
     * Get the piece of image representing the object from the frame image.
     */
    private void displayImage() {
        displayBox = getBorderedSize(currentObject.getOuterBBox(), borderPX);

        // get the selected box from the whole image
        this.origImg = Utils.getSelectedImg(frameImg, displayBox);

        this.resize = new Resize(displayBox.width, displayBox.height, currentObject.getUserPreference().getZoomingIndex());

        // display the image on the panel
        showImage();

        prepareFrame();
    }

    /**
     * Display the image with the segmented box.
     */
    private void showImage() {
        // resize image with the ratio relative to the original image, in order to prevent data loose
        workImg = resize.resizeImage(origImg);

        // if the image was highlighted, do it again
        if (HISTO_ORIGINAL.equals(jBHistogramEq.getText())) {
            workImg = Utils.histogramEqColor(workImg);
        }

        // get the graphics of the image
        Graphics2D g2d = workImg.createGraphics();

        if ((currentObject instanceof ObjectBBox) || (currentObject instanceof ObjectScribble)) {
            // draw the outer box of the object
            Rectangle bBox = drawOuterBox(g2d);

            // display the object map if the object is scribble
            displayObjMap(bBox);

        } else if (currentObject instanceof ObjectPolygon) {
            // draw the polygon object
            drawPolygon(g2d);
        }

        g2d.dispose();

        // display the image
        ImageIcon iconLogo = new ImageIcon(workImg);
        jLImagePreview.setIcon(iconLogo);
    }

    /**
     * Draw the outer box of the object; a rectangle which is resized to the
     * proper image size.
     *
     * @param g2d the graphics object
     * @return the rectangle drawn on the image
     */
    private Rectangle drawOuterBox(Graphics2D g2d) {
        // compute the position of the object box, to be displayed
        Rectangle bBox = resize.resizeBox(new Rectangle(borderSize.getxLeft(),
                borderSize.getyTop(),
                currentObject.getOuterBBox().width,
                currentObject.getOuterBBox().height));

        g2d.setColor(objectColor);

        g2d.drawRect(bBox.x, bBox.y, bBox.width, bBox.height);

        return bBox;
    }

    /**
     * Draw the outer box of the object; a rectangle which is resized to the
     * proper image size.
     *
     * @param g2d the graphics object
     * @return the rectangle drawn on the image
     */
    private void drawPolygon(Graphics2D g2d) {

        java.awt.Polygon polygon = new Polygon(((ObjectPolygon) currentObject).getPolygon().xpoints,
                ((ObjectPolygon) currentObject).getPolygon().ypoints,
                ((ObjectPolygon) currentObject).getPolygon().npoints);

        // shift the polygon with the bordered added
        for (int index = 0; index < polygon.npoints; index++) {
            polygon.xpoints[index] -= displayBox.x;
            polygon.ypoints[index] -= displayBox.y;
        }

        // compute the position of the object box, to be displayed
        polygon = resize.originalToResized(polygon);

        g2d.setColor(objectColor);

        g2d.drawPolygon(polygon);

        // make the line thicker
        g2d.setStroke(new BasicStroke(3));

        // draw the points of the polygon
        for (int index = 0; index < polygon.npoints; index++) {
            g2d.drawLine(polygon.xpoints[index], polygon.ypoints[index],
                    polygon.xpoints[index], polygon.ypoints[index]);
        }

        // reset the thicknes of the line
        g2d.setStroke(new BasicStroke(0));
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
                Point transformedPoint = resize.resizedToOriginal(x - bBox.x, y - bBox.y);
                int posX = transformedPoint.x;
                int posY = transformedPoint.y;

                if (objMap[posX][posY] > 0) {
                    bkg = Utils.getRGB(workImg.getRGB(x, y));
                    fg = Utils.getRGB(objectColor.getRGB());

                    rgb[0] = (int) ((fg[0] * alpha) + (bkg[0] * (1 - alpha)));
                    rgb[1] = (int) ((fg[1] * alpha) + (bkg[1] * (1 - alpha)));
                    rgb[2] = (int) ((fg[2] * alpha) + (bkg[2] * (1 - alpha)));

                    workImg.setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
                }
            }
        }
    }

    private void initOtherVariables() {
        // add keyboard listener to be able to set events on the wanted keys
        boxKeyEventDispatch = new BBoxWinKeyEventDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(boxKeyEventDispatch);

        // display a label with the object id
        displayObjId();

        // add the size of the object        
        setFrameTitle();

        enableScribbleObjOpt(currentObject instanceof ObjectScribble);

        // compute the preffered width of the combo boxes based on the specified text
        jCBObjType.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
        jCBObjClass.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
        jCBObjValue.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
    }

    /**
     * Initialises the resolution of the screen on which the application is
     * drawn.
     */
    private void initResolutions() {
        screenRes = new ScreenResolution(this);
    }

    /**
     * Code related to the frame like: pack, set location etc.
     */
    private void prepareFrame() {
        this.pack();
        this.setMinimumSize(getPreferredSize());
    }

    /**
     * Create the proper design for the components representing the object
     * attributes and populate the combo boxes with the data defined in the
     * tree.
     */
    private void initAttributesComponents() {
        if (objectAttributes == null) {
            return;
        }

        initAttributesComponents(objectAttributes, jCBObjType);

        CustomTreeNode ctn = objectAttributes.getChild(jCBObjType.getSelectedItem().toString());

        initAttributesComponents(ctn, jCBObjClass);

        if ((ctn != null) && (!ctn.isLeaf())) {
            initAttributesComponents(ctn.getChild(jCBObjClass.getSelectedItem().toString()), jCBObjValue);
        }
    }

    /**
     * Create the proper design for the components representing the object
     * attributes and populate the combo boxes with the data defined in the
     * database.
     *
     * @param selectedNode the list of object attributes, including: the list of
     * types, the list of classes, the list of values
     * @param attribute the combo box for which the attribute is initialized
     */
    public void initAttributesComponents(CustomTreeNode selectedNode, JComboBox attribute) {
        if (selectedNode == null) {
            return;
        }

        List<String> typeAttrib = selectedNode.getChildren();

        attribute.removeAllItems();

        if ((typeAttrib != null) && (!typeAttrib.isEmpty())) {
            // set the attributes of the combo boxes
            typeAttrib.stream().forEach(objType -> attribute.addItem(Utils.capitalize(objType)));
        }

        attribute.repaint();
    }

    /**
     * Set the wanted type, class, value and occlusion for the preview.
     *
     * @param objType  - wanted object type
     * @param objClass - wanted object class
     * @param objValue - wanted object value
     * @param occluded - wanted object occlusion
     */
    public void setTypeClassValOcc(String objType, String objClass, String objValue, String occluded) {
        jCBObjType.setSelectedItem(objType);
        jCBObjClass.setSelectedItem(objClass);
        jCBObjValue.setSelectedItem(objValue);
        jCBOccluded.setSelectedItem(occluded);
    }

    /**
     * Returns the selected type of object.
     *
     * @return - the chosen attribute for type for the object
     */
    public String getObjType() {
        return jCBObjType.getItemAt(jCBObjType.getSelectedIndex());
    }

    /**
     * Returns the selected class of object.
     *
     * @return - the chosen attribute for class for the object
     */
    public String getObjClass() {
        return jCBObjClass.getItemAt(jCBObjClass.getSelectedIndex());
    }

    /**
     * Returns the selected value of object.
     *
     * @return - the chosen attribute for value for the object
     */
    public String getObjValue() {
        return jCBObjValue.getItemAt(jCBObjValue.getSelectedIndex());
    }

    /**
     * Returns the occlusion of object.
     *
     * @return - the specified occlusion of the object
     */
    public String getObjOccluded() {
        return jCBOccluded.getItemAt(jCBOccluded.getSelectedIndex());
    }

    /**
     * Allows another module to put an observer into the current module.
     *
     * @param o - the observer to be added
     */
    public void addObserver(Observer o) {
        observable.addObserver(o);

        // notify to remove the gui key event dispatcher
        observable.notifyObservers(ObservedActions.Action.REMOVE_GUI_KEY_EVENT_DISPATCHER);
    }

    /**
     * Allows another module to erase an observer from the current module.
     *
     * @param o - the observer to be deleted
     */
    public void deleteObserver(Observer o) {
        observable.deleteObserver(o);
    }

    /**
     * Return the color of the segmented object as selected by the user in the
     * edit window.
     *
     * @return - the color choosen by the user for the object
     */
    public Color getObjectColor() {
        return objectColor;
    }

    /**
     * Close the window and release the key dispatcher.
     */
    private void closeWindow() {
        // remove own key event dispatcher
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(boxKeyEventDispatch);

        // notify to add the gui key event dispatcher back
        observable.notifyObservers(ObservedActions.Action.ADD_GUI_KEY_EVENT_DISPATCHER);

        dispose();
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
     * When the window is opening it will have its own key dispatcher. The
     * dispatcher of the gui should no longer be active.
     */
    public void removeOtherKeyEventDispatcher() {
        // notify to remove the gui key event dispatcher
        observable.notifyObservers(ObservedActions.Action.REMOVE_GUI_KEY_EVENT_DISPATCHER);
    }

    /**
     * Sets the title of the frame with the current size of the image and the
     * size of the work image (which can be different due to the zooming).
     */
    private void setFrameTitle() {
        String title = "";

        // add the size of the object (original)
        title += "Preview " + currentObject.getOuterBBox().width + "x" + currentObject.getOuterBBox().height;

        // if the work image has different size, show it in brackets (for zooming)
        if ((workImg.getWidth() != origImg.getWidth())
                || (workImg.getHeight() != origImg.getHeight())) {

            // compute the size of the box with the zooming factor
            Rectangle resizedOuterBox = resize.resizeBox(currentObject.getOuterBBox());

            // display the zoomed size
            title += " (" + resizedOuterBox.width + "x" + resizedOuterBox.height + ")";
        }

        setTitle(title);
    }

    /**
     * Shows the object id in the frame and sets its color and background for
     * better readability.
     */
    private void displayObjId() {
        // add the object id
        // set the text of the object
        jLObjId.setText("Object id: " + currentObject.getObjectId());

        // set the color of the object id label
        setObjIdColor();

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
        jFTFBorder.setValue(borderPX);
    }

    /**
     * Restore the object to the original position. The user made changes but
     * does not want to keep them.
     */
    private void restoreObjOrigPos() {
        // reset the object coordinates
        currentObject.setOuterBBox(new Rectangle(origObjPos));

        // refresh the displayed image
        displayImage();

        // change the title of the frame
        setFrameTitle();

        // refresh also the image on the gui
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    /**
     * Change the border size for the event Ctrl + mouse scroll.
     *
     * @param notches how much the mouse scroll moves and in which direction
     */
    private void changeBorderSize(int notches) {
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

    /**
     * Updates the value of the border with the value specified by the user in
     * the text box.
     */
    private void updateBorderValue(int value) {
        // get the value of the border only if it is valid
        if ((jFTFBorder.getValue() != null)) {
            // the border has to be greater than a min value
            value = Math.max(value, Constants.MIN_BORDER);

            // the border cannot be more than the distance from the box to the image border
            value = Math.min(value, getMaxPossibleBorder());

            jFTFBorder.setValue(value);

            // set the border size
            borderPX = ((Number) jFTFBorder.getValue()).intValue();

            // save the user preference
            currentObject.getUserPreference().setBorderSize(borderPX);

            // refresh the displayed image
            displayImage();
        }
    }

    /**
     * Zoom the image based on the movement of the mouse scroll.
     *
     * @param notches how much the mouse scroll moves and in which direction
     */
    private void zoomImage(int notches) {
        if (notches < 0) {
            // Mouse wheel moved UP = zoom in
            // check if the resize is not generating a bigger image than the screen
            Dimension origImgDimension = new Dimension(origImg.getWidth(), origImg.getHeight());
            if (resize.isSizeIncreaseOK(origImgDimension, screenRes.getScreenResolution())) {
                currentObject.getUserPreference().setZoomingIndex(resize.incrementWidthHeight(origImgDimension));
            }
        } else {
            Dimension origImgDimension = new Dimension(-origImg.getWidth(), -origImg.getHeight());
            // Mouse wheel moved DOWN = zoom out
            if (resize.isSizeDecreaseOK(origImgDimension)) {
                currentObject.getUserPreference().setZoomingIndex(resize.incrementWidthHeight(origImgDimension));
            }
        }

        // display the image on the panel
        showImage();

        // change the title of the frame
        setFrameTitle();

        // resize the frame and position it in the middle of the screen
        prepareFrame();
    }

    /**
     * Compute the distances from the box to the borders and sort it.
     *
     * @return the value of the max displayable border
     */
    private int getMaxPossibleBorder() {
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

    /**
     * Checks if the frame attributes are correctly set and asks the user to
     * correct them. If the user chooses to correct them, the application does
     * not continue with the saving of data; it will cancel the process and go
     * back to the normal state.
     *
     * @return true if the user wants to save the data as it is and false if the
     * saving of data shall not be done yet, because the user wants to correct
     * it
     */
    private boolean isObjectAttributesSet() {
        // make sure the user wants to check the frame attributes
        if (!userPrefs.isCheckObjectAttributes()) {
            return true;
        }

        StringBuilder invalidAttribute = new StringBuilder();

        // check each frame attribute and create a list of missing frame attributes
        Utils.checkAttributeValid(invalidAttribute, jCBObjType.getItemAt(jCBObjType.getSelectedIndex()), jLType.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBObjClass.getItemAt(jCBObjClass.getSelectedIndex()), jLClass.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBObjValue.getItemAt(jCBObjValue.getSelectedIndex()), jLValue.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBOccluded.getItemAt(jCBOccluded.getSelectedIndex()), jLOccluded.getText());

        // if missing attributes were found, display the message window to warn the user to correct the attributes
        if (invalidAttribute.length() > 0) {
            // remove the last comma and the last space from the string
            invalidAttribute.replace(invalidAttribute.length() - 2, invalidAttribute.length(), "");

            // ask the user if the current attributes shall be saved or not; if the user wants to correct them, stop the application and let him fix them
            int userChoice = Utils.messageAttributesSet(invalidAttribute.toString(), true, this);

            if (userChoice == JOptionPane.CANCEL_OPTION) {
                // the user chose to edit the attributes; display the dialog and allow it
                observable.notifyObservers(ObservedActions.Action.DISPLAY_EDIT_ATTRIBUTES_WIN);
            }

            return ((JOptionPane.YES_OPTION != userChoice) && (JOptionPane.CANCEL_OPTION != userChoice));
        }

        return true;
    }

    /**
     * Enable options related to the scribble object edit.
     *
     * @param enable true if the options shall be enabled
     */
    private void enableScribbleObjOpt(boolean enable) {
        // make the filter button invisible if the object is not scribble
        jBFilterObjMap.setVisible(enable);

        // disable menu options        
        jMEditObject.setEnabled(!enable);
    }

    /**
     * The key event dispatcher for listening the keys and the implementation of
     * the actions for some defined keys.
     */
    private class BBoxWinKeyEventDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (frameIsFocusOwner()) {
                dispatchKeyGeneral(e);
            }

            return false;
        }

        /**
         * Handles the keys which have to be enabled all the time in the frame.
         */
        private void dispatchKeyGeneral(KeyEvent e) {
            int eventId = e.getID();
            int key = e.getKeyCode();

            handleCloseDialogKeys(key);

            // if the object is the preview of the scribble object, no edit can be done for the it
            if (currentObject instanceof ObjectScribble) {
                return;
            }

            if (eventId == KeyEvent.KEY_PRESSED) {
                if (e.isControlDown()) {
                    handleCtrlKey(key);
                } else if (e.isAltDown()) {
                    handleAltKey(key);
                } else {
                    handleNormalKey(key);
                }
            }
        }

        /**
         * Handle the keyboard events when Control key is pressed.
         *
         * @param key the key code
         */
        private void handleCtrlKey(int key) {
            switch (key) {
                case KeyEvent.VK_A:
                    // decrease left
                    changeSize(1, 0, 0, 0);
                    break;

                case KeyEvent.VK_W:
                    // decrease top
                    changeSize(0, 1, 0, 0);
                    break;

                case KeyEvent.VK_D:
                    // decrease right
                    changeSize(0, 0, -1, 0);
                    break;

                case KeyEvent.VK_S:
                    // decrease bottom
                    changeSize(0, 0, 0, -1);
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Handle the keyboard events when Alt key is pressed.
         *
         * @param key the key code
         */
        private void handleAltKey(int key) {
            switch (key) {
                case KeyEvent.VK_A:
                    // increase left
                    changeSize(-1, 0, 0, 0);
                    break;

                case KeyEvent.VK_W:
                    // increase top
                    changeSize(0, -1, 0, 0);
                    break;

                case KeyEvent.VK_D:
                    // increase right
                    changeSize(0, 0, 1, 0);
                    break;

                case KeyEvent.VK_S:
                    // increase bottom
                    changeSize(0, 0, 0, 1);
                    break;

                case KeyEvent.VK_Z:
                    // decrease box
                    changeSize(1, 1, -1, -1);
                    break;

                case KeyEvent.VK_X:
                    // increase box
                    changeSize(-1, -1, 1, 1);
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Handle the keyboard events.
         *
         * @param key the key code
         */
        private void handleNormalKey(int key) {
            switch (key) {
                case KeyEvent.VK_DELETE:
                    deleteObject();
                    break;

                case KeyEvent.VK_A:
                    // move the box to the left                            
                    moveObject(-1, 0);
                    break;

                case KeyEvent.VK_D:
                    // move the box to the right
                    moveObject(1, 0);
                    break;

                case KeyEvent.VK_W:
                    // move the box up
                    moveObject(0, -1);
                    break;

                case KeyEvent.VK_S:
                    // move the box down
                    moveObject(0, 1);
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Handle the keyboard events for closing the application: cancel and
         * save cases.
         *
         * @param key the key code
         */
        private void handleCloseDialogKeys(int key) {
            switch (key) {
                case KeyEvent.VK_ESCAPE:
                    // cancel the object being edited
                    cancelObject();
                    break;

                case KeyEvent.VK_ENTER:
                    // if the textfield id the focus owner, do not save the object
                    if (jFTFBorder.isFocusOwner()) {
                        break;
                    }

                    saveObject();
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Check if there is another window opened on top of the current one.
         *
         * @return - true if the current frame is the focus owner
         */
        private boolean frameIsFocusOwner() {
            return (BoundingBoxWindow.this.getFocusOwner() != null);
        }
    }

    /**
     * The delete key was pressed and the object has to be removed. Handle the
     * remove of an object by pressing the delete key.
     */
        private void deleteObject() {
            // if the textfield id the focus owner, do not erase the object
            if (jFTFBorder.isFocusOwner()) {
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

        /**
     * Close the window and delete the object. The user does not want to have
     * it.
         */
        private void removeObject() {
            closeWindow();

            // cancel current object
            observable.notifyObservers(ObservedActions.Action.REMOVE_SELECTED_OBJECT);
        }

        /**
         * Moves an object in the image with the specified offsets.
         */
        private void moveObject(int offsetX, int offsetY) {
            // move the object in the display window
        currentObject.move(offsetX, offsetY, null, null, new Dimension(frameImg.getWidth(), frameImg.getHeight()));

            // refresh the displayed image
            displayImage();

            // refresh also the image on the gui
            observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
        }

        /**
         * Changes the size of the object on any direction.
         */
        private void changeSize(int left, int top, int right, int bottom) {
        currentObject.changeSize(left, top, right, bottom, null, null, new Dimension(frameImg.getWidth(), frameImg.getHeight()));

            // refresh the displayed image
            displayImage();

            // change the title of the frame
            setFrameTitle();

            // refresh also the image on the gui
            observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
        }

        /**
     * Notifies the controller that the object was segmented and it has to be
     * saved.
     */
    private void saveObject() {
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

    /**
     * Notifies the controller that the object was cancel.
     */
    private void cancelObject() {
        // notify to track the object if the box is in edit mode
        if (actionOwner != ObservedActions.Action.SAVE_BOUNDING_BOX) {
            // save the object in the ground truth
            observable.notifyObservers(ObservedActions.Action.SAVE_LABEL);

        }

        cancelWindow();
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPBackground = new javax.swing.JPanel();
        jPObjAttributes = new javax.swing.JPanel();
        jLType = new javax.swing.JLabel();
        jLClass = new javax.swing.JLabel();
        jLValue = new javax.swing.JLabel();
        jLOccluded = new javax.swing.JLabel();
        jBSaveObj = new javax.swing.JButton();
        jCBObjClass = new javax.swing.JComboBox<>();
        jCBObjType = new javax.swing.JComboBox<>();
        jCBObjValue = new javax.swing.JComboBox<>();
        jCBOccluded = new javax.swing.JComboBox<>();
        jLObjId = new javax.swing.JLabel();
        jLImagePreview = new javax.swing.JLabel();
        jPOptions = new javax.swing.JPanel();
        jBHistogramEq = new javax.swing.JButton();
        jBChangeObjColor = new javax.swing.JButton();
        jBFilterObjMap = new javax.swing.JButton();
        jBRestorePos = new javax.swing.JButton();
        jLBorder = new javax.swing.JLabel();
        jFTFBorder = new javax.swing.JFormattedTextField();
        jBSetBorder = new javax.swing.JButton();
        jMBBBoxWindow = new javax.swing.JMenuBar();
        jMFile = new javax.swing.JMenu();
        jMICloseDialog = new javax.swing.JMenuItem();
        jMISaveObj = new javax.swing.JMenuItem();
        jMEditObject = new javax.swing.JMenu();
        jMIMoveLeft = new javax.swing.JMenuItem();
        jMIMoveRight = new javax.swing.JMenuItem();
        jMIMoveUp = new javax.swing.JMenuItem();
        jMIMoveDown = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMIDecreaseLeft = new javax.swing.JMenuItem();
        jMIDecreaseRight = new javax.swing.JMenuItem();
        jMIDecreaseTop = new javax.swing.JMenuItem();
        jMIDecreaseBottom = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMIIncreaseLeft = new javax.swing.JMenuItem();
        jMIIncreaseRight = new javax.swing.JMenuItem();
        jMIIncreaseTop = new javax.swing.JMenuItem();
        jMIIncreaseBottom = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMIDecreaseBox = new javax.swing.JMenuItem();
        jMIIncreaseBox = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMIDeleteObj = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPBackground.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPBackground.setLayout(new java.awt.GridBagLayout());

        jPObjAttributes.setBackground(new java.awt.Color(255, 255, 255));
        jPObjAttributes.setLayout(new java.awt.GridBagLayout());

        jLType.setLabelFor(jCBObjType);
        jLType.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLType, gridBagConstraints);

        jLClass.setLabelFor(jCBObjClass);
        jLClass.setText("Class");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLClass, gridBagConstraints);

        jLValue.setLabelFor(jCBObjValue);
        jLValue.setText("Value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLValue, gridBagConstraints);

        jLOccluded.setLabelFor(jCBOccluded);
        jLOccluded.setText("Occluded");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLOccluded, gridBagConstraints);

        jBSaveObj.setBackground(new java.awt.Color(255, 255, 255));
        jBSaveObj.setText("Save");
        jBSaveObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveObjActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 2, 0);
        jPObjAttributes.add(jBSaveObj, gridBagConstraints);

        jCBObjClass.setPreferredSize(new java.awt.Dimension(145, 23));
        jCBObjClass.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBObjClassItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjClass, gridBagConstraints);

        jCBObjType.setPreferredSize(new java.awt.Dimension(145, 23));
        jCBObjType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBObjTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjType, gridBagConstraints);

        jCBObjValue.setPreferredSize(new java.awt.Dimension(145, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjValue, gridBagConstraints);

        jCBOccluded.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "false", "true (25%)", "true (50%)", "true (75%)", "true (100%)" }));
        jCBOccluded.setPreferredSize(new java.awt.Dimension(145, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBOccluded, gridBagConstraints);

        jLObjId.setBackground(new java.awt.Color(255, 255, 255));
        jLObjId.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLObjId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPObjAttributes, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jLImagePreview, gridBagConstraints);

        jPOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPOptions.setLayout(new java.awt.GridBagLayout());

        jBHistogramEq.setBackground(new java.awt.Color(255, 255, 255));
        jBHistogramEq.setText("Highlight");
        jBHistogramEq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBHistogramEqActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBHistogramEq, gridBagConstraints);

        jBChangeObjColor.setBackground(new java.awt.Color(255, 255, 255));
        jBChangeObjColor.setText("Obj Color");
        jBChangeObjColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBChangeObjColorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBChangeObjColor, gridBagConstraints);

        jBFilterObjMap.setBackground(new java.awt.Color(255, 255, 255));
        jBFilterObjMap.setText("Filter");
        jBFilterObjMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBFilterObjMapActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 18;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBFilterObjMap, gridBagConstraints);

        jBRestorePos.setBackground(new java.awt.Color(255, 255, 255));
        jBRestorePos.setText("Restore Pos");
        jBRestorePos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRestorePosActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBRestorePos, gridBagConstraints);

        jLBorder.setText("Border (px)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jLBorder, gridBagConstraints);

        jFTFBorder.setText("10");
        jFTFBorder.setPreferredSize(new java.awt.Dimension(50, 20));
        jFTFBorder.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFTFBorderPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jFTFBorder, gridBagConstraints);

        jBSetBorder.setBackground(new java.awt.Color(255, 255, 255));
        jBSetBorder.setText("Set Border");
        jBSetBorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetBorderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBSetBorder, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        jPBackground.add(jPOptions, gridBagConstraints);

        jMFile.setText("File");

        jMICloseDialog.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMICloseDialog.setText("Cancel and Close");
        jMICloseDialog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMICloseDialogActionPerformed(evt);
            }
        });
        jMFile.add(jMICloseDialog);

        jMISaveObj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        jMISaveObj.setText("Save and Close");
        jMISaveObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISaveObjActionPerformed(evt);
            }
        });
        jMFile.add(jMISaveObj);

        jMBBBoxWindow.add(jMFile);

        jMEditObject.setText("Edit");

        jMIMoveLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        jMIMoveLeft.setText("Move Left");
        jMIMoveLeft.setToolTipText("Move the whole object, one pixel, left");
        jMIMoveLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveLeft);

        jMIMoveRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        jMIMoveRight.setText("Move Right");
        jMIMoveRight.setToolTipText("Move the whole object, one pixel, right");
        jMIMoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveRight);

        jMIMoveUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        jMIMoveUp.setText("Move Up");
        jMIMoveUp.setToolTipText("Move the whole object, one pixel, up");
        jMIMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveUpActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveUp);

        jMIMoveDown.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        jMIMoveDown.setText("Move Down");
        jMIMoveDown.setToolTipText("Move the whole object, one pixel, down");
        jMIMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveDownActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveDown);
        jMEditObject.add(jSeparator1);

        jMIDecreaseLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseLeft.setText("Decrease Left");
        jMIDecreaseLeft.setToolTipText("Remove one pixel from the left side of the object");
        jMIDecreaseLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseLeft);

        jMIDecreaseRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseRight.setText("Decrease Right");
        jMIDecreaseRight.setToolTipText("Remove one pixel from the right side of the object");
        jMIDecreaseRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseRight);

        jMIDecreaseTop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseTop.setText("Decrease Top");
        jMIDecreaseTop.setToolTipText("Remove one pixel from the top side of the object");
        jMIDecreaseTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseTopActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseTop);

        jMIDecreaseBottom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseBottom.setText("Decrease Bottom");
        jMIDecreaseBottom.setToolTipText("Remove one pixel from the bottom side of the object");
        jMIDecreaseBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBottomActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseBottom);
        jMEditObject.add(jSeparator3);

        jMIIncreaseLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseLeft.setText("Increase Left");
        jMIIncreaseLeft.setToolTipText("Add a pixel on the left side of the object");
        jMIIncreaseLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseLeft);

        jMIIncreaseRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseRight.setText("Increase Right");
        jMIIncreaseRight.setToolTipText("Add a pixel on the right side of the object");
        jMIIncreaseRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseRight);

        jMIIncreaseTop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseTop.setText("Increase Top");
        jMIIncreaseTop.setToolTipText("Add a pixel on the top side of the object");
        jMIIncreaseTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseTopActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseTop);

        jMIIncreaseBottom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseBottom.setText("Increase Bottom");
        jMIIncreaseBottom.setToolTipText("Add a pixel on the bottom side of the object");
        jMIIncreaseBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseBottomActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseBottom);
        jMEditObject.add(jSeparator2);

        jMIDecreaseBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.ALT_MASK));
        jMIDecreaseBox.setText("Decrease Box");
        jMIDecreaseBox.setToolTipText("Remove one pixel from all sides of the object");
        jMIDecreaseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBoxActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseBox);

        jMIIncreaseBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseBox.setText("Increase Box");
        jMIIncreaseBox.setToolTipText("Add one pixel to all sides of the object");
        jMIIncreaseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseBoxActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseBox);
        jMEditObject.add(jSeparator4);

        jMIDeleteObj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMIDeleteObj.setText("Delete Object");
        jMIDeleteObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDeleteObjActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDeleteObj);

        jMBBBoxWindow.add(jMEditObject);

        setJMenuBar(jMBBBoxWindow);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 722, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        int notches = evt.getWheelRotation();
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
            // Ctrl + mouse scroll => change the border size
            changeBorderSize(notches);
        } else {
            // mouse scroll => zoom
            zoomImage(notches);
        }
    }//GEN-LAST:event_formMouseWheelMoved

    private void jBSaveObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveObjActionPerformed
        saveObject();
    }//GEN-LAST:event_jBSaveObjActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // cancel the object being edited
        cancelObject();
    }//GEN-LAST:event_formWindowClosing

    private void jBHistogramEqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHistogramEqActionPerformed
        if (HISTO_HIGHLIGHT.equals(jBHistogramEq.getText())) {
            jBHistogramEq.setText(HISTO_ORIGINAL);
        } else if (HISTO_ORIGINAL.equals(jBHistogramEq.getText())) {
            jBHistogramEq.setText(HISTO_HIGHLIGHT);
        }

        // draw the image
        showImage();
    }//GEN-LAST:event_jBHistogramEqActionPerformed

    private void jBChangeObjColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBChangeObjColorActionPerformed
        Color newColor = JColorChooser.showDialog(
                BoundingBoxWindow.this,
                "Choose Object Color",
                objectColor);

        if (newColor == null) {
            return;
        }

        if (objColorsList.contains(newColor) || Constants.COLORS_LIST.contains(newColor)) {
            newColor = Utils.changeColor(newColor, objColorsList);

            String title = "Color warning";
            String message = "\nThe chosen color was already used for an object or it is reserved.\nIt will be changed to the one previewed here.";

            MessageWindow infoWin = new MessageWindow(new javax.swing.JFrame(), true, title, message, "OK", new MonochromeIcon(32, 32, newColor));
            infoWin.setLocationRelativeTo(this);
            infoWin.setVisible(true);
        }

        // set the color of the object
        objectColor = newColor;

        // refresh the image on the panel
        showImage();

        // refresh the color of the object id label
        setObjIdColor();
    }//GEN-LAST:event_jBChangeObjColorActionPerformed

    private void jBFilterObjMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBFilterObjMapActionPerformed
        // return if the object is not scribble
        if (!(currentObject instanceof ObjectScribble)) {
            return;
        }

        Utils.filterObjectMap(((ObjectScribble) currentObject).getObjectMap());

        // show the new image
        showImage();
    }//GEN-LAST:event_jBFilterObjMapActionPerformed

    private void jBRestorePosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRestorePosActionPerformed
        restoreObjOrigPos();
    }//GEN-LAST:event_jBRestorePosActionPerformed

    private void jFTFBorderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFTFBorderPropertyChange
        if ((jFTFBorder.getValue() == null)) {
            return;
        }

        updateBorderValue(((Number) jFTFBorder.getValue()).intValue());
    }//GEN-LAST:event_jFTFBorderPropertyChange

    private void jBSetBorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetBorderActionPerformed
        if ((jFTFBorder.getValue() == null)) {
            return;
        }

        updateBorderValue(((Number) jFTFBorder.getValue()).intValue());
    }//GEN-LAST:event_jBSetBorderActionPerformed

    private void jMICloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMICloseDialogActionPerformed
        // cancel the object being edited
        cancelObject();
    }//GEN-LAST:event_jMICloseDialogActionPerformed

    private void jMIMoveLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveLeftActionPerformed
        // move the object to the left
        moveObject(-1, 0);
    }//GEN-LAST:event_jMIMoveLeftActionPerformed

    private void jMIMoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveRightActionPerformed
        // move the object to the right
        moveObject(1, 0);
    }//GEN-LAST:event_jMIMoveRightActionPerformed

    private void jMIMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveUpActionPerformed
        // move the object up
        moveObject(0, -1);
    }//GEN-LAST:event_jMIMoveUpActionPerformed

    private void jMIMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveDownActionPerformed
        // move the object down
        moveObject(0, 1);
    }//GEN-LAST:event_jMIMoveDownActionPerformed

    private void jMIDecreaseLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseLeftActionPerformed
        // decrease left
        changeSize(1, 0, 0, 0);
    }//GEN-LAST:event_jMIDecreaseLeftActionPerformed

    private void jMIDecreaseRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseRightActionPerformed
        // decrease right
        changeSize(0, 0, -1, 0);
    }//GEN-LAST:event_jMIDecreaseRightActionPerformed

    private void jMIDecreaseTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseTopActionPerformed
        // decrease top
        changeSize(0, 1, 0, 0);
    }//GEN-LAST:event_jMIDecreaseTopActionPerformed

    private void jMIDecreaseBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBottomActionPerformed
        // decrease bottom
        changeSize(0, 0, 0, -1);
    }//GEN-LAST:event_jMIDecreaseBottomActionPerformed

    private void jMIIncreaseLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseLeftActionPerformed
        // increase left
        changeSize(-1, 0, 0, 0);
    }//GEN-LAST:event_jMIIncreaseLeftActionPerformed

    private void jMIIncreaseRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseRightActionPerformed
        // increase right
        changeSize(0, 0, 1, 0);
    }//GEN-LAST:event_jMIIncreaseRightActionPerformed

    private void jMIIncreaseTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseTopActionPerformed
        // increase top
        changeSize(0, -1, 0, 0);
    }//GEN-LAST:event_jMIIncreaseTopActionPerformed

    private void jMIIncreaseBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBottomActionPerformed
        // increase bottom
        changeSize(0, 0, 0, 1);
    }//GEN-LAST:event_jMIIncreaseBottomActionPerformed

    private void jMIDecreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBoxActionPerformed
        // decrease box
        changeSize(1, 1, -1, -1);
    }//GEN-LAST:event_jMIDecreaseBoxActionPerformed

    private void jMIIncreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBoxActionPerformed
        // increase box
        changeSize(-1, -1, 1, 1);
    }//GEN-LAST:event_jMIIncreaseBoxActionPerformed

    private void jMIDeleteObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDeleteObjActionPerformed
        deleteObject();
    }//GEN-LAST:event_jMIDeleteObjActionPerformed

    private void jMISaveObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISaveObjActionPerformed
        saveObject();
    }//GEN-LAST:event_jMISaveObjActionPerformed

    private void jCBObjTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBObjTypeItemStateChanged
        if ((objectAttributes == null) || (jCBObjType.getSelectedItem() == null)) {
            return;
        }

        CustomTreeNode selectedNode = objectAttributes.getChild(jCBObjType.getSelectedItem().toString());

        initAttributesComponents(selectedNode, jCBObjClass);
    }//GEN-LAST:event_jCBObjTypeItemStateChanged

    private void jCBObjClassItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBObjClassItemStateChanged
        if ((objectAttributes == null) || (jCBObjClass.getSelectedItem() == null)) {
            return;
        }

        CustomTreeNode ctn = objectAttributes.getChild(jCBObjType.getSelectedItem().toString());

        if ((ctn != null) && (!ctn.isLeaf())) {
            CustomTreeNode selectedNode = ctn.getChild(jCBObjClass.getSelectedItem().toString());
            initAttributesComponents(selectedNode, jCBObjValue);
        }
    }//GEN-LAST:event_jCBObjClassItemStateChanged

    /**
     * Set the object attributes tree.
     *
     * @param objectAttributes the list of object attributes, saved as a tree
     */
    public void setObjectAttributes(CustomTreeNode objectAttributes) {
        this.objectAttributes = objectAttributes;
        initAttributesComponents();
    }

    /**
     * The entry point of application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BoundingBoxWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                BufferedImage bi = ImageIO.read(new File(common.Icons.REINDEER_ICON_PATH));

                BoundingBoxWindow dialog = new BoundingBoxWindow(new javax.swing.JFrame(), bi, null, null, ObservedActions.Action.OPEN_BBOX_SEGMENTATION, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(BoundingBoxWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBChangeObjColor;
    private javax.swing.JButton jBFilterObjMap;
    private javax.swing.JButton jBHistogramEq;
    private javax.swing.JButton jBRestorePos;
    private javax.swing.JButton jBSaveObj;
    private javax.swing.JButton jBSetBorder;
    private javax.swing.JComboBox<String> jCBObjClass;
    private javax.swing.JComboBox<String> jCBObjType;
    private javax.swing.JComboBox<String> jCBObjValue;
    private javax.swing.JComboBox<String> jCBOccluded;
    private javax.swing.JFormattedTextField jFTFBorder;
    private javax.swing.JLabel jLBorder;
    private javax.swing.JLabel jLClass;
    private javax.swing.JLabel jLImagePreview;
    private javax.swing.JLabel jLObjId;
    private javax.swing.JLabel jLOccluded;
    private javax.swing.JLabel jLType;
    private javax.swing.JLabel jLValue;
    private javax.swing.JMenuBar jMBBBoxWindow;
    private javax.swing.JMenu jMEditObject;
    private javax.swing.JMenu jMFile;
    private javax.swing.JMenuItem jMICloseDialog;
    private javax.swing.JMenuItem jMIDecreaseBottom;
    private javax.swing.JMenuItem jMIDecreaseBox;
    private javax.swing.JMenuItem jMIDecreaseLeft;
    private javax.swing.JMenuItem jMIDecreaseRight;
    private javax.swing.JMenuItem jMIDecreaseTop;
    private javax.swing.JMenuItem jMIDeleteObj;
    private javax.swing.JMenuItem jMIIncreaseBottom;
    private javax.swing.JMenuItem jMIIncreaseBox;
    private javax.swing.JMenuItem jMIIncreaseLeft;
    private javax.swing.JMenuItem jMIIncreaseRight;
    private javax.swing.JMenuItem jMIIncreaseTop;
    private javax.swing.JMenuItem jMIMoveDown;
    private javax.swing.JMenuItem jMIMoveLeft;
    private javax.swing.JMenuItem jMIMoveRight;
    private javax.swing.JMenuItem jMIMoveUp;
    private javax.swing.JMenuItem jMISaveObj;
    private javax.swing.JPanel jPBackground;
    private javax.swing.JPanel jPObjAttributes;
    private javax.swing.JPanel jPOptions;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    // End of variables declaration//GEN-END:variables
}
