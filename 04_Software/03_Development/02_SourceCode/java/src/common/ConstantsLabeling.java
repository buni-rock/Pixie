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
package common;

/**
 * The type Constants labeling.
 *
 * @author Olimpia Popica
 */
public class ConstantsLabeling {

    //----------------------Label type definition-----------------------------//
    /**
     * Label type definition: 2D bounding box.
     */
    public static final String LABEL_2D_BOUNDING_BOX = "2D_bounding_box";

    /**
     * Label type definition: 3D bounding box.
     */
    public static final String LABEL_3D_BOUNDING_BOX = "3D_bounding_box";

    /**
     * Label type definition: pixel segmentation.
     */
    public static final String LABEL_SCRIBBLE = "pixel_segmentation_scribble";

    /**
     * Label type definition: polygon segmentation.
     */
    public static final String LABEL_POLYGON = "polygon_segmentation";

    /**
     * Label type definition: free drawing segmentation.
     */
    public static final String LABEL_FREE_DRAW = "free_drawing_segmentation";

    //----------------------Label source definition---------------------------//
    /**
     * Label source definition: manual.
     */
    public static final String LABEL_SOURCE_MANUAL = "manual";

    //----------------------Actions while in scribble mode--------------------//
    /**
     * Types of possible actions while in scribble mode: erase scribbles.
     */
    public static final int ACTION_TYPE_ERASE = -1;

    /**
     * Types of possible actions while in scribble mode: draw background
     * scribble.
     */
    public static final int ACTION_TYPE_BACKGROUND = 0;

    /**
     * Types of possible actions while in scribble mode: draw object scribble.
     */
    public static final int ACTION_TYPE_OBJECT = 1;

    /**
     * Utility classes, which are collections of static members, are not meant
     * to be instantiated. Even abstract utility classes, which can be extended,
     * should not have public constructors. Java adds an implicit public
     * constructor to every class which does not define at least one explicitly.
     * Hence, at least one non-public constructor should be defined.
     */
    private ConstantsLabeling() {
        throw new IllegalStateException("Utility class, do not instantiate!");
    }
}
