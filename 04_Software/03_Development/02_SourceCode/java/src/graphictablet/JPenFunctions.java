/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphictablet;

import observers.NotifyObservers;
import observers.ObservedActions;
import java.util.Observer;
import jpen.PButton;
import jpen.PButtonEvent;
import jpen.PKind;
import jpen.PKindEvent;
import jpen.PLevel;
import jpen.PLevelEvent;
import jpen.PScrollEvent;
import jpen.event.PenListener;

/**
 * The type J pen functions.
 *
 * @author Olimpia Popica
 */
public class JPenFunctions implements PenListener {

    private int brushSize;
    private int brushSizePrev;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final NotifyObservers observable = new NotifyObservers();

    /**
     * Instantiates a new J pen functions.
     */
    public JPenFunctions() {

    }

    @Override
    public void penButtonEvent(PButtonEvent ev) {

        PKind type = ev.pen.getKind();
        // Discard events from mouse
        if (type == PKind.valueOf(PKind.Type.CURSOR)) {
            return;
        }

        // Pen pressed is LEFT
        if (ev.pen.getButtonValue(PButton.Type.LEFT)) {
//            Utils.outputText("LEFT");
        }
        if (ev.pen.getButtonValue(PButton.Type.CENTER)) {
//            Utils.outputText("CENTER");
        }
        // Pen button pressed is RIGHT
        if (ev.pen.getButtonValue(PButton.Type.RIGHT)) {
//            Utils.outputText("RIGHT");
        }
    }

    @Override
    public void penKindEvent(PKindEvent ev) {
//        Utils.outputText("Kind event");
    }

    @Override
    public void penLevelEvent(PLevelEvent evt) {
        // Get kind of event: does it come from mouse (CURSOR), STYLUS or ERASER?
        PKind type = evt.pen.getKind();

        // Discard events from mouse
        if (type == PKind.valueOf(PKind.Type.CURSOR)) {
            return;
        }

        // Set the brush's size relative to the pressure
        brushSize = (int) ((evt.pen.getLevelValue(PLevel.Type.PRESSURE) * 60.0f) * 0.5f);

        // notify the main thread that the brush size is changed
        observable.notifyObservers(ObservedActions.Action.REFRESH_BRUSH_SIZE);

    }

    @Override
    public void penScrollEvent(PScrollEvent ev) {
//        Utils.outputText("scroll event");
    }

    @Override
    public void penTock(long availableMillis) {
//          Utils.outputText("TOCK - available period fraction: " + availableMillis);
    }

    /**
     * Gets brush size.
     *
     * @return the brush size
     */
    public int getBrushSize() {
        return brushSize;
    }

    /**
     * Gets brush size prev.
     *
     * @return the brush size prev
     */
    public int getBrushSizePrev() {
        return brushSizePrev;
    }

    /**
     * Sets brush size prev.
     *
     * @param size the size
     */
    public void setBrushSizePrev(int size) {
        this.brushSizePrev = size;
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
