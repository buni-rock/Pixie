/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import gui.viewer.ExportImagesGUI;
import gui.viewer.Messages;
import gui.support.BrushOptions;
import gui.support.CustomTreeNode;
import library.BoxLRTB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.CRC32;

/**
 * The type Utils.
 *
 * @author Olimpia Popica
 */
public class Utils {

    /**
     * logger instance
     */
    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. Java adds an implicit public
     * constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private Utils() {
        throw new IllegalStateException("Utility class, do not instantiate!");
    }

    /**
     * Get the image from the specified box.
     *
     * @param origImg - the original image from where the selection shall be
     * copied
     * @param posOrig - the position where the image has to be copied from
     * @return - the image contained in the specified crop
     */
    public static BufferedImage getSelectedImg(BufferedImage origImg, Rectangle posOrig) {
        Rectangle pos = posOrig;
        BufferedImage tempImg = null;

        if ((pos != null) && (pos.width > 0) && (pos.height > 0)) {
            tempImg = new BufferedImage(pos.width, pos.height, origImg.getType());

            for (int y = pos.y; y < pos.y + pos.height; y++) {
                for (int x = pos.x; x < pos.x + pos.width; x++) {
                    tempImg.setRGB(x - pos.x, y - pos.y, origImg.getRGB(x, y));
                }
            }
        }

        return tempImg;
    }

    /**
     * Compute the l^2-Norm of the input vector.
     * <p>
     * |x| = sqrt(x1^2 + x2^2 + x3^2 + ... + xn^2)
     *
     * @param vector - the input vector
     * @return - the value of the norm of the vector
     */
    public static double computeVectorL2Norm(double[] vector) {
        double sum = 0.0;

        // compute the sum of squares
        for (int index = 0; index < vector.length; index++) {
            sum += vector[index] * vector[index];
        }

        // compute the compute square root of the sum of squares
        return Math.sqrt(sum);
    }

    /**
     * Compute squared euclidian distance double.
     *
     * @param vectorA the vector a
     * @param vectorB the vector b
     * @return the double
     */
    public static double computeSquaredEuclidianDistance(double[] vectorA, double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            LOG.error("The input arrays have different sizes!!!");
        }

        double squaredSum = 0.0;

        // compute the sum of square differences
        for (int index = 0; index < vectorA.length; index++) {
            squaredSum += (vectorA[index] - vectorB[index]) * (vectorA[index] - vectorB[index]);
        }

        return (squaredSum / vectorA.length);
    }

    /**
     * Gets the current date and time in the form of "yyyy-MM-dd HH:mm:ss".
     *
     * @return - the current date and time in the format: "yyyy-MM-dd HH:mm:ss"
     */
    public static String getCurrentTimeStamp() {
        java.util.Date dt = new java.util.Date();

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(dt);
    }

    /**
     * Compute the location of the window relative to the mouse location. The
     * window shall be centered on the mouse location.
     *
     * @param winDim - the dimension of the window to be centered
     * @return - the point where the window shall be located
     */
    public static Point winLocRelativeToMouse(Dimension winDim) {
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) (mouse.getX() - (winDim.width / 2.0));
        int y = (int) (mouse.getY() - (winDim.height / 2.0));

        // check if the window will get out of the image
        x = Math.min(Math.max(0, x), Math.max(0, (screenSize.width - winDim.width)));
        y = Math.min(Math.max(0, y), Math.max(0, (screenSize.height - winDim.height - 100)));

        return (new Point(x, y));
    }

    /**
     * Check if the component com1 fits inside component comp2.
     *
     * @param comp1 - the size of the component to be checked if it fits
     * @param comp2 - the size of the component where the other component should
     * fit in
     * @return - return true if the component 1 fits in the component 2
     */
    public static boolean checkPlausability(Dimension comp1, Dimension comp2) {
        return ((comp1.width > 0) && (comp1.width <= comp2.width))
                && (comp1.height > 0) && (comp1.height <= comp2.height);
    }

    /**
     * Checks the extension of the given file and returns true if it is an image
     * file, or false otherwise.
     *
     * @param fileName - the name of the file to be checked
     * @return - true for image files and false for other file types
     */
    public static boolean isImageFile(String fileName) {
        return checkExtension(fileName, common.Constants.IMG_EXTENSION_LIST);
    }

    /**
     * Checks the extension of the given file and returns true if it is a video
     * file, or false otherwise.
     *
     * @param fileName - the name of the file to be checked
     * @return - true for video files and false for other file types
     */
    public static boolean isVideoFile(String fileName) {
        return checkExtension(fileName, common.Constants.VIDEO_EXTENSION_LIST);
    }

    /**
     * Get the extension of a file.
     *
     * @param f - the file itself
     * @return - the string representing the extension of the file
     */
    public static String getExtension(File f) {
        String fileName = f.getName();
        return getExtension(fileName);
    }

    /**
     * Get the extension of the string representing the name of a file.
     *
     * @param fileName - the name of the file
     * @return - the string representing the extension of the file
     */
    public static String getExtension(String fileName) {
        String ext = null;
        int i = fileName.lastIndexOf('.');
        if ((i > 0) && (i < fileName.length() - 1)) {
            ext = fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
        }
        return ext;
    }

    /**
     * Obtains the data from the raster of the given image as a byte array.
     *
     * @param image The image
     * @return The image data
     * @throws IllegalArgumentException If the given image is not backed by a
     * DataBufferByte. Usually, only BufferedImages of with the types
     * BufferedImage.TYPE_BYTE_* have a DataBufferByte
     */
    public static byte[] getByteData(BufferedImage image) {
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (!(dataBuffer instanceof DataBufferByte)) {
            throw new IllegalArgumentException(
                    "Image does not contain a DataBufferByte");
        }
        DataBufferByte dataBufferByte = (DataBufferByte) dataBuffer;
        byte data[] = dataBufferByte.getData();
        return data;
    }

    /**
     * Copy one buffered image to another.
     *
     * @param src - source image
     * @param dst - destination image
     */
    public static void copySrcIntoDstAt(final BufferedImage src, final BufferedImage dst) {
        byte[] srcBuf = getByteData(src);
        byte[] dstBuf = getByteData(dst);
        int representationFactor;

        // width * hight * 3 because of rgb components and byte representation
        switch (src.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_BYTE_INDEXED:
                representationFactor = 1;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                representationFactor = 3;
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                representationFactor = 4;
                break;
            default:
                representationFactor = 3;
                break;
        }

        System.arraycopy(srcBuf, 0, dstBuf, 0, src.getWidth() * src.getHeight() * representationFactor);
    }

    /**
     * Copy one buffered image to another and return the copied image.
     *
     * @param src - source image
     * @return - the copied image of the original
     */
    public static BufferedImage createImageCopy(final BufferedImage src) {
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

        byte[] srcBuf = getByteData(src);
        byte[] dstBuf = getByteData(dst);
        int representationFactor;

        // width * hight * 3 because of rgb components and byte representation
        switch (src.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                representationFactor = 1;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                representationFactor = 3;
                break;
            case BufferedImage.TYPE_4BYTE_ABGR:
                representationFactor = 4;
                break;
            default:
                representationFactor = 3;
                break;
        }

        System.arraycopy(srcBuf, 0, dstBuf, 0, src.getWidth() * src.getHeight() * representationFactor);

        return dst;
    }

    /**
     * Convert a matrix into a gray scale buffered image.
     *
     * @param width - the width of the image
     * @param height - the height of the image
     * @param imageType - the type of image to create
     * @param imgMatrix - the matrix with the pixels of the image
     * @return - a buffered image, based on the input matrix
     */
    public static BufferedImage convertToBuffImage(int width, int height, int imageType, double[][] imgMatrix) {
        BufferedImage bi = new BufferedImage(width, height, imageType);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = Utils.limit(0, (int) imgMatrix[x][y], 255);
                bi.setRGB(x, y, new Color(pixel, pixel, pixel).getRGB());
            }
        }

        return bi;
    }

    /**
     * Apply the histogram equalisation for the input image.
     *
     * @param originalImage - the image to be processed
     * @return - the image with the equalisation of histogram applied - in
     * grayscale
     */
    public static BufferedImage histogramEqGrayscale(BufferedImage originalImage) {
        BufferedImage workImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());

        int[] rgb;
        int[] meanIntensity = new int[256];
        int[] histCum = new int[256];
        int[] transf = new int[256];

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                rgb = getRGB(originalImage.getRGB(i, j));

                int average = (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);

                meanIntensity[average]++;
            }
        }
        histCum[0] = meanIntensity[0];

        for (int i = 1; i < 256; i++) {
            histCum[i] = meanIntensity[i] + histCum[i - 1];
        }

        for (int i = 0; i < 256; i++) {
            transf[i] = (255 * histCum[i]) / (originalImage.getWidth() * originalImage.getHeight());
        }

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                rgb = getRGB(originalImage.getRGB(i, j));
                int average = (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
                workImage.setRGB(i, j, new Color(transf[average], transf[average], transf[average]).getRGB());
            }
        }
        return workImage;

    }

    /**
     * Apply the histogram equalisation for the input image.
     *
     * @param originalImage - the image to be processed
     * @return - the image with the equalisation of histogram applied - in color
     */
    public static BufferedImage histogramEqColor(BufferedImage originalImage) {
        BufferedImage workImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());

        int[] rgb;
        int[] meanIntensityR = new int[256];
        int[] histCumR = new int[256];
        int[] transfR = new int[256];

        int[] meanIntensityG = new int[256];
        int[] histCumG = new int[256];
        int[] transfG = new int[256];

        int[] meanIntensityB = new int[256];
        int[] histCumB = new int[256];
        int[] transfB = new int[256];

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                rgb = getRGB(originalImage.getRGB(i, j));
                meanIntensityR[rgb[0]]++;
                meanIntensityG[rgb[1]]++;
                meanIntensityB[rgb[2]]++;
            }
        }
        histCumR[0] = meanIntensityR[0];
        histCumG[0] = meanIntensityG[0];
        histCumB[0] = meanIntensityB[0];

        for (int i = 1; i < 256; i++) {
            histCumR[i] = meanIntensityR[i] + histCumR[i - 1];
            histCumG[i] = meanIntensityG[i] + histCumG[i - 1];
            histCumB[i] = meanIntensityB[i] + histCumB[i - 1];
        }

        for (int i = 0; i < 256; i++) {
            transfR[i] = (255 * histCumR[i]) / (originalImage.getWidth() * originalImage.getHeight());
            transfG[i] = (255 * histCumG[i]) / (originalImage.getWidth() * originalImage.getHeight());
            transfB[i] = (255 * histCumB[i]) / (originalImage.getWidth() * originalImage.getHeight());
        }

        for (int i = 0; i < originalImage.getWidth(); i++) {
            for (int j = 0; j < originalImage.getHeight(); j++) {
                rgb = getRGB(originalImage.getRGB(i, j));
                workImage.setRGB(i, j, new Color(transfR[rgb[0]], transfG[rgb[1]], transfB[rgb[2]]).getRGB());
            }
        }
        return workImage;
    }

    /**
     * Changes the input color to a similar color, which is not reserved.
     *
     * @param inputColor - the input color
     * @param objColorsList the obj colors list
     * @return - a color which is not reserved and it is similar to the input
     * one
     */
    public static Color changeColor(Color inputColor, List<Color> objColorsList) {
        int[] rgb = getRGB(inputColor.getRGB());
        Random rand = new Random();
        int randRange = 60;

        // change the color
        for (int index = 0; index < rgb.length; index++) {

            // add random values in the range [-0.5*range; +0.5*range)
            rgb[index] += (rand.nextInt(randRange) - (int) (randRange * 0.5f));

            // limit the value to byte
            rgb[index] = limit(0, rgb[index], 255);
        }

        // set the new color 
        Color color = new Color(rgb[0], rgb[1], rgb[2]);

        // make sure that the color is not reserved as well
        if (objColorsList.contains(color) || Constants.COLORS_LIST.contains(color)) {
            changeColor(color, objColorsList);
        }

        return color;
    }

    /**
     * Parse the given file path and name in order to extract just the file name
     * (with or without the extension, depending on the choice).
     *
     * @param fileNamePath the original file name, including its path
     * @param withExtension true if the returned name should include the file
     * extension; false if just the name of the file alone should be provided
     * @return the name of the file (with or without extension - as specified),
     * without its path
     */
    public static String getFileName(String fileNamePath, boolean withExtension) {
        // extract the name of the file and save it to create a folder there and store the info
        String[] tempStr;
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
            tempStr = fileNamePath.split(File.separator + File.separator);
        } else {
            tempStr = fileNamePath.split(File.separator);
        }

        if (withExtension) {
            return tempStr[tempStr.length - 1];
        } else {
            // get the name of the file without extension
            return tempStr[tempStr.length - 1].substring(0, tempStr[tempStr.length - 1].lastIndexOf('.'));
        }
    }

    /**
     * Adjusts the contrast of the image - NOT TESTED yet - to be checked and
     * optimised.
     *
     * @param workImage the work image
     * @param min the min
     * @param max the max
     * @return buffered image
     */
    public static BufferedImage contrast(BufferedImage workImage, int min, int max) {
        int[] rgb;
        int minimR = 255;
        int maximR = 0;
        int minimG = 255;
        int maximG = 0;
        int minimB = 255;
        int maximB = 0;

        // find the minimum and maximum values for R G and B
        for (int i = 0; i < workImage.getWidth(); i++) {
            for (int j = 0; j < workImage.getHeight(); j++) {
                rgb = getRGB(workImage.getRGB(i, j));

                minimR = Math.min(minimR, rgb[0]);
                minimG = Math.min(minimG, rgb[1]);
                minimB = Math.min(minimB, rgb[2]);

                maximR = Math.max(maximR, rgb[0]);
                maximG = Math.max(maximG, rgb[1]);
                maximB = Math.max(maximB, rgb[2]);
            }
        }

        // adjust the intensities
        for (int i = 0; i < workImage.getWidth(); i++) {
            for (int j = 0; j < workImage.getHeight(); j++) {
                rgb = getRGB(workImage.getRGB(i, j));
                int intensR = (rgb[0] - minimR) * (max - min) / (maximR - minimR) + min;
                int intensG = (rgb[1] - minimG) * (max - min) / (maximG - minimG) + min;
                int intensB = (rgb[2] - minimB) * (max - min) / (maximB - minimB) + min;
                workImage.setRGB(i, j, new Color(intensR, intensG, intensB).getRGB());
            }
        }
        return workImage;
    }

    /**
     * Extract the RGB information from a pixel.
     *
     * @param pixel - the packed value of the color of the pixel
     * @return - returns an array which represents in this order the values of
     * R, G and B components of the pixel
     */
    public static int[] getRGB(int pixel) {
        int[] rgb = new int[3];
        rgb[0] = ((pixel & 0x00FF0000) >>> 16);   //red color
        rgb[1] = ((pixel & 0x0000FF00) >>> 8);    //green color
        rgb[2] = (pixel & 0x000000FF);            //blue color
        return rgb;
    }

    /**
     * Returns a list of all the files (jpg and png) contained by the specified
     * folder.
     *
     * @param folder - the folder for which the list of image files is wanted
     * @return - an array of strings representing all the image files found
     * inside
     */
    public static List<String> listFilesForFolder(final File folder) {
        List<String> filesList = new ArrayList<>();

        if (folder.length() > 0) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry);
                } else if (fileEntry.getName().endsWith(".jpg") || fileEntry.getName().endsWith(".JPG")
                        || fileEntry.getName().endsWith(".png") || fileEntry.getName().endsWith(".PNG")) {
                    filesList.add(fileEntry.getAbsolutePath());
                    LOG.trace(fileEntry.getAbsolutePath());
                }
            }
        }

        return filesList;
    }

    /**
     * Fill in a matrix with a default value.
     *
     * @param width - the width of the matrix
     * @param height - the height of the matrix
     * @param value - the value to be filled in
     * @return - the matrix filled in with the wanted default value
     */
    public static int[][] initMatrix(int width, int height, int value) {
        int[][] array = new int[height][width];

        for (int y = 0; y < height; y++) {
            Arrays.fill(array[y], value);
        }

        return array;
    }

    /**
     * Fill in a matrix with a default value.
     *
     * @param width - the width of the matrix
     * @param height - the height of the matrix
     * @param value - the value to be filled in
     * @return - the matrix containing the wanted default value
     */
    public static float[][] initMatrix(int width, int height, float value) {
        float[][] array = new float[height][width];

        for (int y = 0; y < height; y++) {
            Arrays.fill(array[y], value);
        }

        return array;
    }

    /**
     * Generate a custom mouse icon to have it as preview while drawing
     * scribbles.
     *
     * @param brushOpt - the configuration of the icon to be generated
     * @param actionType - the id of the object for which the icon is created
     * @param objColor the obj color
     * @return - the cursor to be displayed by the mouse
     */
    public static Cursor generateCustomIcon(BrushOptions brushOpt, int actionType, Color objColor) {
        Cursor cursor;
        if (brushOpt.getBrushSize() > Constants.MIN_MOUSE_BRUSH_ICON) {
            Toolkit toolkit = Toolkit.getDefaultToolkit();

            Dimension dim = toolkit.getBestCursorSize(brushOpt.getBrushSize(), brushOpt.getBrushSize());
            BufferedImage buffered = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffered.createGraphics();
            float density = brushOpt.getBrushDensity();
            Random rand = new Random();
            byte[][] circleMask = getCircleMask(brushOpt.getBrushSize());  // the mask making the mouse pointer a circle

            if (brushOpt.getBrushSize() <= 5) {
                density = 1.0f;
            } else if (brushOpt.getBrushSize() > 5 && brushOpt.getBrushSize() < 10) {
                density = Math.max(0.5f, density);
            }

            // objects smaller than 0 are for erase purposes and the density is always full
            if (actionType < 0) {
                density = 1.0f;
            }

            // draw the inner part of the brush, the one which will show after a click
            g.setColor(getDrawingColor(actionType, objColor));

            for (int x = 1; x <= brushOpt.getBrushSize(); x++) {
                for (int y = 1; y <= brushOpt.getBrushSize(); y++) {
                    float randFloat = rand.nextFloat();
                    if ((randFloat < density) && (circleMask[x - 1][y - 1] == (byte) 1)) {
                        g.drawLine(x, y, x, y);
                    }
                }
            }

            // draw a white box around the brush for better visibility
            g.setColor(Color.white);

            g.drawOval(0, 0, brushOpt.getBrushSize() + 1, brushOpt.getBrushSize() + 1);

            g.dispose();

            int drawlLocation = (int) (brushOpt.getBrushSize() * 0.5f);
            cursor = toolkit.createCustomCursor(buffered, new Point(drawlLocation, drawlLocation), "myCursor");

        } else {
            cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }

        return cursor;
    }

    /**
     * Choose the best background for text: white for dark text, black for
     * bright text.
     *
     * @param color - the color for which the background shall be computed.
     * @param transparency - the wanted transparency for the returned color
     * @return - the adviced color for the background based on luminance
     */
    public static Color getContrastColor(Color color, int transparency) {
        // no precision needed here
        int y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;

        return ((y >= 128) ? new Color(0, 0, 0, transparency) : new Color(255, 255, 255, transparency));
    }

    /**
     * Returns the color: 0 = red (bkg), 1 = the color of the current object
     * (object), other: white (eraser).
     *
     * @param id - the value for which the color has to be established
     * @param objColor - the color of the object (object = not bkg , not erase)
     * @return - the corresponding color 0 = red, 1 = the color of the current
     * object, other = white
     */
    public static Color getDrawingColor(int id, Color objColor) {
        switch (id) {
            case 0:
                return Color.red;
            case 1:
                return objColor;
            default:
                return Color.white;
        }
    }

    /**
     * Maps the objects to colors.
     *
     * @param id - the id of the object, in the database
     * @return - the color of the object with the specified id
     */
    public static Color getColorOfObjByID(long id) {
        // compute the index of the color based on the object id
        int colorIndex = (int) (id % Constants.COLORS_LIST.size());

        return Constants.COLORS_LIST.get(colorIndex);
    }

    /**
     * Limit value between specified min and max values
     * <p>
     * value = [min,max]
     *
     * @param min min value
     * @param value value to be limited
     * @param max max value
     * @return returns the bounded value
     */
    public static int limit(int min, int value, int max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    /**
     * Limit value between specified min and max values
     * <p>
     * value = [min,max]
     *
     * @param min min value
     * @param value value to be limited
     * @param max max value
     * @return returns the bounded value
     */
    public static long limit(long min, long value, long max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    /**
     * Computes the CRC32 for a specified file
     *
     * @param fileName file for which the CRC32 is calculated
     * @return CRC32 for the specified file in String format
     */
    public static String CRC32(String fileName) {
        try (FileInputStream fi = new FileInputStream(fileName)) {

            /* allocates a buffer of 1MB for reading the video file
               this size seems to provide the fastest runtime
             */
            byte[] b = new byte[1024 * 1024];
            int len;
            CRC32 crc = new CRC32();

            while ((len = fi.read(b)) >= 0) {
                crc.update(b, 0, len);
            }

            // create the hash
            return (new BigInteger(Long.toString(crc.getValue())).toString(16));
        } catch (FileNotFoundException ex) {
            LOG.error("File {} not found", fileName);
            LOG.debug("File {} not found exception {}", fileName, ex);
        } catch (IOException ex) {
            LOG.error("File {} not accessible", fileName);
            LOG.debug("File {} not accessible exception {}", fileName, ex);
        }
        return null;
    }

    /**
     * Filter the object map array.
     * <p>
     * The filtering is done by sliding a window over the input array and
     * compute the sum of the values in the window (sum of 1s). If the sum is
     * smaller than a threshold, make the middle value of the window 0, else 1.
     * <p>
     * The filter aims to keep the white pixels which are in a neighbourhood of
     * other white pixels and discard the lonely pixels.
     *
     * @param objMap - the object map to be filtered
     */
    public static void filterObjectMap(byte[][] objMap) {
        if (objMap == null) {
            return;
        }

        // run N times the filtering algorithm
        // for (int steps = 0; steps < 10; steps++) {
        int filterSize = 3;
        byte[][] inputMap = new byte[objMap.length][objMap[0].length];

        for (int line = 0; line < objMap.length; line++) {
            inputMap[line] = objMap[line].clone();
        }

        for (int y = 0; y < inputMap[0].length - 2; y++) {
            for (int x = 0; x < inputMap.length - 2; x++) {
                int sum = 0;
                for (int j = 0; j < filterSize; j++) {
                    for (int i = 0; i < filterSize; i++) {
                        sum += inputMap[x + i][y + j];
                    }
                }

                if (sum < Math.ceil(filterSize * filterSize * 0.5)) {
                    objMap[x + (int) (filterSize * 0.5f)][y + (int) (filterSize * 0.5f)] = 0;
                } else {
                    objMap[x + (int) (filterSize * 0.5f)][y + (int) (filterSize * 0.5f)] = 1;
                }
            }
        }

        // filter the first and last line
        for (int x = 0; x < inputMap.length - 2; x++) {
            int sum0 = 0;   // sum for the first line
            int sumN = 0;   // sum for the last line
            for (int j = 0; j < filterSize - 1; j++) {
                for (int i = 0; i < filterSize; i++) {
                    sum0 += inputMap[x + i][j];
                    sumN += inputMap[x + i][inputMap[0].length - 1 - j];
                }
            }
            // filter first line
            if (sum0 < Math.ceil((filterSize - 1) * filterSize * 0.5)) {
                objMap[x + (int) (filterSize * 0.5f)][0] = 0;
            } else {
                objMap[x + (int) (filterSize * 0.5f)][0] = 1;
            }

            // filter last line
            if (sumN < Math.ceil((filterSize - 1) * filterSize * 0.5)) {
                objMap[x + (int) (filterSize * 0.5f)][inputMap[0].length - 1] = 0;
            } else {
                objMap[x + (int) (filterSize * 0.5f)][inputMap[0].length - 1] = 1;
            }
        }

        // filter the first and last column
        for (int y = 0; y < inputMap[0].length - 2; y++) {
            int sum0 = 0;   // sum for the first column
            int sumN = 0;   // sum for the last column
            for (int j = 0; j < filterSize; j++) {
                for (int i = 0; i < filterSize - 1; i++) {
                    sum0 += inputMap[i][y + j];
                    sumN += inputMap[inputMap.length - 1 - i][y + j];
                }
            }

            // filter first column
            if (sum0 < Math.ceil(filterSize * (filterSize - 1) * 0.5)) {
                objMap[0][y + (int) (filterSize * 0.5f)] = 0;
            } else {
                objMap[0][y + (int) (filterSize * 0.5f)] = 1;
            }

            // filter last column
            if (sumN < Math.ceil(filterSize * (filterSize - 1) * 0.5)) {
                objMap[inputMap.length - 1][y + (int) (filterSize * 0.5f)] = 0;
            } else {
                objMap[inputMap.length - 1][y + (int) (filterSize * 0.5f)] = 1;
            }
        }

        // filter the corners
        // left - top
        int sum = objMap[0][0] + objMap[0][1] + objMap[1][0] + objMap[1][1];
        objMap[0][0] = (byte) ((sum < 3) ? 0 : 1);

        // right - top
        sum = objMap[inputMap.length - 1][0] + objMap[inputMap.length - 1][1] + objMap[inputMap.length - 2][0] + objMap[inputMap.length - 2][1];
        objMap[inputMap.length - 1][0] = (byte) ((sum < 3) ? 0 : 1);

        // left - bottom
        sum = objMap[0][inputMap[0].length - 1] + objMap[0][inputMap[0].length - 2] + objMap[1][inputMap[0].length - 1] + objMap[1][inputMap[0].length - 2];
        objMap[0][inputMap[0].length - 1] = (byte) ((sum < 3) ? 0 : 1);

        // right - bottom
        sum = objMap[inputMap.length - 1][inputMap[0].length - 1]
                + objMap[inputMap.length - 2][inputMap[0].length - 1]
                + objMap[inputMap.length - 1][inputMap[0].length - 2]
                + objMap[inputMap.length - 2][inputMap[0].length - 2];
        objMap[inputMap.length - 1][inputMap[0].length - 1] = (byte) ((sum < 3) ? 0 : 1);

    }

    /**
     * Computes the integer logarithm in base 2 of the input number.
     *
     * @param n - the input number
     * @return - the integer value of the log base 2 of n
     */
    public static int log2N(int n) {
        if (n == 0) {
            return 0;
        }
        return (31 - Integer.numberOfLeadingZeros(n));
    }

    /**
     * A sigmoid function is a mathematical function having an "S" shaped curve
     * (sigmoid curve).
     *
     * @param value the value/point for which the sigmoid function has to be
     * computed
     * @return - the value of the sigmoid function in the given point
     */
    public static double sigmoidFunction(double value) {
        return 1.0 / (1.0 + Math.exp(-value));
    }

    /**
     * Based on the size of the generated box, compute a border size to be added
     * to the displayed image.
     *
     * @param box - the original box size
     * @param borderPercentageWidth - the percentage of border to be added on
     * width
     * @param borderPercentageHeight - the percentage of border to be added on
     * height
     * @param minBorder - the min border in pixels
     * @param frameSize - the max size the box can have, after being bordered
     * @return - the size of the possible border for the input box
     */
    public static BoxLRTB getBorderedSize(Rectangle box,
            double borderPercentageWidth,
            double borderPercentageHeight,
            int minBorder,
            Dimension frameSize) {

        int borderWidth = (int) (borderPercentageWidth * box.width);
        int borderHeight = (int) (borderPercentageHeight * box.height);

        // if the border is too small, make it at least 10 pixels
        if (borderWidth < minBorder) {
            borderWidth = minBorder;
        }

        if (borderHeight < minBorder) {
            borderHeight = minBorder;
        }

        // set the border as 20% of the size of the box
        BoxLRTB borderSize = new BoxLRTB(borderWidth, borderHeight);

        // limit the bordering in such way as to not get out of the image
        if ((box.x - borderWidth) < 0) {
            borderSize.setxLeft(box.x);
        }

        if ((box.y - borderHeight) < 0) {
            borderSize.setyTop(box.y);
        }

        if ((box.x + box.width + borderWidth) > frameSize.width) {
            borderSize.setxRight(frameSize.width - (box.x + box.width));
        }

        if ((box.y + box.height + borderHeight) > frameSize.height) {
            borderSize.setyBottom(frameSize.height - (box.y + box.height));
        }

        // return the new size of the border to be added
        return borderSize;
    }

    /**
     * Based on the size of the generated box, compute a border size to be added
     * to the displayed image.
     *
     * @param box - the original box size
     * @param targetBorder - the wanted border in pixels
     * @param frameSize - the max size the box can have, after being bordered
     * @return - the size of the possible border for the input box
     */
    public static BoxLRTB getBorderedSize(Rectangle box,
            int targetBorder,
            Dimension frameSize) {

        // set the border as 20% of the size of the box
        BoxLRTB borderSize = new BoxLRTB(targetBorder, targetBorder);

        // limit the bordering in such way as to not get out of the image
        if ((box.x - targetBorder) < 0) {
            borderSize.setxLeft(box.x);
        }

        if ((box.y - targetBorder) < 0) {
            borderSize.setyTop(box.y);
        }

        if ((box.x + box.width + targetBorder) > frameSize.width) {
            borderSize.setxRight(frameSize.width - (box.x + box.width));
        }

        if ((box.y + box.height + targetBorder) > frameSize.height) {
            borderSize.setyBottom(frameSize.height - (box.y + box.height));
        }

        // return the new size of the border to be added
        return borderSize;
    }

    /**
     * Check if it is possible to connect to the specified server.
     *
     * @param serverAddress the address of the server where the connection shall
     * be checked
     * @param portNo the number of the port on which the connection shall be
     * done
     * @return true if the server is reachable and false if the address and port
     * does not represent a valid path to a server
     */
    public static boolean isServerOnline(String serverAddress, int portNo) {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(serverAddress, portNo);
            // conect to the server
            try (Socket socket = new Socket()) {
                // conect to the server
                socket.connect(socketAddress, 100);
            }
            return true;
        } catch (Exception e) {
            LOG.error("Communication with the server couldn't be established");
            LOG.debug("Communication with the server couldn't be established {}", e);
            return false;
        }
    }

    /**
     * Generates a mask, representing a circle, of the size specified by the
     * size parameter. The mask contains a circle with 1s and background (as
     * 0s).The mask is to be used in the drawing of the mouse brush and for the
     * scribbles generation.
     * <p>
     * <p>
     * x = cx + r * cos(a)
     * <p>
     * y = cy + r * sin(a)
     * <p>
     * Where r is the radius, cx,cy the origin, and a the angle (in radian).
     *
     * @param size - the size of the mask
     * @return - the mask representing a filled circle, of radius = size / 2
     */
    public static byte[][] getCircleMask(int size) {
        switch (size) {
            case 3:
                return CircleTemplate.CIRCLE_MASK_3X3;

            case 4:
                return CircleTemplate.CIRCLE_MASK_4X4;

            case 5:
                return CircleTemplate.CIRCLE_MASK_5X5;

            case 6:
                return CircleTemplate.CIRCLE_MASK_6X6;

            case 7:
                return CircleTemplate.CIRCLE_MASK_7X7;

            case 8:
                return CircleTemplate.CIRCLE_MASK_8X8;

            case 9:
                return CircleTemplate.CIRCLE_MASK_9X9;

            case 10:
                return CircleTemplate.CIRCLE_MASK_10X10;

            case 11:
                return CircleTemplate.CIRCLE_MASK_11X11;

            case 12:
                return CircleTemplate.CIRCLE_MASK_12X12;

            case 13:
                return CircleTemplate.CIRCLE_MASK_13X13;

            case 14:
                return CircleTemplate.CIRCLE_MASK_14X14;

            case 15:
                return CircleTemplate.CIRCLE_MASK_15X15;

            case 16:
                return CircleTemplate.CIRCLE_MASK_16X16;

            case 17:
                return CircleTemplate.CIRCLE_MASK_17X17;

            case 18:
                return CircleTemplate.CIRCLE_MASK_18X18;

            case 19:
                return CircleTemplate.CIRCLE_MASK_19X19;

            case 20:
                return CircleTemplate.CIRCLE_MASK_20X20;

            case 21:
                return CircleTemplate.CIRCLE_MASK_21X21;

            case 22:
                return CircleTemplate.CIRCLE_MASK_22X22;

            case 23:
                return CircleTemplate.CIRCLE_MASK_23X23;

            case 24:
                return CircleTemplate.CIRCLE_MASK_24X24;

            case 25:
                return CircleTemplate.CIRCLE_MASK_25X25;

            case 26:
                return CircleTemplate.CIRCLE_MASK_26X26;

            case 27:
                return CircleTemplate.CIRCLE_MASK_27X27;

            case 28:
                return CircleTemplate.CIRCLE_MASK_28X28;

            case 29:
                return CircleTemplate.CIRCLE_MASK_29X29;

            case 30:
                return CircleTemplate.CIRCLE_MASK_30X30;

            default:
                return CircleTemplate.CIRCLE_MASK_5X5;
        }
    }

    /**
     * Returns the capitalised string
     *
     * @param name string to be capitalised
     * @return the capitalised string
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Create the directories structure for the given path.
     *
     * @param path - the path which has to be created if it does not exist
     */
    public static void createFolderPath(String path) {
        // create the directory structure
        File file = new File(path + "text.txt");
        file.getParentFile().mkdirs();
    }

    /**
     * Check if the extension of the file is one of the wanted. Only files which
     * represent images can be loaded.
     *
     * @param fileName the name of the current file in the directory structure
     * @param collection the collection which has to be checked for matching
     * (the list of extensions)
     * @return true if the file is a wanted type; false if the file represents a
     * not wanted format
     */
    public static boolean checkExtension(String fileName, List<String> collection) {
        // convert the name of the file to lower case, to avoid missing file types (jpg vs JPG)        
        return collection.stream().anyMatch((String extension) -> fileName.toLowerCase().endsWith("." + extension));
    }

    /**
     * Get an input string and replace all non alpha numeric characters with a
     * given string.
     *
     * @param inputStr the string to be changed
     * @param replaceStr the character to replace any non alpha numeric
     * character in the input string
     * @return string
     */
    public static String replaceNonAlphaNum(String inputStr, String replaceStr) {
        String output = inputStr.replaceAll("[^A-Za-z0-9 ]", replaceStr);

        // if the output string ends with a replace string, remove it
        if (output.endsWith(replaceStr)) {
            output = output.substring(0, output.length() - replaceStr.length());
        }

        return output;
    }

    /**
     * Converts a number representing bytes to the biggest possible multiple.
     *
     * @param bytes the number to be converted
     * @param si true if the display system should be decimal (kilobyte - kB,
     * megabyte - MB etc.); false if the display system should be binary
     * (kibibyte - KB, mebibyte - MiB, gibibyte - GiB etc.)
     * @return the transformation of the number to the biggest metric
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Mirror the image/buffer data
     *
     * @param image the image containing the pixel data which has to be
     * mirrored. This function will alter the bytes of the input image, so that
     * after this function the input image will contain the mirrored information
     */
    public static void mirrorImage(BufferedImage image) {
        byte[] bgr = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] mirror = new byte[bgr.length];
        final int multiplier = 3; // BGR

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                mirror[(j * (image.getWidth() * multiplier)) + ((((image.getWidth() - 1) - i) * multiplier) + 0)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 0)]; // R
                mirror[(j * (image.getWidth() * multiplier)) + ((((image.getWidth() - 1) - i) * multiplier) + 1)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 1)]; // G
                mirror[(j * (image.getWidth() * multiplier)) + ((((image.getWidth() - 1) - i) * multiplier) + 2)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 2)]; // B
            }
        }

        System.arraycopy(mirror, 0, bgr, 0, image.getWidth() * image.getHeight() * multiplier);
    }

    /**
     * Flip vertically the image
     *
     * @param image the image containing the pixel data which has to be flipped
     * vertically. This function will alter the bytes of input image, so that
     * after this function the input image will contain the flipped information
     */
    public static void flipVerticallyImage(BufferedImage image) {
        byte[] bgr = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        byte[] flipped = new byte[bgr.length];
        final int multiplier = 3; // BGR

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                flipped[((image.getHeight() - 1) - j) * (image.getWidth() * multiplier) + (i * multiplier + 0)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 0)]; // R
                flipped[((image.getHeight() - 1) - j) * (image.getWidth() * multiplier) + (i * multiplier + 1)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 1)]; // G
                flipped[((image.getHeight() - 1) - j) * (image.getWidth() * multiplier) + (i * multiplier + 2)] = bgr[(j * (image.getWidth() * multiplier)) + (i * multiplier + 2)]; // B
            }
        }

        System.arraycopy(flipped, 0, bgr, 0, image.getWidth() * image.getHeight() * multiplier);
    }

    /**
     * Export the original image and the result image, concatenated, for demo
     * purpose.
     *
     * @param leftImage the image to be displayed on the left side of the output
     * image
     * @param rightImage the image to be displayed on the right side of the
     * output image
     * @param outputPath the path where to output the image
     * @param fileType the file type/extension for the export
     * @param bkgColor the color used for the background between the images
     */
    public static void exportTwoImages(BufferedImage leftImage, BufferedImage rightImage, String outputPath, String fileType, Color bkgColor) {
        // inform the user if the images are different
        if ((leftImage.getWidth() != rightImage.getWidth()) || (leftImage.getHeight() != rightImage.getHeight())) {
            LOG.info("The width or height of the exported images differs! left: {}x{}, right: {}x{}",
                    leftImage.getWidth(), leftImage.getHeight(), rightImage.getWidth(), rightImage.getHeight());
        }

        // compute the size of the result image, adding also some space between them
        int width = leftImage.getWidth() + 50 + rightImage.getWidth();      // 50 represents the inset between the images
        int height = Math.max(leftImage.getHeight(), rightImage.getHeight());

        // generate a new image composed of proposed images
        BufferedImage resultImg = new BufferedImage(width, height, leftImage.getType());
        Graphics2D g2DResImg = (Graphics2D) resultImg.getGraphics();

        g2DResImg.setColor(bkgColor);
        g2DResImg.fillRect(0, 0, width, height);

        // put the left image
        g2DResImg.drawImage(leftImage, 0, 0, leftImage.getWidth(), leftImage.getHeight(), null);

        // add the right image
        g2DResImg.drawImage(rightImage,
                resultImg.getWidth() - rightImage.getWidth() - 1,
                0, rightImage.getWidth(), rightImage.getHeight(), null);

        g2DResImg.dispose();

        // export the image
        ExportImage.exportImage(resultImg, outputPath, fileType);
    }

    /**
     * Searches the list of radio buttons of the given button group, for the
     * specified text. When found, the radio button is selected.
     *
     * @param group the group being searched
     * @param text the text being searched
     */
    public static void selectRadioButton(ButtonGroup group, String text) {
        // get the list of elements of the button group
        Enumeration<AbstractButton> elements = group.getElements();

        // search in the button group for the user selected value
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();

            // if the selected value is found, select the corresponding radio button
            if (button.getActionCommand().equals(text)) {
                group.setSelected(button.getModel(), true);
                break;
            }
        }
    }

    /**
     * Allow the user to choose a path where to save its files and set it in the
     * path text field.
     *
     * @param currentDirectory the directory where the browser should open
     * @param parent the parent frame/dialog of the browser dialog
     * @return the path selected by the user
     */
    public static String browseFilePath(String currentDirectory, Component parent) {
        String selectedPath = "";

        JFileChooser chooser = new JFileChooser();

        // set the initial directory
        chooser.setCurrentDirectory(new File(currentDirectory));

        // allow only the choosing of directories
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            // set the chosen text as the path where to save the file
            try {
                selectedPath = chooser.getSelectedFile().getCanonicalPath();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ExportImagesGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return selectedPath;
    }

    /**
     * Check if the sent text equals to the predefined invalid attribute. When
     * this is true, it means the attribute was not correctly chosen.
     *
     * @param text the text where to append the invalid attribute
     * @param attribute the attribute to be checked
     * @param attributeName the name of the attribute being checked
     */
    public static void checkAttributeValid(StringBuilder text, String attribute, String attributeName) {
        if (Constants.INVALID_ATTRIBUTE_TEXT.equalsIgnoreCase(attribute)) {
            text.append(attributeName);
            text.append(", ");
        }
    }

    /**
     * Asks the user to correct the attributes. If the user chooses to correct
     * them, the application does not continue with the saving of data; it will
     * cancel the process and go back to the normal state.
     *
     * @param invalidAttribute a string with the invalid attributes (is any)
     * @param editAttribAvailable true if the edit of attributes is allowed
     * @param parent the parent component of the message window
     * @return the choice of the user, whether he wants to save the data as it
     * is or the saving of data shall not be done yet, because the user wants to
     * correct it (or the attributes)
     */
    public static int messageAttributesSet(String invalidAttribute, boolean editAttribAvailable, Component parent) {
        // if the application is running offline, show the path to the attributes
        String attributesPathMsg = editAttribAvailable ? "If you do not find the wanted attributes, please go to Menu->Options->Attributes<br>"
                + "and define the missing attributes!<br><br>" : "";

        // ask the user to correct the missing attributes
        String message = "<html>There are invalid attributes (or attributes which were not yet set):<br>"
                + invalidAttribute.toLowerCase(Locale.ENGLISH) + ".<br><br>"
                + "<font color=#4d0000>"
                + "<b>Do you want to correct them now?</b><br>"
                + "</font>"
                + "<i>(If you choose No, the current attributes will be saved as ground truth)<br>"
                + attributesPathMsg
                + "</i></html>";

        // display the warning dialog
        if (editAttribAvailable) {
            Object[] options = {"Yes", "No", "Edit Attributes"};
            return Messages.showQuestionMessage(parent, message, "Invalid attributes!!!", JOptionPane.YES_NO_CANCEL_OPTION, options, options[0]);
        } else {
            Object[] options = {"Yes", "No"};
            return Messages.showQuestionMessage(parent, message, "Invalid attributes!!!", JOptionPane.YES_NO_OPTION, options, options[0]);
        }
    }

    /**
     * Opens the folder or file from the specified path, in an explorer window
     * or in a default file specific application.
     *
     * @param path the path to the wanted folder/file
     */
    public static void openFileFolderInExplorer(String path) {
        try {
            if (new File(path).exists()) {
                Desktop.getDesktop().open(new File(path));
            }
        } catch (IOException e) {
            LOG.debug("The open file/folder in explorer failed!");
            LOG.debug("The open file/folder in explorer failed! {}", e);
        }
    }

    /**
     * Check if the point is inside the bounds.
     *
     * @param point the point to be checked if it is inside the bounds
     * @param bounds the max values that the point's x and y can have
     * @return return true if the point is inside the bounds or false otherwise
     */
    public static boolean checkBounds(Point point, Dimension bounds) {
        return (point.x >= 0 && point.x < bounds.width)
                && point.y >= 0 && point.y < bounds.height;
    }

    /**
     * Clone the input tree; create a new tree, which is the copy of the input
     * one.
     *
     * @param rootNode the root node of the tree to be copied
     * @return a new tree, with new nodes, which represent the deep copy of the
     * input tree
     */
    public static CustomTreeNode cloneTree(CustomTreeNode rootNode) {
        // the list of nodes of the input tree
        List<CustomTreeNode> nodesList = new ArrayList<>();
        // the list of nodes of the cloned tree
        List<CustomTreeNode> clonedList = new ArrayList<>();
        // the new tree, representing the deep copy of the input one
        CustomTreeNode clonedTree = new CustomTreeNode(rootNode.getRoot());

        // in the nodes list, add the first nodes
        nodesList.add(rootNode);
        clonedList.add(clonedTree);

        // keep track of the size of the list of nodes
        int listLength = nodesList.size();

        // take every node and create copies of the children node
        for (int index = 0; index < listLength; index++) {
            CustomTreeNode node = nodesList.get(index);
            // add the list of children to the list of nodes to be copied
            List<CustomTreeNode> children = node.getChildrenNodes();
            nodesList.addAll(children);
            // by adding new chi
            listLength += children.size();

            // create new nodes, based on the children of the current node
            clonedList.get(index).addChildrenStr(node.getChildren());
            clonedList.addAll(clonedList.get(index).getChildrenNodes());
        }

        return clonedTree;
    }
}
