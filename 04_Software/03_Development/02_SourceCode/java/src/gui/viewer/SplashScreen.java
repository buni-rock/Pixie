/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.viewer;

import common.Icons;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

////TO DO: CHOOSE THE BEST IMPLEMENTATION FOR THE SPLASH SCREEN!

/**
 * The type Splash screen.
 *
 * @author Olimpia Popica
 */
public final class SplashScreen extends JWindow {

    /**
     * The Border layout 1.
     */
    BorderLayout borderLayout1 = new BorderLayout();
    /**
     * The Image label.
     */
    JLabel imageLabel = new JLabel();
    /**
     * The South panel.
     */
    JPanel southPanel = new JPanel();
    /**
     * The South panel flow layout.
     */
    FlowLayout southPanelFlowLayout = new FlowLayout();
    /**
     * The Progress bar.
     */
    JProgressBar progressBar = new JProgressBar();
    /**
     * The Image icon.
     */
    ImageIcon imageIcon;

    /**
     * Instantiates a new Splash screen.
     *
     * @param imageIcon the image icon
     */
    public SplashScreen(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;

        try {
            jbInit();
        } catch (Exception ex) {
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Jb init.
     */
// note - this class created with JBuilder
    void jbInit() {
        imageLabel.setIcon(imageIcon);
        this.getContentPane().setLayout(borderLayout1);
        southPanel.setLayout(southPanelFlowLayout);
        southPanel.setBackground(Color.BLACK);
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);
        southPanel.add(progressBar, null);

        toFront();
        setAlwaysOnTop(true);

        this.pack();
        setLocationRelativeTo(null);
    }

    /**
     * Sets progress max.
     *
     * @param maxProgress the max progress
     */
    public void setProgressMax(int maxProgress) {
        progressBar.setMaximum(maxProgress);
    }

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    public void setProgress(int progress) {
        final int theProgress = progress;
        SwingUtilities.invokeLater(() -> progressBar.setValue(theProgress));
    }

    /**
     * Sets progress.
     *
     * @param message  the message
     * @param progress the progress
     */
    public void setProgress(String message, int progress) {
        final int theProgress = progress;
        final String theMessage = message;
        setProgress(progress);
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(theProgress);
            setMessage(theMessage);
        });
    }

    /**
     * Sets screen visible.
     *
     * @param b the b
     */
    public void setScreenVisible(boolean b) {
        final boolean boo = b;
        SwingUtilities.invokeLater(() -> setVisible(boo));
    }

    private void setMessage(String message) {
        if (message == null) {
            message = "";
            progressBar.setStringPainted(false);
        } else {
            progressBar.setStringPainted(true);
        }
        progressBar.setString(message);
    }

    /**
     * Start splash.
     */
    public static void startSplash() {
        Thread threadSplash = new Thread(() -> {
            SplashScreen splash = new SplashScreen(Icons.SPLASH_SCREEN);
            splash.setVisible(true);
            String[] message = {"Loading modules", "Configuring app", "Testing network", "Checking connection", "Searching database"};
            for (int x1 = 0; x1 < 100; x1++) {
                try {
                    Thread.sleep(50);
                    splash.setProgress(message[(x1 / 10) % 5], x1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
                    // clean up state
                    Thread.currentThread().interrupt();
                }
            }
            splash.dispose();
        });

        threadSplash.start();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        // Throw a nice little title page up on the screen first
        SplashScreen splash = new SplashScreen(Icons.SPLASH_SCREEN);
        // Normally, we'd call splash.showSplash() and get on with the program.
        // But, since this is only a test...
        splash.setVisible(true);

        String[] message = {"Loading modules", "Configuring app", "Testing network", "Checking connection", "Searching database"};

        for (int x = 0; x < 100; x++) {
            try {
                Thread.sleep(50);
                splash.setProgress(message[(x / 10) % 5], x);
            } catch (InterruptedException ex) {
                Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
                // clean up state
                Thread.currentThread().interrupt();
            }
        }
    }
}
