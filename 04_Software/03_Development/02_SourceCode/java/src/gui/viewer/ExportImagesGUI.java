/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.viewer;

import common.Constants;
import common.Icons;
import common.Utils;
import gui.support.PanelResolution;
import library.Resize;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * The type Export images gui.
 *
 * @author Olimpia Popica
 */
public class ExportImagesGUI extends javax.swing.JDialog {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bGLeftImg;
    private javax.swing.ButtonGroup bGRightImg;
    private javax.swing.JButton jBClose;
    private javax.swing.JButton jBExport;
    private javax.swing.JButton jBExportBKGColor;
    private javax.swing.JLabel jLFileName;
    private javax.swing.JLabel jLLeftImage;
    private javax.swing.JLabel jLRightImage;
    private javax.swing.JLabel jLSaveToPath;
    private javax.swing.JPanel jPBackground;
    private javax.swing.JPanel jPChooseImg;
    private javax.swing.JPanel jPExport;
    private javax.swing.JPanel jPImagesBKG;
    private javax.swing.JPanel jPLeftImage;
    private javax.swing.JPanel jPLeftImg;
    private javax.swing.JPanel jPOptions;
    private javax.swing.JPanel jPRightImg;
    private javax.swing.JRadioButton jRBOrigImgLeft;
    private javax.swing.JRadioButton jRBOrigImgRight;
    private javax.swing.JRadioButton jRBSegmImgLeft;
    private javax.swing.JRadioButton jRBSegmentedImgRight;
    private javax.swing.JRadioButton jRBSemanticImgLeft;
    private javax.swing.JRadioButton jRBSemanticImgRight;
    private javax.swing.JTextField jTFFileName;
    private javax.swing.JTextField jTFPath;
    // End of variables declaration//GEN-END:variables

    /**
     * Horizontal inset for the preview panels.
     */
    private static final int HORIZONTAL_INSETS = 16;
    /**
     * Vertical inset for the preview panels.
     */
    private static final int VERTICAL_INSETS = 30;

    /**
     * The original image, the one being segmented (clear of modifications).
     */
    private final transient BufferedImage originalImg;

    /**
     * The image containing the drawn boxes.
     */
    private final transient BufferedImage segmentedImg;

    /**
     * The image containing the drawn boxes, resized to fit the preview panel.
     */
    private transient BufferedImage segmentedImgResized;

    /**
     * The image containing pixel segmentation results.
     */
    private final transient BufferedImage semanticImg;

    /**
     * The file type to which the images should be exported.
     */
    private final String preferredFileType;

    /**
     * Creates new form ExportImagesGUI.
     *
     * @param parent            the parent component of the dialog
     * @param originalImg       the original image, the one being segmented (clear of modifications)
     * @param segmentedImg      the image containing the drawn boxes
     * @param semanticImg       the image containing pixel segmentation results
     * @param filePath          the path to the selected fileF
     * @param fileName          the proposed name of the exported file
     * @param preferredFileType the preferred type of image to which the files should be exported
     * @param existsScribbleObj true if a scribble object exists and the export of the semantic image should be available
     */
    public ExportImagesGUI(java.awt.Frame parent,
            BufferedImage originalImg,
            BufferedImage segmentedImg,
            BufferedImage semanticImg,
            String filePath,
            String fileName,
            String preferredFileType,
            boolean existsScribbleObj) {
        super(parent, true);
        initComponents();

        // init local members
        this.originalImg = originalImg;
        this.segmentedImg = segmentedImg;
        this.semanticImg = semanticImg;
        this.preferredFileType = preferredFileType;

        // make sure the received data is correct
        if ((this.originalImg == null) || (this.segmentedImg == null) || (this.semanticImg == null)) {
            return;
        }

        // has to be done before loading the user preferences, in order to prevent selecting something which will not exist later on
        updateButtonGroup(existsScribbleObj);

        // set the path to the selected file
        jTFPath.setText(filePath);
        jTFFileName.setText(fileName);

        // add keyboard listener for implementing the escape and enter keys
        addKeyboardListener();

        // show the preview of the received images
        displayImages();

        // prepare the frame for display
        prepareFrame(parent);
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

        bGLeftImg = new javax.swing.ButtonGroup();
        bGRightImg = new javax.swing.ButtonGroup();
        jPBackground = new javax.swing.JPanel();
        jPExport = new javax.swing.JPanel();
        jPOptions = new javax.swing.JPanel();
        jLSaveToPath = new javax.swing.JLabel();
        jTFPath = new javax.swing.JTextField();
        jPChooseImg = new javax.swing.JPanel();
        jPRightImg = new javax.swing.JPanel();
        jRBOrigImgRight = new javax.swing.JRadioButton();
        jRBSegmentedImgRight = new javax.swing.JRadioButton();
        jRBSemanticImgRight = new javax.swing.JRadioButton();
        jPLeftImg = new javax.swing.JPanel();
        jRBOrigImgLeft = new javax.swing.JRadioButton();
        jRBSegmImgLeft = new javax.swing.JRadioButton();
        jRBSemanticImgLeft = new javax.swing.JRadioButton();
        jBExportBKGColor = new javax.swing.JButton();
        jLFileName = new javax.swing.JLabel();
        jTFFileName = new javax.swing.JTextField();
        jBClose = new javax.swing.JButton();
        jBExport = new javax.swing.JButton();
        javax.swing.JButton jBOpenFolder = new javax.swing.JButton();
        jPImagesBKG = new javax.swing.JPanel();
        jPLeftImage = new javax.swing.JPanel();
        jLLeftImage = new javax.swing.JLabel();
        javax.swing.JPanel jPRightImage = new javax.swing.JPanel();
        jLRightImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPBackground.setLayout(new java.awt.GridBagLayout());

        jPExport.setBackground(new java.awt.Color(255, 255, 255));
        jPExport.setLayout(new java.awt.GridBagLayout());

        jPOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
        jPOptions.setToolTipText("");
        jPOptions.setLayout(new java.awt.GridBagLayout());

        jLSaveToPath.setLabelFor(jTFPath);
        jLSaveToPath.setText("Save to path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPOptions.add(jLSaveToPath, gridBagConstraints);

        jTFPath.setEditable(false);
        jTFPath.setBackground(new java.awt.Color(255, 255, 255));
        jTFPath.setToolTipText("Go to Menu->Options->Configuration to change the default save path of images!!");
        jTFPath.setPreferredSize(new java.awt.Dimension(300, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jTFPath, gridBagConstraints);

        jPChooseImg.setBackground(new java.awt.Color(255, 255, 255));
        jPChooseImg.setLayout(new java.awt.GridBagLayout());

        jPRightImg.setBackground(new java.awt.Color(255, 255, 255));
        jPRightImg.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Image"));
        jPRightImg.setLayout(new java.awt.GridBagLayout());

        jRBOrigImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGRightImg.add(jRBOrigImgRight);
        jRBOrigImgRight.setText(Constants.ORIGINAL_IMG);
        jRBOrigImgRight.setActionCommand(Constants.ORIGINAL_IMG);
        jRBOrigImgRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBOrigImgRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPRightImg.add(jRBOrigImgRight, gridBagConstraints);

        jRBSegmentedImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGRightImg.add(jRBSegmentedImgRight);
        jRBSegmentedImgRight.setSelected(true);
        jRBSegmentedImgRight.setText(Constants.SEGMENTED_IMG);
        jRBSegmentedImgRight.setActionCommand(Constants.SEGMENTED_IMG);
        jRBSegmentedImgRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSegmentedImgRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPRightImg.add(jRBSegmentedImgRight, gridBagConstraints);

        jRBSemanticImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGRightImg.add(jRBSemanticImgRight);
        jRBSemanticImgRight.setText(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgRight.setActionCommand(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSemanticImgRightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPRightImg.add(jRBSemanticImgRight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPChooseImg.add(jPRightImg, gridBagConstraints);

        jPLeftImg.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftImg.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Image"));
        jPLeftImg.setLayout(new java.awt.GridBagLayout());

        jRBOrigImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGLeftImg.add(jRBOrigImgLeft);
        jRBOrigImgLeft.setSelected(true);
        jRBOrigImgLeft.setText(Constants.ORIGINAL_IMG);
        jRBOrigImgLeft.setActionCommand(Constants.ORIGINAL_IMG);
        jRBOrigImgLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBOrigImgLeftActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPLeftImg.add(jRBOrigImgLeft, gridBagConstraints);

        jRBSegmImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGLeftImg.add(jRBSegmImgLeft);
        jRBSegmImgLeft.setText(Constants.SEGMENTED_IMG);
        jRBSegmImgLeft.setActionCommand(Constants.SEGMENTED_IMG);
        jRBSegmImgLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSegmImgLeftActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPLeftImg.add(jRBSegmImgLeft, gridBagConstraints);

        jRBSemanticImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGLeftImg.add(jRBSemanticImgLeft);
        jRBSemanticImgLeft.setText(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgLeft.setActionCommand(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSemanticImgLeftActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPLeftImg.add(jRBSemanticImgLeft, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPChooseImg.add(jPLeftImg, gridBagConstraints);

        jBExportBKGColor.setBackground(new java.awt.Color(255, 255, 255));
        jBExportBKGColor.setText("Background Color");
        jBExportBKGColor.setToolTipText("Select the color behind the exported images!");
        jBExportBKGColor.setPreferredSize(new java.awt.Dimension(145, 23));
        jBExportBKGColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportBKGColorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPChooseImg.add(jBExportBKGColor, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jPChooseImg, gridBagConstraints);

        jLFileName.setLabelFor(jTFFileName);
        jLFileName.setText("File Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPOptions.add(jLFileName, gridBagConstraints);

        jTFFileName.setText("JoinedImgs.png");
        jTFFileName.setPreferredSize(new java.awt.Dimension(300, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPOptions.add(jTFFileName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExport.add(jPOptions, gridBagConstraints);

        jBClose.setBackground(new java.awt.Color(255, 255, 255));
        jBClose.setText("Close");
        jBClose.setPreferredSize(new java.awt.Dimension(90, 23));
        jBClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExport.add(jBClose, gridBagConstraints);

        jBExport.setBackground(new java.awt.Color(255, 255, 255));
        jBExport.setText("Export");
        jBExport.setPreferredSize(new java.awt.Dimension(90, 23));
        jBExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExport.add(jBExport, gridBagConstraints);

        jBOpenFolder.setBackground(new java.awt.Color(255, 255, 255));
        jBOpenFolder.setText("Open Folder");
        jBOpenFolder.setMinimumSize(new java.awt.Dimension(108, 32));
        jBOpenFolder.setPreferredSize(new java.awt.Dimension(106, 23));
        jBOpenFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBOpenFolderActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExport.add(jBOpenFolder, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPBackground.add(jPExport, gridBagConstraints);

        jPImagesBKG.setBackground(new java.awt.Color(255, 255, 255));
        jPImagesBKG.setLayout(new java.awt.GridBagLayout());

        jPLeftImage.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Image"));
        jPLeftImage.setToolTipText("Click on the image for preview.");
        jPLeftImage.setPreferredSize(new java.awt.Dimension(350, 275));
        jPLeftImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPLeftImageMouseClicked(evt);
            }
        });
        jPLeftImage.setLayout(new java.awt.GridBagLayout());
        jPLeftImage.add(jLLeftImage, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPImagesBKG.add(jPLeftImage, gridBagConstraints);

        jPRightImage.setBackground(new java.awt.Color(255, 255, 255));
        jPRightImage.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Image"));
        jPRightImage.setToolTipText("Click on the image for preview.");
        jPRightImage.setPreferredSize(new java.awt.Dimension(350, 275));
        jPRightImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPRightImageMouseClicked(evt);
            }
        });
        jPRightImage.setLayout(new java.awt.GridBagLayout());
        jPRightImage.add(jLRightImage, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPImagesBKG.add(jPRightImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jPImagesBKG, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        getContentPane().add(jPBackground, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportActionPerformed
        exportSelectedImages();
    }//GEN-LAST:event_jBExportActionPerformed

    private void jBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCloseActionPerformed
        dispose();
    }//GEN-LAST:event_jBCloseActionPerformed

    private void jRBOrigImgLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBOrigImgLeftActionPerformed
        updateLeftImage();
    }//GEN-LAST:event_jRBOrigImgLeftActionPerformed

    private void jRBSegmImgLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSegmImgLeftActionPerformed
        updateLeftImage();
    }//GEN-LAST:event_jRBSegmImgLeftActionPerformed

    private void jRBSemanticImgLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSemanticImgLeftActionPerformed
        updateLeftImage();
    }//GEN-LAST:event_jRBSemanticImgLeftActionPerformed

    private void jRBOrigImgRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBOrigImgRightActionPerformed
        updateRightImage();
    }//GEN-LAST:event_jRBOrigImgRightActionPerformed

    private void jRBSegmentedImgRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSegmentedImgRightActionPerformed
        updateRightImage();
    }//GEN-LAST:event_jRBSegmentedImgRightActionPerformed

    private void jRBSemanticImgRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSemanticImgRightActionPerformed
        updateRightImage();
    }//GEN-LAST:event_jRBSemanticImgRightActionPerformed

    private void jBExportBKGColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportBKGColorActionPerformed
        // select a new color for the in between background of the exported image
        Color newColor = JColorChooser.showDialog(
                ExportImagesGUI.this,
                "Choose a Color",
                jPImagesBKG.getBackground());

        // preview the color on the panel with the images
        jPImagesBKG.setBackground(newColor);
    }//GEN-LAST:event_jBExportBKGColorActionPerformed

    private void jBOpenFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBOpenFolderActionPerformed
        Utils.openFileFolderInExplorer(jTFPath.getText());
    }//GEN-LAST:event_jBOpenFolderActionPerformed

    private void jPLeftImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPLeftImageMouseClicked
        previewImage(bGLeftImg.getSelection().getActionCommand());
    }//GEN-LAST:event_jPLeftImageMouseClicked

    private void jPRightImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPRightImageMouseClicked
        previewImage(bGRightImg.getSelection().getActionCommand());
    }//GEN-LAST:event_jPRightImageMouseClicked

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
            java.util.logging.Logger.getLogger(ExportImagesGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>
        try {
            BufferedImage imageOrig = ImageIO.read(new File(Icons.SPLASH_SCREEN_PATH));
            BufferedImage imageSeg = ImageIO.read(new File(Icons.EXCLAMATION_ICON_PATH));
            BufferedImage imageRes = ImageIO.read(new File(Icons.REINDEER_ICON_PATH));

            /* Create and display the dialog */
            java.awt.EventQueue.invokeLater(() -> {
                ExportImagesGUI dialog = new ExportImagesGUI(null, imageOrig, imageSeg, imageRes, Icons.SPLASH_SCREEN_PATH, "JoinedImages", "PNG", true);
                dialog.setSegmentedImgResized(imageSeg);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            });
        } catch (IOException ex) {
            Logger.getLogger(ExportImagesGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>
    }

    /**
     * Add the images to preview, for the user to know which is which and not
     * get confused by the naming.
     */
    private void displayImages() {
        updateLeftImage();

        updateRightImage();
    }

    /**
     * Add keyboard listener to the dialog window.
     */
    private void addKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher((KeyEvent e) -> {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_ESCAPE) || (key == KeyEvent.VK_ENTER)) {
                dispose();
            }

            return false;
        });
    }

    /**
     * Prepare the frame to be displayed.
     *
     * @param parent the parent component of the dialog.
     */
    private void prepareFrame(java.awt.Frame parent) {
        this.setTitle("Export Joined Images");

        this.pack();

        this.setResizable(false);

        this.setLocationRelativeTo(parent);
    }

    /**
     * Export the selected images from the previewed ones. It exports the images
     * side by side, with the specified size option (keep the original size of
     * each image or resize to same size).
     */
    private void exportSelectedImages() {
        // get the left selection
        BufferedImage leftBI = getSelectedImage(bGLeftImg.getElements());

        // get the left selection
        BufferedImage rigthBI = getSelectedImage(bGRightImg.getElements());

        // make sure the path exists
        Utils.createFolderPath(jTFPath.getText());

        // export the images
        Utils.exportTwoImages(leftBI, rigthBI, jTFPath.getText() + File.separator + jTFFileName.getText(), preferredFileType, jPImagesBKG.getBackground());
    }

    /**
     * Based on the selected radio button group, get the selected image and
     * return it (original image, segmented etc.).
     *
     * @param elements the list of radio buttons of the radio button group which
     * has to be analyzed in order to get the correct image
     * @return the image specified by the user in the radio button group
     */
    private BufferedImage getSelectedImage(Enumeration<AbstractButton> elements) {
        // go over all buttons and see which one is selected
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            if (button.isSelected()) {
                // get the correct image based on the name of the radio button
                return selectImage(button.getText());
            }
        }

        return originalImg;
    }

    /**
     * Return the image specified by the input text.
     *
     * @param text the name of the image to be returned
     * @return the image corresponding to the input text
     */
    private BufferedImage selectImage(String text) {
        switch (text) {
            case Constants.ORIGINAL_IMG:
                return originalImg;

            case Constants.SEGMENTED_IMG:
                return segmentedImg;

            case Constants.SEGMENTED_IMG_RESIZED:
                if (segmentedImgResized == null) {
                    return segmentedImg;
                }
                return segmentedImgResized;

            case Constants.SEMANTIC_SEGMENTATION_IMG:
                return semanticImg;

            default:
                return originalImg;
        }
    }

    /**
     * Set the image being displayed on the left panel.
     *
     * @param image the image displayed on the left panel
     */
    private void setLeftImage(BufferedImage image) {
        Dimension fitSize = PanelResolution.computeOptimalPanelSize(new Dimension(image.getWidth(), image.getHeight()), getPreviewSize());
        Resize resize = new Resize(image.getWidth(), image.getHeight(), fitSize.width, fitSize.height);
        ImageIcon imageIcon = new ImageIcon(resize.resizeImage(image));
        jLLeftImage.setIcon(imageIcon);
    }

    /**
     * Set the image being displayed on the right panel.
     *
     * @param image the image displayed on the right panel
     */
    private void setRightImage(BufferedImage image) {
        Dimension fitSize = PanelResolution.computeOptimalPanelSize(new Dimension(image.getWidth(), image.getHeight()), getPreviewSize());
        Resize resize = new Resize(image.getWidth(), image.getHeight(), fitSize.width, fitSize.height);
        ImageIcon imageIcon = new ImageIcon(resize.resizeImage(image));
        jLRightImage.setIcon(imageIcon);
    }

    /**
     * Update the left panel with the selected image.
     */
    private void updateLeftImage() {
        String selection = bGLeftImg.getSelection().getActionCommand().equals(Constants.SEGMENTED_IMG) ? Constants.SEGMENTED_IMG_RESIZED : bGLeftImg.getSelection().getActionCommand();
        setLeftImage(selectImage(selection));
    }

    /**
     * Update the right panel with the selected image.
     */
    private void updateRightImage() {
        String selection = bGRightImg.getSelection().getActionCommand().equals(Constants.SEGMENTED_IMG) ? Constants.SEGMENTED_IMG_RESIZED : bGRightImg.getSelection().getActionCommand();
        setRightImage(selectImage(selection));
    }

    /**
     * Update the button group in such way as to have in the group just the
     * correct buttons and disable the fields not needed.
     */
    private void updateButtonGroup(boolean enable) {
        jRBSemanticImgLeft.setEnabled(enable);
        jRBSemanticImgRight.setEnabled(enable);
    }

    /**
     * Preview the selected image from the wanted panel. Display the original
     * image, in original size.
     *
     * @param selection the image to be previewed: original, segmented or
     * semantic.
     */
    private void previewImage(String selection) {
        BufferedImage previewImg = selectImage(selection);

        if (previewImg != null) {
            new ImagePreview(this, true, previewImg, selection).setVisible(true);
        }
    }

    /**
     * Return the size of the available preview space (it consists from the size
     * of the preview panel minus the internal insets).
     *
     * @return the available dimension of the preview panel (smaller than the actual panel size)
     */
    public Dimension getPreviewSize() {
        return new Dimension(jPLeftImage.getWidth() - HORIZONTAL_INSETS,
                jPLeftImage.getHeight() - VERTICAL_INSETS);
    }

    /**
     * Set the resized image of the segmented image, ready to be displayed on
     * the preview panel.
     *
     * @param segmentedImgResized the image to be displayed on the preview, without any other changes
     */
    public void setSegmentedImgResized(BufferedImage segmentedImgResized) {
        this.segmentedImgResized = segmentedImgResized;
        // refresh the display (for the case the segmented image is showing)
        displayImages();
    }

}
