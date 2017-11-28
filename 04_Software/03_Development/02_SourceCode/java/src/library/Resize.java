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
package library;

import static common.Constants.MAX_SCREEN_PERCENT_RESIZE;
import common.Utils;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * The type Resize.
 *
 * @author Olimpia Popica
 */
public class Resize {

    /* The resize ratio on width (widthImg1 / widthImg2) */
    private double ratioWidth;

    /* The resize ratio on height (heightImg1 / heightImg2) */
    private double ratioHeight;

    /* The zoomed width in pixels */
    private int zoomingWidth;

    /* The zoomed height in pixels */
    private int zoomingHeight;

    /* Zooming index */
    private int zoomingIndex;

    /* Zooming factor 10 = 10%; 5 = 20%; 4 = 25%; 2 = 50%; 1 = 100%*/
    private static final int ZOOMING_FACTOR = 4;

    /**
     * Instantiates a new Resize.
     *
     * @param ratioWidth  the ratio width
     * @param ratioHeight the ratio height
     */
    public Resize(double ratioWidth, double ratioHeight) {
        this.ratioWidth = ratioWidth;
        this.ratioHeight = ratioHeight;
    }

    /**
     * Instantiates a new Resize.
     *
     * @param ratioWidth  the ratio width
     * @param ratioHeight the ratio height
     * @param width       the width
     * @param height      the height
     */
    public Resize(double ratioWidth, double ratioHeight, int width, int height) {
        this.ratioWidth = ratioWidth;
        this.ratioHeight = ratioHeight;
        this.zoomingWidth = width;
        this.zoomingHeight = height;
        this.zoomingIndex = 0;
    }

    /**
     * Resize the width and height to fit the max display size In case the
     * provided width and height are smaller than max allowed size they will be
     * zoomed in(maximized) to max allowed size. In case the width and height
     * are bigger than max allowed size they will be zoomed out(minimized) to
     * max allowed size
     *
     * @param width  object's width
     * @param height object's height
     */
    public Resize(int width, int height) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int widthSc = gd.getDisplayMode().getWidth();
        int heightSc = gd.getDisplayMode().getHeight();
        double ratioW = (double) width / (widthSc * MAX_SCREEN_PERCENT_RESIZE);
        double ratioH = (double) height / (heightSc * MAX_SCREEN_PERCENT_RESIZE);
        double cropRatio = (double) width / (double) height;

        computeResizeRatios(ratioW, ratioH, cropRatio, width, height, widthSc, heightSc);
    }

    /**
     * Resize the window to user prefer size. In case the zoomed user preference
     * is not set, then resize the width and height to fit the max display size
     * In case the provided width and height are smaller than max allowed size
     * they will be zoomed in(maximized) to max allowed size. In case the width
     * and height are bigger than max allowed size they will be zoomed
     * out(minimized) to max allowed size
     *
     * @param width           object's width
     * @param height          object's height
     * @param userZoomedIndex user's prefer zoomed index
     */
    public Resize(int width, int height, int userZoomedIndex) {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int widthSc = gd.getDisplayMode().getWidth();
        int heightSc = gd.getDisplayMode().getHeight();
        double ratioW;
        double ratioH;
        double cropRatio;

        /* did the user saved its zoomed preference? */
        if (userZoomedIndex == Integer.MAX_VALUE) {
            ratioW = (double) width / (widthSc * MAX_SCREEN_PERCENT_RESIZE);
            ratioH = (double) height / (heightSc * MAX_SCREEN_PERCENT_RESIZE);
            cropRatio = (double) width / (double) height;
        } else {
            int tmpWidth = Math.abs(width) + userZoomedIndex * Math.abs(width / ZOOMING_FACTOR);
            int tmpHeight = Math.abs(height) + userZoomedIndex * Math.abs(height / ZOOMING_FACTOR);

            ratioW = (double) width / (double) tmpWidth;
            ratioH = (double) height / (double) tmpHeight;
            cropRatio = (double) width / (double) height;
        }

        computeResizeRatios(ratioW, ratioH, cropRatio, width, height, widthSc, heightSc);
    }

    /**
     * Resize the width and height to fit the max display size In case the
     * provided width and height are smaller than max allowed size they will be
     * zoomed in(maximized) to max allowed size. In case the width and height
     * are bigger than max allowed size they will be zoomed out(minimized) to
     * max allowed size
     *
     * @param currentWidth  object's width
     * @param currentHeight object's height
     * @param newWidth      - the wanted width for the object
     * @param newHeight     - the wanted height for the object
     */
    public Resize(int currentWidth, int currentHeight, int newWidth, int newHeight) {
        // compute the ratio for width and height
        this.ratioWidth = (double) currentWidth / (double) newWidth;
        this.ratioHeight = (double) currentHeight / (double) newHeight;

        this.zoomingIndex = ((int) ((double) currentHeight / ratioHeight) - currentHeight) / Math.abs(currentHeight / ZOOMING_FACTOR);

        this.zoomingWidth = currentWidth + zoomingIndex * Math.abs(currentWidth / ZOOMING_FACTOR);
        this.zoomingHeight = currentHeight + zoomingIndex * Math.abs(currentHeight / ZOOMING_FACTOR);

        this.zoomingWidth = Utils.limit(10, this.zoomingWidth, (int) newWidth);
        this.zoomingHeight = Utils.limit(10, this.zoomingHeight, (int) newHeight);
    }

    /**
     *
     * @param ratioW max possible ratio on width, such as the image will not use
     * more than a certain percentage of the screen width
     * @param ratioH max possible ratio on height, such as the image will not
     * use more than a certain percentage of the screen height
     * @param cropRatio the ratio of the crop; the ratio between the original
     * width and height of the crop
     * @param width object's width
     * @param height object's height
     * @param widthSc the width of the screen
     * @param heightSc the height of the screen
     */
    private void computeResizeRatios(double ratioW, double ratioH, double cropRatio, int width, int height, int widthSc, int heightSc) {
        /* keep original aspect ratio            |-|     |-----|
         * 1.75 = objects allmost squared        | |  or |     |
         * < 1.75 include all objects which are  |_|     |_____|
         */
        if (cropRatio < 1.75) {
            this.ratioWidth = ratioH;
            this.ratioHeight = ratioH;
            this.zoomingIndex = ((int) ((double) height / ratioH) - height) / Math.abs(height / ZOOMING_FACTOR);
            this.zoomingWidth = width + zoomingIndex * Math.abs(width / ZOOMING_FACTOR);
            this.zoomingHeight = height + zoomingIndex * Math.abs(height / ZOOMING_FACTOR);

            this.zoomingWidth = Utils.limit(10, this.zoomingWidth, (int) (widthSc * MAX_SCREEN_PERCENT_RESIZE));
            this.zoomingHeight = Utils.limit(10, this.zoomingHeight, (int) (heightSc * MAX_SCREEN_PERCENT_RESIZE));
        } else {
            this.ratioWidth = ratioW;
            this.ratioHeight = ratioW;
            this.zoomingIndex = ((int) ((double) width / ratioW) - width) / Math.abs(width / ZOOMING_FACTOR);
            this.zoomingWidth = width + zoomingIndex * Math.abs(width / ZOOMING_FACTOR);
            this.zoomingHeight = height + zoomingIndex * Math.abs(height / ZOOMING_FACTOR);

            this.zoomingWidth = Utils.limit(10, this.zoomingWidth, (int) (widthSc * MAX_SCREEN_PERCENT_RESIZE));
            this.zoomingHeight = Utils.limit(10, this.zoomingHeight, (int) (heightSc * MAX_SCREEN_PERCENT_RESIZE));
        }
    }

    /**
     * Resizes an image to the wanted size, based on the provided resize ratio.
     * The resize is done the same for width and height.
     *
     * @param origImg - the image to be resized
     * @return - returns the new resized image
     */
    public BufferedImage resizeImage(BufferedImage origImg) {
        if ((origImg == null) || (Double.compare(ratioWidth, 0.0) == 0) || (Double.compare(ratioHeight, 0.0) == 0)) {
            return null;
        }

        int w1 = origImg.getWidth();
        int h1 = origImg.getHeight();
        int w2 = (int) (w1 / ratioWidth);
        int h2 = (int) (h1 / ratioHeight);
        float wRatio = (float) w2 / (float) w1;
        float hRatio = (float) h2 / (float) h1;

        BufferedImage workImg = new BufferedImage(w2, h2, BufferedImage.TYPE_3BYTE_BGR);

        // for down-scale
        if ((ratioWidth >= 1.0f) && (ratioHeight >= 1.0f)) {
            for (int y = 0; y < h2; y++) {
                for (int x = 0; x < w2; x++) {
                    workImg.setRGB(x, y, origImg.getRGB((int) (x * ratioWidth + 0.5f), (int) (y * ratioHeight + 0.5f)));
                }
            }
        } else {
            // for up-scale
            for (int y = 0; y < h2; y++) {
                for (int x = 0; x < w2; x++) {
                    workImg.setRGB(x, y, origImg.getRGB((int) (x / wRatio), (int) (y / hRatio)));
                }
            }
        }

        return workImg;
    }

    /**
     * Computes the corespondent position in the original image, of the given
     * pixel from the resized image.
     *
     * @param x - the X coordinate of the pixel from the resized image
     * @param y - the Y coordinate of the pixel from the resized image
     * @return - returns the corresponding point in the original image
     */
    public Point resizedToOriginal(int x, int y) {
        Point origPoint = new Point();
        origPoint.setLocation((int) (x * ratioWidth), (int) (y * ratioHeight));
        return origPoint;
    }

    /**
     * Computes the corespondent value in the original image, of the given value
     * from the resized image.
     *
     * @param value the value from the resized image
     * @return the corresponding value in the original image
     */
    public int resizedToOriginal(int value) {
        return (int) (value * ratioWidth);
    }

    /**
     * Computes the corespondent dimension in the original image, of the given
     * dimension from the resized image.
     *
     * @param origImgDim the dimension from the resized image
     * @return the corresponding dimension in the original image
     */
    public Dimension resizedToOriginal(Dimension origImgDim) {
        int width = (int) Math.round(origImgDim.width * ratioWidth);
        int height = (int) Math.round(origImgDim.height * ratioHeight);
        return new Dimension(width, height);
    }

    /**
     * Computes the corespondent rectangle in the original image, of the given
     * rectangle from the resized image.
     *
     * @param resizedRectangle the rectangle from the resized image
     * @return the corresponding rectangle in the original image
     */
    public Rectangle resizedToOriginal(Rectangle resizedRectangle) {
        Rectangle origiRectangle = new Rectangle(0, 0, 0, 0);

        int x = (int) Math.round(resizedRectangle.x * ratioWidth);
        int y = (int) Math.round(resizedRectangle.y * ratioHeight);
        int xRightPanel = resizedRectangle.x + resizedRectangle.width;
        int yRightPanel = resizedRectangle.y + resizedRectangle.height;
        int width = ((int) Math.round(xRightPanel * ratioWidth) - x);
        int height = ((int) Math.round(yRightPanel * ratioHeight) - y);

        origiRectangle.setBounds(x, y, width, height);

        return origiRectangle;
    }

    /**
     * Computes the corespondent polygon in the original image, of the given
     * polygon from the resized image.
     *
     * @param polyResized the polygon from the resized image
     * @return the corresponding polygon in the original image
     */
    public Polygon resizedToOriginal(Polygon polyResized) {
        if ((polyResized == null) || (Double.compare(ratioWidth, 0.0) == 0) || (Double.compare(ratioHeight, 0.0) == 0)) {
            return null;
        }

        Polygon polyOrig = new Polygon();

        for (int index = 0; index < polyResized.npoints; index++) {
            Point point = resizedToOriginal(polyResized.xpoints[index], polyResized.ypoints[index]);
            polyOrig.addPoint(point.x, point.y);
        }

        return polyOrig;
    }

    /**
     * Computes the corespondent position in the original image, of the given
     * pixel from the resized image.
     *
     * @param resizedPoint - the (x,y) coordinate of the pixel from the resized image
     * @return - returns a Point object with two elements: x and y coordinates of the point in the original image (in this order)
     */
    public Point resizedToOriginal(Point resizedPoint) {
        if ((resizedPoint == null) || (Double.compare(ratioWidth, 0.0) == 0) || (Double.compare(ratioHeight, 0.0) == 0)) {
            return null;
        }

        Point origPoint = new Point();
        origPoint.setLocation((int) (resizedPoint.getX() * ratioWidth), (int) (resizedPoint.getY() * ratioHeight));
        return origPoint;
    }

    /**
     * Computes the corespondent value in the resized image, of the given value
     * from the original image.
     *
     * @param value the value from the original image
     * @return the corresponding value in the resized image
     */
    public int originalToResized(int value) {
        return (int) (value / ratioWidth);
    }

    /**
     * Computes the corespondent dimension in the resized image, of the given
     * dimension from the original image.
     *
     * @param origImgDim the dimension from the original image
     * @return the corresponding dimension in the resized image
     */
    public Dimension originalToResized(Dimension origImgDim) {
        int width = (int) Math.round(origImgDim.width / ratioWidth);
        int height = (int) Math.round(origImgDim.height / ratioHeight);
        return new Dimension(width, height);
    }

    /**
     * Computes the corespondent rectangle in the resized image, of the given
     * rectangle from the original image.
     *
     * @param origRectangle the rectangle from the original image
     * @return the corresponding rectangle in the resized image
     */
    public Rectangle originalToResized(Rectangle origRectangle) {
        Rectangle resizedRectangle = new Rectangle(0, 0, 0, 0);
        int x = (int) Math.round(origRectangle.x / ratioWidth);
        int y = (int) Math.round(origRectangle.y / ratioHeight);

        int xRightOrig = origRectangle.x + origRectangle.width;
        int yRightOrig = origRectangle.y + origRectangle.height;
        int width = ((int) Math.round(xRightOrig / ratioWidth) - x);
        int height = ((int) Math.round(yRightOrig / ratioHeight) - y);

        resizedRectangle.setBounds(x, y, width, height);

        return resizedRectangle;
    }

    /**
     * Computes the corespondent polygon in the resized image, of the given
     * polygon from the original image.
     *
     * @param polyOrig the polygon from the original image
     * @return the corresponding polygon in the resized image
     */
    public Polygon originalToResized(Polygon polyOrig) {
        if ((polyOrig == null) || (Double.compare(ratioWidth, 0.0) == 0) || (Double.compare(ratioHeight, 0.0) == 0)) {
            return null;
        }

        Polygon polyResized = new Polygon();

        for (int index = 0; index < polyOrig.npoints; index++) {
            Point point = originalToResized(polyOrig.xpoints[index], polyOrig.ypoints[index]);
            polyResized.addPoint(point.x, point.y);
        }

        return polyResized;
    }

    /**
     * Computes the corespondent position in the resized image, of the given
     * pixel from the original image.
     *
     * @param x - the (x,y) coordinate of the pixel from the original image
     * @param y - the (x,y) coordinate of the pixel from the original image
     * @return - returns a Point object with two elements: x and y coordinates of the point in the resized image (in this order)
     */
    public Point originalToResized(int x, int y) {
        return originalToResized(new Point(x, y));
    }

    /**
     * Computes the corespondent position in the resized image, of the given
     * pixel from the original image.
     *
     * @param origPoint - the (x,y) coordinate of the pixel from the original image
     * @return - returns a Point object with two elements: x and y coordinates of the point in the resized image (in this order)
     */
    public Point originalToResized(Point origPoint) {
        if ((origPoint == null) || (Double.compare(ratioWidth, 0.0) == 0) || (Double.compare(ratioHeight, 0.0) == 0)) {
            return null;
        }

        Point resizedPoint = new Point();
        resizedPoint.setLocation((int) (origPoint.getX() / ratioWidth), (int) (origPoint.getY() / ratioHeight));
        return resizedPoint;
    }

    /**
     * Gets ratio width.
     *
     * @return the ratio width
     */
    public double getRatioWidth() {
        return ratioWidth;
    }

    /**
     * Gets ratio height.
     *
     * @return the ratio height
     */
    public double getRatioHeight() {
        return ratioHeight;
    }

    /**
     * Increment the width ratio with the given value.
     *
     * @param value - the value used for incrementing the width. When the value is positive, an increment happens; when the value is negative, a decrement happens.
     */
    public void incrementRatioWidth(double value) {
        ratioWidth = ratioWidth + value;
    }

    /**
     * Increment the height ratio with the given value.
     *
     * @param value - the value used for incrementing the height. When the value is positive, an increment happens; when the value is negative, a decrement happens.
     */
    public void incrementRatioHeight(double value) {
        ratioHeight = ratioHeight + value;
    }

    /**
     * Increment both the width and height ratio with the given value.
     *
     * @param value - the value used for incrementing the width and height. When the value is positive, an increment happens; when the value is negative, a decrement happens.
     */
    public void incrementRatioWidthHeight(double value) {
        ratioWidth = Math.max((ratioWidth + value), 0.1);
        ratioHeight = Math.max((ratioHeight + value), 0.1);
    }

    /**
     * Compute the zoomed width and height based on the crop resolution and
     * zooming factor
     *
     * @param crop resolution of the crop
     * @return the zoomingIndex for the current object
     */
    public int incrementWidthHeight(Dimension crop) {

        /* chek if the user zooms in or out */
        if (crop.width > 0) {
            zoomingIndex++;
        } else {
            zoomingIndex--;
        }

        /* no zooming has been made */
        if (zoomingIndex == 0) {
            zoomingWidth = Math.abs(crop.width);
            zoomingHeight = Math.abs(crop.height);
        } /* zoomed image; recompute the size of the window */ else {
            zoomingWidth = Math.abs(crop.width) + zoomingIndex * Math.abs(crop.width / ZOOMING_FACTOR);
            zoomingHeight = Math.abs(crop.height) + zoomingIndex * Math.abs(crop.height / ZOOMING_FACTOR);
        }

        /* compute the new ratios based on the new size of the window */
        ratioWidth = (double) Math.abs(crop.width) / (double) zoomingWidth;
        ratioHeight = (double) Math.abs(crop.height) / (double) zoomingHeight;

        return zoomingIndex;
    }

    /**
     * Returns true if the two objects have the same resize ratio in both
     * coordinates.
     *
     * @param resizeObj - the object with which the current object should be
     * compared with
     * @return - true when the objects match (have the same ratio on width and
     * height) and false otherwise
     */
    @Override
    public boolean equals(Object resizeObj) {
        if ((resizeObj != null) && (resizeObj instanceof Resize)) {
            Resize resize = (Resize) resizeObj;
            return ((Double.compare(this.ratioWidth, resize.ratioWidth) == 0) && (Double.compare(this.ratioHeight, resize.ratioHeight) == 0));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.ratioWidth) ^ (Double.doubleToLongBits(this.ratioWidth) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.ratioHeight) ^ (Double.doubleToLongBits(this.ratioHeight) >>> 32));
        return hash;
    }

    /**
     * Check if by incrementing the size of the image, it will not become bigger
     * than the screen itself.
     *
     * @param crop      resolution of the crop
     * @param screenRes - the resolution of the current screen
     * @return - true if the resize shall be done; false if by increasing the size of the image, it will generate a bigger image than the screen
     */
    public boolean isSizeIncreaseOK(Dimension crop, Dimension screenRes) {
        int w2 = zoomingWidth + (crop.width / ZOOMING_FACTOR);
        int h2 = zoomingHeight + (crop.height / ZOOMING_FACTOR);

        /* limit the zoom in to not go beyond screen's resolution */
        return ((w2 <= (screenRes.width * MAX_SCREEN_PERCENT_RESIZE)) && (h2 <= (screenRes.height * MAX_SCREEN_PERCENT_RESIZE)));
    }

    /**
     * Check if by decreasing the size of the image, it will not become smaller
     * than the minimum allowed crop resolution.
     *
     * @param crop resolution of the crop
     * @return - true if the resize shall be done; false if by increasing the size of the image, it will generate a bigger image than the screen
     */
    public boolean isSizeDecreaseOK(Dimension crop) {
        int w2 = zoomingWidth + (crop.width / ZOOMING_FACTOR);
        int h2 = zoomingHeight + (crop.height / ZOOMING_FACTOR);

        /* limit the zooming out to a minimum 10x10 pixels */
        return ((w2 > 10) && (h2 > 10));
    }

    /**
     * Gets zooming factor.
     *
     * @return the zooming Factor
     */
    public int getZoomingFactor() {
        return ZOOMING_FACTOR;
    }

    /**
     * Resize the input rectangle with the resize ratio.
     *
     * @param rectangle - the rectangle to be resized
     * @return - the resize rectangle
     */
    public Rectangle resizeBox(Rectangle rectangle) {
        Rectangle resizedBox = new Rectangle(0, 0, 0, 0);

        int x = (int) Math.round(rectangle.x / ratioWidth);
        int y = (int) Math.round(rectangle.y / ratioHeight);
        int xRightPanel = rectangle.x + rectangle.width;
        int yRightPanel = rectangle.y + rectangle.height;

        int width = ((int) Math.round(xRightPanel / ratioWidth) - x);
        int height = ((int) Math.round(yRightPanel / ratioHeight) - y);

        resizedBox.setBounds(x, y, width, height);

        return resizedBox;
    }
}
