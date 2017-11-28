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

import observers.NotifyObservers;
import observers.ObservedActions;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observer;
import javax.swing.JButton;

/**
 * Creates a custom button which can have the background color changed. The
 * focus state is not painted.
 *
 * @author Olimpia Popica
 */
public class CustomColorButton extends JButton implements MouseListener {

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * The color of the background of the object.
     */
    private Color bkgColor;

    /**
     * Creates a new button component which can have the background changed,
     * independent of the look and feel settings.
     *
     * @param bkgColor  - the color of the background of the button
     * @param fontColor - the color of the text written on the button
     * @param text      - the text to be written on the button
     */
    public CustomColorButton(Color bkgColor, Color fontColor, String text) {
        this.bkgColor = bkgColor;

        init(fontColor, text);
    }

    /**
     * Init the specific functionalities of the button.
     */
    private void init(Color fontColor, String text) {
        this.setForeground(fontColor);

        this.setText(text);

        this.setContentAreaFilled(false);

        this.setFocusPainted(false);

        this.addMouseListener(this);
    }

    /**
     * Over-painting component, so it can have different colors.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(bkgColor);

        // draw the button rounded opaque
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // for high quality
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);

        // draw the border
        g2d.setColor(bkgColor.darker().darker());
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
        super.paintComponent(g);
    }

    /**
     * Set the color used for the drawing of the background.
     *
     * @param bkgColor - the color used to paint the background of the button
     */
    public void setBkgColor(Color bkgColor) {
        this.bkgColor = bkgColor;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // not needed for now
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2 && !e.isConsumed()) {
            e.consume();
            observable.notifyObservers(ObservedActions.Action.EDIT_OBJECT_ACTION);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // not needed for now
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // not needed for now
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // not needed for now
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
