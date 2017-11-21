package paintpanels;

import common.Constants;
import common.ConstantsLabeling;
import common.Timings;
import common.Utils;
import gui.support.*;
import library.DrawOptions;
import library.Resize;
import observers.NotifyObservers;
import observers.ObservedActions;
import commonsegmentation.ScribbleInfo;
import commonsegmentation.PixelMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Drawing panel.
 *
 * @author Olimpia Popica
 */
public final class DrawingPanel extends JPanel {

    /**
     * The original image loaded by the user.
     */
    private transient BufferedImage origImg;

    /**
     * The image drawn on the panel
     */
    private transient BufferedImage workImg;

    /**
     * The dimension of the panel
     */
    private Dimension panelSize;

    /**
     * Old position of the mouse in panel coordinates - dependent on click
     * action.
     */
    private Point oldMouse = new Point();

    /**
     * Current mouse position in panel coordinates - dependent on click action.
     */
    private Point currentMouse = new Point();

    /**
     * Previous mouse position in panel coordinates - dependent on click action.
     */
    private final Point prevMouse = new Point();

    /**
     * Shows whether the guide shape shall be drawn or not (the red rectangle
     * for object segmentation).
     */
    private boolean drawGuideShape;
    /**
     * The position of the mouse in the panel - where the mouse moves, without
     * click constrains.
     */
    private final Point mousePosition = new Point();

    /**
     * The type of drawing which is chosen by the user: bounding box, scribble,
     * line etc.
     */
    private DrawConstants.DrawType drawType;

    /**
     * The possible actions: -1 = erase scribbles; 0 = draw background; 1 = draw
     * object
     */
    private int actionType;

    /**
     * Keeps the user defined settings of the scribble.
     */
    private transient BrushOptions brushOpt = new BrushOptions();

    /**
     * Marks the points which were visited/segmented. For the scribble drawing
     * it is needed to know which points were already marked to avoid marking
     * them again or changing their initial value.
     */
    private boolean[][] visited;

    /**
     * The list of segmented points. It is kept for being able to save the
     * information regarding the segmented point and the object ids of each
     * point.
     */
    private List<ScribbleInfo> scribbleList;

    /**
     * The list of points to be displayed (no other purpose, just display). It
     * represents the list of scribbles which were drawn on the crops. point.
     */
    private transient List<DisplayScribbles> displayScribbles;

    /**
     * The list of segmented pixels as required by the cuda module. It contains
     * the list of pixels which represent scribbles.
     */
    private transient List<PixelMap>[] cudaPixelMap;

    /**
     * The ratio between the original image and the size it has to have to fit
     * in the image.
     */
    private transient Resize resize;

    /**
     * Stores the current bounding box drawn by the labeler.
     */
    private Rectangle curBBoxPanelCoord;

    /**
     * Makes the marked methods observable. It is part of the mechanism to
     * notify the frame on top about changes.
     */
    private final transient NotifyObservers observable = new NotifyObservers();

    /**
     * Used to check the timings needed to run different peaces of code. Used
     * also for counting the needed time to label using bounding boxes.
     */
    private final transient Timings times = new Timings();

    /**
     * The label displaying the size of the crop.
     */
    private final JLabel jLBoxSize = new JLabel();

    /**
     * List of bounding boxes/crops to be displayed.
     */
    private transient List<DisplayBBox> bBoxList;

    /**
     * Enables the draw of the crops from the previous frame.
     */
    private boolean drawScribbleHistory;

    /**
     * Enables the draw of crop, to add it to an existent object, which is in
     * edit mode.
     */
    private boolean addCropToObj;

    /**
     * Keep the id of the box which should look selected when the user clicks on
     * it.
     */
    private int idSelectedBox;

    /**
     * The color of the object.
     */
    private Color objColor;

    /**
     * The current polygon being segmented.
     */
    private Polygon currentPolygon;

    /**
     * The list of polygon objects segmented, to be displayed.
     */
    private transient List<DisplayPolygon> polygonList;

    /**
     * Draw an alpha over the object for better visibility in the image.
     */
    private boolean drawAlphaObj;

    /**
     * The value of the alpha to be used for the drawing of the object
     * highlight.
     */
    private int objAlpha;

    /**
     * logger instance
     */
    private final transient Logger log = LoggerFactory.getLogger(DrawingPanel.class);

    /**
     * Load image when the image is given (used for crop).
     *
     * @param image    - the image to be displayed
     * @param panelRes - contains info related to the max resolution possible for the panel and allows the computation of a better resolution, based on the image to be displayed size
     * @param drawType - the current type of drawing for panel
     */
    public DrawingPanel(BufferedImage image, Dimension panelRes, DrawConstants.DrawType drawType) {
        this.origImg = image;
        this.drawType = drawType;

        this.panelSize = panelRes;

        // resize the image to the optimal size
        double ratioWidth = (double) origImg.getWidth() / (double) panelSize.width;
        double ratioHeight = (double) origImg.getHeight() / (double) panelSize.height;
        resize = new Resize(ratioWidth, ratioHeight);

        workImg = resize.resizeImage(origImg);

        log.info("**resized image {}x{}", workImg.getWidth(), workImg.getHeight());

        initPanel();
    }

    /**
     * Configure the basic characteristics of the panel.
     */
    private void initPanel() {
        // for any type of drawing, the mouse listener should be active
        addMouseListener();

        //characteristics of the panel
        this.setFocusable(true);

        this.setPreferredSize(panelSize);
        this.setSize(panelSize);
        this.setMinimumSize(panelSize);
        this.setMaximumSize(panelSize);

        this.repaint();
        this.setVisible(true);

        jLBoxSize.setOpaque(true);
        jLBoxSize.setForeground(Color.red);
        jLBoxSize.setBackground(new Color(0xFF, 0xF9, 0xCD, 180)); //red text + FEF9CD background - based on color contrast
        jLBoxSize.setVisible(false);
        add(jLBoxSize);

        // init the other variables
        initOtherVar();
    }

    /**
     * Initialise needed variable which were not initialised in the constructor.
     */
    private void initOtherVar() {
        // init the list of pixels which are drawn on the image
        scribbleList = new ArrayList<>();

        // init the list of pixels to be displayed in the panel
        displayScribbles = new ArrayList<>();

        // create a new map of the workImg to mark the visited points
        visited = new boolean[origImg.getHeight()][origImg.getWidth()];

        // set the id as invalid
        idSelectedBox = -1;
    }

    /**
     * The implementation of the mouse listener actions.
     */
    private void addMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressedAction(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseReleasedAction(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEnteredAction(e);
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseDraggedAction(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition.setLocation(e.getX(), e.getY());
            }

        });
    }

    /**
     * Implements the action which happens on mouse pressed action.
     *
     * @param e triggered mouse event
     */
    private void mousePressedAction(MouseEvent e) {
        // save coord x,y when mouse is pressed
        oldMouse.setLocation(e.getPoint());
        prevMouse.setLocation(e.getPoint());
        currentMouse.setLocation(e.getPoint());

        switch (drawType) {

            case DRAW_SCRIBBLE:
                // the erase object has id -1
                if (actionType > ConstantsLabeling.ACTION_TYPE_ERASE) {
                    generateScribble();
                } else {
                    eraseFromScribblelList(currentMouse);
                }
                break;

            case EDIT_MODE:
                // if edit mode and ctrl + mouse clicked => add crop to object
                if (((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) || addCropToObj) {
                    // draw the shape of the object
                    drawGuideShape = true;

                } else {
                    // check if any object is selected
                    getSelectedBoxId();

                    // if double click, open the object in edit mode
                    if (e.getClickCount() == 2 && !e.isConsumed()) {
                        e.consume();
                        observable.notifyObservers(ObservedActions.Action.EDIT_OBJECT_ACTION);
                    }

                    // highlight the button of the selected object
                    observable.notifyObservers(ObservedActions.Action.HIGHLIGHT_OBJECT);
                }
                break;

            case DRAW_BOUNDING_BOX:
                // a mouse pressed event was notified and the timer for labeling has to start
                times.start();

            case DRAW_CROP:
                // draw the shape of the object
                drawGuideShape = true;
                break;

            default:
                break;
        }
    }

    /**
     * Implements the action which happens on mouse released action.
     *
     * @param e triggered mouse event
     */
    private void mouseReleasedAction(MouseEvent e) {
        currentMouse.setLocation(e.getPoint());

        //notify the gui to update the cropped image
        switch (drawType) {
            case DRAW_CROP:
                saveCurrentBBox();
                observable.notifyObservers(ObservedActions.Action.OPEN_CROP_SEGMENTATION);
                break;

            case DRAW_BOUNDING_BOX:
                saveCurrentBBox();
                observable.notifyObservers(ObservedActions.Action.OPEN_BBOX_SEGMENTATION);
                break;

            case DRAW_POLYGON:
                currentPolygon.addPoint(currentMouse.x, currentMouse.y);
                break;

            case EDIT_MODE:
                // if edit mode and ctrl + mouse clicked => add crop to object
                if (((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) || addCropToObj) {
                    saveCurrentBBox();
                    observable.notifyObservers(ObservedActions.Action.CTRL_MOUSE_EVENT);

                    // deactivate the drawing features
                    addCropToObj = false;
                    drawGuideShape = false;
                    jLBoxSize.setVisible(drawGuideShape);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Implements the action which happens on mouse entered action.
     *
     * @param e triggered mouse event
     */
    private void mouseEnteredAction(MouseEvent e) {
        // change the mouse cursor when the drawing type is scribble
        if (drawType == DrawConstants.DrawType.DRAW_SCRIBBLE) {
            setCursor(Utils.generateCustomIcon(brushOpt, actionType, objColor));
        } else {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Implements the action which happens on mouse dragged action.
     *
     * @param e triggered mouse event
     */
    private void mouseDraggedAction(MouseEvent e) {
        prevMouse.setLocation(currentMouse.getLocation());

        // save coord x,y when mouse is dragged
        currentMouse.setLocation(e.getPoint());

        // depending on the action, draw scribble or erase them
        if (drawType == DrawConstants.DrawType.DRAW_SCRIBBLE) {
            if (actionType > ConstantsLabeling.ACTION_TYPE_ERASE) {
                generateScribble();
            } else {
                eraseFromScribblelList(currentMouse);
            }

            oldMouse.setLocation(currentMouse.getLocation());
        }

        // if an object is selected and dragged, notify to move it
        if ((idSelectedBox > -1) && !drawGuideShape) {
            observable.notifyObservers(ObservedActions.Action.MOVE_OBJECT_DRAG);
        }
    }

    // Override area
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(workImg, 0, 0, panelSize.width, panelSize.height, this);          //draw image at coordinates (0,0)

        drawFigure(g2D);

        // draw the list of saved boxes
        drawBoxList(g2D);

        // draw the list of saved scribbles
        drawScribbleList(g2D);

        // draw the list of saved polygons
        drawPolygonList(g2D);

        // highlight the selected object
        highlightSelectedBox(g2D);
    }

    private void highlightSelectedBox(Graphics2D g2D) {
        if ((idSelectedBox > -1) && (!bBoxList.isEmpty()) && (idSelectedBox < bBoxList.size())) {
            g2D.setColor(new Color(0, 0, 0, 100));
            // if there is a selected box, draw it transparent
            g2D.fillRect(bBoxList.get(idSelectedBox).getPanelBox().x + 1,
                    bBoxList.get(idSelectedBox).getPanelBox().y + 1,
                    bBoxList.get(idSelectedBox).getPanelBox().width - 1,
                    bBoxList.get(idSelectedBox).getPanelBox().height - 1);
        }
    }

    /**
     * Draw a bounding box / rectangle for all the segmented/saved objects.
     */
    private void drawBBoxList(Graphics2D g2D) {
        // draw all the bounding boxes from the list
        bBoxList.stream().forEach(bBox
                -> DrawOptions.drawBBox(g2D, bBox.getPanelBox(), bBox.getColor(),
                        (drawAlphaObj && (!bBox.isUseDashedLine())), new Color(bBox.getColor().getRed(), bBox.getColor().getGreen(), bBox.getColor().getBlue(), objAlpha),
                        bBox.isUseDashedLine()));
    }

    /**
     * Draw the list of saved polygons.
     *
     * @param g2d the graphics object
     */
    private void drawPolygonObjList(Graphics2D g2D) {
        polygonList.stream().forEach(displayPoly -> {
            Color fillColor = new Color(displayPoly.getColor().getRed(), displayPoly.getColor().getGreen(), displayPoly.getColor().getBlue(), objAlpha);
            DrawOptions.drawPolygon(g2D, displayPoly.getPanelPolygon(), displayPoly.getColor(), drawAlphaObj, fillColor, false);
        });
    }

    /**
     * Draw the points existent in the list, using the defined color for each.
     */
    private void drawScribble(Graphics2D g2D) {
        for (ScribbleInfo si : scribbleList) {
            if ((Utils.checkBounds(si.getImgPos(), getOrigImgSize())) && (visited[si.getImgPosY()][si.getImgPosX()])) {
                DrawOptions.drawPoint(g2D, si.getPanelPos(), Utils.getDrawingColor(si.getDrawingType(), objColor));
            }
        }
    }

    /**
     * The method allows the user to draw freely the wanted shape. The shape is
     * saved and passed to another module which is processing the chosen points.
     */
    private void generateScribble() {

        //compute the workImg coordinates for the case when the panel is not the same size as the workImg
        Point currentImg = resize.resizedToOriginal(currentMouse);
        Point currentPanel = new Point(currentMouse.x, currentMouse.y);     //to avoid overriting all the other panel coordinates
        Point newImgPoint;
        Point newPanelPoint;
        int brushSizeImg = resize.resizedToOriginal(brushOpt.getBrushSize());
        int win2 = (int) (brushSizeImg * 0.5f);   //the window arround the point where the mouse is, where the brush will be created
        Random rand = new Random();
        byte[][] circleMask = Utils.getCircleMask(brushOpt.getBrushSize());  // the mask making the mouse pointer a circle

        //When the brush is theaker than 1 pixel, draw points as specified by the choosen options: brush size and density. 
        //When the brush has size 1 or 0, draw a line of 1 pixel.
        if (win2 > 0) {
            for (int x = -win2; x < brushSizeImg - win2; x++) {
                for (int y = -win2; y < brushSizeImg - win2; y++) {
                    newImgPoint = new Point(currentImg.x + x, currentImg.y + y);

                    float randFloat = rand.nextFloat();

                    //if the point is valid, not visited and chosen to be saved by the random generator, inside the circle brush, paint and save it
                    if (Utils.checkBounds(newImgPoint, getOrigImgSize())
                            && (randFloat < brushOpt.getBrushDensity())
                            && (circleMask[x + win2][y + win2] == (byte) 1)
                            && (!visited[newImgPoint.y][newImgPoint.x])) {
                        // mark the point as visited
                        visited[newImgPoint.y][newImgPoint.x] = true;

                        // compute the panel coordinates
                        newPanelPoint = resize.originalToResized(newImgPoint);

                        // add object in the list to be saved
                        addScribbleToList(newImgPoint, newPanelPoint);
                    }
                }
            }
        } else if (Utils.checkBounds(currentImg, getOrigImgSize()) && (!visited[currentImg.y][currentImg.x])) {
            // mark the point as visited
            visited[currentImg.y][currentImg.x] = true;

            // add object in the list to be saved
            addScribbleToList(currentImg, currentPanel);
        }
    }

    /**
     * Pack the image point and the panel point together with the object id and
     * add it to the list of pixels.
     */
    private void addScribbleToList(Point pointImg, Point pointPanel) {
        ScribbleInfo pi = new ScribbleInfo(actionType, pointImg, pointPanel);
        scribbleList.add(pi);
    }

    /**
     * The function does not erase the pixels, but marks them as free to be
     * chosen again.
     */
    private void eraseFromScribblelList(Point point) {
        //compute the workImg coordinates for the case when the panel is not the same size as the workImg
        Point currentImg = resize.resizedToOriginal(point);
        Point newImgPoint = new Point();

        byte[][] circleMask = Utils.getCircleMask(brushOpt.getBrushSize());  // the mask making the mouse pointer a circle

        int brushSizeImg = resize.resizedToOriginal(brushOpt.getBrushSize() - 1);
        int win2 = (int) (brushSizeImg * 0.5f);   //the window arround the point where the mouse is, where the brush will be created

        if (win2 > 0) {
            for (int x = -win2; x < win2; x++) {
                for (int y = -win2; y < win2; y++) {
                    // erase a circle of pixels, not a square
                    if (circleMask[x + win2][y + win2] == (byte) 1) {
                        // compute the position
                        newImgPoint.setLocation(currentImg.x + x, currentImg.y + y);

                        if (Utils.checkBounds(newImgPoint, getOrigImgSize())
                                && visited[newImgPoint.y][newImgPoint.x]) {
                            // make the point eligible again
                            visited[newImgPoint.y][newImgPoint.x] = false;
                        }
                    }
                }
            }
        }
    }

    /**
     * Write the list of selected pixels.
     *
     * @return - the number of scribbles which will be used for segmentation
     */
    public int flushPixelList() {
        int noScribbles = 0;

        // The map of objects to be saved - the not visited points have to have id -1.
        float[][] objMap = Utils.initMatrix(origImg.getWidth(), origImg.getHeight(), -1.0f);

        // create an empty array of arrays of points for the cuda module
        cudaPixelMap = new ArrayList[Constants.NUMBER_OF_OBJECTS];
        // init the array
        for (int index = 0; index < Constants.NUMBER_OF_OBJECTS; index++) {
            cudaPixelMap[index] = new ArrayList<>();
        }

        // get the list of segmented pixels and save only the valid and visited ones
        for (ScribbleInfo pi : scribbleList) {
            if (visited[pi.getImgPosY()][pi.getImgPosX()]) {
                objMap[pi.getImgPosY()][pi.getImgPosX()] = (float) pi.getDrawingType();
            }
        }

        // copy the whole list in the required format, line by line
        for (int y = 0; y < origImg.getHeight(); y++) {
            for (int x = 0; x < origImg.getWidth(); x++) {

                // fill in also the list of pixels for cuda
                if (objMap[y][x] > (-1)) {
                    // get the color of the pixel 
                    int[] RGB = Utils.getRGB(origImg.getRGB(x, y));

                    // create a new cuda pixel, specific to the matting algorithm
                    PixelMap cudaPixel = new PixelMap((short) x, (short) y, (byte) RGB[0], (byte) RGB[1], (byte) RGB[2]);

                    // save the new cuda pixel in the map
                    cudaPixelMap[(int) objMap[y][x]].add(cudaPixel);

                    // increment the scribble number
                    noScribbles++;
                }
            }
        }
        return noScribbles;
    }

    /**
     * Draw a figure selected by the user in the interface.
     */
    private void drawFigure(Graphics2D g2D) {
        switch (drawType) {
            case DO_NOT_DRAW:
                // do nothing
                break;

            case DRAW_POINT:
                DrawOptions.drawPoint(g2D, currentMouse);
                break;

            case DRAW_LINE:
                DrawOptions.drawLine(g2D, oldMouse, currentMouse);
                break;

            case DRAW_BOUNDING_BOX:
            case DRAW_CROP:
                drawBoxCrop(g2D);
                break;

            case DRAW_POLYGON:
                DrawOptions.drawMouseIndicator(g2D, true, mousePosition, panelSize);

                DrawOptions.drawPolygon(g2D, currentPolygon, Color.red, false);
                break;

            case DRAW_SCRIBBLE:
                drawScribble(g2D);
                break;

            case EDIT_MODE:
                editMode(g2D);
                break;

            default:
                //do nothing
                break;
        }

        repaint();
    }

    /**
     * Display the drawn box on the screen (for crop or bounding box
     * segmentation).
     *
     * @param g2D the graphics used to draw on the panel
     */
    private void drawBoxCrop(Graphics2D g2D) {
        // draw the mouse guidings
        DrawOptions.drawMouseIndicator(g2D, (drawType == DrawConstants.DrawType.DRAW_CROP), mousePosition, panelSize);

        // save the drawn box and display it
        saveCurrentBBox();

        // if thebox is valid and  drawGuideShape is true, display the box and its size
        if ((drawGuideShape) && (curBBoxPanelCoord.width > 0) && (curBBoxPanelCoord.height > 0)) {
            DrawOptions.drawBBox(g2D, curBBoxPanelCoord, Color.red, (drawType == DrawConstants.DrawType.DRAW_CROP));

            displayBoxSize();
        }
    }

    /**
     * Allow the user to add a crop to an object in the case ctrl + click are
     * pressed together in edit mode.
     *
     * @param g2D the graphics used to draw on the panel
     */
    private void editMode(Graphics2D g2D) {
        // if edit mode and ctrl + mouse clicked => add crop to object
        if (drawGuideShape) {
            DrawOptions.drawMouseIndicator(g2D, true, mousePosition, panelSize);

            saveCurrentBBox();
            DrawOptions.drawBBox(g2D, curBBoxPanelCoord, Color.red, (drawType == DrawConstants.DrawType.DRAW_CROP));
        }
    }

    /**
     * Makes sure that the selected bounding box is inside the image and saves
     * it.
     */
    private void saveCurrentBBox() {
        int x = Math.min(oldMouse.x, currentMouse.x);
        int y = Math.min(oldMouse.y, currentMouse.y);
        int w = Math.abs(currentMouse.x - oldMouse.x);
        int h = Math.abs(currentMouse.y - oldMouse.y);

        // normalise the point to the image
        if (!Utils.checkBounds(new Point(x, y), getWorkImgSize())) {
            if (x < 0) {
                // the width of the crop is as well smaller
                w += x;
                x = 0;
            }
            if (y < 0) {
                // the height of the crop is as well smaller
                h += y;
                y = 0;
            }
            if (x >= workImg.getWidth()) {
                x = workImg.getWidth() - 1;
            }
            if (y >= workImg.getHeight()) {
                y = workImg.getHeight() - 1;
            }
        }

        if (!Utils.checkBounds(new Point(x + w, y + h), getWorkImgSize())) {
            if (x + w >= workImg.getWidth()) {
                w = workImg.getWidth() - x;
            }

            if (y + h >= workImg.getHeight()) {
                h = workImg.getHeight() - y;
            }
        }
        curBBoxPanelCoord = new Rectangle(x, y, w, h);
    }

    /**
     * Display the size of the bounding box in the top left corner.
     */
    private void displayBoxSize() {
        Rectangle imgPos = getCurBBoxImageCoords();
        jLBoxSize.setText(imgPos.width + "x" + imgPos.height);

        Point pos = DrawOptions.computeTextLocation(curBBoxPanelCoord, jLBoxSize.getSize(), new Dimension(workImg.getWidth(), workImg.getHeight()));
        jLBoxSize.setLocation(pos.x, pos.y);

        jLBoxSize.setVisible(true);
    }

    /**
     * Refresh the displayed image, considering the maximum available space on
     * the gui.
     *
     * @param image             the image to be displayed
     * @param availableDrawSize the amount of space available on the gui for drawing the image
     */
    public void refreshImage(BufferedImage image, Dimension availableDrawSize) {
        // compute panel size based on maximum available space for it
        recomputePanelSize(new Dimension(image.getWidth(), image.getHeight()), availableDrawSize);

        // resize the displayed image, according to the new panel size
        updateImage(image);
    }

    /**
     * Change the frame with another one.
     *
     * @param image             the new image to be displayed
     * @param availableDrawSize the amount of space available on the gui for drawing the image
     */
    public void newFrame(BufferedImage image, Dimension availableDrawSize) {
        // when the new image has a different size than the previous, the size of the panel has to be adjusted
        if ((image.getWidth() != origImg.getWidth()) || (image.getHeight() != origImg.getHeight())) {
            // compute panel size based on maximum available space for it
            recomputePanelSize(new Dimension(image.getWidth(), image.getHeight()), availableDrawSize);
        }

        // change the displayed image with the given one
        updateImage(image);

        // if the scribbles are not meant to be kept, erase all the related data
        cleanScribbleLists();
    }

    /**
     * Recompute the panel size, considering the maximum available space on the
     * gui.
     *
     * @param availableDrawSize the amount of space available on the gui for
     * drawing the image
     */
    private void recomputePanelSize(Dimension imgSize, Dimension availableDrawSize) {
        // recompute the size of the panel, according to the new image size
        this.panelSize = PanelResolution.computeOptimalPanelSize(new Dimension(imgSize.width, imgSize.height), availableDrawSize);

        this.setPreferredSize(panelSize);
        this.setSize(panelSize);
        this.setMinimumSize(panelSize);
        this.setMaximumSize(panelSize);

        // compute the resize based on the new image size and the updated panel size
        double ratioWidth = (double) imgSize.width / (double) panelSize.width;
        double ratioHeight = (double) imgSize.height / (double) panelSize.height;
        resize = new Resize(ratioWidth, ratioHeight);
    }

    /**
     * The displayed image is resized by the ratio between the original image
     * size and the panel size where the image will be displayed. When the
     * resize ratio is changed, the displayed image has to be changed as well.
     *
     * @param image the original image (in original size)
     */
    private void updateImage(BufferedImage image) {
        // update the image
        this.origImg = image;

        // create the work image, based on the resize 
        if (!resize.equals(new Resize(1.0, 1.0))) {
            times.start();
            workImg = resize.resizeImage(origImg);
            times.stopMS("resized image to " + workImg.getWidth() + "x" + workImg.getHeight() + " in ");
        } else {
            workImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), origImg.getType());
            Utils.copySrcIntoDstAt(origImg, workImg);
        }
    }

    /**
     * Clean the structures which save information related to the scribbles.
     */
    private void cleanScribbleLists() {
        //clean the pixels list
        scribbleList.clear();

        //set all points as not visited
        visited = new boolean[origImg.getHeight()][origImg.getWidth()];
    }

    /**
     * Set the chosen type of drawing which shall be performed on the current
     * panel. See DrawType.java for more details on the options.
     *
     * @param drawType - the predefined type of drawing.
     */
    public void setDrawType(DrawConstants.DrawType drawType) {
        this.drawType = drawType;
    }

    /**
     * Returns the chosen type of drawing which used on the current panel. See
     * DrawType.java for more details on the options.
     *
     * @return - the type of drawing used on the current panel
     */
    public DrawConstants.DrawType getDrawType() {
        return drawType;
    }

    /**
     * Set the user chosen options related to the way a scribble is drawn. See
     * ScribbleOptions.java for more info related on the options.
     *
     * @param scribbleOpt - the configuration wanted by the user
     */
    public void setBrushOptions(BrushOptions scribbleOpt) {
        this.brushOpt = scribbleOpt;
    }

    /**
     * Set the type of the action shall be taken for the object.
     *
     * @param actionType - the type of action: -1 = erase; 0 = background; 1 = object
     */
    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    /**
     * Sets work img.
     *
     * @param workImg the work img
     */
    public void setWorkImg(BufferedImage workImg) {
        this.workImg = workImg;
    }

    /**
     * Returns the description of the current bounding box drawn by the user in
     * panel coordinates.
     *
     * @return - the x and y coordinates of the upper-left corner, the width and the height of the box of the panel coordinates.
     */
    public Rectangle getCurBBoxPanelCoords() {
        return curBBoxPanelCoord;
    }

    /**
     * Returns the description of the current bounding box drawn by the user in
     * image coordinates.
     *
     * @return - the x and y coordinates of the upper-left corner, the width and the height of the box of the image coordinates.
     */
    public Rectangle getCurBBoxImageCoords() {
        // convert to img coordinates the top-left point
        Point tlPointPanel = new Point(curBBoxPanelCoord.x, curBBoxPanelCoord.y);
        Point tlPointImg = resize.resizedToOriginal(tlPointPanel);

        // convert to img coordinates the bottom-right point
        Point brPointPanel = new Point(curBBoxPanelCoord.x + curBBoxPanelCoord.width,
                curBBoxPanelCoord.y + curBBoxPanelCoord.height);
        Point brPointImg = resize.resizedToOriginal(brPointPanel);

        // compute the width and height of the box in the image coordinates
        int widthImg = brPointImg.x - tlPointImg.x;
        int heightImg = brPointImg.y - tlPointImg.y;

        // create an object which represents the selection, in the original image
        return (new Rectangle(tlPointImg.x, tlPointImg.y, widthImg, heightImg));
    }

    /**
     * Set the coordinates of the box in panel coordinates.
     *
     * @param curBBoxPanelCoord - the new bounding box
     */
    public void setCurBBoxPanelCoord(Rectangle curBBoxPanelCoord) {
        this.curBBoxPanelCoord = curBBoxPanelCoord;
    }

    /**
     * Return the list of valid scribbles.
     *
     * @return - the list of scribbles
     */
    public List<ScribbleInfo> getScribbleList() {
        List<ScribbleInfo> outputScribbleList = new ArrayList<>();

        // get the list of segmented pixels and save only the valid and visited ones
        for (ScribbleInfo pi : this.scribbleList) {
            if (visited[pi.getImgPosY()][pi.getImgPosX()]) {
                outputScribbleList.add(pi);
            }
        }

        return outputScribbleList;
    }

    /**
     * Load a predefined list of scribbles.
     *
     * @param scribbleList - the new list of scribbles
     */
    public void setScribbleList(List<ScribbleInfo> scribbleList) {
        this.scribbleList = scribbleList;

        for (ScribbleInfo pi : scribbleList) {
            visited[pi.getImgPosY()][pi.getImgPosX()] = true;
        }
    }

    /**
     * Reset the position of the places where the user executed a click
     * press-release action. Used to reset the bounding boxes and crops from the
     * gui.
     */
    public void resetMousePosition() {
        oldMouse = new Point();
        currentMouse = new Point();
    }

    /**
     * Add the object ids on labels and display them on the work panel.
     */
    private void displayObjectsIds(Graphics2D g2D) {
        // save the color of g2D to be able to restore it
        Color g2dColor = g2D.getColor();

        for (DisplayBBox bBox : bBoxList) {
            // only the outter crop containing the small crops has to have the id shown
            if (!bBox.isUseDashedLine()) {
                DrawOptions.displayObjectId(g2D, bBox.getPanelBox(), bBox.getText(), bBox.getColor(), new Dimension(workImg.getWidth(), workImg.getHeight()));
            }
        }

        g2D.setColor(g2dColor);
    }

    /**
     * Set the list of bounding boxes to be drawn on the interface.
     *
     * @param positions - list of positions where the bounding boxes have to be drawn
     */
    public void setBBoxList(List<DisplayBBox> positions) {
        this.bBoxList = positions;
    }

    /**
     * Set the list of polygons to be drawn on the interface.
     *
     * @param polygonList - list of polygons which have to be drawn
     */
    public void setPolygonDisplayList(List<DisplayPolygon> polygonList) {
        this.polygonList = polygonList;
    }

    /**
     * Get the id of the selected box, from the bBoxList. It is important for
     * being able to retrieve the position of the selected box.
     */
    private void getSelectedBoxId() {
        int cropWidth = workImg.getWidth();
        int cropHeight = workImg.getHeight();

        int idBox = -1;    // set as initial id, an invalide one
        int index = 0;

        for (DisplayBBox displayBox : bBoxList) {
            // if the mouse is positioned inside of one crop
            boolean validPosition = (mousePosition.x >= displayBox.getPanelBox().x)
                    && (mousePosition.x <= (displayBox.getPanelBox().x + displayBox.getPanelBox().width))
                    && (mousePosition.y >= displayBox.getPanelBox().y)
                    && (mousePosition.y <= (displayBox.getPanelBox().y + displayBox.getPanelBox().height));
            boolean validCrop = ((cropWidth > displayBox.getPanelBox().width)
                    && (cropHeight > displayBox.getPanelBox().height));

            if (validPosition && validCrop) {
                //take the index only if the size of the crop is smaller => crop inside crop
                idBox = index;
                cropWidth = displayBox.getPanelBox().width;
                cropHeight = displayBox.getPanelBox().height;
            }

            index++;
        }

        idSelectedBox = idBox;
    }

    /**
     * Sets as selected box, the object from the specified position.
     *
     * @param posImg - the position of the box, in image coordinates
     */
    public void setSelectedBox(Rectangle posImg) {
        Rectangle posPanel = resize.originalToResized(posImg);

        bBoxList.stream().filter(displayBox -> (displayBox.equalPanels(posPanel))).forEach(
                displayBox -> idSelectedBox = bBoxList.indexOf(displayBox));
    }

    /**
     * Sets draw scribble history.
     *
     * @param drawScribbleHistory the draw scribble history
     */
    public void setDrawScribbleHistory(boolean drawScribbleHistory) {
        this.drawScribbleHistory = drawScribbleHistory;
    }

    /**
     * Set the list of scribbles to be displayed. The have no usage in the
     * running of the algorithms.
     *
     * @param cropScribbles - the list of scribbles to be drawn
     */
    public void addScriblesToDisplay(List<DisplayScribbles> cropScribbles) {
        displayScribbles = cropScribbles;
    }

    /**
     * Get the crop which will be drawn as a selected box.
     *
     * @return - the object to be displayed as selected
     */
    public DisplayBBox getSelectedBox() {
        if (idSelectedBox > -1) {
            return bBoxList.get(idSelectedBox);
        }
        return null;
    }

    /**
     * Reset the selection. No box shall be selected.
     */
    public void resetIdSelectedBox() {
        this.idSelectedBox = -1;
    }

    /**
     * Returns the original image, in original size
     *
     * @return - the original image drawn on the current panel.
     */
    public BufferedImage getOrigImg() {
        return origImg;
    }

    /**
     * Returns the work image, in the recomputed size
     *
     * @return - the work image drawn on the current panel.
     */
    public BufferedImage getWorkImg() {
        return workImg;
    }

    /**
     * Reload the work image by recomputing it from the original image.
     */
    public void reloadWorkImg() {
        workImg = resize.resizeImage(origImg);
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
     * Returns the list of pixels needed by the cuda module, in the specific
     * format.
     *
     * @return - the list of pixels belonging to each object
     */
    public List<PixelMap>[] getCudaPixelMap() {
        return cudaPixelMap;
    }

    /**
     * Get the resize characteristics.
     *
     * @return - the resize parameter, encapsulating the resize ratios between the panel and the original image.
     */
    public Resize getResize() {
        return resize;
    }

    /**
     * Get the dimension of the original image.
     *
     * @return the dimension of the original image
     */
    public Dimension getOrigImgSize() {
        return new Dimension(origImg.getWidth(), origImg.getHeight());
    }

    /**
     * Get the dimension of the working image.
     *
     * @return the dimension of the working image
     */
    public Dimension getWorkImgSize() {
        return new Dimension(workImg.getWidth(), workImg.getHeight());
    }

    /**
     * Allow the user to draw a crop in order to add it to an existent object,
     * which is in edit mode.
     *
     * @param addCropToObj - true if the user should be able to draw a crop to add to an object, false otherwise
     */
    public void setAddCropToObj(boolean addCropToObj) {
        // enable only if there is a selected object
        this.addCropToObj = (addCropToObj && (idSelectedBox > -1));
    }

    /**
     * Returns the offset between the current position of the mouse and the
     * previous one.
     *
     * @return - the x and y offset of the mouse - panel coordinates
     */
    public Point getMouseMovementOffsetPanel() {
        Point offset = new Point();
        offset.setLocation(currentMouse.x - prevMouse.x, currentMouse.y - prevMouse.y);
        return offset;
    }

    /**
     * Returns the current position of the mouse.
     *
     * @return - the x and y of the position of the mouse - panel coordinates
     */
    public Point getMouseCurrentPosPanel() {
        return currentMouse;
    }

    /**
     * Get the characteristics of the brush.
     *
     * @return - the object encapsulating the size and the density of the brush
     */
    public BrushOptions getBrushOpt() {
        return brushOpt;
    }

    /**
     * Returns the time when the mouse pressed event happened, for the case of
     * bounding box drawing!
     *
     * @return - the time, in nanoseconds, when the mouse was pressed
     */
    public long getMousePressedTime() {
        return times.getStartTime();
    }

    /**
     * Shows/Removes the rectangle representing the preview shape of the
     * segmentation (the red rectangle for example).
     *
     * @param showGuideShape - true if the shape shall be shown and false otherwise
     */
    public void setShowGuideShape(boolean showGuideShape) {
        // do not draw the shape anymore
        drawGuideShape = showGuideShape;
        jLBoxSize.setVisible(drawGuideShape);
    }

    /**
     * Set the color of the object being segmented.
     *
     * @param objColor - the color of the object being segmented
     */
    public void setObjColor(Color objColor) {
        this.objColor = objColor;
    }

    /**
     * Return the current drawn polygon - image coordinates.
     *
     * @return the current drawn polygon - image coordinates
     */
    public Polygon getCurrentPolygonImg() {
        return resize.resizedToOriginal(currentPolygon);
    }

    /**
     * Initialise the current drawn polygon.
     */
    public void initCurrentPolygon() {
        this.currentPolygon = new Polygon();
    }

    /**
     * Reset the current drawn polygon.
     */
    public void resetCurrentPolygon() {
        this.currentPolygon = null;
    }

    private void drawBoxList(Graphics2D g2D) {
        // draw the crops and boxes from history
        if (bBoxList != null) {
            drawBBoxList(g2D);
            displayObjectsIds(g2D);
        }
    }

    /**
     * Draw the list of scribbles on the panel. Draw the points existent in the
     * list, with the purpose just to be displayed, using the defined color for
     * each.
     *
     * @param g2D the graphics object
     */
    private void drawScribbleList(Graphics2D g2D) {
        // draw the scribbles of the objects
        if (displayScribbles != null && drawScribbleHistory) {
            displayScribbles.stream().forEach(scribble
                    -> DrawOptions.drawPoint(g2D, scribble.getPanelPos(), scribble.getColor()));
        }
    }

    /**
     * Draw the list of polygons on the panel.
     *
     * @param g2D the graphics object
     */
    private void drawPolygonList(Graphics2D g2D) {
        // draw the polygon objects
        if (polygonList != null) {
            drawPolygonObjList(g2D);
        }
    }

    /**
     * Enable/Disable the drawing of a highlight of the object to be able to see
     * faster the segmented objects.
     *
     * @param drawAlphaObj true - draws an alpha on top of the object, of the color of the object; false - does not draw anything additional to the object borders
     */
    public void setDrawAlphaObj(boolean drawAlphaObj) {
        this.drawAlphaObj = drawAlphaObj;
    }

    /**
     * Set the value of the alpha used to highlight the object.
     *
     * @param objAlpha the value of the alpha to be used for highlighting the object (0-255)
     */
    public void setObjAlpha(int objAlpha) {
        this.objAlpha = objAlpha;
    }

}
