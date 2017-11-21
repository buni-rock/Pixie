/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

/**
 * The type Box lrtb.
 *
 * @author Olimpia Popica
 */
public class BoxLRTB {

    /*
    *                top
    *        ____________________
    *       |                    |
    *       |                    |
    * left  |                    |  right
    *       |                    |
    *       |____________________|
    *              bottom
     */
    /**
     * The left coordinate of the box.
     */
    private int xLeft;

    /**
     * The right coordinate of the box.
     */
    private int xRight;

    /**
     * The top coordinate of the box.
     */
    private int yTop;

    /**
     * The bottom coordinate of the box.
     */
    private int yBottom;

    /**
     * Instantiates a new Box lrtb.
     *
     * @param value the value
     */
    public BoxLRTB(int value) {
        this.xLeft = value;
        this.xRight = value;
        this.yTop = value;
        this.yBottom = value;
    }

    /**
     * Instantiates a new Box lrtb.
     *
     * @param widthBorder  the width border
     * @param heightBorder the height border
     */
    public BoxLRTB(int widthBorder, int heightBorder) {
        this.xLeft = widthBorder;
        this.xRight = widthBorder;
        this.yTop = heightBorder;
        this.yBottom = heightBorder;
    }

    /**
     * Gets left.
     *
     * @return the left
     */
    public int getxLeft() {
        return xLeft;
    }

    /**
     * Sets left.
     *
     * @param xLeft the x left
     */
    public void setxLeft(int xLeft) {
        this.xLeft = xLeft;
    }

    /**
     * Gets right.
     *
     * @return the right
     */
    public int getxRight() {
        return xRight;
    }

    /**
     * Sets right.
     *
     * @param xRight the x right
     */
    public void setxRight(int xRight) {
        this.xRight = xRight;
    }

    /**
     * Gets top.
     *
     * @return the top
     */
    public int getyTop() {
        return yTop;
    }

    /**
     * Sets top.
     *
     * @param yTop the y top
     */
    public void setyTop(int yTop) {
        this.yTop = yTop;
    }

    /**
     * Gets bottom.
     *
     * @return the bottom
     */
    public int getyBottom() {
        return yBottom;
    }

    /**
     * Sets bottom.
     *
     * @param yBottom the y bottom
     */
    public void setyBottom(int yBottom) {
        this.yBottom = yBottom;
    }

}
