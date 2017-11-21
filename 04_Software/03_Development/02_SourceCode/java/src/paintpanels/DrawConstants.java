/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
