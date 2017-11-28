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
import common.LTFileFilter;
import observers.ObservedActions;
import common.Timings;
import common.UserPreferences;
import common.Utils;
import graphictablet.JPenFunctions;
import gui.support.ScreenResolution;
import gui.actions.GUIController;
import common.ConstantsLabeling;
import common.ExportImage;
import common.TextAreaAppender;
import common.Icons;
import gui.support.FrameInfo;
import gui.support.DisplayBBox;
import gui.support.ObjectBBox;
import gui.support.ObjectPolygon;
import gui.support.ObjectScribble;
import gui.support.Objects;
import gui.support.PanelResolution;
import library.DrawOptions;
import library.Resize;
import paintpanels.DrawConstants;
import gui.support.LAttributesFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ch.qos.logback.classic.LoggerContext;
import jpen.owner.multiAwt.AwtPenToolkit;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Gui labeling tool.
 *
 * @author Olimpia Popica
 */
public class GUILabelingTool extends javax.swing.JFrame implements Observer {

    /**
     * The resolution of the current screen where the application is showed.
     */
    private transient ScreenResolution screenRes;

    /**
     * User preferences related to the application.
     */
    private transient UserPreferences userPrefs;

    /**
     * The file chooser used to open files.
     */
    private JFileChooser chooser;

    /**
     * Allows the user to activate different functionalities from the graphic
     * tablet.
     */
    private transient JPenFunctions penFunctions;

    /**
     * An instance of the gui controller, which is implementing and managing all
     * the functionalities of the application.
     */
    private transient GUIController gc;

    /**
     * The event dispatcher for the keyboard in order to execute specific tasks
     * for the gui window.
     */
    private transient GUIKeyEventDispatcher guiKeyEventDispatch;

    /**
     * The text to be displayed on the new object for scribbles.
     */
    private static final String NEW_OBJ_BUTTON_TEXT = "+";

    /**
     * The text to be displayed for saving a newly created scribble object.
     */
    private static final String SAVE_OBJ_BUTTON_TEXT = "S";

    /**
     * The text to be displayed on the play button.
     */
    private static final String PLAY_BUTTON_TEXT = "Play";

    /**
     * The text to be displayed on the pause button.
     */
    private static final String PAUSE_BUTTON_TEXT = "Pause";

    /**
     * The information regarding the frame currently displayed.
     */
    private transient FrameInfo currentFrame = new FrameInfo();

    /**
     * Shows if the application is in video mode, with all the videos coming
     * from the server, or in a different mode, like offline where the data is
     * loaded from and stored to, locally.
     */
    private static final transient Constants.AppConfigMode APP_CFG_MODE = Constants.AppConfigMode.PREMIUM_EDITION;

    /**
     * The default value of the DPI.
     */
    private int defaultDPIValue = 96;

    /**
     * If it is set to true, Pixie will run in online mode; it will connect to
     * Behemoth for requesting video files or sending labels.
     *
     * If it is set to false, Pixie will run in offline mode; as standalone
     * application
     */
    private boolean runningOnline;

    /**
     * logger instance
     */
    private final transient Logger log;

    /**
     * Creates new form GUILabelingTool
     *
     * @param log the logger where the actions and exceptions are logged
     */
    public GUILabelingTool(Logger log) {
        this.log = log;

        initComponents();
        initLogger();
        initGUI();
    }

    private void initGUI() {
        // set the app to start in maximized window mode
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        // enable fields, set configurations
        configureComponents();

        // get resolution
        initResolutions();

        // warning for using smaller than HD resolution
        checkResolution();

        // reads the user preferences
        userPrefs = new UserPreferences();

        jCBPlayBackward.setSelected(userPrefs.isPlayBackward());
        jCBFlipVerticallyImg.setSelected(userPrefs.isFlipVertically());
        jCBMirrorImg.setSelected(userPrefs.isMirrorImage());
        jCBShowCurrentObj.setSelected(userPrefs.isShowJustCurrentObj());

        // initialise the properties of the file chooser
        initFileChooser();

        // init the listener for the pen for using the graphic tablet
        penFunctions = new JPenFunctions();
        // add observer to be notified when there are changes from the pen
        penFunctions.addObserver(this);

        // add keyboard listener to be able to set events on the wanted keys
        initKeyEventDispatch();

        initApplicationMode();

        chooseLoadFile();
    }

    /**
     * Enable/Disable fields; set icons and all the needed configurations of the
     * components.
     */
    private void configureComponents() {
        //disable the scribble objects; they will be enabled after loading an image
        jRBBoundingBox.setEnabled(false);
        jRBScribble.setEnabled(false);
        jRBEditMode.setEnabled(false);

        // do not show the objects info unles there are objects
        jPObjInfo.setVisible(false);

        // erase the text of the labels which will be writen later
        jLFrameNo.setText("");

        // show the icons of the menus
        setMenuItemsIcons();
    }

    /**
     * Initialize the logger, in order to display all the log messages in a text
     * logger on the gui.
     */
    private void initLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // find the appender which is an instance of the text area appender and set the text area where it should put the text
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            // iterate the appenders in order to find the wanted one
            for (Iterator<Appender<ILoggingEvent>> index = logger.iteratorForAppenders(); index.hasNext();) {
                Appender<ILoggingEvent> appender = index.next();
                // just the TextAreaAppender type is needed to be correctly initialised
                if (appender instanceof TextAreaAppender) {
                    ((TextAreaAppender) appender).setTextArea(jTALog, jTAShortLog);
                }
            }
        }
    }

    /**
     * Based on the user preferences, init the application in online/offline
     * mode. Load all the mode specific data: attributes etc.
     */
    private void initApplicationMode() {
        // init the current frame info object
        initCurrentFrameInfo();

        // instantiate the gui controller offline
        gc = new GUIController(userPrefs, currentFrame);

        // load the predefined data on the comboboxes
        loadCBdata();

        // init the screen resolution in the controller
        gc.setAvailableDrawSize(computeDrawingSize(getAvailableScreenSize(userPrefs.getPreferredDPI())));

        gc.addObserver(this);
    }

    /**
     * Defines events for the wanted keys, dependent by the labeling type.
     */
    private void initKeyEventDispatch() {
        guiKeyEventDispatch = new GUIKeyEventDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(guiKeyEventDispatch);
    }

    /**
     * Initialize the screen resolution.
     */
    private void initResolutions() {
        screenRes = new ScreenResolution(this);

        defaultDPIValue = Toolkit.getDefaultToolkit().getScreenResolution();
    }

    /**
     * Check if the used resolution is better than the advised one. If not, warn
     * the user.
     */
    private void checkResolution() {
        Dimension dim = screenRes.getScreenResolution();
        if (dim.width < 1920 || dim.height < 1080) {
            String title = "Resolution";
            String message = "The current resolution of your screen is not recomanded for labeling purposes!\nPlease use a resolution of minimum 1920x1080!!";
            Messages.showWarningMessage(this, message, title);
            log.info(message);
        }
    }

    /**
     * Initialise some important features of the file chooser used to load the
     * wanted files.
     */
    private void initFileChooser() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(userPrefs.getLastDirectory()));
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        LTFileFilter filter = new LTFileFilter();

        // add the list of extensions to the filter
        Constants.IMG_EXTENSION_LIST.stream().forEach(extension -> filter.addExtension(extension));

        filter.setDescription("Image and Video Files");
        chooser.setFileFilter(filter);
    }

    /**
     * Initialize the current frame info with the application defaults.
     */
    private void initCurrentFrameInfo() {
        currentFrame = new FrameInfo(jCBIllumination.getItemAt(jCBIllumination.getSelectedIndex()),
                jCBWeather.getItemAt(jCBWeather.getSelectedIndex()),
                jCBRoadType.getItemAt(jCBRoadType.getSelectedIndex()),
                jCBRoadEvent.getItemAt(jCBRoadEvent.getSelectedIndex()),
                jCBCountry.getItemAt(jCBCountry.getSelectedIndex()),
                jCBWipersVisible.isSelected(),
                jCBDirtVisible.isSelected(),
                jCBImageDistorted.isSelected(),
                userPrefs.isSaveFrameObjMap());
    }

    /**
     * Get the predefined data for the combo boxes from the configuration and
     * display it.
     */
    private void loadCBdata() {
        LAttributesFrame frameAttributes = gc.getFrameAttributes();

        if (frameAttributes == null) {
            return;
        } else {
            // remove existing attributes
            removeExistingAttributes();

            // add the new attributes
            frameAttributes.getIlluminationList().stream()
                    .forEach(illumination -> jCBIllumination.addItem(Utils.capitalize(illumination)));

            frameAttributes.getWeatherList().stream()
                    .forEach(weather -> jCBWeather.addItem(Utils.capitalize(weather)));

            frameAttributes.getRoadTypeList().stream()
                    .forEach(roadType -> jCBRoadType.addItem(Utils.capitalize(roadType)));

            frameAttributes.getRoadEventList().stream()
                    .forEach(roadEvent -> jCBRoadEvent.addItem(Utils.capitalize(roadEvent)));

            frameAttributes.getCountryList().stream()
                    .forEach(country -> jCBCountry.addItem(Utils.capitalize(country)));
        }
    }

    /**
     * Remove the existing attributes of the frame.
     */
    private void removeExistingAttributes() {
        // remove all items
        jCBIllumination.removeAllItems();
        jCBWeather.removeAllItems();
        jCBRoadType.removeAllItems();
        jCBRoadEvent.removeAllItems();
        jCBCountry.removeAllItems();

        if (userPrefs.isCheckFrameAnnotations()) {
            // fill in with the invalid data
            jCBIllumination.addItem(Utils.capitalize(Constants.INVALID_ATTRIBUTE_TEXT));
            jCBWeather.addItem(Utils.capitalize(Constants.INVALID_ATTRIBUTE_TEXT));
            jCBRoadType.addItem(Utils.capitalize(Constants.INVALID_ATTRIBUTE_TEXT));
            jCBRoadEvent.addItem(Utils.capitalize(Constants.INVALID_ATTRIBUTE_TEXT));
            jCBCountry.addItem(Utils.capitalize(Constants.INVALID_ATTRIBUTE_TEXT));
        }
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

        jBGLabelType = new javax.swing.ButtonGroup();
        jPBackground = new javax.swing.JPanel();
        jTPLabelingOptions = new javax.swing.JTabbedPane();
        jPLabeling = new javax.swing.JPanel();
        jPView = new javax.swing.JPanel();
        jPViewResults = new javax.swing.JPanel();
        jPDevelopment = new javax.swing.JPanel();
        jLJumpFrame = new javax.swing.JLabel();
        jTFJumpFrame = new javax.swing.JTextField();
        jBJumpFrame = new javax.swing.JButton();
        jLNoFrames = new javax.swing.JLabel();
        jSPShortLog = new javax.swing.JScrollPane();
        jTAShortLog = new javax.swing.JTextArea();
        jPViewResultsImg = new javax.swing.JPanel();
        jPPrevResultImg = new javax.swing.JPanel();
        jPImgOpt = new javax.swing.JPanel();
        jPVLabelType = new javax.swing.JPanel();
        jRBScribble = new javax.swing.JRadioButton();
        jRBBoundingBox = new javax.swing.JRadioButton();
        jBNewScribbleObj = new javax.swing.JButton();
        jRBEditMode = new javax.swing.JRadioButton();
        jRBPolygon = new javax.swing.JRadioButton();
        jBNewPolygonObj = new javax.swing.JButton();
        jSPObjList = new javax.swing.JScrollPane();
        jPObjList = new javax.swing.JPanel();
        jLAlignNorth = new javax.swing.JLabel();
        jPObjInfo = new javax.swing.JPanel();
        jLTotalNoObjs = new javax.swing.JLabel();
        jLNoBBoxObjs = new javax.swing.JLabel();
        jLNoScribbleObjs = new javax.swing.JLabel();
        jLTotalNoObjsText = new javax.swing.JLabel();
        jLNoBBoxObjsText = new javax.swing.JLabel();
        jLNoScribbleObjsText = new javax.swing.JLabel();
        jLNoPolygonObjsText = new javax.swing.JLabel();
        jLNoPolygonObjs = new javax.swing.JLabel();
        jPVideoPrev = new javax.swing.JPanel();
        jPVImgToLabel = new javax.swing.JPanel();
        jPNavigation = new javax.swing.JPanel();
        jBPrevFrame = new javax.swing.JButton();
        jBNextFrame = new javax.swing.JButton();
        jLFrameNo = new javax.swing.JLabel();
        jSVideoPlayer = new javax.swing.JSlider();
        jBPlay = new javax.swing.JButton();
        jCBPlayBackward = new javax.swing.JCheckBox();
        jCBFlipVerticallyImg = new javax.swing.JCheckBox();
        jCBMirrorImg = new javax.swing.JCheckBox();
        jPOptions = new javax.swing.JPanel();
        jPSceneDescription = new javax.swing.JPanel();
        jLIllumination = new javax.swing.JLabel();
        jCBIllumination = new javax.swing.JComboBox<>();
        jLWeather = new javax.swing.JLabel();
        jLRoadType = new javax.swing.JLabel();
        jLRoadEvent = new javax.swing.JLabel();
        jCBWeather = new javax.swing.JComboBox<>();
        jCBRoadType = new javax.swing.JComboBox<>();
        jCBRoadEvent = new javax.swing.JComboBox<>();
        jLCountry = new javax.swing.JLabel();
        jCBCountry = new javax.swing.JComboBox<>();
        jCBWipersVisible = new javax.swing.JCheckBox();
        jCBDirtVisible = new javax.swing.JCheckBox();
        jCBImageDistorted = new javax.swing.JCheckBox();
        jPLoadSource = new javax.swing.JPanel();
        jBLoadFile = new javax.swing.JButton();
        jPCropOpt = new javax.swing.JPanel();
        jCBShowCurrentObj = new javax.swing.JCheckBox();
        jPLog = new javax.swing.JPanel();
        jSPLog = new javax.swing.JScrollPane();
        jTALog = new javax.swing.JTextArea();
        jMBMenu = new javax.swing.JMenuBar();
        jMFile = new javax.swing.JMenu();
        jMILoadFile = new javax.swing.JMenuItem();
        jMIExit = new javax.swing.JMenuItem();
        jMEditObject = new javax.swing.JMenu();
        jMIMoveLeft = new javax.swing.JMenuItem();
        jMIMoveRight = new javax.swing.JMenuItem();
        jMIMoveUp = new javax.swing.JMenuItem();
        jMIMoveDown = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMIDecreaseLeft = new javax.swing.JMenuItem();
        jMIDecreaseRight = new javax.swing.JMenuItem();
        jMIDecreaseTop = new javax.swing.JMenuItem();
        jMIDecreaseBottom = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMIIncreaseLeft = new javax.swing.JMenuItem();
        jMIIncreaseRight = new javax.swing.JMenuItem();
        jMIIncreaseTop = new javax.swing.JMenuItem();
        jMIIncreaseBottom = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMIDecreaseBox = new javax.swing.JMenuItem();
        jMIIncreaseBox = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMICancelObject = new javax.swing.JMenuItem();
        jMIDeleteObject = new javax.swing.JMenuItem();
        jMNavigation = new javax.swing.JMenu();
        jMINextFrame = new javax.swing.JMenuItem();
        jMIPrevFrame = new javax.swing.JMenuItem();
        jMOptions = new javax.swing.JMenu();
        jMIUserConfig = new javax.swing.JMenuItem();
        jSAttribDef = new javax.swing.JPopupMenu.Separator();
        jMIAttributes = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMILanguage = new javax.swing.JMenuItem();
        jMExport = new javax.swing.JMenu();
        jMIExportOrigImg = new javax.swing.JMenuItem();
        jMIExportWorkingImg = new javax.swing.JMenuItem();
        jMIExportSemanticImg = new javax.swing.JMenuItem();
        jMIExportJoinedImg = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMIExportConfig = new javax.swing.JMenuItem();
        jMHelp = new javax.swing.JMenu();
        jMIAbout = new javax.swing.JMenuItem();
        jMIHotkeys = new javax.swing.JMenuItem();
        jMIManualApp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Pixie");
        setBackground(new java.awt.Color(255, 255, 255));
        setName("LabelingApplication"); // NOI18N
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPBackground.setBackground(new java.awt.Color(255, 255, 255));
        jPBackground.setLayout(new java.awt.GridBagLayout());

        jTPLabelingOptions.setBackground(new java.awt.Color(255, 255, 255));
        jTPLabelingOptions.setToolTipText("");
        jTPLabelingOptions.setFocusable(false);

        jPLabeling.setBackground(new java.awt.Color(255, 255, 255));
        jPLabeling.setToolTipText("Create bounding box label.");
        jPLabeling.setLayout(new java.awt.GridBagLayout());

        jPView.setBackground(new java.awt.Color(255, 255, 255));
        jPView.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPView.setLayout(new java.awt.GridBagLayout());

        jPViewResults.setBackground(new java.awt.Color(255, 255, 255));
        jPViewResults.setLayout(new java.awt.GridBagLayout());

        jPDevelopment.setLayout(new java.awt.GridBagLayout());

        jLJumpFrame.setText("Frame");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPDevelopment.add(jLJumpFrame, gridBagConstraints);

        jTFJumpFrame.setText("100");
        jTFJumpFrame.setToolTipText("");
        jTFJumpFrame.setPreferredSize(new java.awt.Dimension(50, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPDevelopment.add(jTFJumpFrame, gridBagConstraints);

        jBJumpFrame.setText("Jump");
        jBJumpFrame.setToolTipText("Jump to the specified number");
        jBJumpFrame.setEnabled(false);
        jBJumpFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBJumpFrameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPDevelopment.add(jBJumpFrame, gridBagConstraints);

        jLNoFrames.setText("No Frames");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPDevelopment.add(jLNoFrames, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPViewResults.add(jPDevelopment, gridBagConstraints);

        jSPShortLog.setAutoscrolls(true);
        jSPShortLog.setMaximumSize(new java.awt.Dimension(400, 96));
        jSPShortLog.setMinimumSize(new java.awt.Dimension(400, 96));
        jSPShortLog.setOpaque(false);
        jSPShortLog.setPreferredSize(new java.awt.Dimension(400, 100));

        jTAShortLog.setEditable(false);
        jTAShortLog.setColumns(20);
        jTAShortLog.setLineWrap(true);
        jTAShortLog.setRows(5);
        jTAShortLog.setWrapStyleWord(true);
        jSPShortLog.setViewportView(jTAShortLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPViewResults.add(jSPShortLog, gridBagConstraints);

        jPViewResultsImg.setBackground(new java.awt.Color(255, 255, 255));
        jPViewResultsImg.setLayout(new java.awt.GridBagLayout());

        jPPrevResultImg.setBackground(new java.awt.Color(255, 255, 255));
        jPPrevResultImg.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPPrevResultImg.setPreferredSize(new java.awt.Dimension(400, 300));
        jPPrevResultImg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPPrevResultImgMouseClicked(evt);
            }
        });
        jPPrevResultImg.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPViewResultsImg.add(jPPrevResultImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPViewResults.add(jPViewResultsImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPView.add(jPViewResults, gridBagConstraints);

        jPImgOpt.setBackground(new java.awt.Color(255, 255, 255));
        jPImgOpt.setLayout(new java.awt.GridBagLayout());

        jPVLabelType.setBackground(new java.awt.Color(255, 255, 255));
        jPVLabelType.setBorder(javax.swing.BorderFactory.createTitledBorder("Label Type"));
        jPVLabelType.setLayout(new java.awt.GridBagLayout());

        jRBScribble.setBackground(new java.awt.Color(255, 255, 255));
        jBGLabelType.add(jRBScribble);
        jRBScribble.setText("Scribble");
        jRBScribble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBScribbleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPVLabelType.add(jRBScribble, gridBagConstraints);

        jRBBoundingBox.setBackground(new java.awt.Color(255, 255, 255));
        jBGLabelType.add(jRBBoundingBox);
        jRBBoundingBox.setSelected(true);
        jRBBoundingBox.setText("Bounding Box");
        jRBBoundingBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBBoundingBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPVLabelType.add(jRBBoundingBox, gridBagConstraints);

        jBNewScribbleObj.setBackground(new java.awt.Color(255, 255, 255));
        jBNewScribbleObj.setText("+");
        jBNewScribbleObj.setEnabled(false);
        jBNewScribbleObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNewScribbleObjActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPVLabelType.add(jBNewScribbleObj, gridBagConstraints);
        jBNewScribbleObj.getAccessibleContext().setAccessibleName("NewObject");

        jRBEditMode.setBackground(new java.awt.Color(255, 255, 255));
        jBGLabelType.add(jRBEditMode);
        jRBEditMode.setText("Edit");
        jRBEditMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBEditModeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPVLabelType.add(jRBEditMode, gridBagConstraints);

        jRBPolygon.setBackground(new java.awt.Color(255, 255, 255));
        jBGLabelType.add(jRBPolygon);
        jRBPolygon.setText("Polygon");
        jRBPolygon.setEnabled(false);
        jRBPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBPolygonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPVLabelType.add(jRBPolygon, gridBagConstraints);

        jBNewPolygonObj.setBackground(new java.awt.Color(255, 255, 255));
        jBNewPolygonObj.setText("+");
        jBNewPolygonObj.setEnabled(false);
        jBNewPolygonObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNewPolygonObjActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPVLabelType.add(jBNewPolygonObj, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPImgOpt.add(jPVLabelType, gridBagConstraints);

        jSPObjList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jSPObjList.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jSPObjList.setAutoscrolls(true);
        jSPObjList.setPreferredSize(new java.awt.Dimension(130, 100));

        jPObjList.setBackground(new java.awt.Color(255, 255, 255));
        jPObjList.setBorder(javax.swing.BorderFactory.createTitledBorder("Objects List"));
        jPObjList.setAutoscrolls(true);
        jPObjList.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.5;
        jPObjList.add(jLAlignNorth, gridBagConstraints);

        jSPObjList.setViewportView(jPObjList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPImgOpt.add(jSPObjList, gridBagConstraints);

        jPObjInfo.setBackground(new java.awt.Color(255, 255, 255));
        jPObjInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Objects Info"));
        jPObjInfo.setLayout(new java.awt.GridBagLayout());

        jLTotalNoObjs.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPObjInfo.add(jLTotalNoObjs, gridBagConstraints);

        jLNoBBoxObjs.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPObjInfo.add(jLNoBBoxObjs, gridBagConstraints);

        jLNoScribbleObjs.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPObjInfo.add(jLNoScribbleObjs, gridBagConstraints);

        jLTotalNoObjsText.setText("Total Objects");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjInfo.add(jLTotalNoObjsText, gridBagConstraints);

        jLNoBBoxObjsText.setText("Box Obj");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjInfo.add(jLNoBBoxObjsText, gridBagConstraints);

        jLNoScribbleObjsText.setText("Scribble Obj");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjInfo.add(jLNoScribbleObjsText, gridBagConstraints);

        jLNoPolygonObjsText.setText("Polygon Obj");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPObjInfo.add(jLNoPolygonObjsText, gridBagConstraints);

        jLNoPolygonObjs.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        jPObjInfo.add(jLNoPolygonObjs, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 0);
        jPImgOpt.add(jPObjInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        jPView.add(jPImgOpt, gridBagConstraints);

        jPVideoPrev.setBackground(new java.awt.Color(255, 255, 255));
        jPVideoPrev.setLayout(new java.awt.GridBagLayout());

        jPVImgToLabel.setBackground(new java.awt.Color(255, 255, 255));
        jPVImgToLabel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPVImgToLabel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        jPVideoPrev.add(jPVImgToLabel, gridBagConstraints);

        jPNavigation.setBackground(new java.awt.Color(255, 255, 255));
        jPNavigation.setBorder(javax.swing.BorderFactory.createTitledBorder("Navigation"));
        jPNavigation.setLayout(new java.awt.GridBagLayout());

        jBPrevFrame.setIcon(Icons.PREVIOUS_ICON);
        jBPrevFrame.setEnabled(false);
        jBPrevFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPrevFrameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPNavigation.add(jBPrevFrame, gridBagConstraints);

        jBNextFrame.setIcon(Icons.NEXT_ICON);
        jBNextFrame.setEnabled(false);
        jBNextFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNextFrameActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPNavigation.add(jBNextFrame, gridBagConstraints);

        jLFrameNo.setText("F: 000001");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPNavigation.add(jLFrameNo, gridBagConstraints);

        jSVideoPlayer.setBackground(new java.awt.Color(255, 255, 255));
        jSVideoPlayer.setValue(0);
        jSVideoPlayer.setEnabled(false);
        jSVideoPlayer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSVideoPlayerMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPNavigation.add(jSVideoPlayer, gridBagConstraints);

        jBPlay.setIcon(Icons.PLAY_ICON);
        jBPlay.setEnabled(false);
        jBPlay.setName("Play"); // NOI18N
        jBPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBPlayActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPNavigation.add(jBPlay, gridBagConstraints);

        jCBPlayBackward.setBackground(new java.awt.Color(255, 255, 255));
        jCBPlayBackward.setText("Play Backward");
        jCBPlayBackward.setEnabled(false);
        jCBPlayBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPlayBackwardActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPNavigation.add(jCBPlayBackward, gridBagConstraints);

        jCBFlipVerticallyImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBFlipVerticallyImg.setText("Flip Vertically");
        jCBFlipVerticallyImg.setEnabled(false);
        jCBFlipVerticallyImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBFlipVerticallyImgActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.02;
        jPNavigation.add(jCBFlipVerticallyImg, gridBagConstraints);

        jCBMirrorImg.setBackground(new java.awt.Color(255, 255, 255));
        jCBMirrorImg.setText("Mirror");
        jCBMirrorImg.setEnabled(false);
        jCBMirrorImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMirrorImgActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPNavigation.add(jCBMirrorImg, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPVideoPrev.add(jPNavigation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 0);
        jPView.add(jPVideoPrev, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPLabeling.add(jPView, gridBagConstraints);

        jPOptions.setBackground(new java.awt.Color(255, 255, 255));
        jPOptions.setLayout(new java.awt.GridBagLayout());

        jPSceneDescription.setBackground(new java.awt.Color(255, 255, 255));
        jPSceneDescription.setBorder(javax.swing.BorderFactory.createTitledBorder("Scene Description"));
        jPSceneDescription.setLayout(new java.awt.GridBagLayout());

        jLIllumination.setText("Illumination");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        jPSceneDescription.add(jLIllumination, gridBagConstraints);

        jCBIllumination.setPreferredSize(new java.awt.Dimension(100, 26));
        jCBIllumination.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBIlluminationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPSceneDescription.add(jCBIllumination, gridBagConstraints);

        jLWeather.setText("Weather");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        jPSceneDescription.add(jLWeather, gridBagConstraints);

        jLRoadType.setText("Road Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        jPSceneDescription.add(jLRoadType, gridBagConstraints);

        jLRoadEvent.setText("Road Event");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        jPSceneDescription.add(jLRoadEvent, gridBagConstraints);

        jCBWeather.setPreferredSize(new java.awt.Dimension(100, 26));
        jCBWeather.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBWeatherActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPSceneDescription.add(jCBWeather, gridBagConstraints);

        jCBRoadType.setPreferredSize(new java.awt.Dimension(100, 26));
        jCBRoadType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBRoadTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPSceneDescription.add(jCBRoadType, gridBagConstraints);

        jCBRoadEvent.setPreferredSize(new java.awt.Dimension(100, 26));
        jCBRoadEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBRoadEventActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPSceneDescription.add(jCBRoadEvent, gridBagConstraints);

        jLCountry.setBackground(new java.awt.Color(255, 255, 255));
        jLCountry.setLabelFor(jCBCountry);
        jLCountry.setText("Country");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        jPSceneDescription.add(jLCountry, gridBagConstraints);

        jCBCountry.setMinimumSize(new java.awt.Dimension(140, 32));
        jCBCountry.setPreferredSize(new java.awt.Dimension(100, 26));
        jCBCountry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCountryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        jPSceneDescription.add(jCBCountry, gridBagConstraints);

        jCBWipersVisible.setBackground(new java.awt.Color(255, 255, 255));
        jCBWipersVisible.setText("wipers");
        jCBWipersVisible.setToolTipText("The wipers are visible in the image");
        jCBWipersVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBWipersVisibleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPSceneDescription.add(jCBWipersVisible, gridBagConstraints);

        jCBDirtVisible.setBackground(new java.awt.Color(255, 255, 255));
        jCBDirtVisible.setText("dirt");
        jCBDirtVisible.setToolTipText("There is dirt in the image");
        jCBDirtVisible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDirtVisibleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPSceneDescription.add(jCBDirtVisible, gridBagConstraints);

        jCBImageDistorted.setBackground(new java.awt.Color(255, 255, 255));
        jCBImageDistorted.setText("distortion");
        jCBImageDistorted.setToolTipText("The image is distorted");
        jCBImageDistorted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBImageDistortedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPSceneDescription.add(jCBImageDistorted, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 3, 3);
        jPOptions.add(jPSceneDescription, gridBagConstraints);

        jPLoadSource.setBackground(new java.awt.Color(255, 255, 255));
        jPLoadSource.setBorder(javax.swing.BorderFactory.createTitledBorder("Load"));
        jPLoadSource.setLayout(new java.awt.GridBagLayout());

        jBLoadFile.setBackground(new java.awt.Color(255, 255, 255));
        jBLoadFile.setIcon(Icons.LOAD_FILE_ICON);
        jBLoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBLoadFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPLoadSource.add(jBLoadFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 3, 3);
        jPOptions.add(jPLoadSource, gridBagConstraints);

        jPCropOpt.setBackground(new java.awt.Color(255, 255, 255));
        jPCropOpt.setBorder(javax.swing.BorderFactory.createTitledBorder("Object Options"));
        jPCropOpt.setLayout(new java.awt.GridBagLayout());

        jCBShowCurrentObj.setBackground(new java.awt.Color(255, 255, 255));
        jCBShowCurrentObj.setText("Show Current Obj");
        jCBShowCurrentObj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBShowCurrentObjActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPCropOpt.add(jCBShowCurrentObj, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 3, 3);
        jPOptions.add(jPCropOpt, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPLabeling.add(jPOptions, gridBagConstraints);

        jTPLabelingOptions.addTab("Labeling", jPLabeling);

        jPLog.setBackground(new java.awt.Color(255, 255, 255));
        jPLog.setLayout(new java.awt.GridBagLayout());

        jTALog.setEditable(false);
        jTALog.setColumns(20);
        jTALog.setRows(5);
        jTALog.setToolTipText("Contains the recording of all actions.");
        jTALog.setWrapStyleWord(true);
        jSPLog.setViewportView(jTALog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPLog.add(jSPLog, gridBagConstraints);

        jTPLabelingOptions.addTab("Log", jPLog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.01;
        gridBagConstraints.weighty = 0.01;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPBackground.add(jTPLabelingOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.05;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(jPBackground, gridBagConstraints);

        jMFile.setText("File");

        jMILoadFile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMILoadFile.setText("Load File");
        jMILoadFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMILoadFileActionPerformed(evt);
            }
        });
        jMFile.add(jMILoadFile);

        jMIExit.setText("Exit");
        jMIExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExitActionPerformed(evt);
            }
        });
        jMFile.add(jMIExit);

        jMBMenu.add(jMFile);

        jMEditObject.setText("Edit");
        jMEditObject.setEnabled(false);

        jMIMoveLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        jMIMoveLeft.setText("Move Left");
        jMIMoveLeft.setToolTipText("Move the whole object, one pixel, left");
        jMIMoveLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveLeft);

        jMIMoveRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        jMIMoveRight.setText("Move Right");
        jMIMoveRight.setToolTipText("Move the whole object, one pixel, right");
        jMIMoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveRight);

        jMIMoveUp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        jMIMoveUp.setText("Move Up");
        jMIMoveUp.setToolTipText("Move the whole object, one pixel, up");
        jMIMoveUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveUpActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveUp);

        jMIMoveDown.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        jMIMoveDown.setText("Move Down");
        jMIMoveDown.setToolTipText("Move the whole object, one pixel, down");
        jMIMoveDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIMoveDownActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIMoveDown);
        jMEditObject.add(jSeparator1);

        jMIDecreaseLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseLeft.setText("Decrease Left");
        jMIDecreaseLeft.setToolTipText("Remove one pixel from the left side of the object");
        jMIDecreaseLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseLeft);

        jMIDecreaseRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseRight.setText("Decrease Right");
        jMIDecreaseRight.setToolTipText("Remove one pixel from the right side of the object");
        jMIDecreaseRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseRight);

        jMIDecreaseTop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseTop.setText("Decrease Top");
        jMIDecreaseTop.setToolTipText("Remove one pixel from the top side of the object");
        jMIDecreaseTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseTopActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseTop);

        jMIDecreaseBottom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMIDecreaseBottom.setText("Decrease Bottom");
        jMIDecreaseBottom.setToolTipText("Remove one pixel from the bottom side of the object");
        jMIDecreaseBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBottomActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseBottom);
        jMEditObject.add(jSeparator3);

        jMIIncreaseLeft.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseLeft.setText("Increase Left");
        jMIIncreaseLeft.setToolTipText("Add a pixel on the left side of the object");
        jMIIncreaseLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseLeftActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseLeft);

        jMIIncreaseRight.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseRight.setText("Increase Right");
        jMIIncreaseRight.setToolTipText("Add a pixel on the right side of the object");
        jMIIncreaseRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseRightActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseRight);

        jMIIncreaseTop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseTop.setText("Increase Top");
        jMIIncreaseTop.setToolTipText("Add a pixel on the top side of the object");
        jMIIncreaseTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseTopActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseTop);

        jMIIncreaseBottom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseBottom.setText("Increase Bottom");
        jMIIncreaseBottom.setToolTipText("Add a pixel on the bottom side of the object");
        jMIIncreaseBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseBottomActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseBottom);
        jMEditObject.add(jSeparator2);

        jMIDecreaseBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.ALT_MASK));
        jMIDecreaseBox.setText("Decrease Box");
        jMIDecreaseBox.setToolTipText("Remove one pixel from all sides of the object");
        jMIDecreaseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDecreaseBoxActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDecreaseBox);

        jMIIncreaseBox.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        jMIIncreaseBox.setText("Increase Box");
        jMIIncreaseBox.setToolTipText("Add one pixel to all sides of the object");
        jMIIncreaseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIIncreaseBoxActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIIncreaseBox);
        jMEditObject.add(jSeparator4);

        jMICancelObject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMICancelObject.setText("Cancel Object");
        jMICancelObject.setToolTipText("Cancel the object in progress / Remove the selection for an existing object");
        jMICancelObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMICancelObjectActionPerformed(evt);
            }
        });
        jMEditObject.add(jMICancelObject);

        jMIDeleteObject.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        jMIDeleteObject.setText("Delete Object");
        jMIDeleteObject.setToolTipText("Delete the selected object");
        jMIDeleteObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIDeleteObjectActionPerformed(evt);
            }
        });
        jMEditObject.add(jMIDeleteObject);

        jMBMenu.add(jMEditObject);

        jMNavigation.setText("Navigation");
        jMNavigation.setEnabled(false);

        jMINextFrame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT, 0));
        jMINextFrame.setText("Next Frame/Image");
        jMINextFrame.setToolTipText("Save data and go to the next frame/image");
        jMINextFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMINextFrameActionPerformed(evt);
            }
        });
        jMNavigation.add(jMINextFrame);

        jMIPrevFrame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, 0));
        jMIPrevFrame.setText("Previous Frame/Image");
        jMIPrevFrame.setToolTipText("Save data and go to the previous frame/image");
        jMIPrevFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIPrevFrameActionPerformed(evt);
            }
        });
        jMNavigation.add(jMIPrevFrame);

        jMBMenu.add(jMNavigation);

        jMOptions.setText("Options");

        jMIUserConfig.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMIUserConfig.setText("Configuration");
        jMIUserConfig.setEnabled(false);
        jMIUserConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIUserConfigActionPerformed(evt);
            }
        });
        jMOptions.add(jMIUserConfig);
        jMOptions.add(jSAttribDef);

        jMIAttributes.setText("Attributes Definition");
        jMIAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIAttributesActionPerformed(evt);
            }
        });
        jMOptions.add(jMIAttributes);
        jMOptions.add(jSeparator7);

        jMILanguage.setText("Language");
        jMILanguage.setEnabled(false);
        jMOptions.add(jMILanguage);

        jMBMenu.add(jMOptions);

        jMExport.setText("Exports");
        jMExport.setEnabled(false);

        jMIExportOrigImg.setText(Constants.ORIGINAL_IMG);
        jMIExportOrigImg.setToolTipText("Exports the clear, original, image");
        jMIExportOrigImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExportOrigImgActionPerformed(evt);
            }
        });
        jMExport.add(jMIExportOrigImg);

        jMIExportWorkingImg.setText(Constants.SEGMENTED_IMG);
        jMIExportWorkingImg.setToolTipText("Exports the image with the segmentation done");
        jMIExportWorkingImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExportWorkingImgActionPerformed(evt);
            }
        });
        jMExport.add(jMIExportWorkingImg);

        jMIExportSemanticImg.setText(Constants.SEMANTIC_SEGMENTATION_IMG);
        jMIExportSemanticImg.setToolTipText("Exports the results image (side panel)");
        jMIExportSemanticImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExportSemanticImgActionPerformed(evt);
            }
        });
        jMExport.add(jMIExportSemanticImg);

        jMIExportJoinedImg.setText(Constants.JOINED_IMGS);
        jMIExportJoinedImg.setToolTipText("Exports the original image and the result one, together");
        jMIExportJoinedImg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExportJoinedImgActionPerformed(evt);
            }
        });
        jMExport.add(jMIExportJoinedImg);
        jMExport.add(jSeparator5);

        jMIExportConfig.setText("Export Configuration");
        jMIExportConfig.setToolTipText("Go to the export configurations");
        jMIExportConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIExportConfigActionPerformed(evt);
            }
        });
        jMExport.add(jMIExportConfig);

        jMBMenu.add(jMExport);

        jMHelp.setText("Help");

        jMIAbout.setText("About");
        jMIAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIAboutActionPerformed(evt);
            }
        });
        jMHelp.add(jMIAbout);

        jMIHotkeys.setText("Hotkeys");
        jMIHotkeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIHotkeysActionPerformed(evt);
            }
        });
        jMHelp.add(jMIHotkeys);

        jMIManualApp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMIManualApp.setText("Manual");
        jMIManualApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIManualAppActionPerformed(evt);
            }
        });
        jMHelp.add(jMIManualApp);

        jMBMenu.add(jMHelp);

        setJMenuBar(jMBMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMIExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExitActionPerformed
        closeApplication();
    }//GEN-LAST:event_jMIExitActionPerformed

    private void jBLoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBLoadFileActionPerformed
        loadNewFile();
    }//GEN-LAST:event_jBLoadFileActionPerformed

    private void jRBScribbleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBScribbleActionPerformed
        setScribbleMode();
    }//GEN-LAST:event_jRBScribbleActionPerformed

    private void jRBBoundingBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBBoundingBoxActionPerformed
        setBoundingBoxMode();
    }//GEN-LAST:event_jRBBoundingBoxActionPerformed

    private void jMILoadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMILoadFileActionPerformed
        loadNewFile();
    }//GEN-LAST:event_jMILoadFileActionPerformed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        // compute the screen resolution (the user might have dragged the application on another screen)
        screenRes.setScreenResolution(this);
        defaultDPIValue = Toolkit.getDefaultToolkit().getScreenResolution();

        adjustWindowDimensions(evt.getNewState(), getAvailableScreenSize(userPrefs.getPreferredDPI()));
        log.info("**screen resolution: {}x{}", screenRes.getScreenResolution().width, screenRes.getScreenResolution().height);
    }//GEN-LAST:event_formWindowStateChanged

    private void jBPrevFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPrevFrameActionPerformed
        getFrame(Constants.PREV_FRAME);
    }//GEN-LAST:event_jBPrevFrameActionPerformed

    private void jBNextFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNextFrameActionPerformed
        // get the next frame; save the data and load other data if there exists any
        getFrame(Constants.NEXT_FRAME);
    }//GEN-LAST:event_jBNextFrameActionPerformed

    private void jBJumpFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBJumpFrameActionPerformed
        long frame;
        frame = Long.parseLong(jTFJumpFrame.getText());

        jSVideoPlayer.setValue((int) frame);

        getFrame(Constants.JUMP_TO_FRAME);
    }//GEN-LAST:event_jBJumpFrameActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeApplication();
    }//GEN-LAST:event_formWindowClosing

    private void jBPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBPlayActionPerformed
        // if the video is not playing, create a new thread and play the video
        if ((PLAY_BUTTON_TEXT).equals(jBPlay.getName())) {
            // save the current work (if wanted) and remove all the drawings from the image
            if (Constants.GTSaveInterruptCodes.FRAME_ATTRIB_NOT_SET == removeObjsOverlays()) {
                // the user wants to correct the frame attributes, do not play the video
                return;
            }

            jBPlay.setName(PAUSE_BUTTON_TEXT);
            jBPlay.setIcon(Icons.PAUSE_ICON);
            // stop the drawing
            gc.setLabelingType(DrawConstants.DrawType.DO_NOT_DRAW);

            gc.playVideo(this);
        } else if ((PAUSE_BUTTON_TEXT).equals(jBPlay.getName())) {
            jBPlay.setName(PLAY_BUTTON_TEXT);
            jBPlay.setIcon(Icons.PLAY_ICON);

            // activate the drawing
            if (jRBScribble.isSelected()) {
                gc.setLabelingType(DrawConstants.DrawType.DRAW_CROP);
            } else if (jRBBoundingBox.isSelected()) {
                gc.setLabelingType(DrawConstants.DrawType.DRAW_BOUNDING_BOX);
            }

            gc.pausePlayVideo();
        }
    }//GEN-LAST:event_jBPlayActionPerformed

    private void jSVideoPlayerMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSVideoPlayerMouseReleased
        if (jSVideoPlayer.isEnabled()) {
            getFrame(Constants.JUMP_TO_FRAME);
        }
    }//GEN-LAST:event_jSVideoPlayerMouseReleased

    private void jMIAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIAboutActionPerformed
        new About(new javax.swing.JFrame(), true, "About Pixie").setVisible(true);
    }//GEN-LAST:event_jMIAboutActionPerformed

    private void jMIManualAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIManualAppActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(Constants.APPLICATION_MANUAL);
                if (myFile.exists()) {
                    Desktop.getDesktop().open(myFile);
                } else {
                    log.error("Application manual not found!");
                }
            } catch (IOException ex) {
                log.error("Application manual not found or not accessible");
                log.debug("Application manual not found or not accessible {}", ex);
            }
        }
    }//GEN-LAST:event_jMIManualAppActionPerformed

    private void jBNewScribbleObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewScribbleObjActionPerformed
        // new object is available for scribble purposes and edit mode
        if (jRBScribble.isSelected()) {
            if (jBNewScribbleObj.getText().equals(NEW_OBJ_BUTTON_TEXT)) {
                jRBBoundingBox.setEnabled(true);
                jRBScribble.setEnabled(true);

                gc.setLabelingType(DrawConstants.DrawType.DRAW_CROP);

                // do not draw transparent box
                gc.resetSelectedObject();

                // init the new object
                gc.initCurrentObject();

                // start counting the time for the scribble object
                gc.startCurrentObjectTimer();

                // trigger the regeneration of the display lists based on the user options
                gc.refreshDisplayList();

                jBNewScribbleObj.setText(SAVE_OBJ_BUTTON_TEXT);

            } else if (jBNewScribbleObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {
                // save the current object in the object list
                addScribbleObjToObjList();

                // no drawing can be done while there is no new object
                gc.setLabelingType(DrawConstants.DrawType.DO_NOT_DRAW);

                jBNewScribbleObj.setText(NEW_OBJ_BUTTON_TEXT);
            }

            // do not draw a box with what was drawn before
            gc.resetBBox();
        } else if (jRBEditMode.isSelected()) {
            // enable the drawing of one crop, one time only
            gc.setAddCropToObj();
        }
    }//GEN-LAST:event_jBNewScribbleObjActionPerformed

    private void jRBEditModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBEditModeActionPerformed
        setEditMode();
    }//GEN-LAST:event_jRBEditModeActionPerformed

    private void jCBPlayBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPlayBackwardActionPerformed
        if (jCBPlayBackward.isSelected()) {
            gc.setPlayMode(Constants.PLAY_MODE_BACKWARD);
        } else {
            gc.setPlayMode(Constants.PLAY_MODE_FORWARD);
        }

        userPrefs.setPlayBackward(jCBPlayBackward.isSelected());

        getFrame(Constants.JUMP_TO_FRAME);
    }//GEN-LAST:event_jCBPlayBackwardActionPerformed

    private void jMIHotkeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIHotkeysActionPerformed
        new About(new javax.swing.JFrame(), true, "Pixie Hotkeys").setVisible(true);
    }//GEN-LAST:event_jMIHotkeysActionPerformed

    private void jRBPolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBPolygonActionPerformed
        setPolygonMode();
    }//GEN-LAST:event_jRBPolygonActionPerformed

    private void jBNewPolygonObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNewPolygonObjActionPerformed
        // new object is available for scribble purposes and edit mode
        if (jRBPolygon.isSelected()) {
            if (jBNewPolygonObj.getText().equals(NEW_OBJ_BUTTON_TEXT)) {
                gc.setLabelingType(DrawConstants.DrawType.DRAW_POLYGON);

                // do not draw transparent box
                gc.resetSelectedObject();

                // init the new object
                gc.initCurrentObject();

                // start counting the time for the scribble object
                gc.startCurrentObjectTimer();

                // trigger the regeneration of the display lists based on the user options
                gc.refreshDisplayList();

                // initialise the polygon object
                gc.initPolygon();

                jBNewPolygonObj.setText(SAVE_OBJ_BUTTON_TEXT);

            } else if (jBNewPolygonObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {
                // no drawing can be done while there is no new object
                gc.setLabelingType(DrawConstants.DrawType.DO_NOT_DRAW);

                // open the preview of the object
                gc.openPolyPrevWin();

                // set the text of the new/save button
                jBNewPolygonObj.setText(NEW_OBJ_BUTTON_TEXT);
            }

            // do not draw a box with what was drawn before
            gc.resetBBox();
        }
    }//GEN-LAST:event_jBNewPolygonObjActionPerformed

    private void jCBFlipVerticallyImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBFlipVerticallyImgActionPerformed
        gc.setFlipVerticallyImage(jCBFlipVerticallyImg.isSelected());

        // save the user preferences
        userPrefs.setFlipVertically(jCBFlipVerticallyImg.isSelected());
    }//GEN-LAST:event_jCBFlipVerticallyImgActionPerformed

    private void jCBMirrorImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMirrorImgActionPerformed
        gc.setMirrorImage(jCBMirrorImg.isSelected());

        // save the user preferences
        userPrefs.setMirrorImage(jCBMirrorImg.isSelected());
    }//GEN-LAST:event_jCBMirrorImgActionPerformed

    private void jMIUserConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIUserConfigActionPerformed
        createConfigWindow().setVisible(true);
    }//GEN-LAST:event_jMIUserConfigActionPerformed

    private void jCBShowCurrentObjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBShowCurrentObjActionPerformed
        userPrefs.setShowJustCurrentObj(jCBShowCurrentObj.isSelected());

        gc.refreshDisplayList();
    }//GEN-LAST:event_jCBShowCurrentObjActionPerformed

    private void jCBIlluminationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBIlluminationActionPerformed
        currentFrame.setIllumination(jCBIllumination.getItemAt(jCBIllumination.getSelectedIndex()));
    }//GEN-LAST:event_jCBIlluminationActionPerformed

    private void jCBWeatherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBWeatherActionPerformed
        currentFrame.setWeather(jCBWeather.getItemAt(jCBWeather.getSelectedIndex()));
    }//GEN-LAST:event_jCBWeatherActionPerformed

    private void jCBRoadTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRoadTypeActionPerformed
        currentFrame.setRoadType(jCBRoadType.getItemAt(jCBRoadType.getSelectedIndex()));
    }//GEN-LAST:event_jCBRoadTypeActionPerformed

    private void jCBRoadEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRoadEventActionPerformed
        currentFrame.setRoadEvent(jCBRoadEvent.getItemAt(jCBRoadEvent.getSelectedIndex()));
    }//GEN-LAST:event_jCBRoadEventActionPerformed

    private void jCBCountryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCountryActionPerformed
        currentFrame.setCountry(jCBCountry.getItemAt(jCBCountry.getSelectedIndex()));
    }//GEN-LAST:event_jCBCountryActionPerformed

    private void jCBWipersVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBWipersVisibleActionPerformed
        currentFrame.setWipersVisible(jCBWipersVisible.isSelected());
    }//GEN-LAST:event_jCBWipersVisibleActionPerformed

    private void jCBDirtVisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDirtVisibleActionPerformed
        currentFrame.setDirtVisible(jCBDirtVisible.isSelected());

    }//GEN-LAST:event_jCBDirtVisibleActionPerformed

    private void jCBImageDistortedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBImageDistortedActionPerformed
        currentFrame.setImageDistorted(jCBImageDistorted.isSelected());
    }//GEN-LAST:event_jCBImageDistortedActionPerformed

    private void jMIAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIAttributesActionPerformed
        displayAttributesEditWin(AttributesDefinition.TAB_FRAME);
    }//GEN-LAST:event_jMIAttributesActionPerformed

    private void jPPrevResultImgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPPrevResultImgMouseClicked
        BufferedImage resultImg = gc.getImageResult();
        if (resultImg != null) {
            new ImagePreview(this, true, resultImg, "Image Result").setVisible(true);
        }
    }//GEN-LAST:event_jPPrevResultImgMouseClicked

    private void jMIExportOrigImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExportOrigImgActionPerformed
        ExportImage.exportImage(gc.getdPImgToLabel().getOrigImg(),
                userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.ORIGINAL_IMG.split(" ")[0]),
                userPrefs.getImgExportExtension());
    }//GEN-LAST:event_jMIExportOrigImgActionPerformed

    private void jMIExportWorkingImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExportWorkingImgActionPerformed
        ExportImage.exportImage(getSegmentedImage(),
                userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.SEGMENTED_IMG.split(" ")[0]),
                userPrefs.getImgExportExtension());
    }//GEN-LAST:event_jMIExportWorkingImgActionPerformed

    private void jMIExportSemanticImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExportSemanticImgActionPerformed
        ExportImage.exportImage(gc.getImageResult(),
                userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.SEMANTIC_SEGMENTATION_IMG.split(" ")[0]),
                userPrefs.getImgExportExtension());
    }//GEN-LAST:event_jMIExportSemanticImgActionPerformed

    private void jMIExportJoinedImgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExportJoinedImgActionPerformed
        // send the the original image, the segmented one and the result image
        ExportImagesGUI eiGUI = new ExportImagesGUI(this, gc.getdPImgToLabel().getOrigImg(),
                getSegmentedImage(),
                gc.getImageResult(),
                userPrefs.getImgExportPath(),
                createExportImgFileName("JoinedImages"),
                userPrefs.getImgExportExtension(),
                gc.isExistsScribbleObj());
        // resize the segmented preview image in order to avoid loosing the edges of the boxes due to the default resize
        Dimension fitSize = PanelResolution.computeOptimalPanelSize(gc.getdPImgToLabel().getOrigImgSize(), eiGUI.getPreviewSize());
        eiGUI.setSegmentedImgResized(getSegmentedImage(fitSize));

        eiGUI.setVisible(true);
    }//GEN-LAST:event_jMIExportJoinedImgActionPerformed

    private void jMIMoveLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveLeftActionPerformed
        // move the object to the left
        gc.moveSelection(-1, 0);
    }//GEN-LAST:event_jMIMoveLeftActionPerformed

    private void jMIMoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveRightActionPerformed
        // move the object to the right
        gc.moveSelection(1, 0);
    }//GEN-LAST:event_jMIMoveRightActionPerformed

    private void jMIMoveUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveUpActionPerformed
        // move the object up
        gc.moveSelection(0, -1);
    }//GEN-LAST:event_jMIMoveUpActionPerformed

    private void jMIMoveDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIMoveDownActionPerformed
        // move the object down
        gc.moveSelection(0, 1);
    }//GEN-LAST:event_jMIMoveDownActionPerformed

    private void jMIDecreaseLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseLeftActionPerformed
        // decrease left
        gc.changeSizeSelection(1, 0, 0, 0);
    }//GEN-LAST:event_jMIDecreaseLeftActionPerformed

    private void jMIDecreaseRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseRightActionPerformed
        // decrease right
        gc.changeSizeSelection(0, 0, -1, 0);
    }//GEN-LAST:event_jMIDecreaseRightActionPerformed

    private void jMIDecreaseTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseTopActionPerformed
        // decrease top
        gc.changeSizeSelection(0, 1, 0, 0);
    }//GEN-LAST:event_jMIDecreaseTopActionPerformed

    private void jMIDecreaseBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBottomActionPerformed
        // decrease bottom
        gc.changeSizeSelection(0, 0, 0, -1);
    }//GEN-LAST:event_jMIDecreaseBottomActionPerformed

    private void jMIIncreaseLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseLeftActionPerformed
        // increase left
        gc.changeSizeSelection(-1, 0, 0, 0);
    }//GEN-LAST:event_jMIIncreaseLeftActionPerformed

    private void jMIIncreaseRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseRightActionPerformed
        // increase right
        gc.changeSizeSelection(0, 0, 1, 0);
    }//GEN-LAST:event_jMIIncreaseRightActionPerformed

    private void jMIIncreaseTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseTopActionPerformed
        // increase top
        gc.changeSizeSelection(0, -1, 0, 0);
    }//GEN-LAST:event_jMIIncreaseTopActionPerformed

    private void jMIIncreaseBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBottomActionPerformed
        // increase bottom
        gc.changeSizeSelection(0, 0, 0, 1);
    }//GEN-LAST:event_jMIIncreaseBottomActionPerformed

    private void jMIDecreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDecreaseBoxActionPerformed
        // decrease box
        gc.changeSizeSelection(1, 1, -1, -1);
    }//GEN-LAST:event_jMIDecreaseBoxActionPerformed

    private void jMIIncreaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIIncreaseBoxActionPerformed
        // increase box
        gc.changeSizeSelection(-1, -1, 1, 1);
    }//GEN-LAST:event_jMIIncreaseBoxActionPerformed

    private void jMICancelObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMICancelObjectActionPerformed
        cancelCurrentObject();
    }//GEN-LAST:event_jMICancelObjectActionPerformed

    private void jMINextFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMINextFrameActionPerformed
        // get the next frame; save the data and load other data if there exists any
        getFrame(Constants.NEXT_FRAME);
    }//GEN-LAST:event_jMINextFrameActionPerformed

    private void jMIPrevFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIPrevFrameActionPerformed
        getFrame(Constants.PREV_FRAME);
    }//GEN-LAST:event_jMIPrevFrameActionPerformed

    private void jMIDeleteObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIDeleteObjectActionPerformed
        // delete the selected object
        gc.removeSelection();
    }//GEN-LAST:event_jMIDeleteObjectActionPerformed

    private void jMIExportConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIExportConfigActionPerformed
        UserConfigs configWin = createConfigWindow();
        configWin.switchToTab(UserConfigs.TAB_IMAGE_EXPORT);
        configWin.setVisible(true);
    }//GEN-LAST:event_jMIExportConfigActionPerformed

    /**
     * Save the previous work and load a new file. For the new file load the
     * ground truth and refresh data structures.
     */
    private void loadNewFile() {
        /* Check if there was another file open and segmentation already done. Ask the user to save its work.*/
        if (Constants.GTSaveInterruptCodes.FRAME_ATTRIB_NOT_SET == saveCurrentWork()) {
            // the frame attributes are not correct and the user wants to fix them
            return;
        }

        // open the file loader and choose the wanted file
        chooseLoadFile();

        // if the window is not maximized, pack the window and set a new min size
        if (this.getExtendedState() == javax.swing.JFrame.NORMAL) {
            packGUI();
        }
    }

    /**
     * Open the chooser window and allow the user to open the wanted file. After
     * choosing the wanted file, load all the needed data: image, ground truth
     * etc.
     */
    private void chooseLoadFile() {
        String newFile;

//        if (runningOnline) {
//            List<VideoFileInfo> videoList = gc.getVideoFileList();
//
//            // open the custom file chooser
//            CustomFileChooser fileChooser = new CustomFileChooser(this, true, videoList);
//            fileChooser.setVisible(true);
//
//            // get the index of the selected file
//            int idxSelection = fileChooser.getSelectedIndex();
//
//            // do not go further if the selection is not valid
//            if ((idxSelection < 0) || (idxSelection >= videoList.size())) {
//                return;
//            }
//
//            // open the selected file
//            newFile = videoList.get(idxSelection).getVideoPath();
//        } else {
            newFile = loadFileChooser();
//        }

        // make sure a valid path is returned
        if (!("").equals(newFile)) {
            // set the name of the chosen file to be able to open it
            gc.setChosenFile(newFile);

            // load the image on panel and all the other data available
            loadFile();

            // start counting the time spent in the current frame for the log table
//            frameTimer.start();
        }
    }

    /**
     * Shows the load file window and allows the user to select the wanted file.
     * The file chooser offers a series of advantages for loading files. Allows
     * the user to choose only specific files; it opens in the last used
     * directory etc.
     */
    private String loadFileChooser() {
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                userPrefs.setLastDirectory(chooser.getSelectedFile().getParent());
                return chooser.getSelectedFile().getCanonicalPath();
            }
        } catch (IOException ex) {
            log.error("File not found or not accessible");
            log.debug("File not found or not accessible {}", ex);
        }
        return "";
    }

    /**
     * Load the selected file, together with its ground truth. Refresh data
     * structures and the gui.
     */
    private void loadFile() {
        // reset the characteristics of the parent panel
        redrawViewPanel();

        // remove the old image if it exists
        jPVImgToLabel.removeAll();

        gc.loadImageOnPanel(getDrawType());

        // add the drawing panel to the gui
        jPVImgToLabel.add(gc.getdPImgToLabel());

        // initialise the characteristics of the video slider
        initVideoSlider();

        // display video info in gui
        updateGUIVideo();

        // show the total number of frames
        jLNoFrames.setText(gc.getNoFrames() + " frames");

        // generate the result image only with background
        initResultImage();

        // add pen listener to be able to use some features of the graphic tablet
        AwtPenToolkit.addPenListener(gc.getdPImgToLabel(), penFunctions);

        // enable and disable some components
        enableComponentsLoadImg();

        // run the matting algorithm if there are scribble objects
        gc.runMattingForObjList();

        // make the current object null
        gc.cancelCurrentObject();

        // reinit the list of used colors
        gc.reinitObjectColorsList();

    }

    /**
     * Load the segmented result image to a visualisation panel.
     */
    private void loadImgResult(BufferedImage resultImg) {
        // remove the old image if it exists

        jPPrevResultImg.removeAll();

        gc.loadImgResult(resultImg);

        jPPrevResultImg.setPreferredSize(new Dimension(Constants.RESULT_PANEL_WIDTH, Constants.RESULT_PANEL_HEIGHT));

        jPPrevResultImg.add(gc.getdPImgResult());

        this.validate();
        this.repaint();
        log.info("Loaded the result image.");
    }

    /**
     * Implementation for getting a red image from the algorithm.
     */
    private void initResultImage() {
        // draw the resulting image
        loadImgResult(gc.getdPImgToLabel().getOrigImg());
    }

    /**
     * Get the selected drawing type.
     */
    private DrawConstants.DrawType getDrawType() {
        if (jRBBoundingBox.isSelected()) {
            // if it is the bounding box selected, draw; else do not
            return DrawConstants.DrawType.DRAW_BOUNDING_BOX;
        }

        return DrawConstants.DrawType.DO_NOT_DRAW;
    }

    /**
     * Enables and disables the needed components when a video/image is loaded.
     */
    private void enableComponentsLoadImg() {
        // enable the user configuration window
        jMIUserConfig.setEnabled(true);
        jMExport.setEnabled(true);

        //// Enable functionalityes
        jRBBoundingBox.setEnabled(true);
        jRBScribble.setEnabled(true);
        jRBEditMode.setEnabled(true);
        jRBPolygon.setEnabled(true);

        // enable the video player buttons
        jBPlay.setEnabled(runningOnline);
        jBPrevFrame.setEnabled(true);
        jBNextFrame.setEnabled(true);
        jBJumpFrame.setEnabled(true);
        jSVideoPlayer.setEnabled(true);
        jCBPlayBackward.setEnabled(true);
        jCBFlipVerticallyImg.setEnabled(true);
        jCBMirrorImg.setEnabled(true);

        // enable navigation menu
        jMNavigation.setEnabled(true);

        // disable the new scribble object button
        jBNewScribbleObj.setEnabled(false);
        setShowSemanticOptions(false);

        // disaable the new polygon object button
        jBNewScribbleObj.setEnabled(false);

        if (jRBScribble.isSelected()) {
            // enable the new scribble object button
            jBNewScribbleObj.setEnabled(true);

            // show the results panel
            setShowSemanticOptions(true);

        } else if (jRBPolygon.isSelected()) {
            // enable the new polygon object button
            jBNewScribbleObj.setEnabled(true);
        }

        if (jBNewScribbleObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {
            jBNewScribbleObj.setText(NEW_OBJ_BUTTON_TEXT);
        }

        if (jBNewPolygonObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {
            jBNewPolygonObj.setText(NEW_OBJ_BUTTON_TEXT);
        }

        // if the load was done during the play of the video, set the button back to play, from pause
        if (jBPlay.getName().equals(PAUSE_BUTTON_TEXT)) {
            jBPlay.setName(PLAY_BUTTON_TEXT);
            jBPlay.setIcon(Icons.PLAY_ICON);

            // pause the previous thread (a new one will be created if the user plays the current video)
            gc.pausePlayVideo();

            // activate the drawing
            if (jRBScribble.isSelected()) {
                gc.setLabelingType(DrawConstants.DrawType.DRAW_CROP);
            } else if (jRBBoundingBox.isSelected()) {
                gc.setLabelingType(DrawConstants.DrawType.DRAW_BOUNDING_BOX);
            }
        }
    }

    /**
     * Save the result image in a file and the segmented objects in the ground
     * truth storage.
     */
    private void saveImgResultsAndData() {

        // set the annotation time
        currentFrame.setFrameDuration("0");

        // save the data in the ground truth storage
        gc.saveDataAsGroundTruth();
    }

    /**
     * Chooses which actions to happen based on the feedback from the observers.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ObservedActions.Action) {
            ObservedActions.Action type = ((ObservedActions.Action) arg);

            switch (type) {
                case REFRESH_FRAME_NO:
                    // while the video is playing, refresh the frame index and the slider
                    updateGUIVideo();
                    break;

                case ADD_OBJECT_ON_PANEL:
                    // update the objects list because a new object was saved
                    addObjButton(gc.getLastSavedObj());
                    break;

                case REFRESH_OBJ_LIST_PANEL:
                    refreshObjPanelList();
                    break;

                case OPEN_POLYGON_SEGMENTATION:
                    savePolygonObject();
                    break;

                case ADD_GUI_KEY_EVENT_DISPATCHER:
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(guiKeyEventDispatch);
                    break;

                case REMOVE_GUI_KEY_EVENT_DISPATCHER:
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(guiKeyEventDispatch);
                    break;

                case HIGHLIGHT_OBJECT:
                    highlightSelectedObject();
                    break;

                case EDIT_OBJECT_ACTION:
                    gc.editObject();
                    break;

                case LOAD_FRAME_ANNOTATION:
                    loadFrameAnnotation();

                    break;

                case REFRESH_DISPLAY:
                    gc.refreshUserConfigs();
                    gc.refreshDisplayList();
                    loadCBdata();
                    break;

                case REFRESH_PANEL_OPTIONS:
                    setShowSemanticOptions(gc.isExistsScribbleObj());
                    break;

                case RELOAD_ATTRIBUTES:
                    // reload frame attributes and reload combo boxes
                    loadCBdata();
                    break;

                case REFRESH_APPLICATION_VIEW:
                    previewScreenCalibration(userPrefs.getPreferredDPI(), false);
                    break;

                case DISPLAY_EDIT_ATTRIBUTES_WIN:
                    // the user chose to edit the attributes; display the dialog and allow it
                    displayAttributesEditWin(AttributesDefinition.TAB_OBJECT);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Add a new object on the panel with saved objects.
     *
     * @param obj the object for which an entry is created
     */
    private void addObjButton(Objects obj) {
        // prepare the position of the button
        GridBagConstraints gbc = new GridBagConstraints();
        // on the panel there is a label already
        gbc.gridy = jPObjList.getComponentCount() - 1;
        gbc.gridx = 0;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.01;
        gbc.insets = new java.awt.Insets(10, 2, 2, 2);

        // get the color of the object based on the labeling type: scribble, bbox
        java.awt.Color labelTypeColor = gc.getLabelTypeColor(obj);

        /*--------------------1. add object button----------------------------*/
        CustomColorButton jBObj = new CustomColorButton(labelTypeColor, Color.black, "id: " + obj.getObjectId());
        jBObj.setName("id: " + obj.getObjectId());
        jBObj.addActionListener(new ObjListButtons());
        jBObj.addObserver(this);

        /*--------------------2. add labels with object attributes------------*/
        // create the labels which keep the info related to the object: type, class, value, occluded
        String text = obj.getObjectType();
        JLabel jLType = new JLabel(text);

        text = obj.getObjectClass();
        JLabel jLClass = new JLabel(text);

        text = obj.getObjectValue();
        JLabel jLValue = new JLabel(text);

        text = obj.getOccluded();
        JLabel jLOccluded = new JLabel(text);

        /*--------------------3. create the panel with all the components-----*/
        // create the panel which keeps the object and its attributes
        JPanel jPObjPrev = new JPanel(new java.awt.GridLayout(5, 1));
        jPObjPrev.add(jBObj);

        jPObjPrev.add(jLType);
        jPObjPrev.add(jLClass);
        jPObjPrev.add(jLValue);
        jPObjPrev.add(jLOccluded);

        jPObjPrev.setBackground(Color.white);

        jPObjList.add(jPObjPrev, gbc);

        // refresh the object relatel info in the panel info (no of objects, how many of each type)
        refreshObjectInfo();
    }

    /**
     * Adds a label which is not needed for active purposes. It just aligns
     * north all the other components.
     */
    private void addButtonsAlignComponent() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 100;        // put the label as low as possible
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.weighty = 0.5;
        jPObjList.add(jLAlignNorth, gbc);
    }

    /**
     * Enable/Disable the fields in order to get to edit mode.
     */
    private void setEditMode() {
        gc.setLabelingType(DrawConstants.DrawType.EDIT_MODE);

        // scribble obj: ask to save the current object if it is not empty
        saveWorkScribbleObject();

        // polygon obj: ask to save the current object if it is not empty
        saveWorkPolygonObject();

        // enable the new object button to be able to add crop to object, in edit mode
        jBNewScribbleObj.setEnabled(true);

        // do not show the guide lines, because there is no segmentation going on
        gc.setShowGuideShape(false);
    }

    /**
     * Enable/Disable the fields in order to get to scribble mode.
     */
    private void setScribbleMode() {
        gc.setLabelingType(DrawConstants.DrawType.DO_NOT_DRAW);

        // polygon obj: ask to save the current object if it is not empty
        saveWorkPolygonObject();

        // display the results panel
        setShowSemanticOptions(true);

        // enable the new object button
        jBNewScribbleObj.setEnabled(true);

        // disable the polygon new object button
        jBNewPolygonObj.setEnabled(false);

        // reset the mouse position in order to avoid to draw a box which is not wanted
        gc.resetBBox();

        // do not draw transparent box
        gc.resetSelectedObject();

        // do not highlight any object in the objects list
        selectObjectButton(0L);
    }

    /**
     * Enable/Disable the fields in order to get to bounding box mode.
     */
    private void setBoundingBoxMode() {
        gc.setLabelingType(DrawConstants.DrawType.DRAW_BOUNDING_BOX);

        // scribble obj: ask to save the current object if it is not empty
        saveWorkScribbleObject();

        // polygon obj: ask to save the current object if it is not empty
        saveWorkPolygonObject();

        // do not draw transparent box
        gc.resetSelectedObject();

        // do not highlight any object in the objects list
        selectObjectButton(0L);

        // reset the mouse position in order to avoid to draw a box which is not wanted
        gc.resetBBox();
    }

    /**
     * Enable/Disable the fields in order to get to polygon mode.
     */
    private void setPolygonMode() {
        gc.setLabelingType(DrawConstants.DrawType.DO_NOT_DRAW);

        // scribble obj: ask to save the current object if it is not empty
        saveWorkScribbleObject();

        // enable the new polygon object button
        jBNewPolygonObj.setEnabled(true);

        // do not draw transparent box
        gc.resetSelectedObject();

        // do not highlight any object in the objects list
        selectObjectButton(0L);

        // reset the mouse position in order to avoid to draw a box which is not wanted
        gc.resetBBox();
    }

    /**
     * Asks the user to save the object in progress to the list of objects. If
     * the user agrees, the object is saved; else it is discarded. Disables the
     * new object button and removes the results panel.
     */
    private void saveWorkScribbleObject() {
        if (jBNewScribbleObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {

            int response = Messages.showWarningYesNoMessager(this,
                    "There is an object in progress.\nThe work will be lost if you choose no!\nDo you want to save it?",
                    "Object not saved!!!");

            if (JOptionPane.YES_OPTION == response) {
                // save the object
                addScribbleObjToObjList();

            } else {
                // reset the scribbles object if it was started but the user does not want to finish it

                // remove the object from the object map
                if (gc.getCurrentObject() != null) {
                    gc.cancelObjectMap(gc.getCurrentObject(), gc.getCurrentObject().getOuterBBox());
                }

                // cancel the object
                gc.cancelCurrentObject();
            }

            // change text to new object
            jBNewScribbleObj.setText(NEW_OBJ_BUTTON_TEXT);
        }

        // remove the results panel of the scribbles
        setShowSemanticOptions(gc.isExistsScribbleObj());

        // disable the new scribble object button
        jBNewScribbleObj.setEnabled(false);

        // disable the new polygon object button
        jBNewPolygonObj.setEnabled(false);
    }

    /**
     * Adds the current scribble object to the list of objects.
     */
    private void addScribbleObjToObjList() {
        // make sure the object is an instance of scribbles
        if (!(gc.getCurrentObject() instanceof ObjectScribble)) {
            return;
        }

        if (!((ObjectScribble) gc.getCurrentObject()).getCropList().isEmpty()) {
            // set the type and source of labeling used for the segmentation of the object
            gc.setSegmentationType(ConstantsLabeling.LABEL_SCRIBBLE);
            gc.setSegmentationSource(ConstantsLabeling.LABEL_SOURCE_MANUAL);

            // save the object in the objects list and erase the current object
            gc.addObjToObjectList();

            // update the result image with the new object
            gc.updateResultPanel();

            // mark the fact that a scribble object was drawn on the interface
            gc.setExistsScribbleObj(true);
            setShowSemanticOptions(true);
        }
    }

    /**
     * Adds the current scribble object to the list of objects.
     */
    private void addPolygonObjToObjList() {
        if (gc.getCurrentObject() instanceof ObjectPolygon) {
            // set the type and source of labeling used for the segmentation of the object
            gc.setSegmentationType(ConstantsLabeling.LABEL_POLYGON);
            gc.setSegmentationSource(ConstantsLabeling.LABEL_SOURCE_MANUAL);

            // save the object in the objects list and erase the current object
            gc.addObjToObjectList();
        }

    }

    /**
     * Ask the user to save the current work. Else erase it.
     *
     * @return an error/interruption code showing if there was something wrong
     * in the saving of data process or all went smooth
     */
    private Constants.GTSaveInterruptCodes saveCurrentWork() {
        if (gc.getObjectsListSize() > 0) {
            int response = Messages.showWarningYesNoMessager(this,
                    "There are objects which were not saved in the ground truth storage.\nThe work will be lost if you choose no!\nDo you want to save them?",
                    "Objects not saved!!!");
            if (JOptionPane.YES_OPTION == response) {
                //make sure the frame attributes are set; warn the user if not and stop the changing of frame
                if (!isFrameAttribSet()) {
                    return Constants.GTSaveInterruptCodes.FRAME_ATTRIB_NOT_SET;
                }

                // save the list of objects
                saveImgResultsAndData();
            }
        }
        // everything seems to be correct
        return Constants.GTSaveInterruptCodes.NO_ERROR;
    }

    /**
     * Get the frame specified by the action parameter: next, prev, jumpTo.
     *
     * @param action - shows which frame shall be retrieved: next, prev, jumpTo
     */
    private void getFrame(int action) {
        // make sure the video is loaded
        if (gc.isDataLoaded()) {

            //make sure the frame attributes are set; warn the user if not and stop the changing of frame
            if (!isFrameAttribSet()) {
                // the attributes are not saved correctly, the user wants to correct them
                return;
            }

            // save the results and the objects in the ground truth storage
            saveImgResultsAndData();

            // reset the timings of the objects because new ones have to be computed
            gc.resetAllObjsDuration();

            switch (action) {
                case Constants.NEXT_FRAME:
                    // automatic export of images if the user wants it
                    autoExportImages();

                    // if the frame is not the last one, load the next frame
                    Timings timer = new Timings();
                    timer.start();
                    gc.nextFrame();
                    timer.stop("next frame");
                    break;

                case Constants.PREV_FRAME:
                    gc.prevFrame();
                    break;

                case Constants.JUMP_TO_FRAME:
                    gc.jumpToFrame(jSVideoPlayer.getValue());
                    break;

                default:
                    log.warn("Unknown command. Known: next, prev, jumpTo!");
                    break;
            }
            // display video info in gui
            updateGUIVideo();

            // generate the result image only with background
            initResultImage();

            // reinit the list of used colors
            gc.reinitObjectColorsList();

            // switch to edit mode because the user has higher chances to correct a BBox/Scribble rather than creating a new one
            jRBEditMode.setSelected(true);
            setEditMode();

            // cancel the current object in order to avoid the carry of the bounding box in the next frames
            gc.cancelCurrentObject();

            // do not select any object
            resetSelectedObj();

            // run the matting algorithm if there are scribble objects
            gc.runMattingForObjList();

            // pop up the objects if the user wants it
            if (action == Constants.NEXT_FRAME) {
                gc.popUpObjects();
            }
        }
    }

    /**
     * Checks if the frame attributes are correctly set and asks the user to
     * correct them. If the user chooses to correct them, the application does
     * not continue with the saving of data; it will cancel the process and go
     * back to the normal state.
     *
     * @return true if the user wants to save the data as it is and false if the
     * saving of data shall not be done yet, because the user wants to correct
     * it
     */
    private boolean isFrameAttribSet() {
        // make sure the user wants to check the frame attributes
        if (!userPrefs.isCheckFrameAnnotations()) {
            return true;
        }

        StringBuilder invalidAttribute = new StringBuilder();

        // check each frame attribute and create a list of missing frame attributes
        Utils.checkAttributeValid(invalidAttribute, jCBIllumination.getItemAt(jCBIllumination.getSelectedIndex()), jLIllumination.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBWeather.getItemAt(jCBWeather.getSelectedIndex()), jLWeather.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBRoadType.getItemAt(jCBRoadType.getSelectedIndex()), jLRoadType.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBRoadEvent.getItemAt(jCBRoadEvent.getSelectedIndex()), jLRoadEvent.getText());
        Utils.checkAttributeValid(invalidAttribute, jCBCountry.getItemAt(jCBCountry.getSelectedIndex()), jLCountry.getText());

        // if missing attributes were found, display the message window to warn the user to correct the attributes
        if (invalidAttribute.length() > 0) {
            // remove the last comma and the last space from the string
            invalidAttribute.replace(invalidAttribute.length() - 2, invalidAttribute.length(), "");

            // ask the user if the current attributes shall be saved or not; if the user wants to correct them, stop the application and let him fix them
            int userChoice = Utils.messageAttributesSet(invalidAttribute.toString(), !runningOnline, this);

            if (userChoice == JOptionPane.CANCEL_OPTION) {
                // the user chose to edit the attributes; display the dialog and allow it
                displayAttributesEditWin(AttributesDefinition.TAB_FRAME);
            }

            // return false if the yes or edit attributes option were chosen
            return ((JOptionPane.YES_OPTION != userChoice) && (JOptionPane.CANCEL_OPTION != userChoice));
        }

        return true;
    }

    /**
     * Reset the selected object. Remove the highlights from the button and box.
     */
    private void resetSelectedObj() {
        // remove the highlight of the object
        gc.resetSelectedObject();

        // remove the highlight of the button
        selectObjectButton(0L);
    }

    /**
     * Remove the list of objects from the side panel and create it again.
     */
    private void refreshObjPanelList() {
        // refresh all the buttons from the panel

        // remove all buttons
        jPObjList.removeAll();

        // add the label which is aligning all the buttons north
        addButtonsAlignComponent();

        // create all the buttons again, from the list of ids
        List<Objects> objIdsList = gc.getObjectsList();

        objIdsList.forEach(obj -> addObjButton(obj));

        jPObjList.repaint();
        jPObjList.validate();

        // refresh the object related info in the panel info (no of objects, how many of each type)
        refreshObjectInfo();
    }

    /**
     * Refresh the information regarding the number of objects displayed on the
     * objects info panel: how many scribble objects, how many bounding box
     * objects etc.
     */
    private void refreshObjectInfo() {
        int noTotalObjs = gc.getObjectsListSize();
        int noBBoxObjs = 0;
        int noScribbleObjs = 0;
        int noPolygonObjs = 0;

        if (gc.getObjectsList() != null) {
            for (Objects obj : gc.getObjectsList()) {
                if (obj instanceof ObjectBBox) {
                    noBBoxObjs++;
                } else if (obj instanceof ObjectScribble) {
                    noScribbleObjs++;
                } else if (obj instanceof ObjectPolygon) {
                    noPolygonObjs++;
                }
            }
        }

        // show the panel only if there are objects
        if (noTotalObjs > 0) {
            jPObjInfo.setVisible(true);

            // set the number of objects
            jLTotalNoObjs.setText(Integer.toString(noTotalObjs));

            // set the number of bounding box objects
            jLNoBBoxObjsText.setVisible(noBBoxObjs != 0);
            jLNoBBoxObjs.setVisible(noBBoxObjs != 0);
            jLNoBBoxObjs.setText(Integer.toString(noBBoxObjs));

            // set the number of scribble objects
            jLNoScribbleObjsText.setVisible(noScribbleObjs != 0);
            jLNoScribbleObjs.setVisible(noScribbleObjs != 0);
            jLNoScribbleObjs.setText(Integer.toString(noScribbleObjs));

            // set the number of scribble objects
            jLNoPolygonObjsText.setVisible(noPolygonObjs != 0);
            jLNoPolygonObjs.setVisible(noPolygonObjs != 0);
            jLNoPolygonObjs.setText(Integer.toString(noPolygonObjs));

        } else {
            jPObjInfo.setVisible(false);
        }
    }

    /**
     * Save the segmented polygon object.
     */
    private void savePolygonObject() {
        // save the current object in the object list
        addPolygonObjToObjList();

        // reset the polygon object
        gc.resetPolygon();

        gc.setPolygonToDisplay();
        gc.setBBoxToDisplay();
    }

    /**
     * Asks the user to save the object in progress to the list of objects. If
     * the user agrees, the object is saved; else it is discarded. Disables the
     * new object button and removes the results panel.
     */
    private void saveWorkPolygonObject() {

        if (jBNewPolygonObj.getText().equals(SAVE_OBJ_BUTTON_TEXT)) {

            int response = Messages.showWarningYesNoMessager(this,
                    "There is an object in progress.\nThe work will be lost if you choose no!\nDo you want to save it?",
                    "Object not saved!!!");

            if (JOptionPane.YES_OPTION == response) {
                // save the current object in the object list
                addPolygonObjToObjList();

            } else {
                // reset the polygon object if it was started but the user does not want to finish it
                // cancel the object
                gc.cancelCurrentObject();
            }

            // change text to new object
            jBNewPolygonObj.setText(NEW_OBJ_BUTTON_TEXT);
        }

        // reset the polygon object
        gc.resetPolygon();

        gc.setPolygonToDisplay();
        gc.setBBoxToDisplay();
    }

    /**
     * Load the frame annotations to the combo boxes on the gui.
     */
    private void loadFrameAnnotation() {
        jCBIllumination.setSelectedItem(Utils.capitalize(currentFrame.getIllumination()));
        jCBWeather.setSelectedItem(Utils.capitalize(currentFrame.getWeather()));
        jCBRoadType.setSelectedItem(Utils.capitalize(currentFrame.getRoadType()));
        jCBRoadEvent.setSelectedItem(Utils.capitalize(currentFrame.getRoadEvent()));
        jCBCountry.setSelectedItem(Utils.capitalize(currentFrame.getCountry()));
        jCBWipersVisible.setSelected(currentFrame.isWipersVisible());
        jCBDirtVisible.setSelected(currentFrame.isDirtVisible());
        jCBImageDistorted.setSelected(currentFrame.isImageDistorted());
    }

    /**
     * Get the selected object and highlight it for the user to see it better.
     */
    private void highlightSelectedObject() {
        Objects selectedObj = gc.getSelectedObject();

        // if no object is selected, cancel the object and reurn
        if (selectedObj == null) {
            // if no object is selected, remove highlight
            selectObjectButton(-1L);

            // if no object is selected, cancel the current object
            gc.cancelCurrentObject();

            return;
        }

        // if there is an object selected, highlight it
        long idObj = selectedObj.getObjectId();

        // highlight the button
        selectObjectButton(idObj);

        // enable the edit buttons (if they have to be disabled, they will be later on)
        enableEditButtons(true);

        // display the preview results panel if it is a scribble object
        if (selectedObj instanceof ObjectScribble) {
            setShowSemanticOptions(true);

            // if the scribble object is selected, disable some edit options
            disableEditOptScribbleObj(selectedObj);
        } else {
            setShowSemanticOptions(gc.isExistsScribbleObj());
        }
    }

    /**
     * When selecting the scribble object (not one of its crops), the user
     * cannot edit its size by increasing/decreasing options, therefore they are
     * deactivated from the menu.
     *
     * @param selectedObj the object selected by the user
     */
    private void disableEditOptScribbleObj(Objects selectedObj) {
        if (selectedObj == null) {
            return;
        }

        // get the box which was selected
        DisplayBBox selectedBox = gc.getdPImgToLabel().getSelectedBox();

        if (selectedBox != null) {
            // convert coordinates to panel coordinates to be able to compare
            Rectangle objBoxPanel = gc.getdPImgToLabel().getResize().originalToResized(selectedObj.getOuterBBox());

            // if the outer box of the obejct and the selection match, it means the scribble object is selected (not a crop)
            if (objBoxPanel.equals(selectedBox.getPanelBox())
                    && (((ObjectScribble) selectedObj).getCrop(selectedBox.getPanelBox(), gc.getdPImgToLabel().getResize()) == null)) {
                // the selected object is the scribble obejct itself; disable some edit options
                enableEditButtons(false);
            }
        }
    }

    /**
     * Enable/Disable some of the menu options, which do not apply to all types
     * of objects, all the time.
     *
     * @param enable true if the fields should be enabled and false otherwise
     */
    private void enableEditButtons(boolean enable) {
        // the decrease object buttons
        jMIDecreaseLeft.setEnabled(enable);
        jMIDecreaseTop.setEnabled(enable);
        jMIDecreaseRight.setEnabled(enable);
        jMIDecreaseBottom.setEnabled(enable);

        // the increase object buttons
        jMIIncreaseLeft.setEnabled(enable);
        jMIIncreaseTop.setEnabled(enable);
        jMIIncreaseRight.setEnabled(enable);
        jMIIncreaseBottom.setEnabled(enable);

        // the increase/decrease box buttons
        jMIDecreaseBox.setEnabled(enable);
        jMIIncreaseBox.setEnabled(enable);
    }

    /**
     * Close application in a safe way. Close all the open resources and stop
     * the application.
     */
    private void closeApplication() {
        // ask the user to save its work if there are objects unsaved

        if (gc.getObjectsListSize() > 0) {
            int response = Messages.showQuestionYesNoCancelMessage(this,
                    "There are objects which were not saved in the ground truth storage.\nThe work will be lost if you choose no!\nDo you want to save them?",
                    "Objects not saved!!!");
            if (JOptionPane.YES_OPTION == response) {
                // make sure the frame attributes are saved and ask the user to correct them otherwise
                if (!isFrameAttribSet()) {
                    // the user wants to correct the data, do not close the application!
                    return;
                }

                // save the list of objects
                saveImgResultsAndData();
            } else if (JOptionPane.CANCEL_OPTION == response) {
                // do not close the application
                return;
            }
        }

        userPrefs.setPlayBackward(jCBPlayBackward.isSelected());

        userPrefs.saveUserPreferences();

        gc.terminateThreads();

        System.exit(0);
    }

    /**
     * Show different screen configurations to the user and let him choose the
     * best one, fitting to its screen configuration. The calibration shall
     * render the gui based on the 98 DPI, 144 DPI and 192 DPI. These DPIs are
     * used by most of the screens.
     */
    private void previewScreenCalibration(int targetDPI, boolean showMessage) {

        adjustWindowDimensions(getExtendedState(), getAvailableScreenSize(targetDPI));

        if (showMessage) {
            Messages.showOkMessage(this, "This is " + targetDPI + " DPI!", targetDPI + " DPI");
        }
    }

    /**
     * Compute the real available screen size, based on DPIs.
     *
     * @param targetDPI the DPI wanted by the user
     * @return the real screen dimension, based on the input DPI.
     */
    public Dimension getAvailableScreenSize(int targetDPI) {
        int realWidth = scalePXonDPI(targetDPI, defaultDPIValue, screenRes.getScreenWidth());
        int realHeight = scalePXonDPI(targetDPI, defaultDPIValue, screenRes.getScreenHeight());

        log.info("real size {} DPI: {}x{}", targetDPI, realWidth, realHeight);

        return new Dimension(realWidth, realHeight);
    }

    /**
     * Scale the given pixel value, based on the default and target values of
     * the DPI.
     *
     * @param targetDPI the used DPI for the application
     * @param defaultDPI the default DPI of the screen
     * @param pxValue the value to be transformed from default DPI screen size
     * to target DPI screen size
     * @return
     */
    private static int scalePXonDPI(int targetDPI, int defaultDPI, int pxValue) {
        // compute how many inches has the screen, based on its default DPI
        float inchValue = pxValue / (float) defaultDPI;

        // compute how many pixels are in the target DPI
        int supposedPXValue = (int) (inchValue * targetDPI);

        // get the resize factor between the wanted and the actual values
        float resizeFactor = supposedPXValue / (float) pxValue;

        // rescale the actual value with the rescale factor in order to get the real value, in target DPI system
        return ((int) (pxValue / resizeFactor));
    }

    /**
     * Compute the amount of space left, after drawing all the components, for
     * the image.
     *
     * @param usedResolution the resolution based on which the computation is
     * done
     * @return the maximum size on which the image to be labeled can be
     * displayed without affecting other gui components
     */
    private Dimension computeDrawingSize(Dimension usedResolution) {
        // get the width of the panels from the left and right of the drawing panel
        int resultImgWidth = Math.max(jPViewResults.getPreferredSize().width, jPViewResults.getWidth());
        int segmentOptWidth = Math.max(jPImgOpt.getPreferredSize().width, jPImgOpt.getWidth());

        // get the heights of the panels from the top and bottom of the drawing panel
        int videoNavigHeight = Math.max(jPNavigation.getPreferredSize().height, jPNavigation.getHeight());
        int annotOptHeight = Math.max(jPOptions.getPreferredSize().height, jPOptions.getHeight());
        int tabHeaderHeight = jTPLabelingOptions.getHeight() - jPLabeling.getHeight();
        int menuHeight = jMBMenu.getHeight();

        // compute the width and heights of the panels surrounding the drawing panel
        int widthPanels = segmentOptWidth + resultImgWidth;
        int heightPanels = videoNavigHeight + annotOptHeight + tabHeaderHeight + 2 * menuHeight; // consider 2 times the size of the menu bar due to the title bar which has similar height

        // based on the screen resolution, compute how mush space is left for the image to be displayed
        int maxWidth = usedResolution.width - widthPanels;
        int maxHeight = usedResolution.height - heightPanels;

        // allow some space for insets, OS toolbar etc.
        Dimension resolution = new Dimension((int) (maxWidth - (maxWidth * Constants.TOLERANCE_WIDTH)), (int) (maxHeight - (maxHeight * Constants.TOLERANCE_HEIGHT)));

        log.info("**hight: {}, {}, {}, {}", videoNavigHeight, annotOptHeight, tabHeaderHeight, menuHeight);
        log.info("**resolutions: {}x{}, width panels: {}, height panels: {}", maxWidth, maxHeight, widthPanels, heightPanels);
        log.info("**max resolution: {}x{}", resolution.width, resolution.height);

        return resolution;
    }

    /**
     * Adjust the size of the screen and its components in such way as to have
     * them all fitting to the screen.
     *
     * @param windowState the state of the window:<br>
     * <ul>
     * <li><code>NORMAL</code>
     * <br>Indicates that no state bits are set.
     * <li><code>ICONIFIED</code>
     * <li><code>MAXIMIZED_HORIZ</code>
     * <li><code>MAXIMIZED_VERT</code>
     * <li><code>MAXIMIZED_BOTH</code> - Concatenates
     * <code>MAXIMIZED_HORIZ</code> and <code>MAXIMIZED_VERT</code>.
     * </ul>
     */
    private void adjustWindowDimensions(int windowState, Dimension usedResolution) {
        // do not change anything if the application is in iconified state
        if (windowState == Frame.ICONIFIED) {
            return;
        }

        // when the drawing panel is valid, compute the
        if (gc.getdPImgToLabel() != null) {
            // compute the drawing size based on the gui components
            gc.setAvailableDrawSize(computeDrawingSize(usedResolution));

            // refresh the drawing panel and its image
            gc.refreshImage();

            // rearrange the view panel, to fit the new drawing panel size
            redrawViewPanel();
        }

        if (windowState == Frame.NORMAL) {
            // if the window is not maximized, pack the window and set a new min size
            packGUI();
        }
    }

    /**
     * Pack the window and set a new min size in order to avoid the resize to a
     * value where the components do no longer fit.
     */
    private void packGUI() {
        // avoid a min size greater than the new arrangement of panel size
        this.setMinimumSize(new Dimension(0, 0));

        this.pack();

        // make the screen the minimum size when the components still fit
        this.setMinimumSize(this.getSize());
    }

    /**
     * Ask the user to save the work; remove all the objects and all the
     * overlays. Cleans the display and the list of objects.
     *
     * @return an error/interruption code showing if there was something wrong
     * in the saving of data process or all went smooth
     */
    private Constants.GTSaveInterruptCodes removeObjsOverlays() {
        // ask user to save work
        if (Constants.GTSaveInterruptCodes.FRAME_ATTRIB_NOT_SET == saveCurrentWork()) {
            // the frame attributes are not correct and the user wants to fix them
            return Constants.GTSaveInterruptCodes.FRAME_ATTRIB_NOT_SET;
        }

        // remove objects
        gc.removeAllObjects();

        // refresh the display list (with nothing) in order to remove the overlays
        gc.refreshDisplayList();

        // everything seems fine
        return Constants.GTSaveInterruptCodes.NO_ERROR;
    }

    /**
     * Get the segmented image (containing the bounding boxes, crops and object
     * outer bounds) and resize it to the specified target dimension.
     *
     * @return the image containing the object bounds (what the user is drawing
     * while segmenting), having the specified size
     */
    private BufferedImage getSegmentedImage() {
        return getSegmentedImage(new Resize(1.0, 1.0));
    }

    /**
     * Get the segmented image (containing the bounding boxes, crops and object
     * outer bounds) and resize it to the specified target dimension.
     *
     * @return the image containing the object bounds (what the user is drawing
     * while segmenting), having the specified size
     */
    private BufferedImage getSegmentedImage(Dimension targetDimension) {
        Dimension origImgDim = gc.getdPImgToLabel().getOrigImgSize();
        Resize resize = new Resize(origImgDim.width, origImgDim.height, targetDimension.width, targetDimension.height);
        return getSegmentedImage(resize);
    }

    /**
     * Get the segmented image (containing the bounding boxes, crops and object
     * outer bounds).
     *
     * @param resize the resize ratio used for the drawing of the objects
     * @return the image containing the object bounds (what the user is drawing
     * while segmenting)
     */
    private BufferedImage getSegmentedImage(Resize resize) {
        Dimension origImgDim = gc.getdPImgToLabel().getOrigImgSize();

        BufferedImage segmentedImage = resize.resizeImage(gc.getdPImgToLabel().getOrigImg());

        Graphics2D g2d = segmentedImage.createGraphics();

        for (Objects object : gc.getObjectsList()) {
            if (object instanceof ObjectPolygon) {
                // draw the polygon edges and points
                drawPolygonObject(object, g2d, resize);
            } else {
                Color alphaColor = new Color(object.getColor().getRed(), object.getColor().getGreen(), object.getColor().getBlue(), userPrefs.getObjAlphaVal());
                // draw the outer box of the object
                DrawOptions.drawBBox(g2d, resize.originalToResized(object.getOuterBBox()), object.getColor(), userPrefs.isShowObjHighlight(), alphaColor, false);
            }

            // display the object id on top of the object
            DrawOptions.displayObjectId(g2d, resize.originalToResized(object.getOuterBBox()), Long.toString(object.getObjectId()), object.getColor(), resize.originalToResized(origImgDim));
        }

        g2d.dispose();

        return segmentedImage;
    }

    /**
     * Draw the polygon object: draw its edges and points.
     *
     * @param object the object for which the data should be extracted and drawn
     * @param g2d the graphics object
     * @param resize the resize ratio used for the drawing of the polygon
     */
    private void drawPolygonObject(Objects object, Graphics2D g2d, Resize resize) {
        Polygon polygon = resize.originalToResized(((ObjectPolygon) object).getPolygon());
        Color alphaColor = new Color(object.getColor().getRed(), object.getColor().getGreen(), object.getColor().getBlue(), userPrefs.getObjAlphaVal());
        DrawOptions.drawPolygon(g2d, polygon, object.getColor(), userPrefs.isShowObjHighlight(), alphaColor, false);
    }

    /**
     * Open the configuration window for the user to adjust preferences.
     *
     * @return the configuration window
     */
    private UserConfigs createConfigWindow() {
        // instantiate the config window
        UserConfigs configWin = new UserConfigs(this, true, userPrefs, defaultDPIValue, gc.isExistsScribbleObj());

        // add observer
        configWin.addObserver(this);

        return configWin;
    }

    /**
     * Export the user specified images for demo purposes.
     */
    private void autoExportImages() {
        // if the user does not want to save the images, stop here
        if (!userPrefs.isImgAutoExportEveryFrame()) {
            return;
        }

        // parse the types of images in order to export all of them
        String[] types = userPrefs.getImgTypeForExport().split(",");

        // make sure the path exists
        Utils.createFolderPath(userPrefs.getImgExportPath());

        for (String type : types) {
            switch (type) {
                case Constants.ORIGINAL_IMG:
                    ExportImage.exportImage(gc.getdPImgToLabel().getOrigImg(),
                            userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.ORIGINAL_IMG.split(" ")[0]),
                            userPrefs.getImgExportExtension());
                    break;

                case Constants.SEGMENTED_IMG:
                    ExportImage.exportImage(getSegmentedImage(),
                            userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.SEGMENTED_IMG.split(" ")[0]),
                            userPrefs.getImgExportExtension());
                    break;

                case Constants.SEMANTIC_SEGMENTATION_IMG:
                    ExportImage.exportImage(gc.getImageResult(),
                            userPrefs.getImgExportPath() + File.separator + createExportImgFileName(Constants.SEMANTIC_SEGMENTATION_IMG.split(" ")[0]),
                            userPrefs.getImgExportExtension());
                    break;

                case Constants.JOINED_IMGS:
                    exportJoinedImages();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Show the Attributes edit window and allow the user to edit the attributes
     * of the object or frame.
     *
     * @param tabChoice the tab on which the dialog should open
     */
    private void displayAttributesEditWin(String tabChoice) {
        // create a new instance of the attributs definition window
        AttributesDefinition attributes = gc.editAttributes(this);

        // switch to the specified tab
        attributes.switchToTab(tabChoice);

        // show the frame
        attributes.setVisible(true);
    }

    /**
     * A class for implementing the behaviour of the buttons for the object
     * list. When a click happens on one of the buttons, the object should be
     * selected and the application should allow edit mode.
     */
    private class ObjListButtons implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // get the text of the button
            String buttonName = e.getActionCommand();

            if (buttonName.startsWith("id:")) {
                // set to edit mode
                jRBEditMode.setSelected(true);
                setEditMode();

                // parse the text in order to get the id of the object
                long objId = Long.parseLong(buttonName.replace("id: ", ""));

                // select the object with the id
                String objType = gc.selectObject(objId);

                // select the button of the object
                selectObjectButton(objId);

                // enable the edit buttons (if they have to be disabled, they will be later on)
                enableEditButtons(true);

                // display the preview results panel if it is a scribble object
                if (objType.equals(ConstantsLabeling.LABEL_SCRIBBLE)) {
                    setShowSemanticOptions(true);

                    // if the scribble object is selected, disable some edit options
                    disableEditOptScribbleObj(gc.getCurrentObject());
                } else {
                    setShowSemanticOptions(gc.isExistsScribbleObj());
                }
            }
        }
    }

    /**
     * Select the pressed button representing an object. The background of the
     * object has to correspond to the labeling type (bbox, scribble etc.).
     */
    private void selectObjectButton(long objId) {
        java.awt.Color labelTypeColor;
        java.awt.Component[] components = jPObjList.getComponents();

        // enable the edit menu when an object is selected and disable it when not
        jMEditObject.setEnabled(objId > 0);

        // go over all the list components
        for (java.awt.Component comp : components) {

            // if the component is a panel, get the subcomponents
            if (!(comp instanceof JPanel)) {
                continue;
            }

            java.awt.Component[] subCompList = ((JPanel) comp).getComponents();

            // parse every panel and get the button, in order to highlight it
            for (java.awt.Component subComp : subCompList) {

                if (!(subComp instanceof CustomColorButton)) {
                    continue;
                }

                if (((CustomColorButton) subComp).getText().equals("id: " + objId)) {
                    // get the color of the object based on the labeling type: scribble, bbox
                    labelTypeColor = gc.getLabelTypeColor(objId);

                    ((CustomColorButton) subComp).setBkgColor(labelTypeColor.darker());
                } else if (((CustomColorButton) subComp).getText().startsWith("id:")) {
                    // parse the text in order to get the id of the object
                    long id = Long.parseLong(((CustomColorButton) subComp).getText().replace("id: ", ""));

                    // get the color of the object based on the labeling type: scribble, bbox
                    labelTypeColor = gc.getLabelTypeColor(id);

                    ((CustomColorButton) subComp).setBkgColor(labelTypeColor);
                }
            }
        }
        jPObjList.repaint();
    }

    /**
     * The key event dispatcher for listening the keys and the implementation of
     * the actions for some defined keys.
     */
    private class GUIKeyEventDispatcher implements KeyEventDispatcher {

        /**
         * Check if there is another window opened on top of the current one.
         *
         * @return - true if the current frame is the focus owner
         */
        private boolean frameIsFocusOwner() {
            return (GUILabelingTool.this.getFocusOwner() != null);
        }

        /**
         * Check if the log panel is the focus owner.
         *
         * @return - true if the log panel is the focus owner
         */
        private boolean logIsFocusOwner() {
            Component focusOwner = GUILabelingTool.this.getFocusOwner();

            return (jTALog == focusOwner) || (jSPLog == focusOwner);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (frameIsFocusOwner() && (!logIsFocusOwner())) {

                if (gc.getdPImgToLabel() == null) {
                    return false;
                }

                // general implementation of keys avaliable all the time in the application
                dispatchKeyGeneral(e);
            }
            return false;
        }

        /**
         * Handles the keys which have the same functionality for all the types
         * of labeling.
         */
        private void dispatchKeyGeneral(KeyEvent e) {
            int eventId = e.getID();
            int key = e.getKeyCode();

            // the mapping of the keys is based on the default assignment of keys  for UGEE graphic tablet
            if (eventId == KeyEvent.KEY_PRESSED) {
                if (e.isControlDown()) {
                    handleCtrlKey(key);
                } else if (e.isAltDown()) {
                    handleAltKey(key);
                } else {
                    handleNormalKey(key);
                }
            }
        }

        /**
         * Handling of the keyboard events when Control key is pressed.
         *
         * @param key the key code
         */
        private void handleCtrlKey(int key) {
            switch (key) {
                case KeyEvent.VK_PLUS:
                    log.info("Key pressed: Ctrl NumPad+ =  Zoom in");
                    break;

                case KeyEvent.VK_MINUS:
                    log.info("Key pressed: Ctrl NumPad- =  Zoom out");
                    break;

                case KeyEvent.VK_A:
                    // decrease left
                    gc.changeSizeSelection(1, 0, 0, 0);
                    break;

                case KeyEvent.VK_W:
                    // decrease top
                    gc.changeSizeSelection(0, 1, 0, 0);
                    break;

                case KeyEvent.VK_D:
                    // decrease right
                    gc.changeSizeSelection(0, 0, -1, 0);
                    break;

                case KeyEvent.VK_S:
                    // decrease bottom
                    gc.changeSizeSelection(0, 0, 0, -1);
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Handling of keyboard events when Alt key is pressed.
         *
         * @param key the key code
         */
        private void handleAltKey(int key) {
            switch (key) {
                case KeyEvent.VK_A:
                    // increase left
                    gc.changeSizeSelection(-1, 0, 0, 0);
                    break;

                case KeyEvent.VK_W:
                    // increase top
                    gc.changeSizeSelection(0, -1, 0, 0);
                    break;

                case KeyEvent.VK_D:
                    // increase right
                    gc.changeSizeSelection(0, 0, 1, 0);
                    break;

                case KeyEvent.VK_S:
                    // increase bottom
                    gc.changeSizeSelection(0, 0, 0, 1);
                    break;

                case KeyEvent.VK_Z:
                    // decrease box
                    gc.changeSizeSelection(1, 1, -1, -1);
                    break;

                case KeyEvent.VK_X:
                    // increase box
                    gc.changeSizeSelection(-1, -1, 1, 1);
                    break;

                default:
                    // do nothing
                    break;
            }
        }

        /**
         * Handling of the keyboard events.
         *
         * @param key the key code
         */
        private void handleNormalKey(int key) {
            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (!jTFJumpFrame.isFocusOwner()) {
                        getFrame(Constants.PREV_FRAME);
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (!jTFJumpFrame.isFocusOwner()) {
                        // get the next frame; save the data and load other data if there exists any
                        getFrame(Constants.NEXT_FRAME);
                    }
                    break;

                case KeyEvent.VK_A:
                    // move the crop to the left                            
                    gc.moveSelection(-1, 0);
                    break;

                case KeyEvent.VK_D:
                    // move the crop to the right
                    gc.moveSelection(1, 0);
                    break;

                case KeyEvent.VK_W:
                    // move the crop up
                    gc.moveSelection(0, -1);
                    break;

                case KeyEvent.VK_S:
                    // move the crop down
                    gc.moveSelection(0, 1);
                    break;

                case KeyEvent.VK_DELETE:
                    gc.removeSelection();
                    break;

                case KeyEvent.VK_ESCAPE:
                    cancelCurrentObject();
                    break;

                default:
                    // do nothing
                    break;
            }
        }
    }

    /**
     * Cancel the current object and remove its selection from the interfaces.
     */
    private void cancelCurrentObject() {
        if (gc.getCurrentObject() instanceof ObjectBBox) {
            cancelBBoxObject();
        } else if (gc.getCurrentObject() instanceof ObjectScribble) {
            cancelScribbleObject();
        } else if (gc.getCurrentObject() instanceof ObjectPolygon) {
            cancelPolygonObject();
        }

        gc.resetSelectedObject();
        selectObjectButton(0L);
    }

    /**
     * Implement the cancel of a bounding box object.
     */
    private void cancelBBoxObject() {
        gc.cancelCurrentObject();
    }

    /**
     * Implement the cancel of a scribble object.
     */
    private void cancelScribbleObject() {
        // remove the object from the object map if the obj is scribble
        if (gc.getCurrentObject() != null) {
            gc.cancelObjectMap(gc.getCurrentObject(), gc.getCurrentObject().getOuterBBox());
        }

        if (gc.cancelCurrentObject()) {
            jBNewScribbleObj.setText(NEW_OBJ_BUTTON_TEXT);

            // no drawing can be done while there is no new object
            gc.setLabelingType(jRBScribble.isSelected() ? DrawConstants.DrawType.DO_NOT_DRAW : DrawConstants.DrawType.EDIT_MODE);
        }
    }

    /**
     * Implement the cancel of a polygon object.
     */
    private void cancelPolygonObject() {
        gc.cancelCurrentObject();

        jBNewPolygonObj.setText(NEW_OBJ_BUTTON_TEXT);

        // no drawing can be done while there is no new object
        gc.setLabelingType(jRBPolygon.isSelected() ? DrawConstants.DrawType.DO_NOT_DRAW : DrawConstants.DrawType.EDIT_MODE);
    }

    /**
     * Show/Hide options related to the semantic segmentation: result panel,
     * export semantic image etc.
     *
     * @param visible true if the components should be visible; false otherwise
     */
    private void setShowSemanticOptions(boolean visible) {
        jPPrevResultImg.setVisible(visible);
        jMIExportSemanticImg.setEnabled(visible);
    }

    /**
     * Update all the graphical components related to the video player.
     */
    private void updateGUIVideo() {
        long frame;

        frame = gc.getFrameNo();

        jLFrameNo.setText("F: " + gc.getFrameNo());
        jSVideoPlayer.setValue((int) frame);
    }

    /**
     * Initialise the characteristics of the video slider based on the loaded
     * video.
     */
    private void initVideoSlider() {
        jSVideoPlayer.setMinimum(1);    //1 is the first frame
        jSVideoPlayer.setMaximum((int) gc.getNoFrames());
        jSVideoPlayer.setValue((int) gc.getFrameNo());
    }

    /**
     * Return true if the application is running online.
     *
     * @return true if the application is online; false if not
     */
    public boolean isRunningOnline() {
        return runningOnline;
    }

    /**
     * Allow the user to select which two images he wants to export: original,
     * segmented, result.
     */
    private void exportJoinedImages() {
        String[] joinedTypes = userPrefs.getImgTypeJoinedExport().split(",");

        // stop if the string is not correctly specified
        if (joinedTypes.length != 2) {
            log.error("Auto export for joined images: the types string for left and right image has length {}!!", joinedTypes.length);
            return;
        }

        // get the left selection
        BufferedImage leftBI = selectImage(joinedTypes[0]);

        // get the left selection
        BufferedImage rigthBI = selectImage(joinedTypes[1]);

        String fileName = createExportImgFileName(joinedTypes[0].split(" ")[0] + "_" + joinedTypes[1].split(" ")[0]);

        // export the image
        Utils.exportTwoImages(leftBI, rigthBI, userPrefs.getImgExportPath() + File.separator + fileName, userPrefs.getImgExportExtension(), Color.white);

        // inform the user about the export
        log.info("Exported the joined images {}!", fileName);
    }

    /**
     * Create the name of the exported file like in the following template:<br>
     * inputFileName_frameNumber_exportType.extension<br>
     * where:<br>
     * inputFileName - the chosen file to be labeled<br>
     * frameNumber - the frame number and it applies just for videos<br>
     * export type - the type of exported image: original, joined etc.<br>
     * extension - the chosen file type to export images: png, jpeg, bmp
     *
     * @param exportName the name to be appended to the file name, defining the
     * type of export: original, segmented, original_segmented etc.
     * @return a string representing the name of the file, the one used to be
     * saved on the disk
     */
    private String createExportImgFileName(String exportName) {
        // create the name of the file being saved
        StringBuilder fileName = new StringBuilder();

        // get the name of the loaded file
        String name = Utils.getFileName(gc.getFileToLabelName(), false);

        fileName.append(name).append("_");

        // add the frame number if the file is a video
        if (Utils.isVideoFile(gc.getFileToLabelName())) {
            fileName.append(gc.getFrameNo()).append("_");
        }

        // add the name of the file types
        fileName.append(exportName);

        // add the point before the extension
        fileName.append(".");

        // add the file extension
        fileName.append(userPrefs.getImgExportExtension());

        return fileName.toString();
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
                return gc.getdPImgToLabel().getOrigImg();

            case Constants.SEGMENTED_IMG:
                return getSegmentedImage();

            case Constants.SEMANTIC_SEGMENTATION_IMG:
                return gc.getImageResult();

            default:
                return gc.getdPImgToLabel().getOrigImg();
        }
    }

    /**
     * The app starts with the panels for viewing the images filling the image.
     * This is no longer required after an image is added. The panel has to have
     * the same size as the image.
     */
    private void redrawViewPanel() {
        // remove and add again the drawing panel
        jPVideoPrev.remove(jPVImgToLabel);
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        jPVideoPrev.add(jPVImgToLabel, gbc);

        // remove and add again the video preview panel
        jPView.remove(jPVideoPrev);
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.0;
        gbc.weighty = 0.01;
        gbc.insets = new java.awt.Insets(10, 5, 5, 0);
        jPView.add(jPVideoPrev, gbc);

        // remove and add again the panel with the results
        jPView.remove(jPViewResults);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.VERTICAL;
        gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.5;
        gbc.weighty = 0.01;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        jPView.add(jPViewResults, gbc);

        jPView.revalidate();
    }

    /**
     * Trigger the steps for configuring the application in such way as to be
     * usable (image size etc.)
     */
    public void firstStartInitialization() {
        if (userPrefs.isFirstRun()) {
            // open the config window in a "first run" mode
            UserConfigs configWin = createConfigWindow();

            // for the case of the first run, add some special configuration to the config window
            if (userPrefs.isFirstRun()) {
                configWin.setConfigFirstRun();
                // disable the first run because it is no longer needed
                userPrefs.setFirstRun(false);
            }
            // show the config window
            configWin.setVisible(true);

            // open the attributes configuration window
            gc.editAttributes(this);
        }
    }

    /**
     * Set the icons for the menu items which have one.
     */
    private void setMenuItemsIcons() {
        jMILoadFile.setIcon(Icons.FOLDER_ICON_16X16);
        jMIExit.setIcon(Icons.EXIT_APPLICATION_ICON_16X16);

        jMINextFrame.setIcon(Icons.NEXT_ICON_16X16);
        jMIPrevFrame.setIcon(Icons.PREVIOUS_ICON_16X16);

        jMIUserConfig.setIcon(Icons.SETTINGS_ICON_16X16);
        jMIAttributes.setIcon(Icons.LIST_ICON_16X16);
        jMILanguage.setIcon(Icons.LANGUAGE_ICON_16X16);

        jMIExportOrigImg.setIcon(Icons.EXPORT_IMAGE_ICON_16X16);
        jMIExportWorkingImg.setIcon(Icons.EXPORT_IMAGE_ICON_16X16);
        jMIExportSemanticImg.setIcon(Icons.EXPORT_IMAGE_ICON_16X16);
        jMIExportJoinedImg.setIcon(Icons.EXPORT_IMAGE_ICON_16X16);
        jMIExportConfig.setIcon(Icons.SETTINGS_ICON_16X16);

        jMIAbout.setIcon(Icons.ABOUT_ICON_16X16);
        jMIHotkeys.setIcon(Icons.KEY_ICON_16X16);
        jMIManualApp.setIcon(Icons.HELP_BOOK_ICON_16X16);
    }

    /**
     * The entry point of application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(GUILabelingTool.class);
        System.getProperty("java.library.path");

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                //  SplashScreen.startSplash()

                GUILabelingTool gui = new GUILabelingTool(logger);
                gui.setVisible(true);
                // for the first run, some special configuration have to be done
                gui.firstStartInitialization();

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                logger.error("Create and display form");
                logger.debug("Create and display form {}", ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup jBGLabelType;
    private javax.swing.JButton jBJumpFrame;
    private javax.swing.JButton jBLoadFile;
    private javax.swing.JButton jBNewPolygonObj;
    private javax.swing.JButton jBNewScribbleObj;
    private javax.swing.JButton jBNextFrame;
    private javax.swing.JButton jBPlay;
    private javax.swing.JButton jBPrevFrame;
    private javax.swing.JComboBox<String> jCBCountry;
    private javax.swing.JCheckBox jCBDirtVisible;
    private javax.swing.JCheckBox jCBFlipVerticallyImg;
    private javax.swing.JComboBox<String> jCBIllumination;
    private javax.swing.JCheckBox jCBImageDistorted;
    private javax.swing.JCheckBox jCBMirrorImg;
    private javax.swing.JCheckBox jCBPlayBackward;
    private javax.swing.JComboBox<String> jCBRoadEvent;
    private javax.swing.JComboBox<String> jCBRoadType;
    private javax.swing.JCheckBox jCBShowCurrentObj;
    private javax.swing.JComboBox<String> jCBWeather;
    private javax.swing.JCheckBox jCBWipersVisible;
    private javax.swing.JLabel jLAlignNorth;
    private javax.swing.JLabel jLCountry;
    private javax.swing.JLabel jLFrameNo;
    private javax.swing.JLabel jLIllumination;
    private javax.swing.JLabel jLJumpFrame;
    private javax.swing.JLabel jLNoBBoxObjs;
    private javax.swing.JLabel jLNoBBoxObjsText;
    private javax.swing.JLabel jLNoFrames;
    private javax.swing.JLabel jLNoPolygonObjs;
    private javax.swing.JLabel jLNoPolygonObjsText;
    private javax.swing.JLabel jLNoScribbleObjs;
    private javax.swing.JLabel jLNoScribbleObjsText;
    private javax.swing.JLabel jLRoadEvent;
    private javax.swing.JLabel jLRoadType;
    private javax.swing.JLabel jLTotalNoObjs;
    private javax.swing.JLabel jLTotalNoObjsText;
    private javax.swing.JLabel jLWeather;
    private javax.swing.JMenuBar jMBMenu;
    private javax.swing.JMenu jMEditObject;
    private javax.swing.JMenu jMExport;
    private javax.swing.JMenu jMFile;
    private javax.swing.JMenu jMHelp;
    private javax.swing.JMenuItem jMIAbout;
    private javax.swing.JMenuItem jMIAttributes;
    private javax.swing.JMenuItem jMICancelObject;
    private javax.swing.JMenuItem jMIDecreaseBottom;
    private javax.swing.JMenuItem jMIDecreaseBox;
    private javax.swing.JMenuItem jMIDecreaseLeft;
    private javax.swing.JMenuItem jMIDecreaseRight;
    private javax.swing.JMenuItem jMIDecreaseTop;
    private javax.swing.JMenuItem jMIDeleteObject;
    private javax.swing.JMenuItem jMIExit;
    private javax.swing.JMenuItem jMIExportConfig;
    private javax.swing.JMenuItem jMIExportJoinedImg;
    private javax.swing.JMenuItem jMIExportOrigImg;
    private javax.swing.JMenuItem jMIExportSemanticImg;
    private javax.swing.JMenuItem jMIExportWorkingImg;
    private javax.swing.JMenuItem jMIHotkeys;
    private javax.swing.JMenuItem jMIIncreaseBottom;
    private javax.swing.JMenuItem jMIIncreaseBox;
    private javax.swing.JMenuItem jMIIncreaseLeft;
    private javax.swing.JMenuItem jMIIncreaseRight;
    private javax.swing.JMenuItem jMIIncreaseTop;
    private javax.swing.JMenuItem jMILanguage;
    private javax.swing.JMenuItem jMILoadFile;
    private javax.swing.JMenuItem jMIManualApp;
    private javax.swing.JMenuItem jMIMoveDown;
    private javax.swing.JMenuItem jMIMoveLeft;
    private javax.swing.JMenuItem jMIMoveRight;
    private javax.swing.JMenuItem jMIMoveUp;
    private javax.swing.JMenuItem jMINextFrame;
    private javax.swing.JMenuItem jMIPrevFrame;
    private javax.swing.JMenuItem jMIUserConfig;
    private javax.swing.JMenu jMNavigation;
    private javax.swing.JMenu jMOptions;
    private javax.swing.JPanel jPBackground;
    private javax.swing.JPanel jPCropOpt;
    private javax.swing.JPanel jPDevelopment;
    private javax.swing.JPanel jPImgOpt;
    private javax.swing.JPanel jPLabeling;
    private javax.swing.JPanel jPLoadSource;
    private javax.swing.JPanel jPLog;
    private javax.swing.JPanel jPNavigation;
    private javax.swing.JPanel jPObjInfo;
    private javax.swing.JPanel jPObjList;
    private javax.swing.JPanel jPOptions;
    private javax.swing.JPanel jPPrevResultImg;
    private javax.swing.JPanel jPSceneDescription;
    private javax.swing.JPanel jPVImgToLabel;
    private javax.swing.JPanel jPVLabelType;
    private javax.swing.JPanel jPVideoPrev;
    private javax.swing.JPanel jPView;
    private javax.swing.JPanel jPViewResults;
    private javax.swing.JPanel jPViewResultsImg;
    private javax.swing.JRadioButton jRBBoundingBox;
    private javax.swing.JRadioButton jRBEditMode;
    private javax.swing.JRadioButton jRBPolygon;
    private javax.swing.JRadioButton jRBScribble;
    private javax.swing.JPopupMenu.Separator jSAttribDef;
    private javax.swing.JScrollPane jSPLog;
    private javax.swing.JScrollPane jSPObjList;
    private javax.swing.JScrollPane jSPShortLog;
    private javax.swing.JSlider jSVideoPlayer;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JTextArea jTALog;
    private javax.swing.JTextArea jTAShortLog;
    private javax.swing.JTextField jTFJumpFrame;
    private javax.swing.JTabbedPane jTPLabelingOptions;
    // End of variables declaration//GEN-END:variables
}
