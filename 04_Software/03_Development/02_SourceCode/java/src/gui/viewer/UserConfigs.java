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

import common.Constants;
import common.UserPreferences;
import common.Utils;
import observers.NotifyObservers;
import observers.ObservedActions;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Observer;
import javax.swing.JRadioButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type User configs.
 *
 * @author Olimpia Popica
 */
public class UserConfigs extends javax.swing.JDialog {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bGExportJoinLeftImg;
    private javax.swing.ButtonGroup bGExportJoinRightImg;
    private javax.swing.ButtonGroup bGExtensions;
    private javax.swing.ButtonGroup bgScreenDPIs;
    private javax.swing.JCheckBox jCBAutoImgExport;
    private javax.swing.JCheckBox jCBFrameAnnotCheck;
    private javax.swing.JCheckBox jCBJoinedImg;
    private javax.swing.JCheckBox jCBObjAlphaHighlight;
    private javax.swing.JCheckBox jCBObjectAttribCheck;
    private javax.swing.JCheckBox jCBOrigImg;
    private javax.swing.JCheckBox jCBResultImg;
    private javax.swing.JCheckBox jCBSaveFrameMap;
    private javax.swing.JCheckBox jCBSegmImg;
    private javax.swing.JCheckBox jCBShowCrops;
    private javax.swing.JCheckBox jCBShowScribbles;
    private javax.swing.JFormattedTextField jFTFDPIValue;
    private javax.swing.JFormattedTextField jFTFObjAlphaVal;
    private javax.swing.JLabel jLFirstStartScreenMsg;
    private javax.swing.JLabel jLHelpImage;
    private javax.swing.JLabel jLHelpText;
    private javax.swing.JLabel jLInfoDefaultVal;
    private javax.swing.JLabel jLObjAlphaVal;
    private javax.swing.JPanel jPDefaultValues;
    private javax.swing.JPanel jPGeneralCfg;
    private javax.swing.JPanel jPLeftImg;
    private javax.swing.JPanel jPRightImg;
    private javax.swing.JPanel jPScreenCfg;
    private javax.swing.JRadioButton jRBOrigImgLeft;
    private javax.swing.JRadioButton jRBOrigImgRight;
    private javax.swing.JRadioButton jRBSegmImgLeft;
    private javax.swing.JRadioButton jRBSegmentedImgRight;
    private javax.swing.JRadioButton jRBSemanticImgLeft;
    private javax.swing.JRadioButton jRBSemanticImgRight;
    private javax.swing.JTextField jTFImgExportPath;
    private javax.swing.JTabbedPane jTPUserCfg;
    // End of variables declaration//GEN-END:variables

    /**
     * The default message which will appear in the help section.
     */
    private static final String DEFAULT_HELP_MSG = "Keep the mouse over a configuration for further explanations.";
    /**
     * Message explaining the highlight of the labeled objects.
     */
    private static final String MSG_OBJ_HIGHLIGHT = "Enables the drawing of a transparent overlay, on top of the labeled object.";
    /**
     * Message explaining the transparency of the labeled objects.
     */
    private static final String MSG_TRANSPARENCY = "Set the transparency value for object overlays.";
    /**
     * Message explaining the transparency value of the labeled objects.
     */
    private static final String MSG_TRANSPARENCY_VAL = "Value 0 means opaque and 255 completely transparent. ";
    /**
     * Message explaining the show scribble option.
     */
    private static final String MSG_SHOW_SCRIBBLES = "Enables the visualization of the drawn scribbles of the objects, on the main gui.";
    /**
     * Message explaining the show crop option.
     */
    private static final String MSG_SHOW_CROPS = "Enables the visualization of the drawn crops of the objects, on the main gui.";
    /**
     * Message explaining the pop object for preview option.
     */
    private static final String MSG_POP_OBJ_PREV = "After pressing the next button, a window will pop-up for each object for reviewing/adjusting its properties.";
    /**
     * Message explaining the save frame map option.
     */
    private static final String MSG_SAVE_FRAME_MAP = "Enables the saving of the object map for all the frames.";
    /**
     * Message explaining the server address.
     */
    private static final String MSG_SERVER_ADDRESS = "The path to the server handling the data flow.";
    /**
     * Message explaining the server port.
     */
    private static final String MSG_SERVER_PORT = "The port to connect to the server handling the data flow.";
    /**
     * Message explaining the test server button.
     */
    private static final String MSG_TEST_SERVER_CONN = "Test the connection to the specified server application.";
    /**
     * Message explaining the save button.
     */
    private static final String MSG_SAVE_CFGS = "Saves and applies the chosen configurations.";
    /**
     * Message explaining the default values for DPI panel.
     */
    private static final String MSG_DEFAULT_VAL_DPI = "A list of standard, commonly used DPI. Select one of them and press the Preview button to see how the application would look like if you save it.";
    /**
     * Message explaining the default values for DPI panel.
     */
    private static final String MSG_CUSTOM_VAL_DPI = "You can define a custom value for the DPI. Insert a value and press the Preview button to see how the application would look like if you save it.";
    /**
     * Message explaining the preview application with new DPI button.
     */
    private static final String MSG_PREVIEW_DPI = "Switches the application to the selected screen scale.";
    /**
     * Message explaining the image format option.
     */
    private static final String MSG_EXPORT_IMAGE_FORMAT = "A list of possible formats for the export of images. JPEG is a lossy format; PNG and BMP are both lossless formats.";
    /**
     * Message explaining the image format option.
     */
    private static final String MSG_AUTOMATIC_EXPORT = "Enables the automatic save of images, frame by frame, on the press of the next button. When selected, it will execute the save without warning or asking the user. To stop the automatic save, it should be disabled.";
    /**
     * Message explaining the image format option.
     */
    private static final String MSG_AUTO_EXPORT_IMG_TYPES = "Select which image type should be automatically saved, frame by frame: original image, segmented image, result image or joined images.";
    /**
     * Message explaining the image format option.
     */
    private static final String MSG_AUTO_EXPORT_JOIN_IMG = "Select which images should be joined and auto exported. Choose the left image and the right image.";
    /**
     * Message explaining the image format option.
     */
    private static final String MSG_EXPORT_IMG_PATH = "Choose the path where the images (automatic or manual) should be exported";
    /**
     * Message explaining the check frame annotation option.
     */
    private static final String MSG_CHECK_FRAME_ATTRIB = "Checks if the frame attributes are different from a default invalid value: \'" + Constants.INVALID_ATTRIBUTE_TEXT + "\'. If any/some of the frame attributes are not set, a message window will pop up and warn the user.";
    /**
     * Message explaining the check frame annotation option.
     */
    private static final String MSG_CHECK_OBJECT_ATTRIB = "Checks if the object attributes are different from a default invalid value: \'" + Constants.INVALID_ATTRIBUTE_TEXT + "\'. If any/some of the object attributes are not set, a message window will pop up and warn the user.";

    /**
     * common image formats; common extensions.
     */
    private static final String JPEG = "JPEG";
    private static final String PNG = "PNG";
    private static final String BMP = "BMP";

    /**
     * The names of the tabs used in the configuration window.
     */
    public static final String TAB_GENERAL = "General";
    /**
     * The constant TAB_NETWORK.
     */
    public static final String TAB_NETWORK = "Network";
    /**
     * The constant TAB_SCREEN.
     */
    public static final String TAB_SCREEN = "Screen";
    /**
     * The constant TAB_IMAGE_EXPORT.
     */
    public static final String TAB_IMAGE_EXPORT = "Image Export";

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * Keeps the user preferences in one object.
     */
    private final transient UserPreferences userPreferences;

    /**
     * The event dispatcher for the keyboard in order to execute specific tasks
     * for the crop window.
     */
    private final transient UserPrefsDispatcher userPrefsDispatch;

    /**
     * The list of often used DPI values.
     */
    private static final int[] DPI_VALUES = {72, 96, 120, 144, 160, 192, 240};
    /**
     * A temporary value for the DPI, which is saving the initial DPI value.
     * When closing the application, if the changes are not saved, the initial
     * value of the DPI will be restored.
     */
    private int tempDPIValue;

    /**
     * True if a scribble object was created and the user has to have the option
     * to export it.
     */
    private final boolean existsScribbleObj;

    /**
     * logger instance
     */
    private final transient Logger log = LoggerFactory.getLogger(UserConfigs.class);

    /**
     * Creates new form UserConfigs
     *
     * @param parent            the parent frame
     * @param modal             true if the dialog shall be the top window and no access to other application windows shall be allowed
     * @param userPrefs         the user preferences from the saved preferences
     * @param defaultDPIValue   the default value of the DPI, for the current screen
     * @param existsScribbleObj true if a scribble object exists and the export of the semantic image should be available
     */
    public UserConfigs(java.awt.Frame parent, boolean modal, UserPreferences userPrefs, int defaultDPIValue, boolean existsScribbleObj) {
        super(parent, modal);
        initComponents();

        // add keyboard listener to be able to set events on the wanted keys
        userPrefsDispatch = new UserPrefsDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(userPrefsDispatch);

        this.existsScribbleObj = existsScribbleObj;

        this.userPreferences = userPrefs;

        updateGUI(defaultDPIValue);

        prepareFrame();

        // remove some fields after the computation of the frame size was done
        // the export joined images choices have to be enabled just if the user wants to export them
        enableJoinedImgTypes(jCBJoinedImg.isSelected() && jCBAutoImgExport.isSelected());
    }

    /**
     * Update the gui according to the user preferences.
     */
    private void updateGUI(int defaultDPIValue) {
        applyUserPreferences();

        enableFields();

        addScreenDPIOptions(defaultDPIValue);

        initHelp();
    }

    /**
     * Enable/Disable/Show/Hide some of the configuration components.
     */
    private void enableFields() {
        jLObjAlphaVal.setEnabled(jCBObjAlphaHighlight.isSelected());
        jFTFObjAlphaVal.setEnabled(jCBObjAlphaHighlight.isSelected());

        // the first start message shall be visible just the first time the application is started
        jLFirstStartScreenMsg.setVisible(userPreferences.isFirstRun());

        // remove the components which should not be visible in offline mode
        // TODO: remove all non-opnesource components
//        if (true) {
//            jTPUserCfg.remove(jPNetworkCfg);
//            jCBPopUpObj.setVisible(false);
//        }
    }

    /**
     * Apply the user preferences to the configuration window, in order to
     * synchronize them.
     */
    private void applyUserPreferences() {
        jCBObjAlphaHighlight.setSelected(userPreferences.isShowObjHighlight());
        jFTFObjAlphaVal.setValue((Number) userPreferences.getObjAlphaVal());
        jCBShowScribbles.setSelected(userPreferences.isShowScribbles());
        jCBShowCrops.setSelected(userPreferences.isShowCrops());
        jCBSaveFrameMap.setSelected(userPreferences.isSaveFrameObjMap());
        jCBFrameAnnotCheck.setSelected(userPreferences.isCheckFrameAnnotations());
        jCBObjectAttribCheck.setSelected(userPreferences.isCheckObjectAttributes());

        // select file type radio button
        Utils.selectRadioButton(bGExtensions, userPreferences.getImgExportExtension());
        jTFImgExportPath.setText(userPreferences.getImgExportPath());

        // select the automatic export of images and enable the options
        jCBAutoImgExport.setSelected(userPreferences.isImgAutoExportEveryFrame());
        enableExportImgSelect(jCBAutoImgExport.isSelected());
        // select image types for automatic export
        applyUserPrefsImgTypes();
        // select images type for joined export of images
        applyUserPrefJoinedExportTypes();
    }

    /**
     * Apply the user preferences regarding which image type should be exported
     * automatically every frame.
     */
    private void applyUserPrefsImgTypes() {
        // the selected images are concatenated in a string, splitted by comma
        String[] types = userPreferences.getImgTypeForExport().split(",");

        for (String type : types) {
            switch (type) {
                case Constants.ORIGINAL_IMG:
                    jCBOrigImg.setSelected(true);
                    break;

                case Constants.SEGMENTED_IMG:
                    jCBSegmImg.setSelected(true);
                    break;

                case Constants.SEMANTIC_SEGMENTATION_IMG:
                    jCBResultImg.setSelected(true);
                    break;

                case Constants.JOINED_IMGS:
                    jCBJoinedImg.setSelected(true);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Apply the user preferences regarding which image types should be exported
     * as a joined image, automatically, every frame.
     */
    private void applyUserPrefJoinedExportTypes() {
        // select the types of images to be exported in a joined manner
        String[] joinedTypes = userPreferences.getImgTypeJoinedExport().split(",");

        // stop if the string is not correctly specified
        if (joinedTypes.length != 2) {
            log.error("Auto export for joined images: the types string for left and right image has length {}!!", joinedTypes.length);
            return;
        }

        // select the radio buttons with the text
        Utils.selectRadioButton(bGExportJoinLeftImg, joinedTypes[0]);
        Utils.selectRadioButton(bGExportJoinRightImg, joinedTypes[1]);
    }

    /**
     * Update the gui with DPI related information.
     */
    private void addScreenDPIOptions(int defaultDPIValue) {
        // save the initial value
        this.tempDPIValue = userPreferences.getPreferredDPI();

        // show the default value of the DPI to the user
        StringBuilder info = new StringBuilder();
        info.append("<html>");
        info.append("The initial DPI value was ");
        info.append(userPreferences.getPreferredDPI());
        info.append(".");
        info.append("<br>");
        info.append("The default DPI value of the screen is ");
        info.append(defaultDPIValue);
        info.append(".");
        info.append("</html>");

        jLInfoDefaultVal.setText(info.toString());
        jFTFDPIValue.setValue(userPreferences.getPreferredDPI());

        // add a list of radio buttons with common DPI values
        addDPIDefaultVals();
    }

    /**
     * Initialize the help section of the gui.
     */
    private void initHelp() {
        // write the default message
        setHelpText(DEFAULT_HELP_MSG);

        // display the help image
        jLHelpImage.setIcon(common.Icons.INFO_ICON);
    }

    /**
     * Code related to the frame like: pack, set location etc.
     */
    private void prepareFrame() {
        this.pack();
        this.setMinimumSize(getPreferredSize());
        this.setLocationRelativeTo(null);
    }

    /**
     * Save the user specified preferences. Avoid using notify, by saving in the
     * input object.
     */
    private void updateUserPrefs() {
        userPreferences.setShowObjHighlight(jCBObjAlphaHighlight.isSelected());
        userPreferences.setObjAlphaVal(((Number) jFTFObjAlphaVal.getValue()).intValue());
        userPreferences.setShowScribbles(jCBShowScribbles.isSelected());
        userPreferences.setShowCrops(jCBShowCrops.isSelected());
//        userPreferences.setPopUpObjects(jCBPopUpObj.isSelected());
        userPreferences.setSaveFrameObjMap(jCBSaveFrameMap.isSelected());
        userPreferences.setImgExportPath(jTFImgExportPath.getText());
        userPreferences.setImgExportExtension(bGExtensions.getSelection().getActionCommand());
        userPreferences.setImgAutoExportEveryFrame(jCBAutoImgExport.isSelected());
        userPreferences.setImgTypeForExport(getAutoExportImgTypes());
        userPreferences.setImgTypeJoinedExport(getAutoExportJoinedImgTypes());
        userPreferences.setCheckFrameAnnotations(jCBFrameAnnotCheck.isSelected());
        userPreferences.setCheckObjectAttributes(jCBObjectAttribCheck.isSelected());
    }

    /**
     * Get the string representing the user preferences regarding the images to
     * be automatically exported, every frame.
     *
     * @return the string representing all the user options, separated by comma
     */
    private String getAutoExportImgTypes() {
        StringBuilder types = new StringBuilder();

        types.append(jCBOrigImg.isSelected() ? Constants.ORIGINAL_IMG : "");
        types.append(",");
        types.append(jCBSegmImg.isSelected() ? Constants.SEGMENTED_IMG : "");
        types.append(",");
        types.append(jCBResultImg.isSelected() ? Constants.SEMANTIC_SEGMENTATION_IMG : "");
        types.append(",");
        types.append(jCBJoinedImg.isSelected() ? Constants.JOINED_IMGS : "");

        return types.toString();
    }

    /**
     * Get the string representing the user preferences regarding the joined
     * images, to be automatically exported, every frame.
     *
     * @return the string representing the user options, separated by comma, for
     * the left and right images
     */
    private String getAutoExportJoinedImgTypes() {
        StringBuilder types = new StringBuilder();
        types.append(bGExportJoinLeftImg.getSelection().getActionCommand());
        types.append(",");
        types.append(bGExportJoinRightImg.getSelection().getActionCommand());

        return types.toString();
    }

    /**
     * Configures the configuration window for the first run. It will have
     * different options which are not there normally.
     */
    public void setConfigFirstRun() {
        // show the message for the first run
        jLFirstStartScreenMsg.setVisible(true);

        // switch to the screen configuration tab
        switchToTab(TAB_SCREEN);
    }

    /**
     * Switches the tabbed pane to the wanted tab, specified by the name.
     *
     * @param tabName the name of the tab where it should change
     */
    public void switchToTab(String tabName) {
        int screenTabIndex = 0;

        // search for the index of the wanted tab in the list of tabs
        for (int index = 0; index < jTPUserCfg.getTabCount(); index++) {
            if (tabName.equals(jTPUserCfg.getTitleAt(index))) {
                screenTabIndex = index;
            }
        }

        // switch to the wanted tab
        jTPUserCfg.setSelectedIndex(screenTabIndex);
    }

    /**
     * Add a list of radio buttons, representing default, generally used, values
     * for the DPI.
     */
    private void addDPIDefaultVals() {
        // set the layout of the panel, based on the amount of elements to be displayed on it
        jPDefaultValues.setLayout(new GridLayout(DPI_VALUES.length, 1));

        for (int index = 0; index < DPI_VALUES.length; index++) {
            String text = DPI_VALUES[index] + " DPI";

            // create a radio button with the given text
            JRadioButton jrbDPIValues = new JRadioButton(text);

            // set its action command to a number for easier usage later (to retrieve the value of the DPI)
            jrbDPIValues.setActionCommand(Integer.toString(DPI_VALUES[index]));

            // add action listener to synchronize the text field with the radio buttons
            jrbDPIValues.addActionListener(e -> jFTFDPIValue.setValue(Integer.parseInt(bgScreenDPIs.getSelection().getActionCommand())));

            // add mouse listener to display the help message when the mouse is on top of the radio buttons
            jrbDPIValues.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    jPDefaultValuesMouseEntered(evt);
                }
            });

            jrbDPIValues.setBackground(Color.white);

            // add the radio button to the button group
            bgScreenDPIs.add(jrbDPIValues);

            // add the radio button to the panel displaying it
            jPDefaultValues.add(jrbDPIValues);

            // select the radio button which has the same value as the user preffered one (if any)
            if (DPI_VALUES[index] == userPreferences.getPreferredDPI()) {
                jrbDPIValues.setSelected(true);
                jFTFDPIValue.setValue(DPI_VALUES[index]);
            }
        }
    }

    /**
     * Set the text on the help section.
     *
     * @param text the text to be displayed on the help section
     */
    private void setHelpText(String text) {
        jLHelpText.setText("<html>" + text + "</html>");
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

        bgScreenDPIs = new javax.swing.ButtonGroup();
        bGExtensions = new javax.swing.ButtonGroup();
        bGExportJoinLeftImg = new javax.swing.ButtonGroup();
        bGExportJoinRightImg = new javax.swing.ButtonGroup();
        javax.swing.JPanel jPBackground = new javax.swing.JPanel();
        jTPUserCfg = new javax.swing.JTabbedPane();
        jPGeneralCfg = new javax.swing.JPanel();
        jCBObjAlphaHighlight = new javax.swing.JCheckBox();
        jCBShowScribbles = new javax.swing.JCheckBox();
        jFTFObjAlphaVal = new javax.swing.JFormattedTextField();
        jLObjAlphaVal = new javax.swing.JLabel();
        jCBSaveFrameMap = new javax.swing.JCheckBox();
        jCBFrameAnnotCheck = new javax.swing.JCheckBox();
        jCBShowCrops = new javax.swing.JCheckBox();
        jCBObjectAttribCheck = new javax.swing.JCheckBox();
        jPScreenCfg = new javax.swing.JPanel();
        javax.swing.JPanel jPCustomValues = new javax.swing.JPanel();
        javax.swing.JLabel jLDPIValue = new javax.swing.JLabel();
        javax.swing.JButton jBPreviewDPI = new javax.swing.JButton();
        jFTFDPIValue = new javax.swing.JFormattedTextField();
        jLInfoDefaultVal = new javax.swing.JLabel();
        jPDefaultValues = new javax.swing.JPanel();
        jLFirstStartScreenMsg = new javax.swing.JLabel();
        javax.swing.JPanel jPExportOptions = new javax.swing.JPanel();
        javax.swing.JPanel jPFileExtensions = new javax.swing.JPanel();
        javax.swing.JRadioButton jRBJPEGExtension = new javax.swing.JRadioButton();
        javax.swing.JRadioButton jRBPNGExtension = new javax.swing.JRadioButton();
        javax.swing.JRadioButton jRBBMPExtension = new javax.swing.JRadioButton();
        javax.swing.JPanel jPFileNameOpt = new javax.swing.JPanel();
        javax.swing.JLabel jLSaveToPath = new javax.swing.JLabel();
        jTFImgExportPath = new javax.swing.JTextField();
        javax.swing.JButton jBBrowse = new javax.swing.JButton();
        javax.swing.JPanel jPContinuousExport = new javax.swing.JPanel();
        jCBAutoImgExport = new javax.swing.JCheckBox();
        javax.swing.JPanel jPExportImgTypes = new javax.swing.JPanel();
        jCBOrigImg = new javax.swing.JCheckBox();
        jCBSegmImg = new javax.swing.JCheckBox();
        jCBResultImg = new javax.swing.JCheckBox();
        jCBJoinedImg = new javax.swing.JCheckBox();
        javax.swing.JPanel jPJoinedFilesSelect = new javax.swing.JPanel();
        jPLeftImg = new javax.swing.JPanel();
        jRBOrigImgLeft = new javax.swing.JRadioButton();
        jRBSegmImgLeft = new javax.swing.JRadioButton();
        jRBSemanticImgLeft = new javax.swing.JRadioButton();
        jPRightImg = new javax.swing.JPanel();
        jRBOrigImgRight = new javax.swing.JRadioButton();
        jRBSegmentedImgRight = new javax.swing.JRadioButton();
        jRBSemanticImgRight = new javax.swing.JRadioButton();
        javax.swing.JPanel jPCommon = new javax.swing.JPanel();
        javax.swing.JPanel jPHelp = new javax.swing.JPanel();
        jLHelpImage = new javax.swing.JLabel();
        jLHelpText = new javax.swing.JLabel();
        javax.swing.JButton jBSaveConfigs = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("User Configurations");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPBackground.setLayout(new java.awt.GridBagLayout());

        jTPUserCfg.setBackground(new java.awt.Color(255, 255, 255));

        jPGeneralCfg.setBackground(new java.awt.Color(255, 255, 255));
        jPGeneralCfg.setLayout(new java.awt.GridBagLayout());

        jCBObjAlphaHighlight.setBackground(new java.awt.Color(255, 255, 255));
        jCBObjAlphaHighlight.setText("Show object highlight. Draw a transparent layer over the object to highlight it.");
        jCBObjAlphaHighlight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBObjAlphaHighlightMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBObjAlphaHighlightMouseEntered(evt);
            }
        });
        jCBObjAlphaHighlight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBObjAlphaHighlightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBObjAlphaHighlight, gridBagConstraints);

        jCBShowScribbles.setBackground(new java.awt.Color(255, 255, 255));
        jCBShowScribbles.setText("Show the scribbles of the objects.");
        jCBShowScribbles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBShowScribblesMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBShowScribblesMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBShowScribbles, gridBagConstraints);

        jFTFObjAlphaVal.setBackground(new java.awt.Color(255, 255, 255));
        jFTFObjAlphaVal.setText("255");
        jFTFObjAlphaVal.setEnabled(false);
        jFTFObjAlphaVal.setPreferredSize(new java.awt.Dimension(70, 26));
        jFTFObjAlphaVal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jFTFObjAlphaValMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jFTFObjAlphaValMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPGeneralCfg.add(jFTFObjAlphaVal, gridBagConstraints);

        jLObjAlphaVal.setLabelFor(jCBObjAlphaHighlight);
        jLObjAlphaVal.setText("The transparency value (0-255):");
        jLObjAlphaVal.setEnabled(false);
        jLObjAlphaVal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLObjAlphaValMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLObjAlphaValMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 50, 2, 2);
        jPGeneralCfg.add(jLObjAlphaVal, gridBagConstraints);

        jCBSaveFrameMap.setBackground(new java.awt.Color(255, 255, 255));
        jCBSaveFrameMap.setText("Save frame object map");
        jCBSaveFrameMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBSaveFrameMapMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBSaveFrameMapMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBSaveFrameMap, gridBagConstraints);

        jCBFrameAnnotCheck.setBackground(new java.awt.Color(255, 255, 255));
        jCBFrameAnnotCheck.setText("Check and ensure frame attributes are correctly set");
        jCBFrameAnnotCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBFrameAnnotCheckMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBFrameAnnotCheckMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBFrameAnnotCheck, gridBagConstraints);

        jCBShowCrops.setBackground(new java.awt.Color(255, 255, 255));
        jCBShowCrops.setText("Show the crops of the object");
        jCBShowCrops.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBShowCropsMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBShowCropsMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBShowCrops, gridBagConstraints);

        jCBObjectAttribCheck.setBackground(new java.awt.Color(255, 255, 255));
        jCBObjectAttribCheck.setText("Check and ensure object attributes are correctly set");
        jCBObjectAttribCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jCBObjectAttribCheckMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBObjectAttribCheckMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPGeneralCfg.add(jCBObjectAttribCheck, gridBagConstraints);

        jTPUserCfg.addTab(TAB_GENERAL, jPGeneralCfg);

        jPScreenCfg.setBackground(new java.awt.Color(255, 255, 255));
        jPScreenCfg.setLayout(new java.awt.GridBagLayout());

        jPCustomValues.setBackground(new java.awt.Color(255, 255, 255));
        jPCustomValues.setBorder(javax.swing.BorderFactory.createTitledBorder("Custom Values"));
        jPCustomValues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPCustomValuesMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPCustomValuesMouseEntered(evt);
            }
        });
        jPCustomValues.setLayout(new java.awt.GridBagLayout());

        jLDPIValue.setText("DPI value");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPCustomValues.add(jLDPIValue, gridBagConstraints);

        jBPreviewDPI.setBackground(new java.awt.Color(255, 255, 255));
        jBPreviewDPI.setText("Preview");
        jBPreviewDPI.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBPreviewDPIMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBPreviewDPIMouseEntered(evt);
            }
        });
        jBPreviewDPI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPreviewDPIActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPCustomValues.add(jBPreviewDPI, gridBagConstraints);

        jFTFDPIValue.setBackground(new java.awt.Color(255, 255, 255));
        jFTFDPIValue.setPreferredSize(new java.awt.Dimension(80, 26));
        jFTFDPIValue.setValue(0);
        jFTFDPIValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jFTFDPIValueMouseEntered(evt);
            }
        });
        jFTFDPIValue.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jFTFDPIValuePropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPCustomValues.add(jFTFDPIValue, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 2);
        jPCustomValues.add(jLInfoDefaultVal, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPScreenCfg.add(jPCustomValues, gridBagConstraints);

        jPDefaultValues.setBackground(new java.awt.Color(255, 255, 255));
        jPDefaultValues.setBorder(javax.swing.BorderFactory.createTitledBorder("Default Values"));
        jPDefaultValues.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPDefaultValuesMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPDefaultValuesMouseEntered(evt);
            }
        });
        jPDefaultValues.setLayout(new java.awt.GridLayout(1, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPScreenCfg.add(jPDefaultValues, gridBagConstraints);

        jLFirstStartScreenMsg.setForeground(new java.awt.Color(204, 0, 0));
        jLFirstStartScreenMsg.setText("Please adjust the value of the DPI to the best fit of your screen.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 2);
        jPScreenCfg.add(jLFirstStartScreenMsg, gridBagConstraints);

        jTPUserCfg.addTab(TAB_SCREEN, jPScreenCfg);

        jPExportOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPExportOptions.setLayout(new java.awt.GridBagLayout());

        jPFileExtensions.setBackground(new java.awt.Color(255, 255, 255));
        jPFileExtensions.setBorder(javax.swing.BorderFactory.createTitledBorder("Image Format"));
        jPFileExtensions.setPreferredSize(new java.awt.Dimension(120, 100));
        jPFileExtensions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPFileExtensionsMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPFileExtensionsMouseEntered(evt);
            }
        });
        jPFileExtensions.setLayout(new java.awt.GridBagLayout());

        jRBJPEGExtension.setBackground(new java.awt.Color(255, 255, 255));
        bGExtensions.add(jRBJPEGExtension);
        jRBJPEGExtension.setSelected(true);
        jRBJPEGExtension.setText(JPEG);
        jRBJPEGExtension.setActionCommand(JPEG);
        jRBJPEGExtension.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBJPEGExtensionMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPFileExtensions.add(jRBJPEGExtension, gridBagConstraints);

        jRBPNGExtension.setBackground(new java.awt.Color(255, 255, 255));
        bGExtensions.add(jRBPNGExtension);
        jRBPNGExtension.setText(PNG);
        jRBPNGExtension.setActionCommand(PNG);
        jRBPNGExtension.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBPNGExtensionMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPFileExtensions.add(jRBPNGExtension, gridBagConstraints);

        jRBBMPExtension.setBackground(new java.awt.Color(255, 255, 255));
        bGExtensions.add(jRBBMPExtension);
        jRBBMPExtension.setText(BMP);
        jRBBMPExtension.setActionCommand(BMP);
        jRBBMPExtension.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBBMPExtensionMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPFileExtensions.add(jRBBMPExtension, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExportOptions.add(jPFileExtensions, gridBagConstraints);

        jPFileNameOpt.setBackground(new java.awt.Color(255, 255, 255));
        jPFileNameOpt.setLayout(new java.awt.GridBagLayout());

        jLSaveToPath.setText("Save to path");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPFileNameOpt.add(jLSaveToPath, gridBagConstraints);

        jTFImgExportPath.setPreferredSize(new java.awt.Dimension(350, 24));
        jTFImgExportPath.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTFImgExportPathMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTFImgExportPathMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPFileNameOpt.add(jTFImgExportPath, gridBagConstraints);

        jBBrowse.setBackground(new java.awt.Color(255, 255, 255));
        jBBrowse.setText("Browse");
        jBBrowse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBBrowseMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBBrowseMouseEntered(evt);
            }
        });
        jBBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPFileNameOpt.add(jBBrowse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExportOptions.add(jPFileNameOpt, gridBagConstraints);

        jPContinuousExport.setBackground(new java.awt.Color(255, 255, 255));
        jPContinuousExport.setBorder(javax.swing.BorderFactory.createTitledBorder("Automatic Export"));
        jPContinuousExport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPContinuousExportMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPContinuousExportMouseEntered(evt);
            }
        });
        jPContinuousExport.setLayout(new java.awt.GridBagLayout());

        jCBAutoImgExport.setBackground(new java.awt.Color(255, 255, 255));
        jCBAutoImgExport.setText("Automatically export every frame");
        jCBAutoImgExport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBAutoImgExportMouseEntered(evt);
            }
        });
        jCBAutoImgExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBAutoImgExportActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPContinuousExport.add(jCBAutoImgExport, gridBagConstraints);

        jPExportImgTypes.setBackground(new java.awt.Color(255, 255, 255));
        jPExportImgTypes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPExportImgTypesMouseEntered(evt);
            }
        });
        jPExportImgTypes.setLayout(new java.awt.GridBagLayout());

        jCBOrigImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBOrigImg.setText(Constants.ORIGINAL_IMG);
        jCBOrigImg.setEnabled(false);
        jCBOrigImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBOrigImgMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPExportImgTypes.add(jCBOrigImg, gridBagConstraints);

        jCBSegmImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBSegmImg.setText(Constants.SEGMENTED_IMG);
        jCBSegmImg.setEnabled(false);
        jCBSegmImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBSegmImgMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPExportImgTypes.add(jCBSegmImg, gridBagConstraints);

        jCBResultImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBResultImg.setText(Constants.SEMANTIC_SEGMENTATION_IMG);
        jCBResultImg.setEnabled(false);
        jCBResultImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBResultImgMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPExportImgTypes.add(jCBResultImg, gridBagConstraints);

        jCBJoinedImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBJoinedImg.setText(Constants.JOINED_IMGS);
        jCBJoinedImg.setEnabled(false);
        jCBJoinedImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jCBJoinedImgMouseEntered(evt);
            }
        });
        jCBJoinedImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBJoinedImgActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPExportImgTypes.add(jCBJoinedImg, gridBagConstraints);

        jPJoinedFilesSelect.setBackground(new java.awt.Color(255, 255, 255));
        jPJoinedFilesSelect.setLayout(new java.awt.GridBagLayout());

        jPLeftImg.setBackground(new java.awt.Color(255, 255, 255));
        jPLeftImg.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Image"));
        jPLeftImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPLeftImgMouseEntered(evt);
            }
        });
        jPLeftImg.setLayout(new java.awt.GridBagLayout());

        jRBOrigImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinLeftImg.add(jRBOrigImgLeft);
        jRBOrigImgLeft.setSelected(true);
        jRBOrigImgLeft.setText(Constants.ORIGINAL_IMG.split(" ")[0]);
        jRBOrigImgLeft.setActionCommand(Constants.ORIGINAL_IMG);
        jRBOrigImgLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBOrigImgLeftMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPLeftImg.add(jRBOrigImgLeft, gridBagConstraints);

        jRBSegmImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinLeftImg.add(jRBSegmImgLeft);
        jRBSegmImgLeft.setText(Constants.SEGMENTED_IMG.split(" ")[0]);
        jRBSegmImgLeft.setActionCommand(Constants.SEGMENTED_IMG);
        jRBSegmImgLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBSegmImgLeftMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPLeftImg.add(jRBSegmImgLeft, gridBagConstraints);

        jRBSemanticImgLeft.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinLeftImg.add(jRBSemanticImgLeft);
        jRBSemanticImgLeft.setText(Constants.SEMANTIC_SEGMENTATION_IMG.split(" ")[0]);
        jRBSemanticImgLeft.setActionCommand(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBSemanticImgLeftMouseEntered(evt);
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
        jPJoinedFilesSelect.add(jPLeftImg, gridBagConstraints);

        jPRightImg.setBackground(new java.awt.Color(255, 255, 255));
        jPRightImg.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Image"));
        jPRightImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPRightImgMouseEntered(evt);
            }
        });
        jPRightImg.setLayout(new java.awt.GridBagLayout());

        jRBOrigImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinRightImg.add(jRBOrigImgRight);
        jRBOrigImgRight.setText(Constants.ORIGINAL_IMG.split(" ")[0]);
        jRBOrigImgRight.setActionCommand(Constants.ORIGINAL_IMG);
        jRBOrigImgRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBOrigImgRightMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPRightImg.add(jRBOrigImgRight, gridBagConstraints);

        jRBSegmentedImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinRightImg.add(jRBSegmentedImgRight);
        jRBSegmentedImgRight.setSelected(true);
        jRBSegmentedImgRight.setText(Constants.SEGMENTED_IMG.split(" ")[0]);
        jRBSegmentedImgRight.setActionCommand(Constants.SEGMENTED_IMG);
        jRBSegmentedImgRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBSegmentedImgRightMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPRightImg.add(jRBSegmentedImgRight, gridBagConstraints);

        jRBSemanticImgRight.setBackground(new java.awt.Color(255, 255, 255));
        bGExportJoinRightImg.add(jRBSemanticImgRight);
        jRBSemanticImgRight.setText(Constants.SEMANTIC_SEGMENTATION_IMG.split(" ")[0]);
        jRBSemanticImgRight.setActionCommand(Constants.SEMANTIC_SEGMENTATION_IMG);
        jRBSemanticImgRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jRBSemanticImgRightMouseEntered(evt);
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
        jPJoinedFilesSelect.add(jPRightImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 28, 2, 2);
        jPExportImgTypes.add(jPJoinedFilesSelect, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 18, 2, 2);
        jPContinuousExport.add(jPExportImgTypes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPExportOptions.add(jPContinuousExport, gridBagConstraints);

        jTPUserCfg.addTab(TAB_IMAGE_EXPORT, jPExportOptions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jTPUserCfg, gridBagConstraints);

        jPCommon.setBackground(new java.awt.Color(255, 255, 255));
        jPCommon.setLayout(new java.awt.GridBagLayout());

        jPHelp.setBackground(new java.awt.Color(255, 255, 234));
        jPHelp.setBorder(javax.swing.BorderFactory.createTitledBorder("Help"));
        jPHelp.setPreferredSize(new java.awt.Dimension(110, 100));
        jPHelp.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPHelp.add(jLHelpImage, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        jPHelp.add(jLHelpText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPCommon.add(jPHelp, gridBagConstraints);

        jBSaveConfigs.setBackground(new java.awt.Color(255, 255, 255));
        jBSaveConfigs.setText("Save");
        jBSaveConfigs.setPreferredSize(new java.awt.Dimension(80, 32));
        jBSaveConfigs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jBSaveConfigsMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jBSaveConfigsMouseEntered(evt);
            }
        });
        jBSaveConfigs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveConfigsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPCommon.add(jBSaveConfigs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPBackground.add(jPCommon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        getContentPane().add(jPBackground, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cancelWindow();
    }//GEN-LAST:event_formWindowClosing

    private void jLObjAlphaValMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLObjAlphaValMouseEntered
        setHelpText(MSG_TRANSPARENCY);
    }//GEN-LAST:event_jLObjAlphaValMouseEntered

    private void jLObjAlphaValMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLObjAlphaValMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jLObjAlphaValMouseExited

    private void jFTFObjAlphaValMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jFTFObjAlphaValMouseEntered
        setHelpText(MSG_TRANSPARENCY_VAL);
    }//GEN-LAST:event_jFTFObjAlphaValMouseEntered

    private void jFTFObjAlphaValMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jFTFObjAlphaValMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jFTFObjAlphaValMouseExited

    private void jBSaveConfigsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveConfigsActionPerformed
        savePreferences();
    }//GEN-LAST:event_jBSaveConfigsActionPerformed

    private void jCBShowScribblesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBShowScribblesMouseEntered
        setHelpText(MSG_SHOW_SCRIBBLES);
    }//GEN-LAST:event_jCBShowScribblesMouseEntered

    private void jCBShowScribblesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBShowScribblesMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBShowScribblesMouseExited

    private void jCBObjAlphaHighlightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBObjAlphaHighlightActionPerformed
        enableFields();
    }//GEN-LAST:event_jCBObjAlphaHighlightActionPerformed

    private void jCBObjAlphaHighlightMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBObjAlphaHighlightMouseEntered
        setHelpText(MSG_OBJ_HIGHLIGHT);
    }//GEN-LAST:event_jCBObjAlphaHighlightMouseEntered

    private void jCBObjAlphaHighlightMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBObjAlphaHighlightMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBObjAlphaHighlightMouseExited

    private void jCBSaveFrameMapMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBSaveFrameMapMouseEntered
        setHelpText(MSG_SAVE_FRAME_MAP);
    }//GEN-LAST:event_jCBSaveFrameMapMouseEntered

    private void jCBSaveFrameMapMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBSaveFrameMapMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBSaveFrameMapMouseExited

    private void jBSaveConfigsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBSaveConfigsMouseEntered
        setHelpText(MSG_SAVE_CFGS);
    }//GEN-LAST:event_jBSaveConfigsMouseEntered

    private void jBSaveConfigsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBSaveConfigsMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jBSaveConfigsMouseExited

    private void jBPreviewDPIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPreviewDPIActionPerformed
        // update the user preferences
        userPreferences.setPreferredDPI(((Number) jFTFDPIValue.getValue()).intValue());

        // notify to preview the application with the new configuration
        observable.notifyObservers(ObservedActions.Action.REFRESH_APPLICATION_VIEW);

        // inform the user about the chosen DPI
        log.info("Preview for {} DPI", userPreferences.getPreferredDPI());
    }//GEN-LAST:event_jBPreviewDPIActionPerformed

    private void jFTFDPIValuePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jFTFDPIValuePropertyChange
        syncDPIDefaultValues();
    }//GEN-LAST:event_jFTFDPIValuePropertyChange

    private void jPDefaultValuesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPDefaultValuesMouseEntered
        setHelpText(MSG_DEFAULT_VAL_DPI);
    }//GEN-LAST:event_jPDefaultValuesMouseEntered

    private void jPDefaultValuesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPDefaultValuesMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jPDefaultValuesMouseExited

    private void jPCustomValuesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPCustomValuesMouseEntered
        setHelpText(MSG_CUSTOM_VAL_DPI);
    }//GEN-LAST:event_jPCustomValuesMouseEntered

    private void jPCustomValuesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPCustomValuesMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jPCustomValuesMouseExited

    private void jBPreviewDPIMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBPreviewDPIMouseEntered
        setHelpText(MSG_PREVIEW_DPI);
    }//GEN-LAST:event_jBPreviewDPIMouseEntered

    private void jBPreviewDPIMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBPreviewDPIMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jBPreviewDPIMouseExited

    private void jFTFDPIValueMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jFTFDPIValueMouseEntered
        setHelpText(MSG_CUSTOM_VAL_DPI);
    }//GEN-LAST:event_jFTFDPIValueMouseEntered

    private void jPFileExtensionsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPFileExtensionsMouseEntered
        setHelpText(MSG_EXPORT_IMAGE_FORMAT);
    }//GEN-LAST:event_jPFileExtensionsMouseEntered

    private void jPFileExtensionsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPFileExtensionsMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jPFileExtensionsMouseExited

    private void jRBJPEGExtensionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBJPEGExtensionMouseEntered
        setHelpText(MSG_EXPORT_IMAGE_FORMAT);
    }//GEN-LAST:event_jRBJPEGExtensionMouseEntered

    private void jRBPNGExtensionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBPNGExtensionMouseEntered
        setHelpText(MSG_EXPORT_IMAGE_FORMAT);
    }//GEN-LAST:event_jRBPNGExtensionMouseEntered

    private void jRBBMPExtensionMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBBMPExtensionMouseEntered
        setHelpText(MSG_EXPORT_IMAGE_FORMAT);
    }//GEN-LAST:event_jRBBMPExtensionMouseEntered

    private void jPContinuousExportMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPContinuousExportMouseEntered
        setHelpText(MSG_AUTOMATIC_EXPORT);
    }//GEN-LAST:event_jPContinuousExportMouseEntered

    private void jPContinuousExportMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPContinuousExportMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jPContinuousExportMouseExited

    private void jCBOrigImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBOrigImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_IMG_TYPES);
    }//GEN-LAST:event_jCBOrigImgMouseEntered

    private void jCBSegmImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBSegmImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_IMG_TYPES);
    }//GEN-LAST:event_jCBSegmImgMouseEntered

    private void jCBResultImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBResultImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_IMG_TYPES);
    }//GEN-LAST:event_jCBResultImgMouseEntered

    private void jCBJoinedImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBJoinedImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_IMG_TYPES);
    }//GEN-LAST:event_jCBJoinedImgMouseEntered

    private void jPLeftImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPLeftImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jPLeftImgMouseEntered

    private void jRBOrigImgLeftMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBOrigImgLeftMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBOrigImgLeftMouseEntered

    private void jRBSegmImgLeftMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBSegmImgLeftMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBSegmImgLeftMouseEntered

    private void jRBSemanticImgLeftMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBSemanticImgLeftMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBSemanticImgLeftMouseEntered

    private void jRBOrigImgRightMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBOrigImgRightMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBOrigImgRightMouseEntered

    private void jRBSegmentedImgRightMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBSegmentedImgRightMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBSegmentedImgRightMouseEntered

    private void jRBSemanticImgRightMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRBSemanticImgRightMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jRBSemanticImgRightMouseEntered

    private void jTFImgExportPathMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTFImgExportPathMouseEntered
        setHelpText(MSG_EXPORT_IMG_PATH);
    }//GEN-LAST:event_jTFImgExportPathMouseEntered

    private void jTFImgExportPathMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTFImgExportPathMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jTFImgExportPathMouseExited

    private void jBBrowseMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBBrowseMouseEntered
        setHelpText(MSG_EXPORT_IMG_PATH);
    }//GEN-LAST:event_jBBrowseMouseEntered

    private void jBBrowseMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jBBrowseMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jBBrowseMouseExited

    private void jCBAutoImgExportMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBAutoImgExportMouseEntered
        setHelpText(MSG_AUTOMATIC_EXPORT);
    }//GEN-LAST:event_jCBAutoImgExportMouseEntered

    private void jCBAutoImgExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBAutoImgExportActionPerformed
        enableExportImgSelect(jCBAutoImgExport.isSelected());
        enableJoinedImgTypes(jCBAutoImgExport.isSelected() && jCBJoinedImg.isSelected());
    }//GEN-LAST:event_jCBAutoImgExportActionPerformed

    private void jCBJoinedImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBJoinedImgActionPerformed
        enableJoinedImgTypes(jCBJoinedImg.isSelected());
    }//GEN-LAST:event_jCBJoinedImgActionPerformed

    private void jBBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBBrowseActionPerformed
        String selectedPath = Utils.browseFilePath(jTFImgExportPath.getText(), this);
        jTFImgExportPath.setText(("".equals(selectedPath)) ? jTFImgExportPath.getText() : selectedPath);
    }//GEN-LAST:event_jBBrowseActionPerformed

    private void jPExportImgTypesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPExportImgTypesMouseEntered
        setHelpText(MSG_AUTOMATIC_EXPORT);
    }//GEN-LAST:event_jPExportImgTypesMouseEntered

    private void jPRightImgMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPRightImgMouseEntered
        setHelpText(MSG_AUTO_EXPORT_JOIN_IMG);
    }//GEN-LAST:event_jPRightImgMouseEntered

    private void jCBShowCropsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBShowCropsMouseEntered
        setHelpText(MSG_SHOW_CROPS);
    }//GEN-LAST:event_jCBShowCropsMouseEntered

    private void jCBShowCropsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBShowCropsMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBShowCropsMouseExited

    private void jCBFrameAnnotCheckMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBFrameAnnotCheckMouseEntered
        setHelpText(MSG_CHECK_FRAME_ATTRIB);
    }//GEN-LAST:event_jCBFrameAnnotCheckMouseEntered

    private void jCBFrameAnnotCheckMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBFrameAnnotCheckMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBFrameAnnotCheckMouseExited

    private void jCBObjectAttribCheckMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBObjectAttribCheckMouseExited
        setHelpText(DEFAULT_HELP_MSG);
    }//GEN-LAST:event_jCBObjectAttribCheckMouseExited

    private void jCBObjectAttribCheckMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCBObjectAttribCheckMouseEntered
        setHelpText(MSG_CHECK_OBJECT_ATTRIB);
    }//GEN-LAST:event_jCBObjectAttribCheckMouseEntered

    /**
     * Allows another module to put an observer into the current module.
     *
     * @param o - the observer to be added
     */
    public void addObserver(Observer o) {
        observable.addObserver(o);

        // notify to remove the gui key event dispatcher
        observable.notifyObservers(ObservedActions.Action.REMOVE_GUI_KEY_EVENT_DISPATCHER);
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
     * Cancel user changes and close the window. The user canceled the changes,
     * the application has to go back to the previous state.
     */
    private void cancelWindow() {
        // the user canceled the changes done to DPI, the application has to go back to the previous state
        updateDPIPreferences(tempDPIValue);

        closeWindow();
    }

    /**
     * Close the window and release the key dispatcher.
     */
    private void closeWindow() {
        // notify to add the gui key event dispatcher back
        observable.notifyObservers(ObservedActions.Action.ADD_GUI_KEY_EVENT_DISPATCHER);

        dispose();
    }

    /**
     * Save the user preferences for later use and notify the main application
     * to update with the new configuration.
     */
    private void savePreferences() {
        // save the user preferences
        updateUserPrefs();

        observable.notifyObservers(ObservedActions.Action.REFRESH_DISPLAY);

        updateDPIPreferences(((Number) jFTFDPIValue.getValue()).intValue());

        // close the config window
        closeWindow();
    }

    /**
     * Change the DPI preferences, and refresh the screen, only if the value of
     * DPI has changed.
     */
    private void updateDPIPreferences(int dpiValue) {
        // update and refresh screen only if the values differ
        if (userPreferences.getPreferredDPI() != dpiValue) {
            userPreferences.setPreferredDPI(dpiValue);
            observable.notifyObservers(ObservedActions.Action.REFRESH_APPLICATION_VIEW);
        }
    }

    /**
     * Keep the text field and the radio buttons synchronized.
     */
    private void syncDPIDefaultValues() {
        // if the button group is null, stop the sync
        if (bgScreenDPIs == null) {
            return;
        }
        // remove any existing selection
        bgScreenDPIs.clearSelection();

        // get the user selected value
        int dpiValue = ((Number) jFTFDPIValue.getValue()).intValue();

        // search in the button group for the user selected value
        Utils.selectRadioButton(bgScreenDPIs, Integer.toString(dpiValue));
    }

    /**
     * Enable/Disable the choices for automatic export of images.
     *
     * @param selected true if the automatic save is on, therefore the options
     * should be enabled
     */
    private void enableExportImgSelect(boolean selected) {
        jCBOrigImg.setEnabled(selected);
        jCBSegmImg.setEnabled(selected);
        jCBResultImg.setEnabled(selected);
        jCBJoinedImg.setEnabled(selected);
    }

    /**
     * Enable/Disable the choices for automatic export of joined images.
     *
     * @param selected true if the automatic save of joined images is on,
     * therefore the options should be enabled
     */
    private void enableJoinedImgTypes(boolean selected) {
        jPLeftImg.setEnabled(selected);
        jPRightImg.setEnabled(selected);

        jRBOrigImgLeft.setEnabled(selected);
        jRBSegmImgLeft.setEnabled(selected);

        jRBOrigImgRight.setEnabled(selected);
        jRBSegmentedImgRight.setEnabled(selected);

        jRBSemanticImgLeft.setEnabled(selected && existsScribbleObj);
        jRBSemanticImgRight.setEnabled(selected && existsScribbleObj);
    }

    /**
     * The key event dispatcher for listening the keys and the implementation of
     * the actions for some defined keys.
     */
    private class UserPrefsDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            dispatchKeyGeneral(e);

            return false;
        }

        /**
         * Handles the keys which have to be enabled all the time in the frame.
         */
        private void dispatchKeyGeneral(KeyEvent e) {
            int eventId = e.getID();
            int key = e.getKeyCode();

            if (eventId == KeyEvent.KEY_PRESSED) {

                switch (key) {

                    case KeyEvent.VK_ESCAPE:
                        cancelWindow();
                        break;

                    case KeyEvent.VK_ENTER:
                        // if the textfield id the focus owner, do not save the object
                        if (jFTFObjAlphaVal.isFocusOwner() || jFTFDPIValue.isFocusOwner()) {
                            break;
                        }

                        savePreferences();
                        break;

                    default:
                        // do nothing
                        break;
                }
            }
        }
    }

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
            java.util.logging.Logger.getLogger(UserConfigs.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            UserConfigs dialog = new UserConfigs(new javax.swing.JFrame(), true, new UserPreferences(), 96, true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

}
