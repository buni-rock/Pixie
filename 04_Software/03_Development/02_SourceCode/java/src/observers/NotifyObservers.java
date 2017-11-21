/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observers;

import java.util.Observable;

/**
 * The type Notify observers.
 *
 * @author Olimpia Popica
 */
public class NotifyObservers extends Observable {

    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
        clearChanged();
    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
        clearChanged();
    }

}
