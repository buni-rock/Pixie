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
package videomodule;

import gui.actions.GUIController;
import observers.NotifyObservers;
import observers.ObservedActions;
import paintpanels.DrawingPanel;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Play images runnable.
 *
 * @author Olimpia Popica
 */
public class PlayImagesRunnable implements Runnable {

    /**
     * Keep the thread running until the user presses the pause button (true =
     * play images; false = pause images).
     */
    private volatile boolean run = true;

    /**
     * An instance of the GUIController for being able to simulate the play of
     * data, based on images.
     */
    private final GUIController gc;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final NotifyObservers observable = new NotifyObservers();

    /**
     * The panel containing the original image, where the segmentation/labeling
     * is happening.
     */
    private final DrawingPanel dPImgToLabel;

    /**
     * Instantiates a new Play images runnable.
     *
     * @param gc the gc
     */
    public PlayImagesRunnable(GUIController gc) {
        this.gc = gc;
        this.dPImgToLabel = gc.getdPImgToLabel();
    }

    @Override
    public void run() {
        while (run) {
            // get next frame
            dPImgToLabel.newFrame(gc.getNextFrame(), gc.getAvailableDrawSize());

            // notify the main thread that the frame number changed
            observable.notifyObservers(ObservedActions.Action.REFRESH_FRAME_NO);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(PlayImagesRunnable.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Is run boolean.
     *
     * @return - true for a running thread and false for a paused/stopped thread
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Set true if the thread should run and false otherwise.
     *
     * @param run - true for a running thread and false for a paused/stopped thread
     */
    public void setRun(boolean run) {
        this.run = run;
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
