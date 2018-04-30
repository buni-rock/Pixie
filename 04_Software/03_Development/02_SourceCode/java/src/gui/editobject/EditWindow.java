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
import gui.viewer.ImagePreview;
import gui.viewer.MessageWindow;
import gui.viewer.MonochromeIcon;
import gui.support.CropObject;
import gui.support.CustomTreeNode;
import gui.support.ObjectPreferences;
import gui.support.ScreenResolution;
import observers.NotifyObservers;
import observers.ObservedActions;
import paintpanels.DrawConstants;
import paintpanels.DrawingPanel;
import paintpanels.ResultPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Observer;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import segmentation.MattingThreading;

/**
 *
 * @author Olimpia Popica
 */
public abstract class EditWindow extends javax.swing.JDialog implements Observer {

    /**
     * The text being displayed on the histogram button when the image has been
     * enhanced and the user can go to the original one.
     */
    protected static final String ORIGINAL_TEXT = "Original";
    /**
     * The text on the show highlighted image button.
     */
    protected static final String HIGHLIGHT_TEXT = "Highlight";

    /**
     * The resolution of the current screen where the application is showed.
     */
    protected transient ScreenResolution screenRes;

    /**
     * The original image of the object in the original size.
     */
    protected transient BufferedImage origImg;

    /**
     * The original image in the frame; the whole image - original size.
     */
    protected final transient BufferedImage frameImg;

    /**
     * The image used to be displayed. It is computed out of the original image
     * in order to keep the details and not loose data.
     */
    protected transient BufferedImage workImg;

    /**
     * The panel containing the preview image.
     */
    protected DrawingPanel dPPreviewImg;

    /**
     * The panel containing the result of the semantic segmentation image.
     */
    protected ResultPanel dPSemanticResultImg;

    /**
     * Used to preview the result image in a new window, with zoom available.
     */
    protected ImagePreview segmentResultPrev;

    /**
     * Encapsulates the call for running the cuda matting on a different thread.
     */
    protected transient MattingThreading mattingThread;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    protected final transient NotifyObservers observable = new NotifyObservers();

    /**
     * Shows who called the crop window: the normal crop or the edit crop. Class
     * ObservedActions shows the possible actions.
     */
    protected final ObservedActions.Action actionOwner;

    /**
     * The type of drawing: background or object.
     */
    protected int drawingType;

    /**
     * The list of colors which were used for the segmented objects in the
     * current frame. It helps to prevent the segmentation of two objects with
     * the same color.
     */
    protected final transient List<Color> objColorsList;

    /**
     * The color of the object, used in the bounding box window. It will not be
     * saved for the object if the object is cancel. It will be saved for the
     * object if the object is saved.
     */
    protected Color objectColor;

    /**
     * The list of object attributes: type, class, value.
     */
    protected CustomTreeNode objectAttributes;

    /**
     * User preferences related to the application.
     */
    protected UserPreferences userPrefs;

    /**
     * The size of the border, in pixels, for displaying purposes (the user has
     * to see some of the environment to be able to label correctly).
     */
    protected int borderPX;

    /**
     * The original position of the object in the image; before being modified.
     */
    protected Rectangle origObjPos;

    /**
     * The selected crop for being edited.
     */
    protected transient CropObject currentCrop;

    /**
     * User preferences regarding the way an object should be displayed in edit
     * mode: how much zoom shall be used, how much border etc.
     */
    protected final transient ObjectPreferences objPreferences;

    /**
     * Creates new form BoundingBoxWindow
     *
     * @param parent the parent component of the dialog
     * @param frameImage the original image, in original size
     * @param objectAttributes the list of object attributes: type, class, value
     * @param actionOwner the scope of the dialog: create new box, edit existing
     * one
     * @param objColorsList the list of already used colors (for other objects)
     * @param userPreferences user preferences regarding application
     * configuration
     * @param objPreferences user preferences regarding the display of the
     * object
     */
    public EditWindow(java.awt.Frame parent,
            BufferedImage frameImage,
            CustomTreeNode objectAttributes,
            ObservedActions.Action actionOwner,
            List<Color> objColorsList,
            UserPreferences userPreferences,
            ObjectPreferences objPreferences) {
        super(parent, true);

        initComponents();

        this.actionOwner = actionOwner;
        this.objColorsList = objColorsList;

        // save the original image
        this.frameImg = frameImage;

        // save the user anf object preferences
        this.userPrefs = userPreferences;
        this.objPreferences = objPreferences;

        // get the tree in the form to be displayed (add if needed the invalid branch)
        this.objectAttributes = objectAttributes;

        initAttributesComponents();

        initResolutions();

        initOtherData();
    }

    private void initOtherData() {
        // compute the preffered width of the combo boxes based on the specified text
        jCBObjType.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
        jCBObjClass.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
        jCBObjValue.setPrototypeDisplayValue(Constants.OBJECT_ATTRIBUTES_TEXT);
    }

    /**
     * Update the displayed image with the specified one.
     *
     * @param image the new image to be displayed
     * @param drawType the type of drawing allowed on the panel
     * @param zoomingIndex the zoom wanted by the user
     * @return
     */
    protected boolean updatePreview(BufferedImage image, DrawConstants.DrawType drawType, int zoomingIndex) {
        if ((image != null)
                && (image.getWidth() > 0)
                && (image.getHeight() > 0)) {

            // remove the old image if it exists
            if (dPPreviewImg != null) {
                this.remove(dPPreviewImg);
                jPPreviewImg.remove(dPPreviewImg);

                dPPreviewImg.updateImage(image);
            } else {
                // Load the file to be labeled in the panel and show it
                dPPreviewImg = new DrawingPanel(image, drawType, zoomingIndex);
                dPPreviewImg.setBackground(new java.awt.Color(255, 255, 255));

                // add observer to be notified when there are changes on the panel
                dPPreviewImg.addObserver(this);
            }

            jPPreviewImg.add(dPPreviewImg);

            this.validate();
            this.repaint();

            return true;
        }

        return false;
    }

    /**
     * Equalize the histogram of the image, for all color components.
     */
    protected void histogramEqualisation() {
        // if the image was highlighted, do it again
        if (HIGHLIGHT_TEXT.equals(jBHistogramEq.getText())) {
            workImg = Utils.histogramEqColor(origImg);
            jBHistogramEq.setText(ORIGINAL_TEXT);
        } else if (ORIGINAL_TEXT.equals(jBHistogramEq.getText())) {
            workImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
            Utils.copySrcIntoDstAt(origImg, workImg);
            jBHistogramEq.setText(HIGHLIGHT_TEXT);
        }

        showImage();
    }

    /**
     * Get the piece of image representing the object from the frame image; load
     * it on a panel; display it and load the object attributes.
     *
     * @param imgPosition the position of the segmented to be be displayed,
     * relative to the full frame (image coordinates)
     */
    protected final void displayImage(Rectangle imgPosition) {
        this.origImg = Utils.getSelectedImg(frameImg, imgPosition);

        // if the image was highlighted, do it again
        if (ORIGINAL_TEXT.equals(jBHistogramEq.getText())) {
            workImg = Utils.histogramEqColor(origImg);
        } else {
            workImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
            Utils.copySrcIntoDstAt(origImg, workImg);
        }

        showImage();

        prepareFrame();
    }

    /**
     * Show the color chooser dialog and allow the user to choose a new color
     * for the object. Replace the color with a similar one, for the case when
     * the color was reserved or already used.
     *
     * @param objColor the current color of the object
     * @return the new color of the object, or the old color if no other was
     * wanted
     */
    protected Color getNewObjColor(Color objColor) {
        Color newColor = JColorChooser.showDialog(this,
                "Choose Object Color",
                objColor);

        if (newColor == null) {
            return objColor;
        }

        if (objColorsList.contains(newColor) || Constants.COLORS_LIST.contains(newColor)) {
            newColor = Utils.changeColor(newColor, objColorsList);

            String title = "Color warning";
            String message = "\nThe chosen color was already used for an object or it is reserved.\nIt will be changed to the one previewed here.";

            MessageWindow infoWin = new MessageWindow(new javax.swing.JFrame(), true, title, message, "OK", new MonochromeIcon(32, 32, newColor));
            infoWin.setLocationRelativeTo(this);
            infoWin.setVisible(true);
        }

        return newColor;
    }

    /**
     * Get the position of the segmented image (the one to be displayed) and
     * call the display image method.
     */
    protected abstract void displayImage();

    /**
     * Display the image and load the object specific data.
     */
    protected abstract void showImage();

    /**
     * Notifies the controller that the object was segmented and it has to be
     * saved.
     */
    protected abstract void saveObject();

    /**
     * Notifies the controller that the object was cancel.
     */
    protected abstract void cancelObject();

    /**
     * The delete key was pressed and the object has to be removed. Handle the
     * remove of an object by pressing the delete key.
     */
    protected abstract void deleteObject();

    /**
     * Moves an object in the image with the specified offsets.
     *
     * @param offsetX how many pixels should the object moved on the X axis
     * @param offsetY how many pixels should the object moved on the Y axis
     */
    protected abstract void moveObject(int offsetX, int offsetY);

    /**
     * Changes the size of the object on any direction.
     *
     * @param left add/remove pixels from the left side of the object
     * @param top add/remove pixels from the top side of the object
     * @param right add/remove pixels from the right side of the object
     * @param bottom add/remove pixels from the bottom side of the object
     */
    protected abstract void changeSize(int left, int top, int right, int bottom);

    /**
     * Restore the object to the original position. The user made changes but
     * does not want to keep them.
     */
    protected abstract void restoreObjOrigPos();

    /**
     * Change the color of the object to a new one, selected by the user.
     */
    protected abstract void changeObjColor();

    /**
     * Shows the object id in the frame and sets its color and background for
     * better readability.
     */
    protected abstract void displayObjId();

    /**
     * Sets the title of the frame with the current size of the image and the
     * size of the work image (which can be different due to the zooming).
     */
    protected abstract void setFrameTitle();

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
    protected final void prepareFrame() {
        this.pack();
        this.setMinimumSize(getPreferredSize());
    }

    /**
     * Zoom the image based on the movement of the mouse scroll.
     *
     * @param notches how much the mouse scroll moves and in which direction
     */
    protected void zoomImage(int notches) {
        if (notches < 0) {
            // Mouse wheel moved UP = zoom in
            // check if the resize is not generating a bigger image than the screen
            Dimension origImgDimension = new Dimension(origImg.getWidth(), origImg.getHeight());
            if (dPPreviewImg.getResize().isSizeIncreaseOK(origImgDimension, screenRes.getScreenResolution())) {
                objPreferences.setZoomingIndex(dPPreviewImg.getResize().incrementWidthHeight(origImgDimension));
            }
        } else {
            Dimension origImgDimension = new Dimension(-origImg.getWidth(), -origImg.getHeight());
            // Mouse wheel moved DOWN = zoom out
            if (dPPreviewImg.getResize().isSizeDecreaseOK(origImgDimension)) {
                objPreferences.setZoomingIndex(dPPreviewImg.getResize().incrementWidthHeight(origImgDimension));
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
     * Return the color of the segmented object as selected by the user in the
     * edit window.
     *
     * @return - the color chosen by the user for the object
     */
    public Color getObjectColor() {
        return objectColor;
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
     * @param objType - wanted object type
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

        // notify to remove the GUI key event dispatcher
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
     * Close the window and release the key dispatcher.
     */
    protected void closeWindow() {
        // notify to add the GUI key event dispatcher back
        observable.notifyObservers(ObservedActions.Action.ADD_GUI_KEY_EVENT_DISPATCHER);

        dispose();
    }

    /**
     * When the window is opening it will have its own key dispatcher. The
     * dispatcher of the GUI should no longer be active.
     */
    public void removeOtherKeyEventDispatcher() {
        // notify to remove the GUI key event dispatcher
        observable.notifyObservers(ObservedActions.Action.REMOVE_GUI_KEY_EVENT_DISPATCHER);
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
    protected boolean isObjectAttributesSet() {
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
     * Set the object attributes tree.
     *
     * @param objectAttributes the list of object attributes, saved as a tree
     */
    public void setObjectAttributes(CustomTreeNode objectAttributes) {
        this.objectAttributes = objectAttributes;
        initAttributesComponents();
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
        jRBOption2.setSelected(true);
    }

    /**
     * Activate the eraser in order to remove the incorrect drawn points.
     */
    private void selectEraser() {
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_ERASE);
        jRBOption3.setSelected(true);
    }

    /**
     * Activate the background, in order to draw scribbles for it.
     */
    private void selectBackground() {
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_BACKGROUND);
        jRBOption1.setSelected(true);
    }

    /**
     * Show or hide the GUI elements related to the semantic segmentation.
     *
     * @param enable true if the semantic options should be available
     */
    protected final void enableSemanticOptions(boolean enable) {
        jPRBOptions.setVisible(enable);
        jPBrushOptions.setVisible(enable);
        jPSemanticResult.setVisible(enable);
        jBFilterObjMap.setVisible(enable);
        jMISemanticSegOptions.setVisible(enable);
        jCBMergeBKG.setVisible(enable);
        jBRunMattingCrop.setVisible(enable);
        jBRunHighlightMatt.setVisible(enable);
    }

    /**
     * Show or hide the GUI elements related to the bounding box segmentation.
     *
     * @param enable true if the bounding box options should be available
     */
    protected final void enableBoxOptions(boolean enable) {
        jLBorder.setVisible(enable);
        jTFBorder.setVisible(enable);
        jBSetBorder.setVisible(enable);
        jLObjId.setVisible(enable);
    }

    /**
     * *************************BOUNDING BOX AREA******************************
     */
    /**
     * Updates the value of the border with the value specified by the user in
     * the text box.
     *
     * @param value
     */
    protected void updateBorderValue(int value) {
        // do nothing; the classes which need to implement this method; will do so
    }

    /**
     * Change the border size for the event Ctrl + mouse scroll.
     *
     * @param notches how much the mouse scroll moves and in which direction
     */
    protected void changeBorderSize(int notches) {
        // do nothing; the classes which need to implement this method; will do so
    }

    /**
     * *********************SEMANTIC SEGMENTATION AREA*************************
     */
    /**
     * Update the type of action and the preview of the brush.
     *
     * @param actionType - the type of action: -1 = erase; 0 = background; 1 =
     * object
     */
    protected void updateObjInfo(int actionType) {
        // do nothing; the classes which need to implement this method; will do so

    }

    /**
     * When a change in the mouse brush happens, update the mouse icon and the
     * brush preview.
     */
    protected void updateMouseAndBrush() {
        // do nothing; the classes which need to implement this method; will do so
    }

    /**
     * Prepare the needed input for the matting application, call it and do the
     * load actions for the GUI.
     *
     * @param imageOption - on which image shall the algorithm be run original,
     * highlighted etc.
     */
    public void runMatting(int imageOption) {
        // do nothing; the classes which need to implement this method; will do so
    }

    /**
     * Filter the object map in order to remove some of the noise from the
     * image.
     */
    protected void filterObjMap() {
        // do nothing; the classes which need to implement this method; will do so
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

        javax.swing.ButtonGroup jBGObjects = new javax.swing.ButtonGroup();
        javax.swing.JPanel jPBackground = new javax.swing.JPanel();
        javax.swing.JPanel jPObjAttributes = new javax.swing.JPanel();
        jLType = new javax.swing.JLabel();
        jLClass = new javax.swing.JLabel();
        jLValue = new javax.swing.JLabel();
        jLOccluded = new javax.swing.JLabel();
        javax.swing.JButton jBSaveObj = new javax.swing.JButton();
        jCBObjClass = new javax.swing.JComboBox<>();
        jCBObjType = new javax.swing.JComboBox<>();
        jCBObjValue = new javax.swing.JComboBox<>();
        jCBOccluded = new javax.swing.JComboBox<>();
        jLObjId = new javax.swing.JLabel();
        jPOptions = new javax.swing.JPanel();
        jBHistogramEq = new javax.swing.JButton();
        javax.swing.JButton jBChangeObjColor = new javax.swing.JButton();
        jBFilterObjMap = new javax.swing.JButton();
        javax.swing.JButton jBRestorePos = new javax.swing.JButton();
        javax.swing.JPanel jPBorder = new javax.swing.JPanel();
        jLBorder = new javax.swing.JLabel();
        jBSetBorder = new javax.swing.JButton();
        jTFBorder = new javax.swing.JTextField();
        jPRBOptions = new javax.swing.JPanel();
        jRBOption3 = new javax.swing.JRadioButton();
        jRBOption2 = new javax.swing.JRadioButton();
        jRBOption1 = new javax.swing.JRadioButton();
        jPPreviewImg = new javax.swing.JPanel();
        jPMoreOpt = new javax.swing.JPanel();
        jBRunMattingCrop = new javax.swing.JButton();
        jCBMergeBKG = new javax.swing.JCheckBox();
        jBRunHighlightMatt = new javax.swing.JButton();
        jPBrushOptions = new javax.swing.JPanel();
        jSCropBrushSize = new javax.swing.JSlider();
        jSCropBrushDensity = new javax.swing.JSlider();
        jLCropBrushSize = new javax.swing.JLabel();
        jLCropBrushDensity = new javax.swing.JLabel();
        jLBrushPreview = new javax.swing.JLabel();
        jLBrushSizePreview = new javax.swing.JLabel();
        jPSemanticResult = new javax.swing.JPanel();
        javax.swing.JMenuBar jMBEditWindow = new javax.swing.JMenuBar();
        javax.swing.JMenu jMFile = new javax.swing.JMenu();
        javax.swing.JMenuItem jMICloseDialog = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMISaveObj = new javax.swing.JMenuItem();
        javax.swing.JMenu jMEditObject = new javax.swing.JMenu();
        javax.swing.JMenuItem jMIMoveLeft = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIMoveRight = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIMoveUp = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIMoveDown = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator1 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIDecreaseLeft = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIDecreaseRight = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIDecreaseTop = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIDecreaseBottom = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator3 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIIncreaseLeft = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIIncreaseRight = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIIncreaseTop = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIIncreaseBottom = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator2 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIDecreaseBox = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIIncreaseBox = new javax.swing.JMenuItem();
        javax.swing.JPopupMenu.Separator jSeparator4 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIDeleteObj = new javax.swing.JMenuItem();
        jMISemanticSegOptions = new javax.swing.JMenu();
        jMSelectBKG = new javax.swing.JMenuItem();
        jMISelectObj = new javax.swing.JMenuItem();
        jMISelectErase = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIIncreaseBrushSize = new javax.swing.JMenuItem();
        javax.swing.JMenuItem jMIDecreaseBrushSize = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem jMIRunMatting = new javax.swing.JMenuItem();

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
        getContentPane().setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPObjAttributes, gridBagConstraints);

        jPOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPOptions.setLayout(new java.awt.GridBagLayout());

        jBHistogramEq.setBackground(new java.awt.Color(255, 255, 255));
        jBHistogramEq.setText(HIGHLIGHT_TEXT);
        jBHistogramEq.setPreferredSize(new java.awt.Dimension(120, 32));
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
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBFilterObjMap, gridBagConstraints);

        jBRestorePos.setBackground(new java.awt.Color(255, 255, 255));
        jBRestorePos.setText("Restore Obj");
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jBRestorePos, gridBagConstraints);

        jPBorder.setBackground(new java.awt.Color(255, 255, 255));
        jPBorder.setLayout(new java.awt.GridBagLayout());

        jLBorder.setText("Border (px)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPBorder.add(jLBorder, gridBagConstraints);

        jBSetBorder.setBackground(new java.awt.Color(255, 255, 255));
        jBSetBorder.setText("Set Border");
        jBSetBorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSetBorderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPBorder.add(jBSetBorder, gridBagConstraints);

        jTFBorder.setText("jTextField1");
        jTFBorder.setPreferredSize(new java.awt.Dimension(60, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPBorder.add(jTFBorder, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        jPOptions.add(jPBorder, gridBagConstraints);

        jPRBOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPRBOptions.setLayout(new java.awt.GridBagLayout());

        jRBOption3.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBOption3);
        jRBOption3.setText("Erase");
        jRBOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBOption3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPRBOptions.add(jRBOption3, gridBagConstraints);

        jRBOption2.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBOption2);
        jRBOption2.setForeground(new java.awt.Color(51, 255, 0));
        jRBOption2.setText("obj");
        jRBOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPRBOptions.add(jRBOption2, gridBagConstraints);

        jRBOption1.setBackground(new java.awt.Color(255, 255, 255));
        jBGObjects.add(jRBOption1);
        jRBOption1.setForeground(new java.awt.Color(255, 0, 0));
        jRBOption1.setSelected(true);
        jRBOption1.setText("bkg");
        jRBOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBOption1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPRBOptions.add(jRBOption1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPOptions.add(jPRBOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        jPBackground.add(jPOptions, gridBagConstraints);

        jPPreviewImg.setBackground(new java.awt.Color(255, 255, 255));
        jPPreviewImg.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPPreviewImg.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPPreviewImg, gridBagConstraints);

        jPMoreOpt.setBackground(new java.awt.Color(255, 255, 255));
        jPMoreOpt.setLayout(new java.awt.GridBagLayout());

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
        jPMoreOpt.add(jBRunMattingCrop, gridBagConstraints);

        jCBMergeBKG.setBackground(new java.awt.Color(255, 255, 255));
        jCBMergeBKG.setSelected(true);
        jCBMergeBKG.setText("mergeBKG");
        jCBMergeBKG.setToolTipText("Check for merging the image as it was segmented.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPMoreOpt.add(jCBMergeBKG, gridBagConstraints);

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
        jPMoreOpt.add(jBRunHighlightMatt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPMoreOpt, gridBagConstraints);

        jPBrushOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPBrushOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Brush"));
        jPBrushOptions.setLayout(new java.awt.GridBagLayout());

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
        jPBrushOptions.add(jSCropBrushSize, gridBagConstraints);

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
        jPBrushOptions.add(jSCropBrushDensity, gridBagConstraints);

        jLCropBrushSize.setText("Size");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPBrushOptions.add(jLCropBrushSize, gridBagConstraints);

        jLCropBrushDensity.setText("Density");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPBrushOptions.add(jLCropBrushDensity, gridBagConstraints);

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
        jPBrushOptions.add(jLBrushPreview, gridBagConstraints);

        jLBrushSizePreview.setText("Brush");
        jLBrushSizePreview.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLBrushSizePreview.setPreferredSize(new java.awt.Dimension(35, 35));
        jLBrushSizePreview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.01;
        jPBrushOptions.add(jLBrushSizePreview, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPBrushOptions, gridBagConstraints);

        jPSemanticResult.setBackground(new java.awt.Color(255, 255, 255));
        jPSemanticResult.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPSemanticResult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPSemanticResultMouseClicked(evt);
            }
        });
        jPSemanticResult.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 5, 5, 5);
        jPBackground.add(jPSemanticResult, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(jPBackground, gridBagConstraints);

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

        jMBEditWindow.add(jMFile);

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

        jMBEditWindow.add(jMEditObject);

        jMISemanticSegOptions.setText("Actions");

        jMSelectBKG.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMSelectBKG.setText("Select Background");
        jMSelectBKG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMSelectBKGActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMSelectBKG);

        jMISelectObj.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        jMISelectObj.setText("Select Object");
        jMISelectObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISelectObjActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMISelectObj);

        jMISelectErase.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        jMISelectErase.setText("Select Erase");
        jMISelectErase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISelectEraseActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMISelectErase);
        jMISemanticSegOptions.add(jSeparator5);

        jMIIncreaseBrushSize.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET, 0));
        jMIIncreaseBrushSize.setText("Increase Brush Size");
        jMIIncreaseBrushSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseBrushSizeActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMIIncreaseBrushSize);

        jMIDecreaseBrushSize.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET, 0));
        jMIDecreaseBrushSize.setText("Decrease Brush Size");
        jMIDecreaseBrushSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBrushSizeActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMIDecreaseBrushSize);
        jMISemanticSegOptions.add(jSeparator6);

        jMIRunMatting.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        jMIRunMatting.setText("Run Matting");
        jMIRunMatting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIRunMattingActionPerformed(evt);
            }
        });
        jMISemanticSegOptions.add(jMIRunMatting);

        jMBEditWindow.add(jMISemanticSegOptions);

        setJMenuBar(jMBEditWindow);

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

    private void jBChangeObjColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBChangeObjColorActionPerformed
        changeObjColor();
    }//GEN-LAST:event_jBChangeObjColorActionPerformed

    private void jBFilterObjMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBFilterObjMapActionPerformed
        filterObjMap();
    }//GEN-LAST:event_jBFilterObjMapActionPerformed

    private void jBRestorePosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRestorePosActionPerformed
        restoreObjOrigPos();
    }//GEN-LAST:event_jBRestorePosActionPerformed

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
        // if the textfield id the focus owner, do not save the object
        if (jTFBorder.isFocusOwner()) {
            jBSetBorderActionPerformed(evt);
            return;
        }
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

    private void jBHistogramEqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHistogramEqActionPerformed
        histogramEqualisation();
    }//GEN-LAST:event_jBHistogramEqActionPerformed

    private void jBSetBorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSetBorderActionPerformed
        if ((jTFBorder.getText() == null) || !Utils.isNumeric(jTFBorder.getText())) {
            return;
        }

        updateBorderValue(Integer.parseInt(jTFBorder.getText()));
    }//GEN-LAST:event_jBSetBorderActionPerformed

    private void jRBOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBOption1ActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_BACKGROUND);
    }//GEN-LAST:event_jRBOption1ActionPerformed

    private void jRBOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBOption2ActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_OBJECT);
    }//GEN-LAST:event_jRBOption2ActionPerformed

    private void jRBOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBOption3ActionPerformed
        updateObjInfo(ConstantsLabeling.ACTION_TYPE_ERASE);
    }//GEN-LAST:event_jRBOption3ActionPerformed

    private void jBRunMattingCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRunMattingCropActionPerformed
        // display the original image
        dPPreviewImg.reloadWorkImg();
        jBHistogramEq.setText(HIGHLIGHT_TEXT);

        // run the matting algorithm on the original image
        runMatting(Constants.RUN_MATT_ORIG_IMG);
    }//GEN-LAST:event_jBRunMattingCropActionPerformed

    private void jBRunHighlightMattActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBRunHighlightMattActionPerformed
        // set the displayed image as highlighted
        dPPreviewImg.setWorkImg(Utils.histogramEqColor(dPPreviewImg.getWorkImg()));
        jBHistogramEq.setText(ORIGINAL_TEXT);

        // run the matting
        runMatting(Constants.RUN_MATT_HIGHLIGHT_IMG);
    }//GEN-LAST:event_jBRunHighlightMattActionPerformed

    private void jSCropBrushSizeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSCropBrushSizeMouseReleased
        updateMouseAndBrush();
    }//GEN-LAST:event_jSCropBrushSizeMouseReleased

    private void jSCropBrushDensityMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSCropBrushDensityMouseReleased
        updateMouseAndBrush();
    }//GEN-LAST:event_jSCropBrushDensityMouseReleased

    private void jPSemanticResultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPSemanticResultMouseClicked
        if (dPSemanticResultImg.getWorkImg() != null) {
            segmentResultPrev = new ImagePreview((Frame) this.getParent(), true, dPSemanticResultImg.getWorkImg(), "Image Result", true, "Filter");
            segmentResultPrev.addObserver(this);
            segmentResultPrev.setVisible(true);
        }
    }//GEN-LAST:event_jPSemanticResultMouseClicked

    private void jMSelectBKGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMSelectBKGActionPerformed
        selectBackground();
    }//GEN-LAST:event_jMSelectBKGActionPerformed

    private void jMISelectObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISelectObjActionPerformed
        selectObject();
    }//GEN-LAST:event_jMISelectObjActionPerformed

    private void jMISelectEraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISelectEraseActionPerformed
        selectEraser();
    }//GEN-LAST:event_jMISelectEraseActionPerformed

    private void jMIIncreaseBrushSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBrushSizeActionPerformed
        increaseBrushSize();
    }//GEN-LAST:event_jMIIncreaseBrushSizeActionPerformed

    private void jMIDecreaseBrushSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBrushSizeActionPerformed
        decreaseBrushSize();
    }//GEN-LAST:event_jMIDecreaseBrushSizeActionPerformed

    private void jMIRunMattingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIRunMattingActionPerformed
        // run the matting app
        runMatting(Constants.RUN_MATT_ORIG_IMG);
    }//GEN-LAST:event_jMIRunMattingActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JButton jBFilterObjMap;
    protected javax.swing.JButton jBHistogramEq;
    protected javax.swing.JButton jBRunHighlightMatt;
    protected javax.swing.JButton jBRunMattingCrop;
    private javax.swing.JButton jBSetBorder;
    protected javax.swing.JCheckBox jCBMergeBKG;
    protected javax.swing.JComboBox<String> jCBObjClass;
    protected javax.swing.JComboBox<String> jCBObjType;
    protected javax.swing.JComboBox<String> jCBObjValue;
    protected javax.swing.JComboBox<String> jCBOccluded;
    private javax.swing.JLabel jLBorder;
    protected javax.swing.JLabel jLBrushPreview;
    protected javax.swing.JLabel jLBrushSizePreview;
    private javax.swing.JLabel jLClass;
    private javax.swing.JLabel jLCropBrushDensity;
    private javax.swing.JLabel jLCropBrushSize;
    protected javax.swing.JLabel jLObjId;
    private javax.swing.JLabel jLOccluded;
    private javax.swing.JLabel jLType;
    private javax.swing.JLabel jLValue;
    private javax.swing.JMenuItem jMISelectErase;
    private javax.swing.JMenuItem jMISelectObj;
    private javax.swing.JMenu jMISemanticSegOptions;
    private javax.swing.JMenuItem jMSelectBKG;
    private javax.swing.JPanel jPBrushOptions;
    private javax.swing.JPanel jPMoreOpt;
    private javax.swing.JPanel jPOptions;
    protected javax.swing.JPanel jPPreviewImg;
    protected javax.swing.JPanel jPRBOptions;
    protected javax.swing.JPanel jPSemanticResult;
    protected javax.swing.JRadioButton jRBOption1;
    protected javax.swing.JRadioButton jRBOption2;
    protected javax.swing.JRadioButton jRBOption3;
    protected javax.swing.JSlider jSCropBrushDensity;
    protected javax.swing.JSlider jSCropBrushSize;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    protected javax.swing.JTextField jTFBorder;
    // End of variables declaration//GEN-END:variables
}
