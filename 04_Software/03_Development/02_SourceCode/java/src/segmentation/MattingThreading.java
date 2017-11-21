/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmentation;

import observers.NotifyObservers;
import observers.ObservedActions;
import commonsegmentation.ScribbleInfo;
import parallelcomputing.PixelSegmentation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Run the cuda matting application on a separate thread, in order to avoid the
 * gui freeze.
 *
 * @author Olimpia Popica
 */
public class MattingThreading implements Runnable {

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final NotifyObservers observable = new NotifyObservers();

    /**
     * Shows who called the matting application. The cuda matting application
     * can be called either for the crop image or for the full image. Class
     * ObservedActions shows the possible actions.
     */
    private final ObservedActions.Action actionOwner;

    /**
     * Object to pixel segmentation algorithm.
     */
    private final PixelSegmentation segmentation;

    /**
     * Initialise the needed data, received from the main thread.
     *
     * @param origImg     - the original image to be processed
     * @param scribbles   - the map of pixels/scribbles for the matting application
     * @param actionOwner - who created the request
     */
    public MattingThreading(BufferedImage origImg, List<ScribbleInfo> scribbles, ObservedActions.Action actionOwner) {

        segmentation = new PixelSegmentation(origImg, (ArrayList<ScribbleInfo>) scribbles);
        this.actionOwner = actionOwner;
    }

    @Override
    public void run() {
        // run the matting algorithm
        runMattingAlgo();

        // notify the main thread that the execution is over and the result image is available to be displayed
        observable.notifyObservers(actionOwner);
    }

    /**
     * Run the matting algorithm on the given input image
     *
     * @return byte [ ] [ ]
     */
    public byte[][] runMattingAlgo() {
        return segmentation.runMattingAlgo();
    }

    /**
     * Returns the map of object indexes, segmented by the algorithm.
     *
     * @return - the matrix of the objects in the image
     */
    public byte[][] getObjMap() {
        return segmentation.getObjMap();
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
}
