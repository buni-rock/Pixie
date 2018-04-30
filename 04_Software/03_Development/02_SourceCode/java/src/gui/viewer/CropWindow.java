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
import gui.support.BrushOptions;
import gui.support.CropObject;
import gui.support.CropWindowConfig;
import graphictablet.JPenFunctions;
import library.Resize;
import observers.NotifyObservers;
import observers.ObservedActions;
import paintpanels.DrawConstants;
import paintpanels.DrawingPanel;
import paintpanels.ResultPanel;
import gui.support.CustomTreeNode;
import commonsegmentation.ScribbleInfo;
import jpen.owner.multiAwt.AwtPenToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import segmentation.MattingThreading;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * The type Crop window.
 *
 * @author Olimpia Popica
 */
public class CropWindow extends javax.swing.JDialog implements Observer {

    /**
     * The event dispatcher for the keyboard in order to execute specific tasks
     * for the crop window.
     */
    private transient CropWinKeyEventDispatcher cwKeyEventDispatch;

    /**
     * Allows the user to activate different functionalities from the graphic
     * tablet.
     */
    private transient JPenFunctions penFunctions;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * The panel containing the cropped image.
     */
    private DrawingPanel dPCropImg;

    /**
     * The panel containing the cropped image result.
     */
    private ResultPanel dPCropResult;

    /**
     * The original image displayed on the panel
     */
    private transient BufferedImage origImg;

    /**
     * The original image in the frame; the whole image - original size.
     */
    private final transient BufferedImage frameImg;

    /**
     * The selected crop for being edited.
     */
    private final transient CropObject currentCrop;

    /**
     * The original position of the crop in the image; before being modified.
     */
    private final Rectangle origCropPos;

    /**
     * Encapsulates the call for running the cuda matting on a different thread.
     */
    private transient MattingThreading mattingThread;

    /**
     * The type of drawing: background or object.
     */
    private int drawingType;

    /**
     * The configuration of the crop window.
     */
    private transient CropWindowConfig cropWindowConfig;

    /**
     * The list of colors which were used for the segmented objects in the
     * current frame. It helps to prevent the segmentation of two objects with
     * the same color.
     */
    private final List<Color> objColorsList;

    /**
     * Shows who called the crop window: the normal crop or the edit crop. Class
     * ObservedActions shows the possible actions.
     */
    private final ObservedActions.Action actionOwner;

    /**
     * Used to preview the result image in a new window, with zoom available.
     */
    private ImagePreview resultPrev;

    /**
     * The list of scribbles to be displayed on the panel.
     */
    private List<ScribbleInfo> scribbleList;

    /**
     * The text on the show original image button.
     */
    private static final String ORIGINAL_TEXT = "Original";
    /**
     * The text on the show highlighted image button.
     */
    private static final String HIGHLIGHT_TEXT = "Highlight";

    /**
     * The list of object attributes: type, class, value.
     */
    private CustomTreeNode objectAttributes;

    /**
     * User preferences related to the application.
     */
    private final UserPreferences userPrefs;

    /**
     * logger instance
     */
    private final transient Logger log = LoggerFactory.getLogger("gui.CropWindow");

    /**
     * Creates new form CropWindow
     *
     * @param parent           the parent component of the dialog
     * @param frameImage       the original image, in original size
     * @param currentCrop      the crop object, being segmented
     * @param cropWinCfg       the configuration of the crop window
     * @param objectAttributes the list of object attributes: type, class, value
     * @param actionOwner      the scope of the dialog: create new crop, edit existing one
     * @param objColorsList    the list of already used colors (for other objects)
     * @param userPreferences user preferences regarding application
     * configuration
     */
    public CropWindow(java.awt.Frame parent,
            BufferedImage frameImage,
            CropObject currentCrop,
            CropWindowConfig cropWinCfg,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences) {
        super(parent, true);

        this.frameImg = frameImage;
        this.currentCrop = currentCrop;
        this.actionOwner = actionOwner;
        this.objColorsList = objColorsList;
        this.userPrefs = userPreferences;

        // save the original object position in order to be able to reset the changes
        this.origCropPos = new Rectangle(currentCrop.getPositionOrig());

        this.objectAttributes = objectAttributes;

        initComponents();

        initOtherVariables();

        initAttributesComponents();

        loadWindowConfig(cropWinCfg);

        loadImage();

        setBrushOptions();

        // add pen listener to be able to use some features of the graphic tablet
        AwtPenToolkit.addPenListener(dPCropImg, penFunctions);

        updateMouseAndBrush();

        // prepare the frame to be shown
        prepareFrame();
    }

    private void initOtherVariables() {
        drawingType = 0;

        // init the listener for the pen for using the graphic tablet
        penFunctions = new JPenFunctions();
        // add observer to be notified when there are changes from the pen
        penFunctions.addObserver(this);

        // remove the text from the labels which should show an image
        jLBrushPreview.setText("");
        jLBrushSizePreview.setText("");

        // add keyboard listener to be able to set events on the wanted keys
        cwKeyEventDispatch = new CropWinKeyEventDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(cwKeyEventDispatch);
    }

    /**
     * Create the proper design for the components representing the object
     * attributes and populate the combo boxes with the data defined in the
     * tree.
     */
    private void initAttributesComponents() {
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

    private void loadWindowConfig(CropWindowConfig cropWinCfg) {
        if (cropWinCfg != null) {
            jCBMergeBKG.setSelected(cropWinCfg.isMergeBKG());
            jSCropBrushSize.setValue(cropWinCfg.getBrushSize());
            jSCropBrushDensity.setValue(cropWinCfg.getBrushDensity());
            this.cropWindowConfig = cropWinCfg;

            displayObjectId();
        }
    }

    private void loadImage() {
        this.origImg = Utils.getSelectedImg(frameImg, currentCrop.getPositionOrig());

        if ((origImg != null)
                && (origImg.getWidth() > 0)
                && (origImg.getHeight() > 0)) {

            // remove the old image if it exists
            if (dPCropImg != null) {
                this.remove(dPCropImg);
                jPCropImg.remove(dPCropImg);
            }

            // Load the file to be labeled in the panel and show it
            dPCropImg = new DrawingPanel(origImg, new Dimension(origImg.getWidth(), origImg.getHeight()), DrawConstants.DrawType.DRAW_SCRIBBLE);
            dPCropImg.setBackground(new java.awt.Color(255, 255, 255));
            jPCropImg.add(dPCropImg);

            this.validate();
            this.repaint();

            loadCropResult(origImg);

            // set the drawing type to avoid changing from obj to bkg when editing the object
            dPCropImg.setActionType(drawingType);

            // add the existing scriblles
            if (scribbleList != null) {
                dPCropImg.setScribbleList(scribbleList);
            }

            // set the color on the drawing panel
            dPCropImg.setObjColor(cropWindowConfig.getObjectColor());

            // add the title of the frame
            setFrameTitle();
        }
    }

    /**
     * Load the segmented result of the crop image to a visualisation panel.
     */
    private void loadCropResult(BufferedImage cropResult) {
        // remove the old image if it exists
        if (dPCropResult != null) {
            this.remove(dPCropResult);
            jPCropResult.remove(dPCropResult);
        }

        // load the viewer of the result
        dPCropResult = new ResultPanel(cropResult, new Dimension(cropResult.getWidth(), cropResult.getHeight()), cropWindowConfig.getObjectColor());
        dPCropResult.updateResultImg();
        dPCropResult.setBackground(new java.awt.Color(255, 255, 255));

        jPCropResult.add(dPCropResult);

        this.validate();
        this.repaint();

    }

    /**
     * Refresh the segmented crop image to a visualisation panel.
     */
    private void refreshCropResult() {
        if (dPCropResult != null) {
            dPCropResult.updateResultImg();
        }

        this.validate();
        this.repaint();
    }

    /**
     * Prepare the needed input for the matting application, call it and do the
     * load actions for the gui.
     *
     * @param imageOption - on which image shall the algorithm be run original, highlighted etc.
     */
    public void runMatting(int imageOption) {
        Thread cudaMattingThread;

        if ((dPCropImg != null) && (jBRunMattingCrop.isEnabled())) {
            // save the scribble points
            int noScribbles = dPCropImg.flushPixelList();

            // prepare the thread data to run the matting application
            if (noScribbles > 0) {
                // disable the matting button, to avoid calling the thread several times
                jBRunMattingCrop.setEnabled(false);
                jBRunHighlightMatt.setEnabled(false);

                mattingThread = new MattingThreading(getMattingImage(imageOption), dPCropImg.getScribbleList(), ObservedActions.Action.REFRESH_CROP_RESULT);

                // add observer to be notified when the thread is finished
                mattingThread.addObserver(this);

                cudaMattingThread = new Thread(mattingThread);
                cudaMattingThread.setName("Matting Thread");
                cudaMattingThread.start();

                currentCrop.resetFilterCount();
            }
        }
    }

    /**
     * Set the list of scribbles drawn for the segmentation of the current
     * object.
     *
     * @param scribbleList - the list of scribbles which are already available for the object
     */
    public void setScribbleList(List<ScribbleInfo> scribbleList) {
        this.scribbleList = scribbleList;

        /* TODO: workaround for displaying the scribbles from Behemoth */
        Resize resize = new Resize(1.0, 1.0);

        for (ScribbleInfo scribble : scribbleList) {
            scribble.setPanelPos(resize.originalToResized(scribble.getImgPos()));
        }

        dPCropImg.setScribbleList(this.scribbleList);
    }

    /**
     * Get the object map of the segmentation.
     *
     * @return - the map containing the ids of the objects
     */
    public byte[][] getObjectMap() {
        return dPCropResult.getObjMap();
    }

    /**
     * Get the configuration of the crop window.
     *
     * @return - the configuration of the modifiable fields of the crop window: merge BKG, brush size and density
     */
    public CropWindowConfig getCropConfig() {
        return cropWindowConfig;
    }

    /**
     * Get the result image, after applying the segmentation algorithms.
     *
     * @return - the image representing the result of the segmentation
     */
    public BufferedImage getResultImage() {
        return dPCropResult.getWorkImg();
    }

    /**
     * Get the list of scribbles drawn for the segmentation of the current
     * object.
     *
     * @return - the list of scribbles used for segmentation
     */
    public List<ScribbleInfo> getScribbleList() {
        return dPCropImg.getScribbleList();
    }

    /**
     * Get the color of the scribbles used for the segmentation of the object.
     *
     * @return - the color of the scribbles, represented in RGB
     */
    public Color getScribbleColor() {
        return cropWindowConfig.getObjectColor();
    }

    /**
     * Removes the scribbles which no longer fit in the image; the ones which
     * would generate a coordinates outer box exception.
     */
    private void removeScribbles() {
        // copy the panel scribble list in the scribble list, to avoid erasing them after the new load of image
        scribbleList = new ArrayList<>();
        scribbleList.addAll(dPCropImg.getScribbleList());

        if (scribbleList != null) {
            for (Iterator<ScribbleInfo> it = scribbleList.iterator(); it.hasNext();) {
                ScribbleInfo si = it.next();

                if ((si.getImgPosX() >= currentCrop.getPositionOrig().width)
                        || (si.getImgPosY() >= currentCrop.getPositionOrig().height)) {
                    // remove the scribble
                    it.remove();
                }
            }
        }
    }

    /**
     * Chooses which actions to happen based on the feedback from the observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ObservedActions.Action) {
            ObservedActions.Action type = ((ObservedActions.Action) arg);
            switch (type) {
                case REFRESH_CROP_RESULT:
                    updateCropResult();
                    break;

                case REFRESH_BRUSH_SIZE:
                    refreshBrushSize();
                    break;

                case FILTER_OBJECT_MAP:
                    filterObjMap();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Update the type of action and the preview of the brush.
     *
     * @param actionType - the type of action: -1 = erase; 0 = background; 1 =
     * object
     */
    private void updateObjInfo(int actionType) {
        this.drawingType = actionType;
        dPCropImg.setActionType(actionType);

        updateMouseAndBrush();
    }

    /**
     * When a change in the mouse brush happens, update the mouse icon and the
     * brush preview.
     */
    private void updateMouseAndBrush() {
        // update the brush
        setBrushOptions();
        brushPreview();

        // update the mouse icon
        dPCropImg.setCursor(Utils.generateCustomIcon(dPCropImg.getBrushOpt(), drawingType, cropWindowConfig.getObjectColor()));
    }

    /**
     * Allows the user to apply the wanted options to the current drawing
     * environment.
     */
    private void setBrushOptions() {
        BrushOptions brushOpt = new BrushOptions();
        brushOpt.setBrushSize(jSCropBrushSize.getValue());
        brushOpt.setBrushDensity((float) jSCropBrushDensity.getValue() / (float) jSCropBrushDensity.getMaximum());

        dPCropImg.setBrushOptions(brushOpt);
    }

    /**
     * Show an image with a preview of the brush using the current selected
     * options (size, density, color).
     */
    private void brushPreview() {
        Random rand = new Random();
        float density = (float) jSCropBrushDensity.getValue() / (float) jSCropBrushDensity.getMaximum();
        int win = jSCropBrushSize.getValue();

        byte[][] circleMask = Utils.getCircleMask(win);  // the mask making the mouse pointer a circle

        //minimum size of the window 1
        win = win < 1 ? 1 : win;

        BufferedImage brushPrev;
        BufferedImage brushSizePrev;

        //for the erase option draw a special preview
        if (drawingType > ConstantsLabeling.ACTION_TYPE_ERASE) {
            brushPrev = new BufferedImage(win, win, BufferedImage.TYPE_INT_RGB);
            brushSizePrev = new BufferedImage(win, win, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < win; x++) {
                for (int y = 0; y < win; y++) {
                    float randInt = rand.nextFloat();
                    //set white background for the preview
                    brushPrev.setRGB(x, y, Color.white.getRGB());
                    brushSizePrev.setRGB(x, y, Color.white.getRGB());

                    // draw just if it is inside the circle
                    if (circleMask[x][y] == (byte) 1) {
                        //for the size, the drawing is full
                        brushSizePrev.setRGB(x, y, Utils.getDrawingColor(drawingType, cropWindowConfig.getObjectColor()).getRGB());

                        //if the is chosen to be saved by the random generator, add it in the preview image
                        if (randInt < density) {
                            brushPrev.setRGB(x, y, Utils.getDrawingColor(drawingType, cropWindowConfig.getObjectColor()).getRGB());
                        }
                    }
                }
            }
        } else {
            win += 2;   //border the icon with one pixel
            brushPrev = new BufferedImage(win, win, BufferedImage.TYPE_INT_RGB);
            brushSizePrev = new BufferedImage(win, win, BufferedImage.TYPE_INT_RGB);

            //draw black border arround the icon for visibility
            for (int x = 1; x < win - 1; x++) {
                for (int y = 1; y < win - 1; y++) {
                    brushSizePrev.setRGB(x, y, Color.white.getRGB());
                    brushPrev.setRGB(x, y, Color.white.getRGB());
                }
            }
        }

        ImageIcon iconLogo = new ImageIcon(brushPrev);
        jLBrushPreview.setIcon(iconLogo);

        //draw also the preview of the brush size
        iconLogo = new ImageIcon(brushSizePrev);
        jLBrushSizePreview.setIcon(iconLogo);
    }

    /**
     * Allows another module to put an observer into the current module.
     *
     * @param o - the observer to be added
     */
    public void addObserver(Observer o) {
        observable.addObserver(o);
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
     * When the window is opening it will have its own key dispatcher. The
     * dispatcher of the gui should no longer be active.
     */
    public void removeOtherKeyEventDispatcher() {
        // notify to remove the gui key event dispatcher
        observable.notifyObservers(ObservedActions.Action.REMOVE_GUI_KEY_EVENT_DISPATCHER);
    }

    private void closeWindow() {
        // remove own key event dispatcher
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(cwKeyEventDispatch);

        // notify to add the gui key event dispatcher back
        observable.notifyObservers(ObservedActions.Action.ADD_GUI_KEY_EVENT_DISPATCHER);

        dispose();
    }

    /**
     * Returns the selected type of object.
     *
     * @return - the chosen attribute for type for the object
     */
    public String getObjType() {
        // make sure the selection is valid
        if (jCBObjType.getSelectedIndex() > -1) {
            return jCBObjType.getItemAt(jCBObjType.getSelectedIndex());
        }

        return "";
    }

    /**
     * Returns the selected class of object.
     *
     * @return - the chosen attribute for class for the object
     */
    public String getObjClass() {
        // make sure the selection is valid
        if (jCBObjClass.getSelectedIndex() > -1) {
            return jCBObjClass.getItemAt(jCBObjClass.getSelectedIndex());
        }

        return "";
    }

    /**
     * Returns the selected value of object.
     *
     * @return - the chosen attribute for value for the object
     */
    public String getObjValue() {
        // make sure the selection is valid
        if (jCBObjValue.getSelectedIndex() > -1) {
            return jCBObjValue.getItemAt(jCBObjValue.getSelectedIndex());
        }

        return "";
    }

    /**
     * Returns the occlusion of object.
     *
     * @return - the specified occlusion of the object
     */
    public String getObjOccluded() {
        // make sure the selection is valid
        if (jCBOccluded.getSelectedIndex() > -1) {
            return jCBOccluded.getItemAt(jCBOccluded.getSelectedIndex());
        }

        return "";
    }

    /**
     * Sets the title of the frame with the current size of the image.
     */
    private void setFrameTitle() {
        setTitle("Cropped Image  " + origImg.getWidth() + "x" + origImg.getHeight());
    }

    /**
     * Returns the original image or the modified one, based on user choice.
     *
     * @return - original image or the work image
     */
    private BufferedImage getMattingImage(int imageOption) {

        switch (imageOption) {

            case Constants.RUN_MATT_HIGHLIGHT_IMG:
                //the user wants to run the matting on the highlighted image
                return Utils.histogramEqColor(dPCropImg.getWorkImg());

            case Constants.RUN_MATT_ORIG_IMG:
                //the user wants to run the matting on the original image
                return dPCropImg.getOrigImg();

            default:
                // option unknown: return the original image
                return dPCropImg.getOrigImg();
        }
    }

    private void displayObjectId() {
        // add the object id
        // set the text of the object radio button
        if (cropWindowConfig.getObjectId() > 0) {
            jRBCropObj1.setText("obj_" + cropWindowConfig.getObjectId());
        }

        // set the color of the text of the object radio button
        jRBCropObj1.setForeground(cropWindowConfig.getObjectColor());
    }

    /**
     * Filter the object map, in order to remove the outlier points.
     */
    private void filterObjMap() {
        if (dPCropResult == null) {
            return;
        }
        // filter the object map to remove outlier points
        dPCropResult.filterObjectMap();
        refreshCropResult();

        if (resultPrev != null) {
            // refresh the image with the output of the filtering
            resultPrev.setImage(dPCropResult.getWorkImg());
        }
        // count how many times the filtering was done (statistics purposes)
        currentCrop.incrementFilterCount();
    }

    /**
     * Update the crop result.
     */
    private void updateCropResult() {
        if (mattingThread != null) {
            // the thread finished the run and the result should be ready
            dPCropResult.setObjMap(mattingThread.getObjMap());
            refreshCropResult();
        }

        // enable the button, so the user can run again the algorithm
        jBRunMattingCrop.setEnabled(true);
        jBRunHighlightMatt.setEnabled(true);
    }

    /**
     * Refresh the size of the brush.
     */
    private void refreshBrushSize() {
        if ((penFunctions.getBrushSize() != 0)
                && (penFunctions.getBrushSize() != penFunctions.getBrushSizePrev())) {

            jSCropBrushSize.setValue(penFunctions.getBrushSize());

            penFunctions.setBrushSizePrev(penFunctions.getBrushSize());

            updateMouseAndBrush();
        }
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
     * The key event dispatcher for listening the keys and the implementation of
     * the actions for some defined keys.
     */
    private class CropWinKeyEventDispatcher implements KeyEventDispatcher {

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

            // the mapping of the keys is based on the default assignment of keys  for UGEE graphic tablet
            if (eventId == KeyEvent.KEY_PRESSED) {
                if (e.isControlDown()) {
                    switch (key) {
                        case KeyEvent.VK_PLUS:
                            log.trace("Key pressed: Ctrl NumPad+ =  Zoom in");
                            break;

                        case KeyEvent.VK_MINUS:
                            log.trace("Key pressed: Ctrl NumPad- =  Zoom out");
                            break;

                        case KeyEvent.VK_Z:
                            selectBackground();

                            log.trace("Key pressed: Ctrl Z = Background");
                            break;

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
                } else if (e.isAltDown()) {
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
                } else {

                    switch (key) {
                        case KeyEvent.VK_CLOSE_BRACKET:
                            increaseBrushSize();

                            log.trace("Key pressed: Close Bracket = Brush larger");
                            break;

                        case KeyEvent.VK_OPEN_BRACKET:
                            decreaseBrushSize();

                            log.trace("Key pressed: Open Bracket = Brush smaller");
                            break;

                        case KeyEvent.VK_V:
                            selectObject();

                            log.trace("Key pressed: V = Obiect 1");
                            break;

                        case KeyEvent.VK_B:
                            selectEraser();

                            log.trace("Key pressed:  B = Erase");

                            break;

                        case KeyEvent.VK_E:
                            // run the matting app
                            runMatting(Constants.RUN_MATT_ORIG_IMG);
                            log.trace("Key pressed: E = Run matting");
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

                        case KeyEvent.VK_DELETE:
                            closeWindow();
                            observable.notifyObservers(ObservedActions.Action.REMOVE_SELECTED_OBJECT);
                            break;

                        case KeyEvent.VK_ESCAPE:
                            // cancel the object being edited
                            cancelObject();
                            break;

                        case KeyEvent.VK_ENTER:
                            saveCrop();
                            break;

                        default:
                            // do nothing
                            break;
                    }
                }
            }
        }
    }

        /**
     * Increase the size of the brush with one.
     */
    private void increaseBrushSize() {
        // increase the size of the brush
        jSCropBrushSize.setValue(jSCropBrushSize.getValue() + 1);
        jSCropBrushSize.setValue(jSCropBrushSize.getValue() + 1);

        updateMouseAndBrush();
    }

    /**
     * Decrease the size of the brush with one.
     */
    private void decreaseBrushSize() {
        // decrease the size of the brush
        jSCropBrushSize.setValue(jSCropBrushSize.getValue() - 1);
        jSCropBrushSize.setValue(jSCropBrushSize.getValue() - 1);

        updateMouseAndBrush();
    }

    /**
     * Activate the object, in order to draw scribbles for it.
     */
    private void selectObject() {
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_OBJECT);
        jRBCropObj1.setSelected(true);
    }

    /**
     * Activate the eraser in order to remove the incorrect drawn points.
     */
    private void selectEraser() {
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_ERASE);
        jRBCropErase.setSelected(true);
    }

    /**
     * Activate the background, in order to draw scribbles for it.
     */
    private void selectBackground() {
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_BACKGROUND);
        jRBCropBKG.setSelected(true);
    }

    /**
         * Moves an object in the image with the specified offsets.
         */
        private void moveObject(int offsetX, int offsetY) {
            // move the object in the display window
            Rectangle newPos = new Rectangle(currentCrop.getPositionOrig().x + offsetX,
                    currentCrop.getPositionOrig().y + offsetY,
                    currentCrop.getPositionOrig().width,
                    currentCrop.getPositionOrig().height);

            if ((newPos.x >= 0)
                    && (newPos.y >= 0)
                && (newPos.x + newPos.width < frameImg.getWidth())
                && (newPos.y + newPos.height < frameImg.getHeight())) {

                currentCrop.setPositionOrig(newPos);

                // remove the scribbles which are out of the boundaries
                removeScribbles();

                // refresh the displayed image
                loadImage();

                // run matting on the new image
                runMatting(Constants.RUN_MATT_ORIG_IMG);

                // refresh also the image on the gui
                observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
            }
        }

        /**
         * Changes the size of the box with the specified offsets.
         *
         * @param left - how much should the object be modified on the left side
         * @param top - how much should the object be modified on the top part
     * @param right - how much should the object be modified on the right side
     * @param bottom - how much should the object be modified on the bottom part
         */
        private void changeSize(int left, int top, int right, int bottom) {
            // move the object in the display window
            Rectangle newPos = currentCrop.getPositionOrig();
            newPos.setBounds(newPos.x + left, newPos.y + top, newPos.width + right - left, newPos.height + bottom - top);

            currentCrop.setPositionOrig(newPos);

            // remove the scribbles which are out of the boundaries
            removeScribbles();

            // refresh the displayed image
            loadImage();

            // run matting on the new image
            runMatting(Constants.RUN_MATT_ORIG_IMG);

            // add the title of the frame
            setFrameTitle();

            // prepare the frame to be shown
            prepareFrame();

            // refresh also the image on the gui
            observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
        }

        /**
         * Check if there is another window opened on top of the current one.
         *
         * @return - true if the current frame is the focus owner
         */
        private boolean frameIsFocusOwner() {
            return (CropWindow.this.getFocusOwner() != null);
        }

    /**
     * Notifies the controller that the crop was segmented and it has to be
     * saved.
     */
    private void saveCrop() {
        if (!isObjectAttributesSet()) {
            // the user wants to correct the data, do not close the application!
            return;
        }

        // save the configuration of the crop window for the next time it will be opened
        cropWindowConfig = new CropWindowConfig(jCBMergeBKG.isSelected(), jSCropBrushSize.getValue(), jSCropBrushDensity.getValue(), jRBCropObj1.getForeground(), cropWindowConfig.getObjectId());

        // notify the other form that the needed info is available
        observable.notifyObservers(actionOwner);

        closeWindow();
    }

    /**
     * Notifies the controller that the object was cancel.
     */
    private void cancelObject() {
        // notify to track the object if the box is in edit mode
        if ((actionOwner != ObservedActions.Action.SAVE_BOUNDING_BOX)
                && (actionOwner != ObservedActions.Action.ADD_CROP_TO_OBJECT)) {
            // save the object in the ground truth
            observable.notifyObservers(ObservedActions.Action.CANCEL_CURRENT_OBJECT);
        }

        closeWindow();
    }

    /**
     * Code related to the frame like: pack, set location etc.
     */
    private void prepareFrame() {
        this.pack();
        this.setMinimumSize(getPreferredSize());
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

        jBGObjects = new javax.swing.ButtonGroup();
        jPCropBackground = new javax.swing.JPanel();
        jPCropArea = new javax.swing.JPanel();
        jBSaveScribble = new javax.swing.JButton();
        jPCropBrushOptions = new javax.swing.JPanel();
        jSCropBrushSize = new javax.swing.JSlider();
        jSCropBrushDensity = new javax.swing.JSlider();
        jLCropBrushSize = new javax.swing.JLabel();
        jLCropBrushDensity = new javax.swing.JLabel();
        jLBrushPreview = new javax.swing.JLabel();
        jLBrushSizePreview = new javax.swing.JLabel();
        jPCropResult = new javax.swing.JPanel();
        jPObjAttributes = new javax.swing.JPanel();
        jLClass = new javax.swing.JLabel();
        jLType = new javax.swing.JLabel();
        jLValue = new javax.swing.JLabel();
        jLOccluded = new javax.swing.JLabel();
        jCBObjClass = new javax.swing.JComboBox<>();
        jCBObjType = new javax.swing.JComboBox<>();
        jCBObjValue = new javax.swing.JComboBox<>();
        jCBOccluded = new javax.swing.JComboBox<>();
        jPOptions = new javax.swing.JPanel();
        jBChangeObjColor = new javax.swing.JButton();
        jRBCropErase = new javax.swing.JRadioButton();
        jRBCropObj1 = new javax.swing.JRadioButton();
        jRBCropBKG = new javax.swing.JRadioButton();
        jBHistogramEq = new javax.swing.JButton();
        jBFilterObjMap = new javax.swing.JButton();
        jBRestorePos = new javax.swing.JButton();
        jPSegmentation = new javax.swing.JPanel();
        jBRunMattingCrop = new javax.swing.JButton();
        jCBMergeBKG = new javax.swing.JCheckBox();
        jPCropImg = new javax.swing.JPanel();
        jBRunHighlightMatt = new javax.swing.JButton();
        jMBCropWindow = new javax.swing.JMenuBar();
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
        jMIncreaseBottom = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMIDecreaseBox = new javax.swing.JMenuItem();
        jMIIncreaseBox = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMIDeleteObj = new javax.swing.JMenuItem();
        jMIMattingOptions = new javax.swing.JMenu();
        jMSelectBKG = new javax.swing.JMenuItem();
        jMISelectObj = new javax.swing.JMenuItem();
        jMISelectErase = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMIncreaseBrushSize = new javax.swing.JMenuItem();
        jMIDecreaseBrushSize = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMIRunMatting = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPCropBackground.setBackground(new Color(0, 0, 0, 200));
        jPCropBackground.setLayout(new java.awt.GridBagLayout());

        jPCropArea.setBackground(new java.awt.Color(255, 255, 255));
        jPCropArea.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPCropArea.setLayout(new java.awt.GridBagLayout());

        jBSaveScribble.setBackground(new java.awt.Color(255, 255, 255));
        jBSaveScribble.setText("Save");
        jBSaveScribble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveScribbleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPCropArea.add(jBSaveScribble, gridBagConstraints);

        jPCropBrushOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPCropBrushOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Brush"));
        jPCropBrushOptions.setLayout(new java.awt.GridBagLayout());

        jSCropBrushSize.setBackground(new java.awt.Color(255, 255, 255));
        jSCropBrushSize.setMaximum(30);
        jSCropBrushSize.setMinimum(1);
        jSCropBrushSize.setOrientation(javax.swing.JSlider.VERTICAL);
        jSCropBrushSize.setPaintTicks(true);
        jSCropBrushSize.setToolTipText("");
        jSCropBrushSize.setValue(5);
        jSCropBrushSize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSCropBrushSizeMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPCropBrushOptions.add(jSCropBrushSize, gridBagConstraints);

        jSCropBrushDensity.setBackground(new java.awt.Color(255, 255, 255));
        jSCropBrushDensity.setMinimum(1);
        jSCropBrushDensity.setOrientation(javax.swing.JSlider.VERTICAL);
        jSCropBrushDensity.setPaintTicks(true);
        jSCropBrushDensity.setValue(20);
        jSCropBrushDensity.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSCropBrushDensityMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPCropBrushOptions.add(jSCropBrushDensity, gridBagConstraints);

        jLCropBrushSize.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPCropBrushOptions.add(jLCropBrushSize, gridBagConstraints);

        jLCropBrushDensity.setText("Density");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPCropBrushOptions.add(jLCropBrushDensity, gridBagConstraints);

        jLBrushPreview.setText("Brush");
        jLBrushPreview.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLBrushPreview.setPreferredSize(new java.awt.Dimension(35, 35));
        jLBrushPreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.02;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPCropBrushOptions.add(jLBrushPreview, gridBagConstraints);

        jLBrushSizePreview.setText("Brush");
        jLBrushSizePreview.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLBrushSizePreview.setPreferredSize(new java.awt.Dimension(35, 35));
        jLBrushSizePreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.01;
        jPCropBrushOptions.add(jLBrushSizePreview, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPCropArea.add(jPCropBrushOptions, gridBagConstraints);

        jPCropResult.setBackground(new java.awt.Color(255, 255, 255));
        jPCropResult.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPCropResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPCropResultMouseClicked(evt);
            }
        });
        jPCropResult.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        jPCropArea.add(jPCropResult, gridBagConstraints);

        jPObjAttributes.setBackground(new java.awt.Color(255, 255, 255));
        jPObjAttributes.setLayout(new java.awt.GridBagLayout());

        jLClass.setText("Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLClass, gridBagConstraints);

        jLType.setText("Class");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLType, gridBagConstraints);

        jLValue.setText("Value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLValue, gridBagConstraints);

        jLOccluded.setText("Occluded");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jLOccluded, gridBagConstraints);

        jCBObjClass.setPreferredSize(new java.awt.Dimension(150, 23));
        jCBObjClass.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBObjClassItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjClass, gridBagConstraints);

        jCBObjType.setPreferredSize(new java.awt.Dimension(150, 23));
        jCBObjType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBObjTypeItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjType, gridBagConstraints);

        jCBObjValue.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBObjValue, gridBagConstraints);

        jCBOccluded.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "false", "true (25%)", "true (50%)", "true (75%)", "true (100%)" }));
        jCBOccluded.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjAttributes.add(jCBOccluded, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        jPCropArea.add(jPObjAttributes, gridBagConstraints);

        jPOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPOptions.setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPOptions.add(jBChangeObjColor, gridBagConstraints);

        jRBCropErase.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBCropErase);
        jRBCropErase.setText("Erase");
        jRBCropErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBCropEraseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPOptions.add(jRBCropErase, gridBagConstraints);

        jRBCropObj1.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBCropObj1);
        jRBCropObj1.setForeground(new java.awt.Color(51, 255, 0));
        jRBCropObj1.setText("obj");
        jRBCropObj1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBCropObj1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPOptions.add(jRBCropObj1, gridBagConstraints);

        jRBCropBKG.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBCropBKG);
        jRBCropBKG.setForeground(new java.awt.Color(255, 0, 0));
        jRBCropBKG.setSelected(true);
        jRBCropBKG.setText("bkg");
        jRBCropBKG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBCropBKGActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPOptions.add(jRBCropBKG, gridBagConstraints);

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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPOptions.add(jBHistogramEq, gridBagConstraints);

        jBFilterObjMap.setBackground(new java.awt.Color(255, 255, 255));
        jBFilterObjMap.setText("Filter Obj Map");
        jBFilterObjMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBFilterObjMapActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPOptions.add(jBFilterObjMap, gridBagConstraints);

        jBRestorePos.setBackground(new java.awt.Color(255, 255, 255));
        jBRestorePos.setText("Restore Pos");
        jBRestorePos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRestorePosActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPOptions.add(jBRestorePos, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPCropArea.add(jPOptions, gridBagConstraints);

        jPSegmentation.setBackground(new java.awt.Color(255, 255, 255));
        jPSegmentation.setLayout(new java.awt.GridBagLayout());

        jBRunMattingCrop.setBackground(new java.awt.Color(255, 255, 255));
        jBRunMattingCrop.setText("Run Matting");
        jBRunMattingCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRunMattingCropActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPSegmentation.add(jBRunMattingCrop, gridBagConstraints);

        jCBMergeBKG.setBackground(new java.awt.Color(255, 255, 255));
        jCBMergeBKG.setSelected(true);
        jCBMergeBKG.setText("mergeBKG");
        jCBMergeBKG.setToolTipText("Check for merging the image as it was segmented.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPSegmentation.add(jCBMergeBKG, gridBagConstraints);

        jPCropImg.setBackground(new java.awt.Color(255, 255, 255));
        jPCropImg.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPCropImg.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPSegmentation.add(jPCropImg, gridBagConstraints);

        jBRunHighlightMatt.setBackground(new java.awt.Color(255, 255, 255));
        jBRunHighlightMatt.setText("Run Highlight");
        jBRunHighlightMatt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBRunHighlightMattActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPSegmentation.add(jBRunHighlightMatt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPCropArea.add(jPSegmentation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPCropBackground.add(jPCropArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        getContentPane().add(jPCropBackground, gridBagConstraints);

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

        jMBCropWindow.add(jMFile);

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

        jMIncreaseBottom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jMIncreaseBottom.setText("Increase Bottom");
        jMIncreaseBottom.setToolTipText("Add a pixel on the bottom side of the object");
        jMIncreaseBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIncreaseBottomActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIncreaseBottom);
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

        jMBCropWindow.add(jMEditObject);

        jMIMattingOptions.setText("Actions");

        jMSelectBKG.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMSelectBKG.setText("Select Background");
        jMSelectBKG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMSelectBKGActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMSelectBKG);

        jMISelectObj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        jMISelectObj.setText("Select Object");
        jMISelectObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISelectObjActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMISelectObj);

        jMISelectErase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        jMISelectErase.setText("Select Erase");
        jMISelectErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISelectEraseActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMISelectErase);
        jMIMattingOptions.add(jSeparator5);

        jMIncreaseBrushSize.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET, 0));
        jMIncreaseBrushSize.setText("Increase Brush Size");
        jMIncreaseBrushSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIncreaseBrushSizeActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMIncreaseBrushSize);

        jMIDecreaseBrushSize.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET, 0));
        jMIDecreaseBrushSize.setText("Decrease Brush Size");
        jMIDecreaseBrushSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBrushSizeActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMIDecreaseBrushSize);
        jMIMattingOptions.add(jSeparator6);

        jMIRunMatting.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        jMIRunMatting.setText("Run Matting");
        jMIRunMatting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIRunMattingActionPerformed(evt);
            }
        });
        jMIMattingOptions.add(jMIRunMatting);

        jMBCropWindow.add(jMIMattingOptions);

        setJMenuBar(jMBCropWindow);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBSaveScribbleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveScribbleActionPerformed
        saveCrop();
    }//GEN-LAST:event_jBSaveScribbleActionPerformed

    private void jBRunMattingCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRunMattingCropActionPerformed
        // display the original image
        dPCropImg.reloadWorkImg();
        jBHistogramEq.setText(HIGHLIGHT_TEXT);

        // run the matting algorithm on the original image
        runMatting(Constants.RUN_MATT_ORIG_IMG);
    }//GEN-LAST:event_jBRunMattingCropActionPerformed

    private void jBHistogramEqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHistogramEqActionPerformed
        if (jBHistogramEq.getText().equals(HIGHLIGHT_TEXT)) {
            dPCropImg.setWorkImg(Utils.histogramEqColor(dPCropImg.getWorkImg()));
            jBHistogramEq.setText(ORIGINAL_TEXT);
        } else if (jBHistogramEq.getText().equals(ORIGINAL_TEXT)) {
            dPCropImg.reloadWorkImg();
            jBHistogramEq.setText(HIGHLIGHT_TEXT);
        }
    }//GEN-LAST:event_jBHistogramEqActionPerformed

    private void jRBCropBKGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBCropBKGActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_BACKGROUND);

    }//GEN-LAST:event_jRBCropBKGActionPerformed

    private void jRBCropObj1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBCropObj1ActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_OBJECT);
    }//GEN-LAST:event_jRBCropObj1ActionPerformed

    private void jRBCropEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBCropEraseActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_ERASE);
    }//GEN-LAST:event_jRBCropEraseActionPerformed

    private void jSCropBrushSizeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSCropBrushSizeMouseReleased
        setBrushOptions();
        brushPreview();
    }//GEN-LAST:event_jSCropBrushSizeMouseReleased

    private void jSCropBrushDensityMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSCropBrushDensityMouseReleased
        setBrushOptions();
        brushPreview();
    }//GEN-LAST:event_jSCropBrushDensityMouseReleased

    private void jBChangeObjColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBChangeObjColorActionPerformed
        Color newColor = JColorChooser.showDialog(
                CropWindow.this,
                "Choose Object Color",
                jRBCropObj1.getForeground());

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
        cropWindowConfig.setObjectColor(newColor);

        // update the local viewer
        jRBCropObj1.setForeground(newColor);

        // update the result panel color
        dPCropResult.setObjColor(newColor);

        // set the color on the drawing panel
        dPCropImg.setObjColor(newColor);

        // set the object as active 
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_OBJECT);
        jRBCropObj1.setSelected(true);

        // update the result image with the new color
        refreshCropResult();
    }//GEN-LAST:event_jBChangeObjColorActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // cancel the object being edited
        cancelObject();
    }//GEN-LAST:event_formWindowClosing

    private void jPCropResultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPCropResultMouseClicked
        if (dPCropResult.getWorkImg() != null) {
            resultPrev = new ImagePreview((Frame) this.getParent(), true, dPCropResult.getWorkImg(), "Image Result", true, "Filter");
            resultPrev.addObserver(this);
            resultPrev.setVisible(true);
        }
    }//GEN-LAST:event_jPCropResultMouseClicked

    private void jBFilterObjMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBFilterObjMapActionPerformed
        filterObjMap();
    }//GEN-LAST:event_jBFilterObjMapActionPerformed

    private void jBRunHighlightMattActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRunHighlightMattActionPerformed
        // set the displayed image as highlighted
        dPCropImg.setWorkImg(Utils.histogramEqColor(dPCropImg.getWorkImg()));
        jBHistogramEq.setText(ORIGINAL_TEXT);

        // run the matting
        runMatting(Constants.RUN_MATT_HIGHLIGHT_IMG);
    }//GEN-LAST:event_jBRunHighlightMattActionPerformed

    private void jBRestorePosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRestorePosActionPerformed
        // reset the object coordinates
        currentCrop.setPositionOrig(new Rectangle(origCropPos));

        // remove the scribbles which are out of the boundaries
        removeScribbles();

        // refresh the displayed image
        loadImage();

        // run matting on the new image
        runMatting(Constants.RUN_MATT_ORIG_IMG);

        // add the title of the frame
        setFrameTitle();

        // prepare the frame to be shown
        prepareFrame();

        // refresh also the image on the gui
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }//GEN-LAST:event_jBRestorePosActionPerformed

    private void jMICloseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMICloseDialogActionPerformed
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

    private void jMIncreaseBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIncreaseBottomActionPerformed
        // increase bottom
        changeSize(0, 0, 0, 1);
    }//GEN-LAST:event_jMIncreaseBottomActionPerformed

    private void jMIDecreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBoxActionPerformed
        // decrease box
        changeSize(1, 1, -1, -1);
    }//GEN-LAST:event_jMIDecreaseBoxActionPerformed

    private void jMIIncreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBoxActionPerformed
        // increase box
        changeSize(-1, -1, 1, 1);
    }//GEN-LAST:event_jMIIncreaseBoxActionPerformed

    private void jMIDeleteObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDeleteObjActionPerformed
        closeWindow();
        observable.notifyObservers(ObservedActions.Action.REMOVE_SELECTED_OBJECT);
    }//GEN-LAST:event_jMIDeleteObjActionPerformed

    private void jMISaveObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISaveObjActionPerformed
        saveCrop();
    }//GEN-LAST:event_jMISaveObjActionPerformed

    private void jMSelectBKGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMSelectBKGActionPerformed
        selectBackground();
    }//GEN-LAST:event_jMSelectBKGActionPerformed

    private void jMISelectObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISelectObjActionPerformed
        selectObject();
    }//GEN-LAST:event_jMISelectObjActionPerformed

    private void jMISelectEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISelectEraseActionPerformed
        selectEraser();
    }//GEN-LAST:event_jMISelectEraseActionPerformed

    private void jMIncreaseBrushSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIncreaseBrushSizeActionPerformed
        increaseBrushSize();
    }//GEN-LAST:event_jMIncreaseBrushSizeActionPerformed

    private void jMIDecreaseBrushSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBrushSizeActionPerformed
        decreaseBrushSize();
    }//GEN-LAST:event_jMIDecreaseBrushSizeActionPerformed

    private void jMIRunMattingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIRunMattingActionPerformed
        // run the matting app
        runMatting(Constants.RUN_MATT_ORIG_IMG);
    }//GEN-LAST:event_jMIRunMattingActionPerformed

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

        Logger logger = LoggerFactory.getLogger("gui.CropWindow");
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
            logger.error("Look and feels exception");
            logger.debug("Look and feels exception {}", ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                BufferedImage bi = ImageIO.read(new File(common.Icons.REINDEER_ICON_PATH));
                CropWindow dialog = new CropWindow(new javax.swing.JFrame(), bi, null, null, null, ObservedActions.Action.OPEN_CROP_SEGMENTATION, null, null);
                dialog.setVisible(true);
            } catch (IOException ex) {
                logger.error("Create and display dialogue");
                logger.debug("Create and display dialogue {}", ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBChangeObjColor;
    private javax.swing.JButton jBFilterObjMap;
    private javax.swing.ButtonGroup jBGObjects;
    private javax.swing.JButton jBHistogramEq;
    private javax.swing.JButton jBRestorePos;
    private javax.swing.JButton jBRunHighlightMatt;
    private javax.swing.JButton jBRunMattingCrop;
    private javax.swing.JButton jBSaveScribble;
    private javax.swing.JCheckBox jCBMergeBKG;
    private javax.swing.JComboBox<String> jCBObjClass;
    private javax.swing.JComboBox<String> jCBObjType;
    private javax.swing.JComboBox<String> jCBObjValue;
    private javax.swing.JComboBox<String> jCBOccluded;
    private javax.swing.JLabel jLBrushPreview;
    private javax.swing.JLabel jLBrushSizePreview;
    private javax.swing.JLabel jLClass;
    private javax.swing.JLabel jLCropBrushDensity;
    private javax.swing.JLabel jLCropBrushSize;
    private javax.swing.JLabel jLOccluded;
    private javax.swing.JLabel jLType;
    private javax.swing.JLabel jLValue;
    private javax.swing.JMenuBar jMBCropWindow;
    private javax.swing.JMenu jMEditObject;
    private javax.swing.JMenu jMFile;
    private javax.swing.JMenuItem jMICloseDialog;
    private javax.swing.JMenuItem jMIDecreaseBottom;
    private javax.swing.JMenuItem jMIDecreaseBox;
    private javax.swing.JMenuItem jMIDecreaseBrushSize;
    private javax.swing.JMenuItem jMIDecreaseLeft;
    private javax.swing.JMenuItem jMIDecreaseRight;
    private javax.swing.JMenuItem jMIDecreaseTop;
    private javax.swing.JMenuItem jMIDeleteObj;
    private javax.swing.JMenuItem jMIIncreaseBox;
    private javax.swing.JMenuItem jMIIncreaseLeft;
    private javax.swing.JMenuItem jMIIncreaseRight;
    private javax.swing.JMenuItem jMIIncreaseTop;
    private javax.swing.JMenu jMIMattingOptions;
    private javax.swing.JMenuItem jMIMoveDown;
    private javax.swing.JMenuItem jMIMoveLeft;
    private javax.swing.JMenuItem jMIMoveRight;
    private javax.swing.JMenuItem jMIMoveUp;
    private javax.swing.JMenuItem jMIRunMatting;
    private javax.swing.JMenuItem jMISaveObj;
    private javax.swing.JMenuItem jMISelectErase;
    private javax.swing.JMenuItem jMISelectObj;
    private javax.swing.JMenuItem jMIncreaseBottom;
    private javax.swing.JMenuItem jMIncreaseBrushSize;
    private javax.swing.JMenuItem jMSelectBKG;
    private javax.swing.JPanel jPCropArea;
    private javax.swing.JPanel jPCropBackground;
    private javax.swing.JPanel jPCropBrushOptions;
    private javax.swing.JPanel jPCropImg;
    private javax.swing.JPanel jPCropResult;
    private javax.swing.JPanel jPObjAttributes;
    private javax.swing.JPanel jPOptions;
    private javax.swing.JPanel jPSegmentation;
    private javax.swing.JRadioButton jRBCropBKG;
    private javax.swing.JRadioButton jRBCropErase;
    private javax.swing.JRadioButton jRBCropObj1;
    private javax.swing.JSlider jSCropBrushDensity;
    private javax.swing.JSlider jSCropBrushSize;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    // End of variables declaration//GEN-END:variables
}
