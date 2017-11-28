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
package paintpanels;

/**
 * Definition of drawing constants.
 *
 * @author Olimpia Popica
 */
public class DrawConstants {

    /**
     * Definition of the types of drawing the draw panel supports.
     */
    public enum DrawType {
        /**
         * Do not draw draw type.
         */
        DO_NOT_DRAW,        // panel used just to show image, not to draw
        /**
         * Draw point draw type.
         */
        DRAW_POINT,         // draw points on the panel
        /**
         * Draw line draw type.
         */
        DRAW_LINE,          // draw lines on the panel
        /**
         * Draw bounding box draw type.
         */
        DRAW_BOUNDING_BOX,  // draw bounding boxes on the panel
        /**
         * Draw scribble draw type.
         */
        DRAW_SCRIBBLE,      // draw scribbles on the panel
        /**
         * Draw crop draw type.
         */
        DRAW_CROP,          // crop image
        /**
         * Draw polygon draw type.
         */
        DRAW_POLYGON,       // draw a polygon made of n vertices
        /**
         * The Edit mode.
         */
        EDIT_MODE           // allow the user to edit the crops which were drawen at the previous step
    }
    
    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors.
     *
     * Java adds an implicit public constructor to every class which does not
     * define at least one explicitly. Hence, at least one non-public
     * constructor should be defined.
     */
    private DrawConstants() {
        throw new IllegalStateException("Utility class DrawConstants! Do not instantiate!");
    }

}
