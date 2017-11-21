/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

import common.Utils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * The type Draw options.
 *
 * @author Olimpia Popica
 */
public class DrawOptions {

    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. Java adds an implicit public
     * constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private DrawOptions() {
        throw new IllegalStateException("Utility class, do not instantiate!");
    }

    /**
     * Display mouse guiding lines for better precision in segmentation. It will
     * draw an horizontal and a vertical line at the point coordinates.
     *
     * @param g2d        the graphics object
     * @param dashedLine true = the line used for drawing the line will be dashed; false = the line will be solid/plain
     * @param point      the place where the lines should intersect
     * @param bounds     the bounds of the drawing
     */
    public static void drawMouseIndicator(Graphics2D g2d, boolean dashedLine, Point point, Dimension bounds) {
        g2d.setStroke(dashedLine ? getDashedStroke() : new BasicStroke(0));

        // draw mouse indicators
        g2d.setColor(Color.yellow);
        g2d.drawLine(point.x, 0, point.x, bounds.height);
        g2d.drawLine(0, point.y, bounds.width, point.y);
    }

    /**
     * Draw a point at the specified coordinates.
     *
     * @param g2d   the graphics object
     * @param point the point to be represented
     */
    public static void drawPoint(Graphics2D g2d, Point point) {
        g2d.drawLine(point.x, point.y, point.x, point.y);
    }

    /**
     * Draw a point at the specified coordinates, with the specified color.
     *
     * @param g2d   the graphics object
     * @param point the point to be represented
     * @param color the color of the point
     */
    public static void drawPoint(Graphics2D g2d, Point point, Color color) {
        g2d.setColor(color);

        g2d.drawLine(point.x, point.y, point.x, point.y);
    }

    /**
     * Draw a line from where the mouse was clicked, to the current point.
     *
     * @param g2d          the graphics object
     * @param initialPoint the point where the line starts
     * @param endPoint     the point where the line ends
     */
    public static void drawLine(Graphics2D g2d, Point initialPoint, Point endPoint) {
        g2d.drawLine(initialPoint.x, initialPoint.y, endPoint.x, endPoint.y);
    }

    /**
     * Fill a bounding box / rectangle starting at the specified position.
     *
     * @param g2d   the graphics object
     * @param box   the rectangle specification
     * @param color the color of the rectangle
     */
    public static void fillBBox(Graphics2D g2d, Rectangle box, Color color) {
        if ((box.width > 0) && (box.height > 0)) {
            g2d.setColor(color);
            g2d.fillRect(box.x, box.y, box.width, box.height);
        }
    }

    /**
     * Draw a bounding box / rectangle starting at the specified position,
     * having the specified line (dashed or plain).
     *
     * @param g2d        the graphics object
     * @param box        the rectangle specification
     * @param color      the color of the rectangle
     * @param dashedLine true if the line used for the drawing of the rectangle should be dashed; false if the line should be solid/plain
     */
    public static void drawBBox(Graphics2D g2d, Rectangle box, Color color, boolean dashedLine) {
        g2d.setStroke(dashedLine ? getDashedStroke() : new BasicStroke(0));

        if ((box.width > 0) && (box.height > 0)) {
            g2d.setColor(color);
            g2d.drawRect(box.x, box.y, box.width, box.height);
        }

        g2d.setStroke(new BasicStroke(0));
    }

    /**
     * Draw a bounding box / rectangle starting at the specified position,
     * having the specified line (dashed or plain), and being filled as
     * specified.
     *
     * @param g2d          the graphics object
     * @param box          the rectangle specification
     * @param color        the color of the rectangle
     * @param filledBox    true if the box should be filled with color
     * @param fillingColor the color used for the filling of the rectangle
     * @param dashedLine   true if the line used for the drawing of the rectangle should be dashed; false if the line should be solid/plain
     */
    public static void drawBBox(Graphics2D g2d, Rectangle box, Color color,
            boolean filledBox, Color fillingColor, boolean dashedLine) {
        g2d.setStroke(dashedLine ? getDashedStroke() : new BasicStroke(0));

        if (filledBox) {
            // draw the fill of the rectangle    
            g2d.setColor(fillingColor);
            g2d.fillRect(box.x, box.y, box.width, box.height);
        }

        drawBBox(g2d, box, color, dashedLine);
    }

    /**
     * Draw a polygon which passes through all the points drawn by the user.
     *
     * @param g2d        the graphics object
     * @param polygon    the polygon to be drawn on the interface
     * @param color      the color of the polygon
     * @param dashedLine true = the line used for drawing the polygon will be dashed; false = the line will be solid/plain
     */
    public static void drawPolygon(Graphics2D g2d, Polygon polygon, Color color, boolean dashedLine) {
        g2d.setStroke(dashedLine ? getDashedStroke() : new BasicStroke(0));

        if (polygon.npoints > 0) {
            g2d.setColor(color);
            g2d.drawPolygon(polygon);

            // make the line thicker
            g2d.setStroke(new BasicStroke(2));

            for (int index = 0; index < polygon.npoints; index++) {
                g2d.drawLine(polygon.xpoints[index], polygon.ypoints[index],
                        polygon.xpoints[index], polygon.ypoints[index]);
            }
        }

        g2d.setStroke(new BasicStroke(0));
    }

    /**
     * Draw a polygon which passes through all the points drawn by the user.
     *
     * @param g2d          the graphics object
     * @param polygon      the polygon to be drawn on the interface
     * @param color        the color of the polygon
     * @param filledPoly   true if the box should be filled with color
     * @param fillingColor the color used for the filling of the rectangle
     * @param dashedLine   true = the line used for drawing the polygon will be dashed; false = the line will be solid/plain
     */
    public static void drawPolygon(Graphics2D g2d, Polygon polygon, Color color,
            boolean filledPoly, Color fillingColor, boolean dashedLine) {
        if (filledPoly) {
            // draw the fill of the polygon
            g2d.setColor(fillingColor);
            g2d.fillPolygon(polygon);
        }

        drawPolygon(g2d, polygon, color, dashedLine);
    }

    /**
     * Compute the coordinates of a text box and add the specified text on top
     * of a box.
     *
     * @param g2d       the graphics object
     * @param outerBox  the box on top of which the text should be written
     * @param text      the text to be displayed
     * @param color     the color of the displayed text
     * @param imageSize the size of the image (to avoid getting out of bounds)
     */
    public static void displayObjectId(Graphics2D g2d, Rectangle outerBox, String text, Color color, Dimension imageSize) {
        // draw the background with a contrasting color
        g2d.setColor(Utils.getContrastColor(color, 160));

        // get the size of the text in order to compute the size of the bkg rectangle
        Rectangle2D textSize = g2d.getFontMetrics().getStringBounds(text, g2d);

        // compute the position of the box in the image
        Point pos = computeTextLocation(outerBox, new Dimension((int) textSize.getWidth(), (int) textSize.getHeight()), imageSize);

        // draw the background
        g2d.fillRect(pos.x,
                pos.y,
                (int) textSize.getWidth() + 4,
                (int) textSize.getHeight());

        // set the color for the text
        g2d.setColor(color);
        // make the text bold
        g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, g2d.getFont().getSize()));
        // write the text at the specified coordinates
        g2d.drawString(text, pos.x + 2, pos.y + (int) textSize.getHeight() - 3);
    }

    /**
     * Compute the position where a box with text shall be displayed, in such
     * way that it will not go out of the image.
     *
     * @param box       the initial position of the box
     * @param labelSize the size of the text box
     * @param bounds    the bounds where the text should fit
     * @return the position where the box shall be drawn, in order to avoid getting out of the bounds.
     */
    public static Point computeTextLocation(Rectangle box, Dimension labelSize, Dimension bounds) {
        Point pos = new Point(box.x, box.y - labelSize.height - 1);

        if (pos.x < 0) {
            pos.setLocation(0, pos.y);
        }

        if (pos.y < 0) {
            pos.setLocation(pos.x, 0);
        }

        if ((box.x + labelSize.width) >= bounds.width) {
            pos.setLocation(bounds.width - labelSize.width - 1, pos.y);
        }

        if ((box.y + labelSize.height) >= bounds.height) {
            pos.setLocation(pos.x, bounds.height - labelSize.height - 1);
        }

        return pos;
    }

    /**
     * Get the configuration of the Stroke in such way as to have a dashed line.
     *
     * @return the basic stroke representing a dashed line
     */
    public static BasicStroke getDashedStroke() {
        return new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{2f}, 0f);
    }
}
