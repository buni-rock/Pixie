/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
