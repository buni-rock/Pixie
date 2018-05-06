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

import common.Constants;
import common.ConstantsLabeling;
import common.UserPreferences;
import common.Utils;
import gui.support.BrushOptions;
import gui.support.CropObject;
import gui.support.CropWindowConfig;
import gui.support.ObjectPreferences;
import graphictablet.JPenFunctions;
import observers.ObservedActions;
import paintpanels.DrawConstants;
import paintpanels.ResultPanel;
import commonsegmentation.ScribbleInfo;
import gui.support.CustomTreeNode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import javax.swing.ImageIcon;
import jpen.owner.multiAwt.AwtPenToolkit;
import segmentation.MattingThreading;

/**
 *
 * @author Olimpia Popica
 */
public final class CropEdit extends EditWindow {

    /**
     * Allows the user to activate different functionalities from the graphic
     * tablet.
     */
    private transient JPenFunctions penFunctions;

    /**
     * The configuration of the crop window.
     */
    private transient CropWindowConfig cropWindowConfig;

    /**
     * The list of scribbles to be displayed on the panel.
     */
    private List<ScribbleInfo> scribbleList;

    /**
     * Creates new form CropWindow
     *
     * @param parent the parent component of the dialog
     * @param frameImage the original image, in original size
     * @param currentCrop the crop object, being segmented
     * @param cropWinCfg the configuration of the crop window
     * @param objectAttributes the list of object attributes: type, class, value
     * @param actionOwner the scope of the dialog: create new crop, edit
     * existing one
     * @param objColorsList the list of already used colors (for other objects)
     * @param userPreferences user preferences regarding application
     * configuration
     * @param objPreferences user preferences regarding the display of the
     * object
     */
    public CropEdit(java.awt.Frame parent,
            BufferedImage frameImage,
            CropObject currentCrop,
            CropWindowConfig cropWinCfg,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences,
            ObjectPreferences objPreferences) {
        super(parent, frameImage, objectAttributes, actionOwner, objColorsList, userPreferences, objPreferences);

        this.currentCrop = currentCrop;

        // save the original object position in order to be able to reset the changes
        this.origObjPos = new Rectangle(currentCrop.getPositionOrig());

        initOtherVariables();

        loadWindowConfig(cropWinCfg);

        displayImage();

        setBrushOptions();

        // add pen listener to be able to use some features of the graphic tablet
        AwtPenToolkit.addPenListener(dPPreviewImg, penFunctions);

        updateMouseAndBrush();

        enableSemanticOptions(true);
        enableBoxOptions(false);

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

    private void displayObjectId() {
        // add the object id
        // set the text of the object radio button
        if (cropWindowConfig.getObjectId() > 0) {
            jRBOption2.setText("obj_" + cropWindowConfig.getObjectId());
        }

        // set the color of the text of the object radio button
        jRBOption2.setForeground(cropWindowConfig.getObjectColor());
    }

    @Override
    protected final void displayImage() {
        displayImage(currentCrop.getPositionOrig());
    }

    @Override
    protected void showImage() {
        if (updatePreview(workImg, DrawConstants.DrawType.DRAW_SCRIBBLE, objPreferences.getZoomingIndex())) {

            loadCropResult(origImg);

            // set the drawing type to avoid changing from obj to bkg when editing the object
            dPPreviewImg.setActionType(drawingType);

            // add the existing scriblles
            if (scribbleList != null) {
                dPPreviewImg.setScribbleList(scribbleList);
            } else if (dPPreviewImg.getScribbleList().size() > 0) {
                scribbleList = dPPreviewImg.getScribbleList();
            }

            // update the scribble list with the zooming factor
            dPPreviewImg.updateScribbleList();
            
            // set the color on the drawing panel
            dPPreviewImg.setObjColor(cropWindowConfig.getObjectColor());

            // add the title of the frame
            setFrameTitle();
        }
    }

    /**
     * Load the segmented result of the crop image to a visualisation panel.
     */
    private void loadCropResult(BufferedImage cropResult) {
        // remove the old image if it exists
        if (dPSemanticResultImg != null) {
            this.remove(dPSemanticResultImg);
            jPSemanticResult.remove(dPSemanticResultImg);
        }

        // load the viewer of the result
        dPSemanticResultImg = new ResultPanel(cropResult, new Dimension(cropResult.getWidth(), cropResult.getHeight()), cropWindowConfig.getObjectColor());
        dPSemanticResultImg.updateResultImg();
        dPSemanticResultImg.setBackground(new java.awt.Color(255, 255, 255));

        jPSemanticResult.add(dPSemanticResultImg);

        this.validate();
        this.repaint();

    }

    @Override
    protected void saveObject() {
        if (!isObjectAttributesSet()) {
            // the user wants to correct the data, do not close the application!
            return;
        }
        // save the configuration of the crop window for the next time it will be opened
        cropWindowConfig = new CropWindowConfig(jCBMergeBKG.isSelected(), jSCropBrushSize.getValue(), jSCropBrushDensity.getValue(), jRBOption2.getForeground(), cropWindowConfig.getObjectId());

        // notify the other form that the needed info is available
        observable.notifyObservers(actionOwner);

        closeWindow();
    }

    @Override
    protected void cancelObject() {
        // notify to track the object if the box is in edit mode
        if ((actionOwner != ObservedActions.Action.SAVE_BOUNDING_BOX)
                && (actionOwner != ObservedActions.Action.ADD_CROP_TO_OBJECT)) {
            // save the object in the ground truth
            observable.notifyObservers(ObservedActions.Action.CANCEL_CURRENT_OBJECT);
        }

        closeWindow();
    }

    @Override
    protected void deleteObject() {
        closeWindow();
        observable.notifyObservers(ObservedActions.Action.REMOVE_SELECTED_OBJECT);
    }

    @Override
    protected void moveObject(int offsetX, int offsetY) {
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
            displayImage();

            // run matting on the new image
            runMatting(Constants.RUN_MATT_ORIG_IMG);

            // refresh also the image on the GUI
            observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
        }
    }

    @Override
    protected void changeSize(int left, int top, int right, int bottom) {
        // move the object in the display window
        Rectangle newPos = currentCrop.getPositionOrig();
        newPos.setBounds(newPos.x + left, newPos.y + top, newPos.width + right - left, newPos.height + bottom - top);

        currentCrop.setPositionOrig(newPos);

        // remove the scribbles which are out of the boundaries
        removeScribbles();

        // refresh the displayed image
        displayImage();

        // run matting on the new image
        runMatting(Constants.RUN_MATT_ORIG_IMG);

        // add the title of the frame
        setFrameTitle();

        // refresh also the image on the GUI
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    @Override
    /**
     * Restore the object to the original position. The user made changes but
     * does not want to keep them.
     */
    protected void restoreObjOrigPos() {
        // reset the object coordinates
        currentCrop.setPositionOrig(new Rectangle(origObjPos));

        // remove the scribbles which are out of the boundaries
        removeScribbles();

        // refresh the displayed image
        displayImage();

        // run matting on the new image
        runMatting(Constants.RUN_MATT_ORIG_IMG);

        // add the title of the frame
        setFrameTitle();

        // refresh also the image on the GUI
        observable.notifyObservers(ObservedActions.Action.SELECTED_OBJECT_MOVED);
    }

    @Override
    protected void changeObjColor() {
        Color newColor = getNewObjColor(jRBOption2.getForeground());
        // set the color of the object
        cropWindowConfig.setObjectColor(newColor);

        // update the local viewer
        jRBOption2.setForeground(newColor);

        // update the result panel color
        dPSemanticResultImg.setObjColor(newColor);

        // set the color on the drawing panel
        dPPreviewImg.setObjColor(newColor);

        // set the object as active 
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_OBJECT);
        jRBOption2.setSelected(true);

        // update the result image with the new color
        refreshCropResult();
    }

    @Override
    protected void displayObjId() {
        // add the object id
        // set the text of the object radio button
        if (cropWindowConfig.getObjectId() > 0) {
            jRBOption2.setText("obj_" + cropWindowConfig.getObjectId());
        }

        // set the color of the text of the object radio button
        jRBOption2.setForeground(cropWindowConfig.getObjectColor());
    }

    @Override
    protected void setFrameTitle() {
        setTitle("Cropped Image  " + origImg.getWidth() + "x" + origImg.getHeight());
    }

    /**
     * Update the type of action and the preview of the brush.
     *
     * @param actionType - the type of action: -1 = erase; 0 = background; 1 =
     * object
     */
    @Override
    protected void updateObjInfo(int actionType) {
        this.drawingType = actionType;
        dPPreviewImg.setActionType(actionType);

        updateMouseAndBrush();
    }

    /**
     * When a change in the mouse brush happens, update the mouse icon and the
     * brush preview.
     */
    @Override
    protected void updateMouseAndBrush() {
        // update the brush
        setBrushOptions();
        brushPreview();

        // update the mouse icon
        dPPreviewImg.setCursor(Utils.generateCustomIcon(dPPreviewImg.getBrushOpt(), drawingType, cropWindowConfig.getObjectColor()));
    }

    /**
     * Removes the scribbles which no longer fit in the image; the ones which
     * would generate a coordinates outer box exception.
     */
    private void removeScribbles() {
        // copy the panel scribble list in the scribble list, to avoid erasing them after the new load of image
        scribbleList = new ArrayList<>();
        scribbleList.addAll(dPPreviewImg.getScribbleList());

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
     * Refresh the segmented crop image to a visualisation panel.
     */
    private void refreshCropResult() {
        if (dPSemanticResultImg != null) {
            dPSemanticResultImg.updateResultImg();
        }

        this.validate();
        this.repaint();
    }

    /**
     * Allows the user to apply the wanted options to the current drawing
     * environment.
     */
    private void setBrushOptions() {
        BrushOptions brushOpt = new BrushOptions();
        brushOpt.setBrushSize(jSCropBrushSize.getValue());
        brushOpt.setBrushDensity((float) jSCropBrushDensity.getValue() / (float) jSCropBrushDensity.getMaximum());

        dPPreviewImg.setBrushOptions(brushOpt);
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
     * Prepare the needed input for the matting application, call it and do the
     * load actions for the GUI.
     *
     * @param imageOption - on which image shall the algorithm be run original,
     * highlighted etc.
     */
    @Override
    public void runMatting(int imageOption) {
        Thread cudaMattingThread;

        if ((dPPreviewImg != null) && (jBRunMattingCrop.isEnabled())) {
            // save the scribble points
            int noScribbles = dPPreviewImg.flushPixelList();

            // prepare the thread data to run the matting application
            if (noScribbles > 0) {
                // disable the matting button, to avoid calling the thread several times
                jBRunMattingCrop.setEnabled(false);
                jBRunHighlightMatt.setEnabled(false);

                mattingThread = new MattingThreading(getMattingImage(imageOption), dPPreviewImg.getScribbleList(), ObservedActions.Action.REFRESH_CROP_RESULT);

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
     * @param scribbleList - the list of scribbles which are already available
     * for the object
     */
    public void setScribbleList(List<ScribbleInfo> scribbleList) {
        this.scribbleList = scribbleList;
        dPPreviewImg.setScribbleList(this.scribbleList);
    }

    /**
     * Get the object map of the segmentation.
     *
     * @return - the map containing the ids of the objects
     */
    public byte[][] getObjectMap() {
        return dPSemanticResultImg.getObjMap();
    }

    /**
     * Get the configuration of the crop window.
     *
     * @return - the configuration of the modifiable fields of the crop window:
     * merge BKG, brush size and density
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
        return dPSemanticResultImg.getWorkImg();
    }

    /**
     * Get the list of scribbles drawn for the segmentation of the current
     * object.
     *
     * @return - the list of scribbles used for segmentation
     */
    public List<ScribbleInfo> getScribbleList() {
        return dPPreviewImg.getScribbleList();
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
     * Returns the original image or the modified one, based on user choice.
     *
     * @return - original image or the work image
     */
    private BufferedImage getMattingImage(int imageOption) {

        switch (imageOption) {

            case Constants.RUN_MATT_HIGHLIGHT_IMG:
                //the user wants to run the matting on the highlighted image
                return Utils.histogramEqColor(dPPreviewImg.getWorkImg());

            case Constants.RUN_MATT_ORIG_IMG:
                //the user wants to run the matting on the original image
                return dPPreviewImg.getOrigImg();

            default:
                // option unknown: return the original image
                return dPPreviewImg.getOrigImg();
        }
    }

    /**
     * Filter the object map, in order to remove the outlier points.
     */
    @Override
    protected void filterObjMap() {
        if (dPSemanticResultImg == null) {
            return;
        }
        // filter the object map to remove outlier points
        dPSemanticResultImg.filterObjectMap();
        refreshCropResult();

        if (segmentResultPrev != null) {
            // refresh the image with the output of the filtering
            segmentResultPrev.setImage(dPSemanticResultImg.getWorkImg());
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
            dPSemanticResultImg.setObjMap(mattingThread.getObjMap());
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

}
