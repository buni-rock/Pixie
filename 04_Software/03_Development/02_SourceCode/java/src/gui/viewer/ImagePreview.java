/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.viewer;

import gui.support.ScreenResolution;
import java.awt.Rectangle;
import library.Resize;
import observers.NotifyObservers;
import observers.ObservedActions;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Observer;
import javax.swing.ImageIcon;

/**
 * The type Image preview.
 *
 * @author Olimpia Popica
 */
public class ImagePreview extends javax.swing.JDialog {

    /**
     * The original image of the object in the original size.
     */
    private transient BufferedImage origImg;

    /**
     * The image used to be displayed. It is computed out of the original image
     * in order to keep the details and not loose data. Image resized from
     * panelImg. It is used for zooming in/out
     */
    private transient BufferedImage workImg;

    private transient Resize resize;

    /**
     * The resolution of the current screen where the application is showed.
     */
    private transient ScreenResolution screenRes;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * The title of the frame.
     */
    private final String frameTitle;

    /**
     * Creates new form ImagePreview
     *
     * @param parent     - the parent of the dialog form
     * @param modal      - cannot click in any other place while the form is active
     * @param displayImg - the image to be displayed on the form
     * @param frameTitle - the title of the frame
     */
    public ImagePreview(java.awt.Window parent, boolean modal, BufferedImage displayImg, String frameTitle) {
        super(parent, (modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS));
        initComponents();

        initResolutions();

        this.frameTitle = frameTitle;

        this.resize = new Resize(1.0, 1.0);
        setImage(displayImg);

        jBOption.setVisible(false);

        initOtherVariables(parent);
    }

    /**
     * Creates new form ImagePreview
     *
     * @param parent     - the parent of the dialog form
     * @param modal      - cannot click in any other place while the form is active
     * @param displayImg - the image to be displayed on the form
     * @param frameTitle - the title of the frame
     * @param addButton  - true if a button shall be added on the interface, false if just the image shall be displayed
     * @param buttonText - the text to be displayed on the button
     */
    public ImagePreview(java.awt.Frame parent, boolean modal, BufferedImage displayImg, String frameTitle, Boolean addButton, String buttonText) {
        super(parent, modal);
        initComponents();

        initResolutions();

        this.frameTitle = frameTitle;

        prepareImagePreviewPanel(displayImg);

        // add the button on the interface if it is wanted
        jBOption.setText(buttonText);
        jBOption.setVisible(addButton);

        initOtherVariables(parent);
    }

    private void prepareImagePreviewPanel(BufferedImage img) {
        this.origImg = img;

        /* using the ratio based only on the height, avoids the stretching of frames with 
           an aspect ratio of 4:3(640x480)*/
        this.resize = new Resize(img.getWidth(), img.getHeight());

        // copy the original image in the work image
        workImg = resize.resizeImage(img);

        // display the image
        ImageIcon iconLogo = new ImageIcon(workImg);
        jLImagePreview.setIcon(iconLogo);
    }

    private void initOtherVariables(java.awt.Window parent) {
        setFrameTitle();

        addKeyboardListener();

        // resize the frame and position it in the middle of the screen
        prepareFrame();
        setLocationRelativeTo(parent);
    }

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
    private void prepareFrame() {
        this.setResizable(false);
        this.pack();
    }

    /**
     * Changes the displayed image.
     *
     * @param image - the image to be displayed on the panel
     */
    public final void setImage(BufferedImage image) {
        origImg = image;
        // copy the original image in the work image
        workImg = resize.resizeImage(origImg);

        // display the image
        ImageIcon iconLogo = new ImageIcon(workImg);
        jLImagePreview.setIcon(iconLogo);
    }

    /**
     * Sets the title of the frame with the current size of the image and the
     * size of the work image (which can be different due to the zooming).
     */
    private void setFrameTitle() {
        String title = "";

        // add the size of the object (original)
        title += this.frameTitle + " " + origImg.getWidth() + "x" + origImg.getHeight();

        // if the work image has different size, show it in brackets (for zooming)
        if ((workImg.getWidth() != origImg.getWidth())
                || (workImg.getHeight() != origImg.getHeight())) {

            // compute the size of the box with the zooming factor
            Rectangle resizedOuterBox = resize.resizeBox(new Rectangle(0, 0, origImg.getWidth(), origImg.getHeight()));

            // display the zoomed size
            title += " (" + resizedOuterBox.width + "x" + resizedOuterBox.height + ")";
        }

        setTitle(title);
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
     * Close the dialog in a safe way.
     */
    private void closeWindow() {
        // the row was selected (it is known); close the dialog and release resources
        dispose();
    }

    /**
     * Add keyboard listener to the dialog window.
     */
    private void addKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ESCAPE) {
                closeWindow();
            }

            return false;
        });
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

        jPBKG = new javax.swing.JPanel();
        jBOption = new javax.swing.JButton();
        jLImagePreview = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });

        jPBKG.setBackground(new java.awt.Color(255, 255, 255));
        jPBKG.setLayout(new java.awt.GridBagLayout());

        jBOption.setText("jButton1");
        jBOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBOptionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBKG.add(jBOption, gridBagConstraints);

        jLImagePreview.setBackground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPBKG.add(jLImagePreview, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBKG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBKG, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved

        int notches = evt.getWheelRotation();

        if (notches < 0) {
            // Mouse wheel moved UP = zoom in
            // check if the resize is not generating a bigger image than the screen
            Dimension origImgDimension = new Dimension(origImg.getWidth(), origImg.getHeight());
            if (resize.isSizeIncreaseOK(origImgDimension, screenRes.getScreenResolution())) {
                resize.incrementWidthHeight(origImgDimension);
                // resize image with the ratio relative to the original image, in order to prevent data loose
                workImg = resize.resizeImage(origImg);
            }

        } else {
            Dimension origImgDimension = new Dimension(-origImg.getWidth(), -origImg.getHeight());
            // Mouse wheel moved DOWN = zoom out
            if (resize.isSizeDecreaseOK(origImgDimension)) {
                resize.incrementWidthHeight(origImgDimension);
                // resize image with the ratio relative to the original image, in order to prevent data loose
                workImg = resize.resizeImage(origImg);
            }
        }

        setFrameTitle();

        // draw the image
        ImageIcon iconLogo = new ImageIcon(workImg);
        jLImagePreview.setIcon(iconLogo);

        // resize the frame and position it in the middle of the screen
        prepareFrame();
        setLocationRelativeTo(null);
    }//GEN-LAST:event_formMouseWheelMoved

    private void jBOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBOptionActionPerformed
        observable.notifyObservers(ObservedActions.Action.FILTER_OBJECT_MAP);
    }//GEN-LAST:event_jBOptionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBOption;
    private javax.swing.JLabel jLImagePreview;
    private javax.swing.JPanel jPBKG;
    // End of variables declaration//GEN-END:variables

}
