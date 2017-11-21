/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Constants.
 *
 * @author Olimpia Popica
 */
public class Constants {

    /**
     * Number of objects which can be segmented in the application
     */
    public static final int NUMBER_OF_OBJECTS = 2;

    /**
     * The path to the user preferences containing the configuration specific to
     * the user.
     */
    public static final String USER_PREF_FILE_PATH = "." + File.separatorChar + "cfg" + File.separatorChar + "userPreferences.txt";

    /**
     * Defines the when max size of the brush for which the mouse is an arrow.
     * If the brush size is greater than the specified value, an icon is
     * generated for the mouse.
     */
    public static final int MIN_MOUSE_BRUSH_ICON = 2;

    /**
     * The width of the result panel where the segmented image is shown.
     */
    public static final int RESULT_PANEL_WIDTH = 400;
    /**
     * The height of the result panel where the segmented image is shown.
     */
    public static final int RESULT_PANEL_HEIGHT = 300;

    /**
     * The path to where the User manual of Pixie is stored.
     */
    public static final String APPLICATION_MANUAL = "." + File.separatorChar + "doc" + File.separatorChar + "Pixie_Manual.pdf";

    /**
     * The release number of the software.
     */
    public static final String RELEASE_NUMBER = "0.5";
    /**
     * The number of the current sprint.
     */
    public static final String SPRINT_NUMBER = "3";
    /**
     * The software version is made of the release number together with the
     * sprint number. This will help in tracking the implemented issue for each
     * release.
     */
    public static final String SOFTWARE_VERSION = RELEASE_NUMBER + "." + SPRINT_NUMBER;

    /**
     * The version of JCuda used in the current software.
     */
    public static final String JCUDA_VERSION = "0.8.0";
    /**
     * The version of JCuda used in the current software.
     */
    public static final String JOCL_VERSION = "2.0.0";
    /**
     * The version of the jar used for the drawing tablet communication.
     */
    public static final String JPEN_VERSION = "2";
    /**
     * The version of the jar used for the drawing tablet communication.
     */
    public static final String HAMCREST_VERSION = "1.3";
    /**
     * The version of the JUnit for which the tests are running.
     */
    public static final String JUNIT_VERSION = "4.12";
    /**
     * The version of openCV used in the project.
     */
    public static final String OPENCV_VERSION = "3.2";

    /**
     * Play video file in forward mode
     */
    public static final int PLAY_MODE_FORWARD = 0;

    /**
     * Play video file in backward mode
     */
    public static final int PLAY_MODE_BACKWARD = 1;

    /**
     * Marks an action regarding the next frame.
     */
    public static final int NEXT_FRAME = 0;
    /**
     * Marks an action regarding the previous frame.
     */
    public static final int PREV_FRAME = 1;
    /**
     * Marks an action regarding the jump to a certain frame.
     */
    public static final int JUMP_TO_FRAME = 2;

    /**
     * Shows that the matting algorithm shall be run on the original image.
     */
    public static final int RUN_MATT_ORIG_IMG = 0;
    /**
     * Shows that the matting algorithm shall be run on the highlighted image.
     */
    public static final int RUN_MATT_HIGHLIGHT_IMG = 1;

    /**
     * The percentage of border which has to be added to the box, in preview
     * mode.
     */
    public static final float BORDER_PERCENTAGE = 0.2f;
    /**
     * The min border which shall be added to the box, in preview mode.
     */
    public static final int MIN_BORDER = 10;

    /**
     * Allow the max percentage of the screen to be used for resizing the
     * BBoxes/Crops
     */
    public static final double MAX_SCREEN_PERCENT_RESIZE = 0.85;

    /**
     * How much percentage shall be left for insets and other screen
     * decorations, on the horizontal direction.
     */
    public static final float TOLERANCE_WIDTH = 0.06f;

    /**
     * How much percentage shall be left for insets and other screen
     * decorations, on the vertical direction.
     */
    public static final float TOLERANCE_HEIGHT = 0.12f;

    /**
     * The text used to mark an invalid attribute.
     */
    public static final String INVALID_ATTRIBUTE_TEXT = "undefined";
    
    /**
     * The default text in the root of the object attributes tree.
     */
    public static final String OBJECT_ATTRIBUTES_TEXT = "Object Attributes";
    
     /**
     * The default text in the root of the frame attributes tree.
     */
    public static final String FRAME_ATTRIBUTES_TEXT = "Frame Attributes";

    /**
     * The name of the original image.
     */
    public static final String ORIGINAL_IMG = "Original Image";
    /**
     * The name of the segmented image.
     */
    public static final String SEGMENTED_IMG = "Working Panel";
    /**
     * The name of the segmented image.
     */
    public static final String SEGMENTED_IMG_RESIZED = "Working Panel Resized";
    /**
     * The name of the result image.
     */
    public static final String SEMANTIC_SEGMENTATION_IMG = "Semantic Segmentation";
    /**
     * The name of the joined images.
     */
    public static final String JOINED_IMGS = "Joined Images";

    /**
     * The name of the default folder where to export files.
     */
    public static final String EXPORT_IMGS_PATH = new File("").getAbsolutePath() + File.separatorChar + "Export";

    /**
     * The possible options of getting a grayscale value out of an rgb one.
     */
    public enum GrayscaleOption {
        /**
         * Standard luminance grayscale option.
         */
        STANDARD_LUMINANCE, // L = 0.2126*R + 0.7152*G + 0.0722*B
        /**
         * Gimp luminance grayscale option.
         */
        GIMP_LUMINANCE, // L = 0.222*R + 0.717*G + 0.061*B
        /**
         * Fast grayscale grayscale option.
         */
        FAST_GRAYSCALE          // L = ((2*R + 5*G + 1*B) / 8) = ((r<<1 + g<<2 + g + b) >> 3)
    }

    /**
     * The possible normalisation technics of a vector.
     */
    public enum Normalisation {
        /**
         * L 2 norm block normalisation.
         */
        L2_NORM_BLOCK, // |x| = sqrt(x1^2 + x2^2 + x3^2 + ... + xn^2)
        /**
         * L 2 hys block normalisation.
         */
        L2_HYS_BLOCK, // l2-norm followd by clipping (limiting the maximum values to 0.2) and renormalising
        /**
         * Tile level normalisation.
         */
        TILE_LEVEL, // normalisation at tile level (divide by the max of the tile)
        /**
         * Block level normalisation.
         */
        BLOCK_LEVEL, // normalisation at block level (divide by the max of the block)
        /**
         * Image level normalisation.
         */
        IMAGE_LEVEL, // normalisation at image level (divide by the max of the image)
        /**
         * Do nothing normalisation.
         */
        DO_NOTHING
    }

    /**
     * Defines the possible modes of the application.
     */
    public enum AppConfigMode {
        /**
         * Premium edition app config mode.
         */
        PREMIUM_EDITION, // the application opens videos coming from the server and sends the data back to be saved in the database
        /**
         * The Community edition.
         */
        COMMUNITY_EDITION       // the application opens pictures coming from a local source and saves the data locally on the machine (in a file)
    }

    /**
     * Defines the possible errors or interruptions which could happen during the save of ground
     * truth.
     */
    public enum GTSaveInterruptCodes {
        /**
         * No error gt save interrupt codes.
         */
        NO_ERROR,
        /**
         * Frame attrib not set gt save interrupt codes.
         */
        FRAME_ATTRIB_NOT_SET,
        /**
         * Data not found gt save interrupt codes.
         */
        DATA_NOT_FOUND
    }

    /**
     * The constant IMG_EXTENSION_LIST.
     */
    public static final List<String> IMG_EXTENSION_LIST = Arrays.asList("bmp", "jpg", "jpeg", "png");
    /**
     * The constant VIDEO_EXTENSION_LIST.
     */
    public static final List<String> VIDEO_EXTENSION_LIST = Arrays.asList("mp4", "nv12", "bgr", "avi");
    /**
     * The constant EXTENSION_LIST.
     */
    public static final List<String> EXTENSION_LIST = Stream.concat(IMG_EXTENSION_LIST.stream(), VIDEO_EXTENSION_LIST.stream()).collect(Collectors.toList());

    /**
     * The path to the file containing the frame attributes, for offline mode.
     */
    public static final String FRAME_ATTRIBUTES_PATH = "cfg" + File.separator + "frameAttributes.txt";

    /**
     * The path to the file containing the object attributes, for offline mode.
     */
    public static final String OBJECT_ATTRIBUTES_PATH = "cfg" + File.separator + "objectAttributes.txt";

    /**
     * The list of colors used in the application for the objects and which are
     * reserved for object segmentation. The user cannot select and use one of
     * them.
     */
    public static final List<Color> COLORS_LIST = Arrays.asList(Color.black, Color.green, Color.blue, Color.yellow, Color.magenta, Color.cyan,
            (new Color(153, 255, 153)), (new Color(153, 51, 255)), (new Color(51, 204, 255)), (new Color(255, 153, 102)), (new Color(102, 102, 255)),
            (new Color(255, 99, 71)), (new Color(165, 42, 42)), (new Color(154, 205, 50)), (new Color(0, 206, 209)), (new Color(75, 0, 130)),
            (new Color(175, 159, 106)), (new Color(119, 159, 106)), (new Color(0, 159, 106)), (new Color(0, 80, 106)), (new Color(0, 200, 106)),
            (new Color(124, 128, 39)), (new Color(124, 128, 236)), (new Color(185, 112, 56)), (new Color(185, 75, 125)), (new Color(128, 0, 0)),
            (new Color(128, 128, 0)), (new Color(0, 128, 0)), (new Color(128, 0, 128)), (new Color(0, 128, 128)), (new Color(0, 0, 128)),
            (new Color(238, 130, 238)), (new Color(245, 222, 179)), (new Color(210, 105, 30)), (new Color(105, 105, 105)), (new Color(30, 144, 255))
    );

    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. Java adds an implicit public
     * constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private Constants() {
        throw new IllegalStateException("Utility class, do not instantiate!");
    }
}
