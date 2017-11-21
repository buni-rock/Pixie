/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.viewer;

import common.Constants;
import common.Icons;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

/**
 * The type About.
 *
 * @author Olimpia Popica
 */
public class About extends javax.swing.JDialog {

    /**
     * Creates new form About
     *
     * @param parent - the parent of the dialog form
     * @param modal  - cannot click in any other place while the form is active
     * @param title  - the title of the window
     */
    public About(java.awt.Frame parent, boolean modal, String title) {
        super(parent, modal);
        initComponents();

        if (title.startsWith("About Pixie")) {
            initGUIComponents(Icons.REINDEER_SMALL_ICON);
            generateAboutContent();
        } else if (title.startsWith("Pixie Hotkeys")) {
            initGUIComponents(Icons.REINDEER_ICON);
            generateHotkeysContent();
        }

        addKeyboardListener();

        formatFrame(parent, title);
    }

    private void initGUIComponents(ImageIcon icon) {
        ImageIcon iconLogo = icon;
        jLPixieLogo.setIcon(iconLogo);
    }

    /**
     * Generates the content of the about window.
     */
    private void generateAboutContent() {
        jEPAboutText.setContentType("text/html");

        String aboutPixie = "<html>"
                + "<div style=\"font-family:Calibri;font-size:14;\">"
                + "<p>"
                + "<b>" + "Pixie" + "</b>" + " is an application which offers support for labeling purposes:"
                + "<br>" + "- bounding box labeling"
                + "<br>" + "- pixel labeling"
                + "<br>"
                + "Pixie was developed by " + " <b>" + "XXX" + "</b>" + "."
                + "</div>"
                + "<div style=\"font-family:Consolas;font-size:12;\">"
                + "<p>"
                + "<table>"
                + "<tr><td>" + "Software Version: " + "</td><td>" + Constants.SOFTWARE_VERSION + "</td></tr>"
                + "<tr><td>" + "Java version: " + "</td><td>" + System.getProperty("java.version") + "</td></tr>"
                + "<tr><td>" + "JCuda version: " + "</td><td>" + Constants.JCUDA_VERSION + "</td></tr>"
                + "<tr><td>" + "JOCL version: " + "</td><td>" + Constants.JOCL_VERSION + "</td></tr>"
                + "<tr><td>" + "JPen version: " + "</td><td>" + Constants.JPEN_VERSION + "</td></tr>"
                + "<tr><td>" + "Hamcrest version: " + "</td><td>" + Constants.HAMCREST_VERSION + "</td></tr>"
                + "<tr><td>" + "JUnit version: " + "</td><td>" + Constants.JUNIT_VERSION + "</td></tr>"
                + "<tr><td>" + "OpenCV version: " + "</td><td>" + Constants.OPENCV_VERSION + "</td></tr>"
                + "</table>"
                + "</div>"
                + "</html>";
        jEPAboutText.setText(aboutPixie);

    }

    /**
     * Generates the content of the hotkeys window.
     */
    private void generateHotkeysContent() {
        jEPAboutText.setContentType("text/html");

        String aboutPixie
                = "<html>"
                + "<div style=\"font-family:Calibri;font-size:14;\">"
                + "<p>"
                + "The most useful shotrcuts in " + "<b>" + "Pixie" + "</b>" + ":"
                + "</div>"
                + "<div style=\"font-family:Consolas;font-size:12;\">"
                + "<p>"
                + "<table width=100%>"
                + "<tr><td>" + "Load file" + "</td><td align=center>" + "Ctrl+F" + "</td>"
                + "<td>" + "Cancel window" + "</td>" + "<td align=center>" + "ESC" + "</td>" + "</tr>"
                + "<tr><td>" + "Next frame" + "</td><td align=center>" + "Arrow Right" + "</td>"
                + "<td>" + "Prev frame" + "</td>" + "<td align=center>" + "Arrow Left" + "</td>" + "</tr>"
                + "<tr><td>" + "Run matting" + "</td><td align=center>" + "E" + "</td>"
                + "<td>" + "Remove object" + "</td>" + "<td align=center>" + "Delete" + "</td>" + "</tr>"
                + "<tr><td>" + "Increase brush" + "</td><td align=center>" + ")" + "</td>"
                + "<td>" + "Decrease brush" + "</td>" + "<td align=center>" + "(" + "</td>" + "</tr>"
                + "<tr><td>" + "Select bkg" + "</td><td align=center>" + "Ctrl+Z" + "</td>"
                + "<td>" + "Select object" + "</td>" + "<td align=center>" + "V" + "</td>" + "</tr>"
                + "<tr><td>" + "Eraser" + "</td><td align=center>" + "B" + "</td>"
                + "<td>" + "Save object" + "</td>" + "<td align=center>" + "Enter" + "</td>" + "</tr>"
                + "<tr><td>" + "Move object left" + "</td><td align=center>" + "A" + "</td>"
                + "<td>" + "Move object right" + "</td>" + "<td align=center>" + "D" + "</td>" + "</tr>"
                + "<tr><td>" + "Move object up" + "</td><td align=center>" + "W" + "</td>"
                + "<td>" + "Move object down" + "</td>" + "<td align=center>" + "S" + "</td>" + "</tr>"
                + "<tr><td>" + "Increase size left" + "</td><td align=center>" + "Alt+A" + "</td>"
                + "<td>" + "Increase size right" + "</td>" + "<td align=center>" + "Alt+D" + "</td>" + "</tr>"
                + "<tr><td>" + "Increase size up" + "</td><td align=center>" + "Alt+W" + "</td>"
                + "<td>" + "Increase size down" + "</td>" + "<td align=center>" + "Alt+S" + "</td>" + "</tr>"
                + "<tr><td>" + "Decrease size left" + "</td><td align=center>" + "Ctrl+A" + "</td>"
                + "<td>" + "Decrease size right" + "</td>" + "<td align=center>" + "Ctrl+D" + "</td>" + "</tr>"
                + "<tr><td>" + "Decrease size up" + "</td><td align=center>" + "Ctrl+W" + "</td>"
                + "<td>" + "Decrease size down" + "</td>" + "<td align=center>" + "Ctrl+S" + "</td>" + "</tr>"
                + "<tr><td>" + "Decrease size box" + "</td><td align=center>" + "Alt+Z" + "</td>"
                + "<td>" + "Increase size box" + "</td>" + "<td align=center>" + "Alt+X" + "</td>" + "</tr>"
                + "</table>"
                + "</div>"
                + "</html>";
        jEPAboutText.setText(aboutPixie);

    }

    /**
     * Add keyboard listener to the dialog window.
     */
    private void addKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_ESCAPE) {
                dispose();
            }

            return false;
        });
    }

    /**
     * Code related to the frame like: pack, set location, title etc.
     */
    private void formatFrame(java.awt.Frame parent, String title) {
        setTitle(title);

        // do not allow the user to change the size of the window
        this.setResizable(false);
        this.pack();

        setLocationRelativeTo(parent);
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

        jPBackground = new javax.swing.JPanel();
        jLPixieLogo = new javax.swing.JLabel();
        jSPAboutText = new javax.swing.JScrollPane();
        jEPAboutText = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPBackground.setLayout(new java.awt.GridBagLayout());

        jLPixieLogo.setBackground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPBackground.add(jLPixieLogo, gridBagConstraints);

        jSPAboutText.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jSPAboutText.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jEPAboutText.setEditable(false);
        jSPAboutText.setViewportView(jEPAboutText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jSPAboutText, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPBackground, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The entry point of application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(About.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            About dialog = new About(new javax.swing.JFrame(), true, "Pixie Hotkeys"/* "About Pixie"*/);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEPAboutText;
    private javax.swing.JLabel jLPixieLogo;
    private javax.swing.JPanel jPBackground;
    private javax.swing.JScrollPane jSPAboutText;
    // End of variables declaration//GEN-END:variables

}
