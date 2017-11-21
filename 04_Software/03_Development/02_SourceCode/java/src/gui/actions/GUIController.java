/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.actions;

import gui.viewer.AttributesDefinition;
import gui.viewer.BoundingBoxWindow;
import gui.viewer.CropWindow;
import gui.viewer.GUILabelingTool;
import common.*;
import gui.support.*;
import gui.support.Objects;
import observers.NotifyObservers;
import observers.ObservedActions;
import paintpanels.DrawConstants;
import paintpanels.DrawingPanel;
import paintpanels.ResultPanel;
import videomodule.PlayImagesRunnable;
import commonsegmentation.ScribbleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import segmentation.MattingThreading;

/**
 * The type Gui controller.
 *
 * @author Olimpia Popica
 */
public class GUIController implements Observer {

    /**
     * The panel containing the original image, where the segmentation/labeling
     * is happening.
     */
    protected DrawingPanel dPImgToLabel;

    /**
     * The panel with the result of the segmentation based on the inputed pixel
     * list.
     */
    protected ResultPanel dPImgResult;

    /**
     * A thread running the play video function.
     */
    protected Thread videoThread;

    /**
     * The list of objects which were segmented. Contains the list of crops
     * needed to segment the objects.
     */
    protected List<Objects> objectList;

    /**
     * The object being currently segmented.
     */
    private Objects currentObject;

    /**
     * The information regarding the frame currently displayed.
     */
    private final FrameInfo currentFrameInfo;

    /**
     * The crop object keeping the position of the crop in the image, the
     * scribble map and the object map.
     */
    private CropObject cropObj;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    protected final NotifyObservers observable = new NotifyObservers();

    /**
     * An instance of the window displaying the crop to be labeled.
     */
    private CropWindow cropWindow;
    private CropWindowConfig cropWindowCfg;

    /**
     * Display the segmented box and the attributes to be chosen for the object.
     */
    private BoundingBoxWindow bbWindow;

    /**
     * User preferences related to the application.
     */
    protected final UserPreferences userPrefs;

    /**
     * Encapsulates the list of object attributes: type, class, value.
     */
    protected CustomTreeNode objectAttributes;

    /**
     * Encapsulates the list of frame attributes: illumination, weather etc.
     */
    protected LAttributesFrame frameAttributes;

    /**
     * The default value of the object id before being saved and receiving an
     * object id.
     */
    protected static final int DEFAULT_OBJECT_ID = 0;

    /**
     * The default color of a new object (it is important especially for the
     * scribble mode).
     */
    private static final Color DEFAULT_OBJECT_COLOR = new Color(108, 221, 41);

    /**
     * The list of colors which were used for the segmented objects in the
     * current frame. It helps to prevent the segmentation of two objects with
     * the same color.
     */
    private List<Color> objColorsList = new ArrayList<>();

    /**
     * logger instance
     */
    private final Logger log = LoggerFactory.getLogger(GUIController.class);

    /**
     * The amount of space available on the GUi for drawing the image to be
     * labeled.
     */
    protected Dimension availableDrawSize;

    /**
     * Shows if a scribble object exists and helps at the display of the
     * interface (some fields should be displayed just if an scribble object was
     * created).
     */
    private boolean existsScribbleObj;

    /**
     * The list of files found in the directory structure of the selected folder
     * (the selected folder or the folder containing the selected file)
     */
    private List<String> fileList;

    /**
     * The name of the selected file.
     */
    private String chosenFileName;

    /**
     * The path to the chosen directory or the path to directory containing the
     * chosen file.
     */
    private String chosenPath;

    /**
     * The index of the current frame.
     */
    private long currFrameNo;

    /**
     * The image representing the current frame.
     */
    private BufferedImage currentFrame;

    /**
     * Defines how the video shall be played: 0 = forward playing; 1 = reverse
     * playing.
     */
    private int playMode;

    /**
     * True - the image shall be flipped vertically; false - the image shall be
     * as it is loaded from the file, without any processing.
     */
    private boolean flipVertically;

    /**
     * True - the image shall be mirrored; false - the image shall be as it is
     * loaded from the file, without any processing.
     */
    private boolean mirror;

    /**
     * A runnable running the play images function. The play of the images shall
     * run on a separate thread and the user has to be able to pause it from the
     * gui.
     */
    private PlayImagesRunnable imagesRunnable;

    /**
     * The data manager which is handling the writing and read to/from the data
     * files.
     */
    private final JSONDataManager jsonDataManag = new JSONDataManager();

    /**
     * Instantiates a new Gui controller.
     *
     * @param userPreferences user preferences regarding application
     * configuration
     * @param currentFrameInfo info regarding the current frame: annotations
     * regarding the scene
     */
    public GUIController(UserPreferences userPreferences, FrameInfo currentFrameInfo) {
        this.userPrefs = userPreferences;
        this.currentFrameInfo = currentFrameInfo;

        cropWindowCfg = new CropWindowConfig();
        cropWindowCfg.setMergeBKG(userPrefs.isMergeBKG());

        // init the file list to be able to add data inside
        fileList = new ArrayList<>();

        // get the object attributes from the server to be able to display them in the preview window
        objectAttributes = loadObjectAttributes();
    }

    /**
     * Load the attributes of the object from a file.
     *
     * @return the object attributes to be displayed on the interface, as an
     * array list of strings.
     */
    private CustomTreeNode loadObjectAttributes() {
        if (new File(Constants.OBJECT_ATTRIBUTES_PATH).exists()) {
            try (FileInputStream fin = new FileInputStream(Constants.OBJECT_ATTRIBUTES_PATH);
                    ObjectInputStream ois = new ObjectInputStream(fin)) {

                // read the user preferences object
                objectAttributes = (CustomTreeNode) ois.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                String msg = "Read the attributes from the file error";
                log.error(msg);
                log.debug("{} {}", msg, ex);
            }
        }
        return objectAttributes;
    }

    /**
     * Load the selected image for labeling, create and show the panel.
     *
     * @param drawType - the type of drawing used on the panel: bbox, scribble,
     * crop etc.
     */
    public void loadImageOnPanel(DrawConstants.DrawType drawType) {

        // set the name of the file to be labeled
        String fileNameToLabel = getFileToLabelName();

        // load the first frame to be displayed of the data sequence (it can have any index, depending from where the user wants to start)
        BufferedImage firstFrame = getFirstFrame();

        // set the play mode, mirror and flip options
        if (userPrefs.isPlayBackward()) {
            firstFrame = changePlayMode(getPlayMode(userPrefs.isPlayBackward()));
        }

        if (userPrefs.isFlipVertically()) {
            firstFrame = changeFlipVerticallyImage(userPrefs.isFlipVertically());
        }

        if (userPrefs.isMirrorImage()) {
            firstFrame = changeMirrorImage(userPrefs.isMirrorImage());
        }

        // compute the optimal size of the drawing panel (to fit the screen resolution)
        Dimension drawPanelRes = PanelResolution.computeOptimalPanelSize(new Dimension(firstFrame.getWidth(), firstFrame.getHeight()), availableDrawSize);

        // Load the file to be labeled in the panel and show it         
        dPImgToLabel = new DrawingPanel(firstFrame, drawPanelRes, drawType);

        // set the drawing panels characteristics
        dPImgToLabel.setBackground(new java.awt.Color(255, 255, 255));
        dPImgToLabel.setLayout(new java.awt.GridBagLayout());

        // set the user preferences for other fields (set the option regarding the display of the scribbles, alpha etc.)
        refreshUserConfigs();

        // add observer to be notified when there are changes on the panel
        dPImgToLabel.addObserver(this);

        String outputFolder = "GT" + File.separatorChar + Utils.getFileName(fileNameToLabel, false) + File.separatorChar;
        Utils.createFolderPath(outputFolder);

        log.info("Loaded the video: {}", fileNameToLabel);

        // empty the obj list and load the objects from the ground truth storage
        loadGroundTruthData();
    }

    /**
     * Get the path to the name of the file being labeled.
     *
     * @return a string representing the path to the file being labeled
     */
    public String getPathToFileName() {
        return userPrefs.getLastDirectory();
    }

    /**
     * Get the name of the file being labeled.
     *
     * @return a string representing the name of the file being labeled
     */
    public String getFileToLabelName() {
        return chosenFileName;
    }

    /**
     * Get the image representing the first frame of the data to be labeled.
     *
     * @return an image with the first frame of the data to be labeled
     */
    protected BufferedImage getFirstFrame() {
        return getChosenFrameImage();
    }

    /**
     * Create a new bounding box object and open the bounding box segmentation
     * window.
     */
    private void createNewBoundingBox() {
        // a notification saying: the mouse was released while in bounding box labeling mode

        // create a new current object of bounding box type
        initCurrentObject();

        // set the start time of the labeling of the current object
        currentObject.newLabelingDuration().setStartTime(dPImgToLabel.getMousePressedTime());

        // get the position from the drawing panel
        Rectangle curBBoxImage = dPImgToLabel.getCurBBoxImageCoords();

        currentObject.setOuterBBox(curBBoxImage);

        if ((currentObject.getOuterBBox().width > 0) && (currentObject.getOuterBBox().height > 0)) {

            bbWindow = new BoundingBoxWindow(null,
                    dPImgToLabel.getOrigImg(),
                    currentObject,
                    getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                    ObservedActions.Action.SAVE_BOUNDING_BOX,
                    objColorsList,
                    userPrefs);

            bbWindow.addObserver(this);
            bbWindow.removeOtherKeyEventDispatcher();

            bbWindow.setLocation(Utils.winLocRelativeToMouse(bbWindow.getSize()));

            bbWindow.setVisible(true);

        }
    }

    /**
     * Create a new crop based on the segmented peace of image. Open the crop
     * segmentation window and add the new crop to the scribble object, if the
     * user wants to save it.
     */
    private void createNewCrop() {
        if ((currentObject == null) || (!(currentObject instanceof ObjectScribble))) {
            return;
        }

        // a notification saying that a new crop was drawn and it has to be displayed
        cropObj = new CropObject();

        cropObj.setPositionOrig(dPImgToLabel.getCurBBoxImageCoords());

        // set the color for the scribbles of the object
        cropWindowCfg.setObjectColor(currentObject.getColor());

        // set object id for display purposes in the crop window
        cropWindowCfg.setObjectId(currentObject.getObjectId());

        if ((cropObj.getPositionOrig().getWidth() > 0) && (cropObj.getPositionOrig().getHeight() > 0)) {
            cropWindow = new CropWindow(null,
                    dPImgToLabel.getOrigImg(),
                    cropObj,
                    cropWindowCfg,
                    getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                    ObservedActions.Action.ADD_CROP_TO_OBJECT,
                    objColorsList,
                    userPrefs);

            cropWindow.addObserver(this);
            cropWindow.removeOtherKeyEventDispatcher();

            cropWindow.setLocation(Utils.winLocRelativeToMouse(cropWindow.getSize()));

            // set the obj attributes as they were selected earlier (if they were selected)
            if ((currentObject.getObjectType() != null)
                    && (currentObject.getObjectClass() != null)
                    && (currentObject.getObjectValue() != null)) {
                cropWindow.setTypeClassValOcc(currentObject.getObjectType(),
                        currentObject.getObjectClass(),
                        currentObject.getObjectValue(),
                        currentObject.getOccluded());
            }

            cropWindow.setVisible(true);
        }
    }

    /**
     * Get the selected object and open it in edit mode.
     */
    public void editObject() {
        if (dPImgToLabel != null) {
            // get the id of the crop which was clicked
            DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();

            if (selectedBox != null) {
                for (Objects obj : objectList) {
                    if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                        currentObject = obj;

                        // start counting the times for edit
                        currentObject.newLabelingDuration().start();

                        if (obj instanceof ObjectBBox) {
                            // open bounding box edit mode
                            editBox();
                            return;
                        } else if (obj instanceof ObjectScribble) {
                            Rectangle objBoxPanel = dPImgToLabel.getResize().originalToResized(obj.getOuterBBox());
                            if (objBoxPanel.equals(selectedBox.getPanelBox()) && (((ObjectScribble) obj).getCrop(selectedBox.getPanelBox(), dPImgToLabel.getResize()) == null)) {
                                // open object scribble edit mode
                                editObjectScribble();
                                return;
                            } else {
                                // crop selected - open crop edit mode
                                editCrop(selectedBox.getPanelBox());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Edit the whole object; open it in a new window.
     */
    private void editObjectScribble() {
        bbWindow = new BoundingBoxWindow(null,
                dPImgToLabel.getOrigImg(),
                currentObject,
                getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                ObservedActions.Action.UPDATE_OBJECT_SCRIBBLE,
                objColorsList,
                userPrefs);

        bbWindow.addObserver(this);
        bbWindow.removeOtherKeyEventDispatcher();

        bbWindow.setLocationRelativeTo(dPImgToLabel);

        if ((currentObject.getObjectType() != null)
                && (currentObject.getObjectClass() != null)
                && (currentObject.getObjectValue() != null)) {
            bbWindow.setTypeClassValOcc(currentObject.getObjectType(),
                    currentObject.getObjectClass(),
                    currentObject.getObjectValue(),
                    currentObject.getOccluded());
        }

        bbWindow.setVisible(true);
    }

    /**
     * Edit the selected bounding box.
     */
    private void editBox() {
        bbWindow = new BoundingBoxWindow(null,
                dPImgToLabel.getOrigImg(),
                currentObject,
                getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                ObservedActions.Action.UPDATE_BOUNDING_BOX,
                objColorsList,
                userPrefs);

        bbWindow.addObserver(this);
        bbWindow.removeOtherKeyEventDispatcher();

        bbWindow.setLocationRelativeTo(dPImgToLabel);

        bbWindow.setTypeClassValOcc(currentObject.getObjectType(),
                currentObject.getObjectClass(),
                currentObject.getObjectValue(),
                currentObject.getOccluded());

        bbWindow.setVisible(true);
    }

    /**
     * Get the selected crop and open it in a normal crop panel.
     *
     * @param coordPanelBox - the panel coordinates of the crop
     */
    private void editCrop(Rectangle coordPanelBox) {
        cropObj = ((ObjectScribble) currentObject).getCrop(coordPanelBox, dPImgToLabel.getResize());

        if (cropObj == null) {
            return;
        }

        // get scribble list
        List<ScribbleInfo> scribbleList = cropObj.getScribbleList();

        if ((cropObj.getPositionOrig().width > 0) && (cropObj.getPositionOrig().height > 0)) {
            // set object color
            cropWindowCfg.setObjectColor(currentObject.getColor());

            // set object id for display purposes in the crop window
            cropWindowCfg.setObjectId(currentObject.getObjectId());

            // create new window
            cropWindow = new CropWindow(null,
                    dPImgToLabel.getOrigImg(),
                    cropObj,
                    cropWindowCfg,
                    getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                    ObservedActions.Action.UPDATE_CROP_OF_OBJECT,
                    objColorsList,
                    userPrefs);

            cropWindow.addObserver(this);
            cropWindow.removeOtherKeyEventDispatcher();

            cropWindow.setLocationRelativeTo(dPImgToLabel);

            // set the obj attributes as they were selected earlier (if they were selected)
            if ((currentObject.getObjectType() != null)
                    && (currentObject.getObjectClass() != null)
                    && (currentObject.getObjectValue() != null)) {
                cropWindow.setTypeClassValOcc(currentObject.getObjectType(),
                        currentObject.getObjectClass(),
                        currentObject.getObjectValue(),
                        currentObject.getOccluded());
            }

            if (scribbleList != null) {
                cropWindow.setScribbleList(scribbleList);

                // run the algorithm in order to display the results asap
                cropWindow.runMatting(Constants.RUN_MATT_ORIG_IMG);
            }

            cropWindow.setVisible(true);
        }
    }

    /**
     * Add the new crop to the object as being part of it.
     */
    private void addNewCropToObject() {
        // save the config of the window, to be able to open it in the same state as it was left
        cropWindowCfg = cropWindow.getCropConfig();

        // get the scribble list and the object map
        cropObj.setScribbleList(cropWindow.getScribbleList());
        cropObj.setObjectMap(cropWindow.getObjectMap());

        // update the current object with the saved work
        if (currentObject instanceof ObjectScribble) {
            ((ObjectScribble) currentObject).addToCropList(cropObj);
        }

        // update the color of the scribbles to match the color selected in the crop window
        currentObject.setColor(cropWindow.getScribbleColor());

        // update the color of the image result panel for current color
        dPImgResult.setObjColor(cropWindow.getScribbleColor());

        // the object has a new crop, the outer bounding box can be computed
        currentObject.computeOuterBBoxCurObj();

        // get the object attributes
        currentObject.setObjectType(cropWindow.getObjType());
        currentObject.setObjectClass(cropWindow.getObjClass());
        currentObject.setObjectValue(cropWindow.getObjValue());
        currentObject.setOccluded(cropWindow.getObjOccluded());

        // merge the result and refresh the panel the result image
        dPImgResult.mergeCrop(cropWindow.getObjectMap(), cropObj.getPositionOrig(), currentObject.getObjectId(), cropWindowCfg.isMergeBKG());
        dPImgResult.updateResultImg(objectList);

        // refresh the data to be displayed        
        refreshDisplayList();
    }

    /**
     * Update one crop of the list of crops of the object
     */
    private void updateCropOfObject() {
        // save the edit time of the crop
        currentObject.getCurrentLabelingDuration().stop("crop edit");

        // save the config of the window, to be able to open it in the same state as it was left
        cropWindowCfg = cropWindow.getCropConfig();

        // get the scribble list and the object map
        cropObj.setScribbleList(cropWindow.getScribbleList());
        cropObj.setObjectMap(cropWindow.getObjectMap());

        // recompute the outer box
        currentObject.computeOuterBBoxCurObj();

        // get the object attributes
        currentObject.setObjectType(cropWindow.getObjType());
        currentObject.setObjectClass(cropWindow.getObjClass());
        currentObject.setObjectValue(cropWindow.getObjValue());
        currentObject.setOccluded(cropWindow.getObjOccluded());

        // update the object color      
        currentObject.setColor(cropWindowCfg.getObjectColor());

        // update the color of the image result panel for current color
        dPImgResult.setObjColor(cropWindow.getScribbleColor());

        // merge the result and refresh the panel the result image
        dPImgResult.mergeCrop(cropWindow.getObjectMap(), cropObj.getPositionOrig(), currentObject.getObjectId(), cropWindowCfg.isMergeBKG());
        dPImgResult.updateResultImg(objectList);

        // the color of the object might have changed, update it
        reinitObjectColorsList();

        // refresh the data to be diplayed
        refreshDisplayList();

        // notify that the object might be changed, therefore reload the objects
        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);

        // set the labeling source: human or machine
        currentObject.setSegmentationSource(ConstantsLabeling.LABEL_SOURCE_MANUAL);
    }

    /**
     * Saves the segmented bounding box in the objects list.
     */
    private void saveBoundingBoxObj() {
        // set the segmentation type used for labeling the object
        currentObject.setSegmentationType(ConstantsLabeling.LABEL_2D_BOUNDING_BOX);

        // save the object in the objects list 
        addObjToObjectList();

        // display the objects
        setBBoxToDisplay();
    }

    /**
     * Updates the selected bounding box with the data which was changed.
     */
    private void updateBBoxObj() {
        // save the edit time of the bbox
        currentObject.getCurrentLabelingDuration().stop("bounding box edit");

        // notify that the object might be changed, therefore reload the objects
        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);

        // the color of the object might have changed, update it
        reinitObjectColorsList();

        // refresh the objects displayed (for color changes)
        refreshDisplayList();
    }

    /**
     * Updates the scribble object with the new data.
     */
    private void updateObjScribble() {
        // save the edit time of the object
        currentObject.getCurrentLabelingDuration().stop("scribble object edit");

        // update the color of the image result panel for current color
        dPImgResult.setObjColor(bbWindow.getObjectColor());

        // merge the result and refresh the panel the result image
        dPImgResult.mergeCrop(((ObjectScribble) currentObject).getObjectMap(), currentObject.getOuterBBox(), currentObject.getObjectId(), false);
        dPImgResult.updateResultImg(objectList);

        // notify that the object might be changed, therefore reload the objects
        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);

        // the color of the object might have changed, update it
        reinitObjectColorsList();

        // refresh the objects displayed (for color changes)
        refreshDisplayList();
    }

    /**
     * Load the segmented result image to a visualisation panel.
     *
     * @param resultImg - the image to be displayed
     */
    public void loadImgResult(BufferedImage resultImg) {

        // load the viewer of the result
        dPImgResult = new ResultPanel(resultImg, new Dimension(Constants.RESULT_PANEL_WIDTH, Constants.RESULT_PANEL_HEIGHT), Color.red);
        dPImgResult.updateResultImg();
        dPImgResult.setBackground(new java.awt.Color(255, 255, 255));
    }

    /**
     * Move the object with the specified offsets.
     *
     * @param xOffset - the amount of pixels to move on the Ox axis
     * @param yOffset - the amount of pixels to move on the Oy axis
     */
    public void moveSelection(int xOffset, int yOffset) {
        // get the box which was selected
        DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();

        if (selectedBox != null) {
            for (Objects obj : objectList) {
                if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                    obj.move(xOffset, yOffset, selectedBox.getPanelBox(), dPImgToLabel.getResize(), dPImgToLabel.getOrigImgSize());

                    // remove the object from the object map if the obj is scribble because else it will distroy the object map
                    cancelObjectMap(obj, dPImgToLabel.getResize().resizedToOriginal(selectedBox.getPanelBox()));

                }
            }

            // refresh the data to be diplayed
            refreshDisplayList();
        }
    }

    /**
     * Move the object at the position of the mouse.
     */
    public void moveDragSelection() {
        // get the box which was selected
        DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();

        if (selectedBox != null) {
            for (Objects obj : objectList) {
                if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                    // transform the offset from panel to image coordinates
                    Point mouseOffsetPanel = dPImgToLabel.getMouseMovementOffsetPanel();
                    Point mouseOffsetImg = dPImgToLabel.getResize().resizedToOriginal(mouseOffsetPanel);

                    // move the object with the computed offset
                    obj.move(mouseOffsetImg.x, mouseOffsetImg.y, selectedBox.getPanelBox(), dPImgToLabel.getResize(), dPImgToLabel.getOrigImgSize());

                    // remove the object from the object map if the obj is scribble because else it will distroy the object map
                    cancelObjectMap(obj, dPImgToLabel.getResize().resizedToOriginal(selectedBox.getPanelBox()));
                }
            }

            // refresh the data to be diplayed
            refreshDisplayList();
        }
    }

    /**
     * Resize the selection with the specified amount of pixels on each
     * direction.
     *
     * @param left - the amount of pixels to be added on the left side
     * @param top - the amount of pixels to be added on the top part
     * @param right - the amount of pixels to be added on the right side
     * @param bottom - the amount of pixels to be added on the bottom part
     */
    public void changeSizeSelection(int left, int top, int right, int bottom) {
        // get the box which was selected
        DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();

        if (selectedBox != null) {
            for (Objects obj : objectList) {
                if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                    obj.changeSize(left, top, right, bottom, selectedBox.getPanelBox(), dPImgToLabel.getResize(), dPImgToLabel.getOrigImgSize());

                    // remove the object from the object map if the obj is scribble because else it will distroy the object map
                    cancelObjectMap(obj, dPImgToLabel.getResize().resizedToOriginal(selectedBox.getPanelBox()));
                }
            }

            // refresh the data to be diplayed
            refreshDisplayList();
        }
    }

    /**
     * Compute the outer bounding box and send the new list of scribbles and
     * bounding boxes to the display module.
     */
    public void refreshDisplayList() {
        // send the crop and the scribbles to be drawn on the drawing panel
        setBBoxToDisplay();
        setScribblesToDisplay();
        setPolygonToDisplay();

        // notify to enable some options, based on the existing list of objects
        observable.notifyObservers(ObservedActions.Action.REFRESH_PANEL_OPTIONS);
    }

    /**
     * Do not select any crop / Remove selection.
     */
    public void resetSelectedObject() {
        dPImgToLabel.resetIdSelectedBox();
    }

    /**
     * Remove the selection from the list.
     */
    public void removeSelection() {
        // get the box which was selected
        DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();
        if (selectedBox != null) {

            for (Iterator<Objects> it = objectList.iterator(); it.hasNext();) {
                Objects obj = it.next();
                if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {

                    //remove a box from the object
                    if (obj.remove(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                        obj.computeOuterBBoxCurObj();

                        // remove the crop from the object map if the obj is scribble
                        cancelObjectMap(obj, dPImgToLabel.getResize().resizedToOriginal(selectedBox.getPanelBox()));

                    } else {
                        // remove the object from the object map if the obj is scribble
                        cancelObjectMap(obj, obj.getOuterBBox());

                        // remove the object from the object list
                        it.remove();

                        // update object panel
                        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);
                    }
                }
            }

            // disable the selection and erase the current object
            resetSelectedObject();
            eraseCurrentObject();

            // refresh the data to be diplayed
            refreshDisplayList();
        }
    }

    /**
     * Cancel the creation of a new object.
     *
     * @return true if the object was cancel; false if it was not cancel due to
     * another window active
     */
    public boolean cancelCurrentObject() {
        if (cropWindow == null || !cropWindow.isActive()) {

            eraseCurrentObject();

            // send the crop and the scribbles to be drawn on the drawing panel
            setBBoxToDisplay();
            setScribblesToDisplay();
            setPolygonToDisplay();
            return true;
        }
        return false;
    }

    /**
     * Make the current object null.
     */
    public void eraseCurrentObject() {
        currentObject = null;
    }

    /**
     * Set the option of displaying; refresh the configuration.
     */
    public void refreshUserConfigs() {
        dPImgToLabel.setDrawScribbleHistory(userPrefs.isShowScribbles());
        dPImgToLabel.setDrawAlphaObj(userPrefs.isShowObjHighlight());
        dPImgToLabel.setObjAlpha(userPrefs.getObjAlphaVal());
        currentFrameInfo.setSaveFrameObjMap(userPrefs.isSaveFrameObjMap());
    }

    /**
     * Create a new thread which plays a video and start it.
     *
     * @param gui - the gui, which has to be notified when changes happen
     */
    public void playVideo(GUILabelingTool gui) {
        imagesRunnable = new PlayImagesRunnable(this);

        imagesRunnable.addObserver(gui);  // the event has to be reported in the gui not in the controller

        videoThread = new Thread(imagesRunnable);
        videoThread.setName("Play Images Thread");
        videoThread.start();
    }

    /**
     * Play or pause the thread, depending on the current state. Invert the
     * current state.
     */
    public void pausePlayVideo() {
        if (imagesRunnable != null) {
            // if the thread exists and is playing, pause it
            imagesRunnable.setRun(!imagesRunnable.isRun());
        }
    }

    /**
     * Save all the information in the ground truth storage.
     */
    public void saveDataAsGroundTruth() {
        jsonDataManag.initWriteFile(getGTFilePath());

        // send the objects to be saved, one by one
        for (Objects obj : objectList) {
            jsonDataManag.addObject(obj);
        }
        jsonDataManag.addFrame(getCurrentFrameInfo(), dPImgResult.getObjMap());

        jsonDataManag.writeFile();
    }

    /**
     * Save all the information in the communication object
     *
     * @param obj Pixie's current object information
     */
    public void sendLabel(Objects obj) {
        // assign an id to the object when the id is not already set
        if (obj.getObjectId() == DEFAULT_OBJECT_ID) {
            long maxId = 0L;

            // the new id has to be greater than the existing max id
            for (Objects object : objectList) {
                if (maxId < object.getObjectId()) {
                    maxId = object.getObjectId();
                }
            }
            obj.setObjectId(maxId + 1L);
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

                case OPEN_CROP_SEGMENTATION:
                    // create a new crop and add it to the object
                    createNewCrop();
                    break;

                case OPEN_BBOX_SEGMENTATION:
                    // create a new object of type bounding box
                    createNewBoundingBox();
                    break;

                case CTRL_MOUSE_EVENT:
                    ctrlMouseEdit();
                    break;

                case ADD_CROP_TO_OBJECT:
                    addNewCropToObject();
                    break;
                case UPDATE_CROP_OF_OBJECT:
                    updateCropOfObject();

                    // send the object to server
                    sendLabel(currentObject);
                    break;

                case EDIT_OBJECT_ACTION:
                    editObject();
                    break;

                case SAVE_BOUNDING_BOX:
                    saveBoundingBoxObj();
                    break;

                case UPDATE_BOUNDING_BOX:
                    updateBBoxObj();

                    // send the object to server
                    sendLabel(currentObject);
                    break;

                case UPDATE_OBJECT_SCRIBBLE:
                    updateObjScribble();

                    // send the object to server
                    sendLabel(currentObject);
                    break;

                case REMOVE_SELECTED_OBJECT:
                    removeSelection();
                    break;

                case SELECTED_OBJECT_MOVED:
                    refreshDisplayList();
                    break;

                case MOVE_OBJECT_DRAG:
                    moveDragSelection();
                    break;

                case SAVE_LABEL:
                    // send the object to server
                    sendLabel(currentObject);
                    break;

                // redirect the notification which cannot be treated in the controller
                case ADD_GUI_KEY_EVENT_DISPATCHER:
                    // the crop/bbox window was closed and the preview shape and size have to be removed
                    dPImgToLabel.setShowGuideShape(false);
                case REMOVE_GUI_KEY_EVENT_DISPATCHER:
                case HIGHLIGHT_OBJECT:
                case OPEN_POLYGON_SEGMENTATION:
                case DISPLAY_EDIT_ATTRIBUTES_WIN:
                    observable.notifyObservers(type);
                    break;

                case CANCEL_CURRENT_OBJECT:
                    if ((dPImgToLabel.getDrawType() != DrawConstants.DrawType.EDIT_MODE)) {
                        cancelCurrentObject();
                    }

                    // refresh the display list because some things might have changed
                    refreshDisplayList();
                    break;

                case RELOAD_ATTRIBUTES:
                    reloadAttributes();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Implement and edit mode based on the control key and mouse movement.
     *
     * Scribble: add a new crop to the selected object.
     *
     * Bounding box: replace existing box with the new one.
     */
    private void ctrlMouseEdit() {
        if (currentObject instanceof ObjectScribble) {
            // if the selected object is a scribble, add the new crop to the object
            createNewCrop();
        } else if (currentObject instanceof ObjectBBox) {
            // if the selected object is a bounding box, replace the current object with the new drawn object
            // get the position from the drawing panel
            Rectangle curBBoxImage = dPImgToLabel.getCurBBoxImageCoords();
            currentObject.setOuterBBox(curBBoxImage);

            // open the edit window and allow the user to refine the position
            editBox();
        }
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
     * Set the type of label which will be used for the frame.
     *
     * @param drawType - the type of drawing which will be used for labeling
     */
    public void setLabelingType(DrawConstants.DrawType drawType) {
        dPImgToLabel.setDrawType(drawType);
    }

    /**
     * Switch to the previous frame.
     */
    public void prevFrame() {
        // display the previous frame
        dPImgToLabel.newFrame(getPrevFrame(), availableDrawSize);

        // empty the obj list and load the objects from the ground truth
        loadGroundTruthData();
    }

    /**
     * Move to the next frame. Track objects
     */
    public void nextFrame() {
        BufferedImage prevFrame = new BufferedImage(dPImgToLabel.getOrigImg().getWidth(), dPImgToLabel.getOrigImg().getHeight(), dPImgToLabel.getOrigImg().getType());

        // copy current frame as prev frame
        Utils.copySrcIntoDstAt(dPImgToLabel.getOrigImg(), prevFrame);

        // display and move to the next frame
        dPImgToLabel.newFrame(getNextFrame(), availableDrawSize);

        // load the saved objects if there are some
        loadGroundTruth();

        /* update the gui with the new object coordinates */
        refreshDisplayList();
    }

    /**
     * Jump to the specified frame.
     *
     * @param frameNo - the frame number where it should jump
     */
    public void jumpToFrame(long frameNo) {
        if (isNotEndOfDataFile(frameNo)) {
            dPImgToLabel.newFrame(getJumpToFrame(frameNo), availableDrawSize);

            // empty the obj list and load the objects from the ground truth
            loadGroundTruthData();
        }
    }

    /**
     * Returns the image from the specified frame number without moving the
     * video pointer in the file. It is extracting a frame without affecting the
     * normal functionality of the frame grabber.
     *
     * @param frameNr the frame which shall retrieved
     * @return the image representing the specified frame number, without moving
     * the video pointer in the frame grabber
     */
    public BufferedImage getFrame(long frameNr) {
        // return the current frame as it is
        return currentFrame;
    }

    /**
     * The retrieving of the previous frame is depending on the application
     * mode, therefore it will be implemented differently for different
     * configuration.
     *
     * @return the image representing the previous frame to be displayed;
     * modifies the video pointer
     */
    protected BufferedImage getPrevFrame() {
        return getJumpToFrame(currFrameNo - 1);
    }

    /**
     * The retrieving of the next frame is depending on the application mode,
     * therefore it will be implemented differently for different configuration.
     *
     * @return the image representing the next frame to be displayed; modifies
     * the video pointer
     */
    public BufferedImage getNextFrame() {
        return getJumpToFrame(currFrameNo + 1);
    }

    /**
     * The retrieving of the jumpTo frame is depending on the application mode,
     * therefore it will be implemented differently for different configuration.
     *
     * @param jumpToNo the number of the frame to be jumping to
     * @return the image representing the frame with the specified number, from
     * the current video; modifies the video pointer
     */
    protected BufferedImage getJumpToFrame(long jumpToNo) {
        long frameNo = jumpToNo;

        if (playMode == Constants.PLAY_MODE_BACKWARD) {
            // compute the index of the frame
            frameNo = fileList.size() - jumpToNo;

            // make sure the index is in the wanted range (+1 is added due to the fact that the first frame is 1, not 0)
            if (isNotEndOfDataFile(frameNo + 1)) {
                // set the name of the chosen file and the number of the current frame
                chosenFileName = fileList.get((int) frameNo);
                currFrameNo = fileList.size() - frameNo;

                // load the wanted frame
                return getChosenFrameImage();
            } else {
                // the index is out of bounds, return the current frame as it is
                return currentFrame;
            }
        }

        // make sure the jump is done inside the file
        if (isNotEndOfDataFile(frameNo)) {
            // set the number of the current frame and the name of the chosen file
            currFrameNo = frameNo;
            chosenFileName = fileList.get((int) currFrameNo - 1);

            // load the wanted frame
            return getChosenFrameImage();
        } else {
            // the index is out of bounds, return the current frame as it is
            return currentFrame;
        }
    }

    /**
     * Checks input frame number is out of the data file.
     *
     * @param frameNo the frame number for which the end of file is checked
     * @return the boolean state for end of data file
     * <p>
     * true = end of data file
     * <p>
     * false = not end of data file
     * <p>
     * where data file can be a video, a folder of pictures etc.
     */
    public boolean isNotEndOfDataFile(long frameNo) {
        return ((frameNo > 0) && (frameNo <= fileList.size()));
    }

    /**
     * Return the number of frames of the video.
     *
     * @return - the total number of frames of the video
     */
    public long getNoFrames() {
        return fileList.size();
    }

    /**
     * Gets frame rate.
     *
     * @return the video's frame rate
     */
    public long getFrameRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Return the frame number.
     *
     * @return - the current frame number of the video
     */
    public long getFrameNo() {
        return currFrameNo;
    }

    /**
     * Return the play mode wanted by the user: forward or backward.
     *
     * @return the play mode: forward or backward
     */
    public int getPlayMode() {
        return playMode;
    }

    /**
     * Return the play mode wanted by the user: forward or backward.
     *
     * @param playBackward true if the player shall play backwards and false
     * otherwise
     *
     * @return the play mode: forward or backward
     */
    private int getPlayMode(boolean playBackward) {
        return (playBackward ? Constants.PLAY_MODE_BACKWARD : Constants.PLAY_MODE_FORWARD);
    }

    /**
     * Return the segmented image; the result of the segmentation.
     *
     * @return an image representing the result of the labeling
     */
    public BufferedImage getImageResult() {
        if (dPImgResult != null) {
            return dPImgResult.getWorkImg();
        }
        return null;
    }

    /**
     * Gets p img to label.
     *
     * @return the p img to label
     */
    public DrawingPanel getdPImgToLabel() {
        return dPImgToLabel;
    }

    /**
     * Gets p img result.
     *
     * @return the p img result
     */
    public ResultPanel getdPImgResult() {
        return dPImgResult;
    }

    /**
     * Init current object.
     */
    public void initCurrentObject() {
        if (null == dPImgToLabel.getDrawType()) {
            return;
        }

        switch (dPImgToLabel.getDrawType()) {
            case DRAW_CROP:
                this.currentObject = new ObjectScribble();
                break;

            case DRAW_BOUNDING_BOX:
                this.currentObject = new ObjectBBox();
                break;

            case DRAW_POLYGON:
                this.currentObject = new ObjectPolygon();
                break;

            default:
                break;
        }

        // assign a new object id
        this.currentObject.setObjectId(DEFAULT_OBJECT_ID);

        // assign a new color to the object
        this.currentObject.setColor(DEFAULT_OBJECT_COLOR);
    }

    /**
     * Start current object timer.
     */
    public void startCurrentObjectTimer() {
        // init the needed time for the segmentation of the object
        currentObject.newLabelingDuration().start();
    }

    /**
     * Sets the label type used for the segmentation of the current object.
     *
     * @param labelType - the type of label used for the segmentation of the
     * object: 2D/3D bounding box, scribbles
     */
    public void setSegmentationType(String labelType) {
        currentObject.setSegmentationType(labelType);
    }

    /**
     * Sets the label source used for the segmentation of the current object.
     *
     * @param labelSource - the source of the label used for the segmentation of
     * the object: manual, automatic etc.
     */
    public void setSegmentationSource(String labelSource) {
        currentObject.setSegmentationSource(labelSource);
    }

    /**
     * Add the current object to the list of objects.
     */
    public void addObjToObjectList() {
        // stop the timer of the current object; the segmentation is finished
        // init the needed time for the segmentation of the object
        currentObject.getCurrentLabelingDuration().stop("object segmentation");

        // check the object; make sure it is not empty
        if (currentObject.getOuterBBox() != null) {

            // send the object to server
            sendLabel(currentObject);

            // update the object id in the object map
            updateDefaultObjIdMap();

            Color currentColor = (currentObject.getColor() == DEFAULT_OBJECT_COLOR) ? Utils.getColorOfObjByID(currentObject.getObjectId()) : currentObject.getColor();

            // assign a new color to the object
            this.currentObject.setColor(currentColor);

            // set the color for the scribbles of the object
            cropWindowCfg.setObjectColor(currentColor);

            // update the color of the image result panel for current color
            dPImgResult.setObjColor(currentColor);

            /* update the drawing panel*/
            refreshDisplayList();

            // the object seems correct
            objectList.add(currentObject);

            // notify that a new object was added on the list and it has to be displayed in the list
            observable.notifyObservers(ObservedActions.Action.ADD_OBJECT_ON_PANEL);

            // add the used color to the list of used colors
            objColorsList.add(currentObject.getColor());

            // erase the object which was saved
            eraseCurrentObject();
        }
    }

    /**
     * Returns the current object; the one on which the user is working on.
     *
     * @return - the current object of the application
     */
    public Objects getCurrentObject() {
        return currentObject;
    }

    /**
     * Return the last saved object.
     *
     * @return - the last saved object
     */
    public Objects getLastSavedObj() {
        return objectList.get(objectList.size() - 1);
    }

    /**
     * Return the list of objects from the saved object list.
     *
     * @return - the list of objects available in the objects list
     */
    public List<Objects> getObjectsList() {
        return objectList;
    }

    /**
     * Returns the number of objects saved in the objects list.
     *
     * @return - the size of the object list
     */
    public int getObjectsListSize() {
        if (objectList != null) {
            return objectList.size();
        }
        return 0;   // the list was not even initialized yet
    }

    /**
     * Reinit the list of used colors, to make sure each object has its own
     * color.
     */
    public void reinitObjectColorsList() {
        objColorsList = new ArrayList<>();

        for (Objects objects : objectList) {
            objColorsList.add(objects.getColor());
        }
    }

    /**
     * Get the list of bounding boxes and the list of scribbles from the objects
     * and display it on the drawing panel.
     */
    public void setBBoxToDisplay() {
        List<DisplayBBox> positions = new ArrayList<>();

        // for scribble segmentation the current object parts have to be displayed before the save of the object
        if (currentObject != null) {
            // add the current object which is in progress
            showCrops(currentObject, positions);

            // add the outer bounding box containing all the objects
            if (currentObject.getOuterBBox() != null) {
                positions.add(new DisplayBBox(dPImgToLabel.getResize().originalToResized(currentObject.getOuterBBox()),
                        currentObject.getColor(), Long.toString(currentObject.getObjectId()), false));
            }
        }

        // if the user just wants to see the current object, display it and return
        if (userPrefs.isShowJustCurrentObj()) {
            dPImgToLabel.setBBoxList(positions);
            return;
        }

        // the user wants to see all the objects
        // add the saved objects
        for (Objects obj : objectList) {
            // TODO TEMP CODE - to be deleted later
            showPredictions(obj, positions);

            // display the crops
            showCrops(obj, positions);

            // add the outer bounding box containing all the objects
            positions.add(new DisplayBBox(dPImgToLabel.getResize().originalToResized(obj.getOuterBBox()),
                    obj.getColor(), Long.toString(obj.getObjectId()), false));

        }

        dPImgToLabel.setBBoxList(positions);
    }

    /**
     * Show the crops of the scribble object.
     *
     * @param obj the object for which the crops shall be displayed (if the
     * object is an instance of the scribble object)
     * @param positions the list of boxes to be displayed on the screen
     */
    private void showCrops(Objects obj, List<DisplayBBox> positions) {
        if ((obj instanceof ObjectScribble) && (userPrefs.isShowCrops())) {
            for (CropObject cropObj : ((ObjectScribble) obj).getCropList()) {
                // add bounding boxes
                Rectangle cropPosPanel = dPImgToLabel.getResize().originalToResized(cropObj.getPositionOrig());
                positions.add(new DisplayBBox(cropPosPanel, obj.getColor(), true));
            }
        }
    }

    /**
     * Show the predictions for the given object.
     *
     * @param obj the object for which the predictions shall be displayed
     * @param positions the list of boxes to be displayed on the screen
     */
    private void showPredictions(Objects obj, List<DisplayBBox> positions) {

        // add the predicted HOG box
        if (obj.getHogPredictedBBox() != null) {
            positions.add(new DisplayBBox(dPImgToLabel.getResize().originalToResized(obj.getHogPredictedBBox()),
                    obj.getColor().darker().darker(), Long.toString(obj.getObjectId()), true));
        }

        // add the predicted SVM with HOG box
        if (obj.getSvmPredictedBBox() != null) {
            positions.add(new DisplayBBox(dPImgToLabel.getResize().originalToResized(obj.getSvmPredictedBBox()),
                    Color.cyan.darker(), Long.toString(obj.getObjectId()), true));
        }
    }

    /**
     * Select the object with the given id and return its type.
     *
     * @param objId - the id of the selected object
     * @return - the type of object which was selected
     */
    public String selectObject(long objId) {
        for (Objects obj : objectList) {
            if (obj.getObjectId() == objId) {
                // set the selected object as the current object
                currentObject = obj;

                // refresh the display to not loose the show current obj functionality
                refreshDisplayList();

                // select the object
                dPImgToLabel.setSelectedBox(obj.getOuterBBox());

                // return the object type
                if (obj instanceof ObjectScribble) {
                    return ConstantsLabeling.LABEL_SCRIBBLE;
                } else if (obj instanceof ObjectBBox) {
                    return ConstantsLabeling.LABEL_2D_BOUNDING_BOX;
                } else if (obj instanceof ObjectPolygon) {
                    return ConstantsLabeling.LABEL_POLYGON;
                }
            }
        }
        return "";
    }

    /**
     * Get the list of bounding boxes and the list of scribbles from the objects
     * and display it on the drawing panel.
     */
    public void setScribblesToDisplay() {
        List<DisplayScribbles> cropScribbles = new ArrayList<>();
        existsScribbleObj = false;

        if ((currentObject != null) && (currentObject instanceof ObjectScribble)) {
            // mark the fact that a scribble object was found
            existsScribbleObj = true;

            // add the scribbles of the current object which is in progress
            for (CropObject cropObject : ((ObjectScribble) currentObject).getCropList()) {
                // add scribbles from each crop available
                Rectangle cropPosPanel = dPImgToLabel.getResize().originalToResized(cropObject.getPositionOrig());

                cropScribbles.addAll(cropObject.getDisplayScribbleList(cropPosPanel, currentObject.getColor(), dPImgToLabel.getResize()));
            }
        }

        // if the user just wants to see the current object, display it and return
        if (userPrefs.isShowJustCurrentObj()) {
            dPImgToLabel.addScriblesToDisplay(cropScribbles);
            return;
        }

        // the user wants to see all the objects
        // add the saved objects
        for (Objects obj : objectList) {
            if (!(obj instanceof ObjectScribble)) {
                // if the object is not scribble, jump over it
                continue;
            }

            if (((ObjectScribble) obj).getCropList() == null) {
                continue;
            }

            // mark the fact that a scribble object was found
            existsScribbleObj = true;

            for (CropObject cropObject : ((ObjectScribble) obj).getCropList()) {
                // add scribbles from each crop available
                Rectangle cropPosPanel = dPImgToLabel.getResize().originalToResized(cropObject.getPositionOrig());

                cropScribbles.addAll(cropObject.getDisplayScribbleList(cropPosPanel, obj.getColor(), dPImgToLabel.getResize()));
            }

        }

        dPImgToLabel.addScriblesToDisplay(cropScribbles);
    }

    /**
     * Build and display the list of polygons, in panel coordinates, from the
     * saved objects.
     */
    public void setPolygonToDisplay() {
        List<DisplayPolygon> displayPoly = new ArrayList<>();

        if (!userPrefs.isShowJustCurrentObj()) {
            // add the saved objects
            for (Objects obj : objectList) {
                if (obj instanceof ObjectPolygon) {
                    Polygon polygonPanel = dPImgToLabel.getResize().originalToResized(((ObjectPolygon) obj).getPolygon());

                    DisplayPolygon poly = new DisplayPolygon(obj.getColor(), polygonPanel, false);

                    displayPoly.add(poly);
                }
            }
        }

        if ((currentObject != null) && (currentObject instanceof ObjectPolygon)) {
            Polygon polygon = ((ObjectPolygon) currentObject).getPolygon();
            if (polygon != null) {
                Polygon polygonPanel = dPImgToLabel.getResize().originalToResized(polygon);

                DisplayPolygon poly = new DisplayPolygon(currentObject.getColor(), polygonPanel, false);

                displayPoly.add(poly);
            }
        }

        dPImgToLabel.setPolygonDisplayList(displayPoly);
    }

    /**
     * Reset the position of the places where the user executed a click
     * press-release action. Used to reset the bounding boxes and crops from the
     * gui.
     */
    public void resetBBox() {
        dPImgToLabel.resetMousePosition();
    }

    /**
     * Set the label source for all the objects as specified by the sent
     * parameter.
     *
     * @param labelSource - the type of segmentation used for the object:
     * manual, automatic etc.
     */
    public void setAllObjsLabelSource(String labelSource) {
        objectList.stream().forEach((obj) -> {
            obj.setSegmentationSource(labelSource);
        });
    }

    /**
     * Reset the labeling duration for all the objects.
     */
    public void resetAllObjsDuration() {
        objectList.stream().forEach((obj) -> {
            obj.setLabelingDuration(new ArrayList<>());
        });
    }

    /**
     * Get the selected object.
     *
     * @return - the id of the selected object
     */
    public Objects getSelectedObject() {
        // get the box which was selected
        DisplayBBox selectedBox = dPImgToLabel.getSelectedBox();

        if (selectedBox != null) {
            for (Objects obj : objectList) {
                if (obj.contains(selectedBox.getPanelBox(), dPImgToLabel.getResize())) {
                    // set the selected object as the current object
                    currentObject = obj;

                    return obj;
                }
            }
        }
        return null;
    }

    /**
     * Check the object list and see with which labeling type the object was
     * segmented. Depending on the labeling type, return a color.
     *
     * @param objId - the id of the object
     * @return - the color of the labeling type
     */
    public Color getLabelTypeColor(long objId) {
        for (Objects obj : objectList) {
            if (obj.getObjectId() == objId) {
                return getLabelTypeColor(obj);
            }
        }
        return Color.red;
    }

    /**
     * Check the object type and see with which labeling type the object was
     * segmented. Depending on the labeling type, return a color.
     *
     * @param obj - the object for which the color is searched
     * @return - the color of the labeling type
     */
    public Color getLabelTypeColor(Objects obj) {
        if (obj instanceof ObjectBBox) {
            return new Color(179, 255, 255);
        } else if (obj instanceof ObjectScribble) {
            return new Color(179, 255, 179);
        } else if (obj instanceof ObjectPolygon) {
            return new Color(179, 179, 255);
        }

        return Color.red;
    }

    /**
     * Terminate the running threads.
     */
    public void terminateThreads() {
        //terminate the video thread
        if (videoThread != null) {
            videoThread.interrupt();
        }
    }

    /**
     * Sets the play mode and updates the displayed image, according to the new
     * play mode.
     *
     * @param playMode set the video play mode(backward/forward)
     */
    public void setPlayMode(int playMode) {
        dPImgToLabel.newFrame(changePlayMode(playMode), availableDrawSize);
    }

    /**
     * Changes the play mode and get the new image, from the corresponding
     * location in the video, considering the new play mode.
     *
     * @param playMode set the video play mode(backward/forward)
     * @return the new frame, based on the new play mode
     */
    protected BufferedImage changePlayMode(int playMode) {
        this.playMode = playMode;

        long frame = Utils.limit(1L, currFrameNo, getNoFrames());

        return getJumpToFrame(frame);
    }

    /**
     * Checks if the data was loaded. It is considered that the data is loaded
     * if the frame grabber is not null, if a folder with files was loaded etc.
     *
     * @return - true if the data was loaded and false otherwise
     */
    public boolean isDataLoaded() {
        return (currentFrame != null);
    }

    /**
     * Loads the saved objects for the current frame (if there were saved).
     */
    protected void loadGroundTruth() {
        // reinit the list of objects read from the saved ground truth
        objectList = new ArrayList<>();

        // read the ground truth from the saved file and load the list of objects and the frame attributes
        jsonDataManag.readFile(getGTFilePath(), getCurrentFrameInfo(), objectList);

        // notify the gui to change the annotations
        observable.notifyObservers(ObservedActions.Action.LOAD_FRAME_ANNOTATION);

        // notify that a new object was added on the list and it has to be displayed in the list
        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);
    }

    /**
     * Returns the current frame object, containing the current frame
     * information.
     *
     * @return - the current frame information
     */
    public FrameInfo getCurrentFrameInfo() {
        return currentFrameInfo;
    }

    /**
     * Checks if the object exists in the objects list.
     *
     * @param obj - the object to check if it exists in the list
     * @return - true if the object exists and false otherwise
     */
    protected boolean isObjInObjList(Objects obj) {
        return objectList.stream().anyMatch((object) -> (object.getObjectId() == obj.getObjectId()));
    }

    /**
     * Load a new list of objects from the ground truth storage. Erase the
     * current list of objects and put just the loaded ones.
     */
    private void loadGroundTruthData() {
        // remove all the objects from the object list
        removeAllObjects();

        // load the saved objects if there are some
        loadGroundTruth();

        /* update the gui with the new object coordinates */
        refreshDisplayList();
    }

    /**
     * Cancel object map.
     *
     * @param object the object
     * @param area the area
     */
    public void cancelObjectMap(Objects object, Rectangle area) {
        // update the result image if it exists
        if ((dPImgResult != null) && (object instanceof ObjectScribble)) {

            // remove the object id from the object map
            dPImgResult.removeObj(object.getObjectId(), area);

            // update the output image result with the new map
            dPImgResult.updateResultImg(objectList);
        }
    }

    /**
     * Pop up the objects for review if the user chose to check them.
     */
    public void popUpObjects() {
        if (userPrefs.isPopUpObjects()) {
            if ((objectList != null) && (objectList.size() > 0)) {

                // MAT-281: make a shallow copy of the array list; else when an object is erased, there will be ConcurrentModificationException
                List<Objects> tempObjList = new ArrayList<>(objectList);

                for (Objects obj : tempObjList) {
                    // set the object as the current one
                    currentObject = obj;

                    // select the object in the image
                    dPImgToLabel.setSelectedBox(currentObject.getOuterBBox());

                    // highlight the object in the side object list
                    observable.notifyObservers(ObservedActions.Action.HIGHLIGHT_OBJECT);

                    // refresh the display to mark the selected box
                    refreshDisplayList();

                    // start counting the times for edit
                    currentObject.newLabelingDuration().start();

                    if (obj instanceof ObjectBBox) {
                        // open bounding box edit mode
                        editBox();

                    } else if (obj instanceof ObjectScribble) {
                        // open the entire object
                        editObjectScribble();
                    }
                }

                // update the result image with all the merged objects
                dPImgResult.updateResultImg(objectList);

                // cancel the current object to avoid issues in displaying the objects ???
                cancelCurrentObject();
            }
        }
    }

    /**
     * Enables the user to draw one time a crop, which will be added to the
     * current object.
     */
    public void setAddCropToObj() {
        // enable only if the current object is scribble
        if (currentObject instanceof ObjectScribble) {
            dPImgToLabel.setAddCropToObj(true);
        }
    }

    /**
     * Get the information from the crops and run the matting algorithm. Compute
     * the outer box and update the result panel
     *
     * @param obj - the object for which the matting has to run
     */
    private void runMattingAllCrops(Objects obj) {
        // run the matting algorithm for all the crops              
        ((ObjectScribble) obj).getCropList().forEach((crop) -> {
            // get the cropped image
            BufferedImage bi = Utils.getSelectedImg(dPImgToLabel.getOrigImg(), crop.getPositionOrig());

            // instantiate the matting class
            MattingThreading matt = new MattingThreading(bi, crop.getScribbleList(), ObservedActions.Action.DO_NOTHING);

            // run the matting algorithm and save its output - the object map
            crop.setObjectMap(matt.runMattingAlgo());

            // merge the object map into the result map
            dPImgResult.mergeCrop(crop.getObjectMap(), crop.getPositionOrig(), obj.getObjectId(), false);
        });

        obj.computeOuterBBoxCurObj();
    }

    /**
     * Run the matting algorithm for all the scribble objects, crop by crop, and
     * build the object map, its outer box and result image.
     */
    public void runMattingForObjList() {
        objectList.stream().filter((obj) -> (obj instanceof ObjectScribble)).forEachOrdered(this::runMattingAllCrops);

        /* Original code:
        for (Objects obj : objectList) {
            if (obj instanceof ObjectScribble) {
                runMattingAllCrops(obj);
            }
        }
         */
        // update the result image with all the merged objects
        dPImgResult.updateResultImg(objectList);

        // refresh the display list because the object might have changed
        refreshDisplayList();
    }

    /**
     * Update the result image with all the segmented objects.
     */
    public void updateResultPanel() {
        // update the result image with all the merged objects
        dPImgResult.updateResultImg(objectList);
    }

    /**
     * Enables the drawing of the guide lines and the size of the drawn box.
     *
     * @param show - true if the guide lines shall be visible and false
     * otherwise
     */
    public void setShowGuideShape(boolean show) {
        dPImgToLabel.setShowGuideShape(show);
    }

    /**
     * Initialise the polygon to be displayed on the screen.
     */
    public void initPolygon() {
        dPImgToLabel.initCurrentPolygon();
    }

    /**
     * Reset the current drawn polygon.
     */
    public void resetPolygon() {
        dPImgToLabel.resetCurrentPolygon();
    }

    /**
     * Open the polygon object preview window.
     */
    public void openPolyPrevWin() {
        java.awt.Polygon polygon = dPImgToLabel.getCurrentPolygonImg();

        if (polygon == null) {
            cancelCurrentObject();
            return;
        }

        if (polygon.npoints > 0) {
            // put the polygon in the object
            ((ObjectPolygon) currentObject).setPolygon(polygon);

            // compute the object outerbox
            currentObject.computeOuterBBoxCurObj();

            bbWindow = new BoundingBoxWindow(null,
                    dPImgToLabel.getOrigImg(),
                    currentObject,
                    getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()),
                    ObservedActions.Action.OPEN_POLYGON_SEGMENTATION,
                    objColorsList,
                    userPrefs);

            bbWindow.addObserver(this);

            bbWindow.removeOtherKeyEventDispatcher();

            bbWindow.setLocation(Utils.winLocRelativeToMouse(bbWindow.getSize()));

            bbWindow.setVisible(true);
        }
    }

    /**
     * Set the flip option for the frame grabber. Activate/Deactivate the flip
     * vertically mode. Activating the flip vertically mode, the video component
     * will return a flipped frame
     *
     * @param selected activates/deactivates the flip vertically
     * <p>
     * true - activate the flip
     * <p>
     * false - deactivate the flip
     */
    public void setFlipVerticallyImage(boolean selected) {
        dPImgToLabel.newFrame(changeFlipVerticallyImage(selected), availableDrawSize);

        // refresh current image
        dPImgToLabel.reloadWorkImg();

        // refresh the result image
        dPImgResult.refreshImage(dPImgToLabel.getOrigImg());
    }

    /**
     * Set the mirror option for the frame grabber. Activate/Deactivate the
     * mirroring mode. Activating/Deactivating the mirroring mode, the video
     * component will return a mirrored frame.
     *
     * @param selected activates/deactivates the mirroring
     * <p>
     * true - activate the mirroring
     * <p>
     * false - deactivate the mirroring
     */
    public void setMirrorImage(boolean selected) {
        dPImgToLabel.newFrame(changeMirrorImage(selected), availableDrawSize);

        // refresh current image
        dPImgToLabel.reloadWorkImg();

        // refresh the result image
        dPImgResult.refreshImage(dPImgToLabel.getOrigImg());
    }

    /**
     * Activate/Deactivate the flip vertically mode. Activating the flip
     * vertically mode, the data component will return a flipped frame.
     *
     * @param selected activates/deactivates the flip vertically
     * <p>
     * true - activate the flip
     * <p>
     * false - deactivate the flip
     * @return the current image, flipped vertically
     */
    protected BufferedImage changeFlipVerticallyImage(boolean selected) {
        this.flipVertically = selected;

        Utils.flipVerticallyImage(currentFrame);

        return currentFrame;
    }

    /**
     * Activate/Deactivate the mirroring mode. Activating/Deactivating the
     * mirroring mode, the data component will return a mirrored frame.
     *
     * @param selected activates/deactivates the mirroring
     * <p>
     * true - activate the mirroring
     * <p>
     * false - deactivate the mirroring
     * @return the current image, mirrored
     */
    protected BufferedImage changeMirrorImage(boolean selected) {
        mirror = selected;

        Utils.mirrorImage(currentFrame);

        return currentFrame;
    }

    /**
     * Enable/Disable the drawing of a highlight of the object to be able to see
     * faster the segmented objects.
     *
     * @param drawAlphaObj true - draws an alpha on top of the object, of the
     * color of the object; false - does not draw anything additional to the
     * object borders
     */
    public void setDrawAlphaObj(boolean drawAlphaObj) {
        dPImgToLabel.setDrawAlphaObj(drawAlphaObj);
    }

    /**
     * Set the value of the alpha used to highlight the object.
     *
     * @param objAlpha the value of the alpha to be used for highlighting the
     * object (0-255)
     */
    public void setObjAlpha(int objAlpha) {
        dPImgToLabel.setObjAlpha(objAlpha);
    }

    /**
     * Remove the objects from the list of objects. Start a new fresh list and
     * discard the old one.
     */
    public void removeAllObjects() {
        // create a new object
        objectList = new ArrayList<>();

        // remove all the objects from the side panel object list
        observable.notifyObservers(ObservedActions.Action.REFRESH_OBJ_LIST_PANEL);
    }

    /**
     * Get the attributes of the frame from the application configuration.
     *
     * @return the frame attributes to be displayed on the interface, as an
     * array list of strings.
     */
    public LAttributesFrame getFrameAttributes() {
        if (frameAttributes == null) {
            return loadFrameAttributes();
        } else {
            return frameAttributes;
        }
    }

    /**
     * Get the attributes of the object from the application configuration.
     *
     * @return the object attributes to be displayed on the interface, as an
     * array list of strings.
     */
    public CustomTreeNode getObjectAttributes() {
        return loadObjectAttributes();
    }

    /**
     * Change the old object id with the new one.
     */
    private void updateDefaultObjIdMap() {
        if (currentObject instanceof ObjectScribble) {
            dPImgResult.changeObjId(DEFAULT_OBJECT_ID, currentObject.getObjectId());
        }
    }

    /**
     * Set the name of the file which was chosen by the user.
     *
     * @param fileName the name of the selected file/folder chosen by the user.
     */
    public void setChosenFile(String fileName) {
        // init the files list
        fileList = new ArrayList<>();

        // if the file chosen is a directory, the load shall go recursivly in all subfolders (if any)
        File temp = new File(fileName);

        if (temp.isDirectory()) {
            chosenPath = fileName;
        } else {
            chosenPath = temp.getParent();
            chosenFileName = fileName;
        }

        // create the list of files based on the given path
        getFilesList(chosenPath);

        if ((chosenFileName == null) || (!chosenFileName.equals(fileName))) {
            if (!fileList.isEmpty()) {
                currFrameNo = 1L;
                chosenFileName = fileList.get((int) (currFrameNo - 1L));
            }
        } else {
            // find the index of the chosen file
            currFrameNo = fileList.lastIndexOf(chosenFileName) + 1L;
        }
    }

    /**
     * Create the list of files found in the directory and its subfolders.
     *
     * @param dirName the path to the directory which has to be indexed
     */
    private void getFilesList(String dirName) {
        File[] faFiles = new File(dirName).listFiles();
        for (File file : faFiles) {
            if (common.Utils.checkExtension(file.getName(), Constants.IMG_EXTENSION_LIST)) {
                fileList.add(file.getAbsolutePath());
            }
            if (file.isDirectory()) {
                getFilesList(file.getAbsolutePath());
            }
        }
    }

    /**
     * Allows the user to edit the frame and object attributes.
     *
     * @param parent the parent component of the dialog
     * @return an instance of the attributes definition window
     */
    public AttributesDefinition editAttributes(java.awt.Frame parent) {
        AttributesDefinition attributes = new AttributesDefinition(parent, frameAttributes, objectAttributes);
        // add observer to the frame
        attributes.addObserver(this);

        return attributes;
    }

    /**
     * Refresh the displayed image. The gui might have changed (by maximization
     * etc.), the panel sizes could be different and the image, together with
     * the ground truth, have to be refreshed.
     */
    public void refreshImage() {
        // resize panel and image
        dPImgToLabel.refreshImage(dPImgToLabel.getOrigImg(), availableDrawSize);

        // display the objects based on the new resize ratio
        refreshDisplayList();
    }

    /**
     * Set the amount of space available on the gui for displaying the drawing
     * panel.
     *
     * @param availableDrawSize the amount of space available on the gui for
     * displaying the drawing panel
     */
    public void setAvailableDrawSize(Dimension availableDrawSize) {
        this.availableDrawSize = availableDrawSize;
    }

    /**
     * Get the amount of space available on the gui for displaying the drawing
     * panel.
     *
     * @return the dimension reserved for displaying the drawing panel
     */
    public Dimension getAvailableDrawSize() {
        return availableDrawSize;
    }

    /**
     * The attributes were changed and have to be updated for both frame and
     * object.
     */
    private void reloadAttributes() {
        // at the level of controller, update object attributes
        objectAttributes = null;
        getObjectAttributes();

        // apply changes when it is the case
        reloadObjectAttributes();

        // notify further the main gui viewer to update frame attributes
        frameAttributes = null;
        observable.notifyObservers(ObservedActions.Action.RELOAD_ATTRIBUTES);
    }

    /**
     * Reload the object attributes on the object edit windows.
     */
    private void reloadObjectAttributes() {
        if (currentObject == null) {
            return;
        }

        if (bbWindow != null) {
            bbWindow.setObjectAttributes(getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()));
            bbWindow.setTypeClassValOcc(currentObject.getObjectType(),
                    currentObject.getObjectClass(),
                    currentObject.getObjectValue(),
                    currentObject.getOccluded());
        }
        if (cropWindow != null) {
            cropWindow.setObjectAttributes(getObjAttribTree(objectAttributes, userPrefs.isCheckObjectAttributes()));
            cropWindow.setTypeClassValOcc(currentObject.getObjectType(),
                    currentObject.getObjectClass(),
                    currentObject.getObjectValue(),
                    currentObject.getOccluded());
        }
    }

    /**
     * Is exists scribble obj boolean.
     *
     * @return true if at least one object of scribble type exists in the list
     */
    public boolean isExistsScribbleObj() {
        return existsScribbleObj;
    }

    /**
     * Set true if at least one object of scribble type exists in the list.
     *
     * @param existsScribbleObj true if at least one object of scribble type
     * exists in the list
     */
    public void setExistsScribbleObj(boolean existsScribbleObj) {
        this.existsScribbleObj = existsScribbleObj;
    }

    /**
     * The display tree can be the same with the object attributes tree, or it
     * can contain also an invalid attributes branch, based on the user
     * configuration.
     *
     * @param objAttributes the list of object attributes, defined by the user
     * @param addInvalidAttribBranch true if a branch with the predefined
     * invalid attribute, should be added to the tree
     * @return a tree, which will be used for the synchronization of the
     * type-class-value
     */
    protected CustomTreeNode getObjAttribTree(CustomTreeNode objAttributes, boolean addInvalidAttribBranch) {
        CustomTreeNode extendedTree;

        if (addInvalidAttribBranch) {
            // create the invalid branch of the tree by creating the levels with invalid nodes
            CustomTreeNode invalidL1 = new CustomTreeNode(Constants.INVALID_ATTRIBUTE_TEXT);
            CustomTreeNode invalidL2 = invalidL1.addChild(Constants.INVALID_ATTRIBUTE_TEXT);
            invalidL2.addChild(Constants.INVALID_ATTRIBUTE_TEXT);

            extendedTree = new CustomTreeNode(objAttributes.getRoot());
            // add the invalid branch
            extendedTree.addChild(invalidL1);
            // add the rest of the tree branches
            extendedTree.addChildren(Utils.cloneTree(objAttributes).getChildrenNodes());
        } else {
            extendedTree = objAttributes;
        }

        return extendedTree;
    }

    /**
     * Returns the image specified in the chosenFileName member.
     *
     * @return the image representing the specified file name
     */
    private BufferedImage getChosenFrameImage() {
        try {
            // Load the file to be labeled in the panel and show it
            currentFrame = ImageIO.read(new File(chosenFileName));

            // make sure the frame has a standard format
            convertToStandardFormat();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // flip, mirror according to the selections
        if (flipVertically) {
            changeFlipVerticallyImage(flipVertically);
        }

        if (mirror) {
            changeMirrorImage(mirror);
        }

        return currentFrame;
    }

    /**
     * Create the path to the file where the ground truth shall be stored.
     *
     * @return a string representing the path to the ground truth file
     */
    private String getGTFilePath() {
        // name the path to the ground truth folder
        String gtPath = chosenPath + File.separator + "GT" + File.separator;

        // create the path to the ground truth folder
        common.Utils.createFolderPath(gtPath);

        // name the file where the ground truth shall be saved (remove the extension of the file)
        String fileName = new File(chosenFileName).getName();
        int position = fileName.lastIndexOf('.');
        if (position != -1) {
            fileName = fileName.substring(0, position);
        }

        return (gtPath + fileName);
    }

    /**
     * Convert the current frame to a standard format (TYPE_3BYTE_BGR) from
     * other standards (4 byte ABGR, int ARGB etc.).
     */
    private void convertToStandardFormat() {
        // if the image is not 3 byte RGB, modify it
        if (currentFrame.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            // create the empty image
            BufferedImage convertedImage = new BufferedImage(currentFrame.getWidth(), currentFrame.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

            // draw the frame into the new image
            convertedImage.createGraphics().drawImage(currentFrame, 0, 0, currentFrame.getWidth(), currentFrame.getHeight(), null);

            // point the current image to the modified one
            currentFrame = convertedImage;
        }
    }

    /**
     * Load the attributes of the frame from a file.
     *
     * @return the frame attributes to be displayed on the interface, as an
     * array list of strings.
     */
    private LAttributesFrame loadFrameAttributes() {
        frameAttributes = new LAttributesFrame();
        String readLine;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.FRAME_ATTRIBUTES_PATH), Charset.forName("UTF-8")))) {
            while ((readLine = br.readLine()) != null) {

                String[] wordsList = readLine.split("=");
                String[] attributes = wordsList[wordsList.length - 1].split(",");

                switch (wordsList[0]) {
                    case "illumination":
                        frameAttributes.getIlluminationList().addAll(Arrays.asList(attributes));
                        break;

                    case "weather":
                        frameAttributes.getWeatherList().addAll(Arrays.asList(attributes));
                        break;

                    case "road_type":
                        frameAttributes.getRoadTypeList().addAll(Arrays.asList(attributes));
                        break;

                    case "road_event":
                        frameAttributes.getRoadEventList().addAll(Arrays.asList(attributes));
                        break;

                    case "country":
                        frameAttributes.getCountryList().addAll(Arrays.asList(attributes));
                        break;

                    default:
                        break;
                }
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(GUIController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return frameAttributes;
    }
}
