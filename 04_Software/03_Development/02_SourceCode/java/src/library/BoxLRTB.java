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
