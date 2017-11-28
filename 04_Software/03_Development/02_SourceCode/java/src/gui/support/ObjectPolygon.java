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
package gui.support;

import library.Resize;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * The type Object polygon.
 *
 * @author Olimpia Popica
 */
public class ObjectPolygon extends Objects {

    /**
     * The polygon saving the vertices of the object.
     */
    private Polygon polygon;

    /**
     * Instantiates a new Object polygon.
     */
    public ObjectPolygon() {
        super();
    }

    @Override
    public void computeOuterBBoxCurObj() {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int index = 0; index < polygon.npoints; index++) {
            Point vertex = new Point(polygon.xpoints[index], polygon.ypoints[index]);

            if (minX > vertex.x) {
                minX = vertex.x;
            }

            if (maxX < vertex.x) {
                maxX = vertex.x;
            }

            if (minY > vertex.y) {
                minY = vertex.y;
            }

            if (maxY < vertex.y) {
                maxY = vertex.y;
            }
        }

        outerBBox = new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void move(int xOffset, int yOffset, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize) {
        for (int index = 0; index < polygon.npoints; index++) {
            Point vertex = new Point(polygon.xpoints[index], polygon.ypoints[index]);
            movePoint(xOffset, yOffset, vertex);
        }

        // move the outer bounding box
        movePoint(xOffset, yOffset, outerBBox.getLocation());
    }

    @Override
    public boolean remove(Rectangle coordPanelBox, Resize resizeRate) {
        // for polygon, it will always be the erase of the entire object, therefore nothing has to be done inside of the object.
        return false;
    }

    @Override
    public void changeSize(int left, int top, int right, int bottom, Rectangle coordPanelBox, Resize resizeRate, Dimension frameSize) {
        // to be implemented
    }

    @Override
    public boolean contains(Rectangle coordPanelBox, Resize resizeRate) {
        Rectangle outerBBoxPanel = resizeRate.originalToResized(outerBBox);

        return outerBBoxPanel.equals(coordPanelBox);
    }

    /**
     * Moves one point with the specified offset and returns the new image
     * coordinates.
     *
     * @param xOffset      - how much should the point move on the X axis
     * @param yOffset      - how much the point should move on the Y axis
     * @param pointPosOrig - the position of the point in image coordinates
     */
    public void movePoint(int xOffset, int yOffset, Point pointPosOrig) {
        pointPosOrig.setLocation(pointPosOrig.x + xOffset, pointPosOrig.y + yOffset);
    }

    /**
     * Return the polygon with the list of points defining the edges of the
     * object.
     *
     * @return the polygon with the list of points representing the border of the object
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * Set the polygon with the list of points defining the edges of the object.
     *
     * @param polygon the polygon with the list of points representing the border of the object
     */
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

}
