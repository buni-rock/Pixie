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
package paintpanels;

import common.Icons;
import gui.viewer.ImagePreview;
import library.Resize;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Image panel.
 *
 * @author Olimpia Popica
 */
public class ImagePanel extends JPanel implements MouseListener {

    /**
     * The image to be displayed.
     */
    private transient BufferedImage image;

    /**
     * The offset/inset on the left of the image, inside the panel.
     */
    private int left;

    /**
     * The offset/inset on the top of the image, inside the panel.
     */
    private int top;

    /**
     * logger instance
     */
    private final transient Logger log = LoggerFactory.getLogger("paintpanels.ImagePanel");

    /**
     * Create a new panel with an image specified by the path parameter.
     *
     * @param filePath the path where the image to be loaded is stored
     */
    public ImagePanel(String filePath) {
        image = readImage(filePath);

        formatPanel();
    }

    /**
     * Create a new image panel with the specified image.
     *
     * @param bi the image to be displayed
     */
    public ImagePanel(BufferedImage bi) {
        image = bi;

        formatPanel();
    }

    /**
     * Create a new image panel with the specified image and the specified
     * resize ratio.
     *
     * @param bi     - the input image to be displayed
     * @param resize - the resize ratio
     */
    public ImagePanel(BufferedImage bi, Resize resize) {
        image = resize.resizeImage(bi);

        formatPanel();

    }

    /**
     * Format the panel; prepare it to be displayed.
     */
    private void formatPanel() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        setPreferredSize(size);
        setSize(size);

    }

    /**
     * Open the file from the specified path and read the image.
     *
     * @param filePath the path to the image on disk
     */
    private BufferedImage readImage(String filePath) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(filePath));

        } catch (IOException ex) {
            log.error("Image not found");
            log.debug("Image not found {}", ex);
        }
        return img;
    }

    /**
     * Set the panel as a titled border panel. Set the title as specified. Set
     * the inner insets of the panel as specified in the insets vecor.
     *
     * @param title  - the title of the bordered panel
     * @param insets - the inner insets of the panel (top-left-bottom-right)
     */
    public void setPanelTitle(String title, int[] insets) {
        top = insets[0];
        left = insets[1];
        int bottom = insets[2];
        int right = insets[3];

        this.setBorder(BorderFactory.createTitledBorder(title));

        Dimension size = new Dimension(image.getWidth() + left + right, image.getHeight() + top + bottom);
        setPreferredSize(size);
        setSize(size);
    }

    /**
     * Change the existing image of the panel with a new one.
     *
     * @param img the image which will replace the existing image
     */
    public void setImage(BufferedImage img) {
        image = img;
        formatPanel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, left, top, null); // see javadoc for more info on the parameters            
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // create the panel with the image
        ImagePanel imgPanel = new ImagePanel(Icons.SPLASH_SCREEN_PATH);

        // create the frame which will display the panel        
        JFrame frame = new JFrame("Image Panel Preview");

        frame.setLayout(new FlowLayout());

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        imgPanel.setPanelTitle("Pixie", new int[]{15, 5, 5, 5});

        // add the panel to the frame
        frame.add(imgPanel);

        // prepare frame for display
        frame.pack();

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        new ImagePreview(null, true, image, "Image Preview").setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        log.trace("Mouse pressed!");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        log.trace("Mouse released!");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        log.trace("Mouse entered!");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        log.trace("Mouse exited!");
    }
}
