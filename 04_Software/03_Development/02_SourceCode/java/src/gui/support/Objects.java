/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

import common.Timings;
import java.awt.Point;
import java.awt.Rectangle;
import library.Resize;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The objects class is the class which defines the needed methods for all the
 * types of labeling.
 *
 * @author Olimpia Popica
 */
public abstract class Objects {

    /**
     * The object id in the video - used to track the object in the video
     * (constant for several frames).
     */
    private long objectId;

    /**
     * The type of segmentation used: bounding box, scribble etc.
     *
     */
    private String segmentationType;

    /**
     * The segmentation source: manual, automatic.
     */
    protected String segmentationSource;

    /**
     * The type of segmented object.
     */
    private String objType;

    /**
     * The class of segmented object.
     */
    private String objClass;

    /**
     * The value of segmented object.
     */
    private String objValue;

    /**
     * Shows if the object is occluded or not.
     */
    private String occluded;

    /**
     * The color of the displayed object.
     */
    private Color color;

    /**
     * The outer bounding box containing all the crops - panel coordinates.
     */
    protected Rectangle outerBBox;

    /**
     * The predicted coordinates of the object in the next frame.
     */
    private volatile Rectangle predictedBBox;
    /**
     * The predicted coordinates of the object in the next frame using HOG
     * features.
     */
    private volatile Rectangle hogPredictedBBox;
    /**
     * The predicted coordinates of the object in the next frame using HOG
     * features and openCV SVM.
     */
    private volatile Rectangle svmPredictedBBox;

    /**
     * The prediction is valid and can be used.
     */
    private final AtomicBoolean validPrediction;

    /**
     * The list of timings needed to complete the segmentation of the object.
     */
    protected List<Timings> labelingDuration;

    /**
     * Local user preferences.
     */
    private ObjectPreferences userPreference;


    /**
     * The thread on which the tracker is running.
     */
    private Thread trackerThread;

    /**
     * Create an instance of the Objects class.
     */
    public Objects() {
        this.labelingDuration = new ArrayList<>();
        this.predictedBBox = new Rectangle(0, 0, 0, 0);
        this.hogPredictedBBox = new Rectangle(0, 0, 0, 0);
        this.svmPredictedBBox = new Rectangle(0, 0, 0, 0);
        this.userPreference = new ObjectPreferences();
        this.validPrediction = new AtomicBoolean(false);
        
        // avoid null pointer in the object attributes
        this.objType = "";
        this.objClass = "";
        this.objValue = "";
        this.occluded = "";
    }

    /**
     * Gets the user preferences for the current object
     *
     * @return user preferences
     */
    public ObjectPreferences getUserPreference() {
        return userPreference;
    }

    /**
     * Sets the user preferences for the current object
     *
     * @param userPreference user preferences
     */
    public void setUserPreference(ObjectPreferences userPreference) {
        this.userPreference = userPreference;
    }

    /**
     * Returns the id of the object.
     *
     * @return - Returns the id of the object.
     */
    public long getObjectId() {
        return objectId;
    }

    /**
     * Sets the id of the object.
     *
     * @param objectId - the id of the object
     */
    public void setObjectId(long objectId) {
        this.objectId = objectId;
    }

    /**
     * Returns the type of object: bounding box, scribble, 3D bounding box.
     *
     * @return - the type of object
     */
    public String getSegmentationType() {
        return segmentationType;
    }

    /**
     * Sets the type of object: bounding box, scribble, 3D bounding box.
     *
     * @param segmentationType - the type of object
     */
    public void setSegmentationType(String segmentationType) {
        this.segmentationType = segmentationType;
    }

    /**
     * Returns the type of segmentation used for the current object: manual,
     * automatic etc.
     *
     * @return - the type of segmentation used for the current object
     */
    public String getSegmentationSource() {
        return segmentationSource;
    }

    /**
     * Sets the type of segmentation used for the object.
     *
     * @param segmentationSource - the segmentation used for the object: manual, automatic etc.
     */
    public void setSegmentationSource(String segmentationSource) {
        this.segmentationSource = segmentationSource;
    }

    /**
     * Set the type of the object which is being labeled.
     *
     * @param objectType - the name of the object type
     */
    public void setObjectType(String objectType) {
        this.objType = objectType;
    }

    /**
     * Return the name of the type of object being labeled.
     *
     * @return - the name of the object type
     */
    public String getObjectType() {
        return this.objType;
    }

    /**
     * Set the class of the object which is being labeled.
     *
     * @param objectClass - the name of the object class
     */
    public void setObjectClass(String objectClass) {
        this.objClass = objectClass;
    }

    /**
     * Return the name of the class of object being labeled.
     *
     * @return - the name of the object class
     */
    public String getObjectClass() {
        return this.objClass;
    }

    /**
     * Set the value of the object which is being labeled.
     *
     * @param objectValue - the name of the object value
     */
    public void setObjectValue(String objectValue) {
        this.objValue = objectValue;
    }

    /**
     * Return the name of the value of object being labeled.
     *
     * @return - the name of the object value
     */
    public String getObjectValue() {
        return objValue;
    }

    /**
     * Return the occlusion characteristic of the object. It will be defined
     * with the customer every time.
     *
     * @return - the string defining if the object is occluded or not
     */
    public String getOccluded() {
        return occluded;
    }

    /**
     * Set the occlusion of the object.
     *
     * @param occluded - the string defining if the object is occluded or not
     */
    public void setOccluded(String occluded) {
        this.occluded = occluded;
    }

    /**
     * Returns the color used to draw the object.
     *
     * @return Returns the color used to draw the object.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the object.
     *
     * @param color - the color of the object
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Returns the rectangle which limits the entire object, the outer bounds of
     * the object - image coordinates.
     *
     * @return - the rectangle containing the object
     */
    public Rectangle getOuterBBox() {
        return outerBBox;
    }

    /**
     * Return the openCV predicted bounding box.
     *
     * @return the predicted position of the object in the next frame
     */
    public Rectangle getPredictedBBox() {
        return predictedBBox;
    }

    /**
     * Return the position of the object in the next frame, based on HOG
     * features template matching.
     *
     * @return - the position of the object in the next frame
     */
    public Rectangle getHogPredictedBBox() {
        return hogPredictedBBox;
    }

    /**
     * Set the position of the object in the next frame. The prediction is done
     * based on matching of HOG features.
     *
     * @param hogPredictedBBox - the position of the object in the next frame
     */
    public void setHogPredictedBBox(Rectangle hogPredictedBBox) {
        this.hogPredictedBBox = hogPredictedBBox;
    }

    /**
     * Return the position of the object in the next frame, based on openCV SVM
     * with HOG features.
     *
     * @return - the position of the object in the next frame
     */
    public synchronized Rectangle getSvmPredictedBBox() {
        return svmPredictedBBox;
    }

    /**
     * Set the position of the object in the next frame. The prediction is done
     * based on openCV SVM with HOG features.
     *
     * @param svmPredictedBBox - the position of the object in the next frame
     */
    public synchronized void setSvmPredictedBBox(Rectangle svmPredictedBBox) {
        this.svmPredictedBBox = svmPredictedBBox;
    }

    /**
     * Compares if the given rectangle is the same with the outer box of the
     * object; it checks if there is overlapping.
     *
     * @param rectCompare - the bounding box which will be checked for overlapping
     * @return - true if the bounding boxes overlap and false otherwise
     */
    public boolean isEqualOuterBBox(Rectangle rectCompare) {
        return outerBBox.equals(rectCompare);
    }

    /**
     * Creates a new timings object to save the time needed for the object.
     * (position 0 is the segmentation time and all the others are edit times)
     *
     * @return - the last created timing, where to map the duration of the current action (new object, edit)
     */
    public Timings newLabelingDuration() {
        Timings times = new Timings();
        this.labelingDuration.add(times);
        return labelingDuration.get(labelingDuration.size() - 1);
    }

    /**
     * *Return the current timer of the object, which is always the last one.
     *
     * @return - the current timer for the object
     */
    public Timings getCurrentLabelingDuration() {
        return labelingDuration.get(labelingDuration.size() - 1);
    }

    /**
     * Returns the list of labeling durations for the object. The first entry is
     * the labeling time and the others are coming from the edit mode.
     *
     * @return - the list of times needed for the segmentation of the object
     */
    public String getLabelingDuration() {

        StringBuilder durationString = new StringBuilder();

        labelingDuration.forEach((times) -> {
            durationString.append(times.getNeededTime()).append(",");
        });

        // remove the last comma
        if (durationString.length() > 0) {
            durationString.setLength(durationString.length() - 1);
            return durationString.toString();
        }
        return "";
    }

    /**
     * Set the labeling duration with an existent duration.
     *
     * @param labelingDuration - the new labeling duration
     */
    public void setLabelingDuration(ArrayList<Timings> labelingDuration) {
        this.labelingDuration = labelingDuration;
    }

    /**
     * Return the thread on which the tracking is done.
     *
     * @return the thread on which the tracking is done
     */
    public Thread getTrackerThread() {
        return trackerThread;
    }

    /**
     * Return true if the prediction of the object is valid and false if not.
     *
     * @return - true if the prediction of the object is valid and false if not.
     */
    public Boolean isValidPrediction() {
        return validPrediction.get();
    }

    /**
     * Set the status of the prediction: true if the prediction of the object is
     * valid and false if not.
     *
     * @param validPrediction true if the prediction of the object is valid and false if not
     */
    public void setValidPrediction(Boolean validPrediction) {
        this.validPrediction.set(validPrediction);
    }

    /**
     * Moves the whole object, together with the components to the new location
     * specified by the parameter.
     *
     * @param newPositionImg - the new location of the object - image coordinates
     * @param frameSize      the size of the original image
     */
    public void move(Point newPositionImg, Dimension frameSize) {
        int xOffset = newPositionImg.x - outerBBox.x;
        int yOffset = newPositionImg.y - outerBBox.y;

        move(xOffset, yOffset, outerBBox, new Resize(1.0, 1.0), frameSize);
    }

    /**
     * Moves the whole object, together with the components to the new location
     * specified by the parameter.
     *
     * @param newPositionPanel - the new location of the object - image coordinates
     * @param resizeRate       - the resize ratio between the image and the panel
     * @param frameSize        the size of the original image
     */
    public void move(Point newPositionPanel, Resize resizeRate, Dimension frameSize) {
        Point newPositionImg = resizeRate.resizedToOriginal(newPositionPanel);

        int xOffset = newPositionImg.x - outerBBox.x;
        int yOffset = newPositionImg.y - outerBBox.y;

        // the coordinates are already image, therefore the resize shall be 1.0.
        move(xOffset, yOffset, outerBBox, new Resize(1.0, 1.0), frameSize);
    }

    /**
     * Changes the size of the object to the new specified size (the object
     * outer box is changed!!!).
     *
     * @param newSize   the new size of the box
     * @param frameSize the size of the original image
     */
    public void changeSize(Rectangle newSize, Dimension frameSize) {
        int offsetLeft = newSize.x - outerBBox.x;
        int offsetTop = newSize.y - outerBBox.y;
        int offsetRight = (newSize.x + newSize.width) - (outerBBox.x + outerBBox.width);
        int offsetBottom = (newSize.y + newSize.height) - (outerBBox.y + outerBBox.height);

        changeSize(offsetLeft, offsetTop, offsetRight, offsetBottom, outerBBox, new Resize(1.0, 1.0), frameSize);
    }

    /**
     * Save the object predicted coordinates for the next frame
     *
     * @param newPosition predicted position in the next frame
     */
    public void setPredictedCoordinates(Point newPosition) {
        predictedBBox.setLocation(newPosition.x, newPosition.y);
    }


    /**
     * Sets the rectangle which limits the entire object, the outer bounds of
     * the object.
     *
     * @param outerBBox - the rectangle containing the object
     */
    public void setOuterBBox(Rectangle outerBBox) {
        this.outerBBox = outerBBox;
    }

    /**
     * Compute the position and size of the outer bounding box containing the
     * crops of the object.
     */
    public abstract void computeOuterBBoxCurObj();

    /**
     * Moves the object completely in the specified direction (crops, scribbles,
     * outer box etc.).
     *
     * @param xOffset       - how much should the object move on the X axis
     * @param yOffset       - how much the object should move on the Y axis
     * @param coordPanelBox - the coordinates of the selected box in image coordinates
     * @param resizeRate    - the resize rate between the image coordinates and the panel coordinates
     * @param frameSize     the size of the original image
     */
    public abstract void move(int xOffset, int yOffset, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize);

    /**
     * Delete the selected box. The method checkd if the box is the same as the
     * whole object. If it is true, it will not delete it. The parent function
     * is responsible for that. This method can manage only the parts of the
     * object, not the object itself.
     *
     * @param coordPanelBox - the coordinates of the selected box in image
     * @param resizeRate    - the resize rate between the image coordinates and the panel coordinates
     * @return - true if one of the boxes was erased and false if the whole object shall be erased from outside.
     */
    public abstract boolean remove(Rectangle coordPanelBox, Resize resizeRate);

    /**
     * Changes the size of the object in each of the directions, with the amount
     * of specified pixels.
     *
     * @param left          - how much should the object be modified on the left side
     * @param top           - how much should the object be modified on the top part
     * @param right         - how much should the object be modified on the right side
     * @param bottom        - how much should the object be modified on the bottom part
     * @param coordPanelBox - the coordinates of the selected box in image coordinates
     * @param resizeRate    - the resize rate between the image coordinates and the panel coordinates
     * @param frameSize     the size of the original image
     */
    public abstract void changeSize(int left, int top, int right, int bottom, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize);

    /**
     * Verifies if the object contains the given bounding box (as a crop or as
     * the objects outer box).
     *
     * @param coordPanelBox - the coordinates of the box in panel coordinates
     * @param resizeRate    - the resize ratio between the image and the panel
     * @return - true if the object contains it; false if not
     */
    public abstract boolean contains(Rectangle coordPanelBox, Resize resizeRate);
}
