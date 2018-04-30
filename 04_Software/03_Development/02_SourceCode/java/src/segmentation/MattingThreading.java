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
