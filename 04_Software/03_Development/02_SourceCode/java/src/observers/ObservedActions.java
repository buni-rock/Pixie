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
package observers;

/**
 * The type Observed actions.
 *
 * @author Olimpia Popica
 */
public class ObservedActions {

    /**
     * The enum Action.
     */
    public enum Action {
        /**
         * Do nothing.
         */
        DO_NOTHING,
        /**
         * A notification saying that a new crop was drawn and it has to be
         * displayed
         */
        OPEN_CROP_SEGMENTATION,
        /**
         * A bounding box was drawn and an window with the preview has to pop up
         * for further checks and attributes choosing.
         */
        OPEN_BBOX_SEGMENTATION,
        /**
         * A polygon was drawn and an window with the preview has to pop up for
         * further checks and attributes choosing.
         */
        OPEN_POLYGON_SEGMENTATION,
        /**
         * The attributes of the bounding box were chosen and the object has to
         * be saved in the object list.
         */
        SAVE_BOUNDING_BOX,
        /**
         * A new crop was segmented, which belongs to an object. Add it to the
         * parent object.
         */
        ADD_CROP_TO_OBJECT,
        /**
         * Notify that an object was saved and it has to be displayed on the
         * panel of the application.
         */
        ADD_OBJECT_ON_PANEL,
        /**
         * The segmentation for the opened crop was done and the result image is
         * available. Refresh the display in order to have the latest image.
         */
        REFRESH_CROP_RESULT,
        /**
         * The number of the frame changed, refresh the display.
         */
        REFRESH_FRAME_NO,
        /**
         * The size of the brush changed. Refresh the display.
         */
        REFRESH_BRUSH_SIZE,
        /**
         * An object has been removed from the list of objects; remove it from
         * the objects list as well.
         */
        REFRESH_OBJ_LIST_PANEL,
        /**
         * Remove the key event dispatcher because another one has to be used.
         */
        REMOVE_GUI_KEY_EVENT_DISPATCHER,
        /**
         * Remove the object, one crop of the object or the bounding box object.
         */
        REMOVE_SELECTED_OBJECT,
        /**
         * A crop was selected for edit. Notify in order to open a window and
         * edit the crop.
         */
        EDIT_OBJECT_ACTION,
        /**
         * A bounding box object is/was in edit mode and has to be saved.
         */
        UPDATE_BOUNDING_BOX,
        /**
         * A complete scribble object is/was in edit mode and it has to be
         * saved.
         */
        UPDATE_OBJECT_SCRIBBLE,
        /**
         * A crop was edited and now it has to be updated inside the parent
         * object.
         */
        UPDATE_CROP_OF_OBJECT,
        /**
         * Add the key event dispatcher.
         */
        ADD_GUI_KEY_EVENT_DISPATCHER,
        /**
         * An object which is under edit mode moved and the operation has to be
         * reflected in the gui.
         */
        SELECTED_OBJECT_MOVED,
        /**
         * One object was selected and now it is dragged/moved.
         */
        MOVE_OBJECT_DRAG,
        /**
         * An object was selected and the corresponding button on the interface
         * has to be highlighted.
         */
        HIGHLIGHT_OBJECT,
        /**
         * A frame was loaded from the database and its annotations have to be
         * displayed on the gui.
         */
        LOAD_FRAME_ANNOTATION,
        /**
         * Notify that the current object was cancel.
         */
        CANCEL_CURRENT_OBJECT,
        /**
         * Shows that the output object map shall be further filter because
         * there are outliers.
         */
        FILTER_OBJECT_MAP,
        /**
         * The Ctrl + mouse released event happened. Choose how to treat it.
         */
        CTRL_MOUSE_EVENT,
        /**
         * Refresh the application with the current configuration.
         */
        REFRESH_DISPLAY,
        /**
         * There were some changes and the panels should be updated; show/hide
         * some panels based on the existence of a scribble object in the list.
         */
        REFRESH_PANEL_OPTIONS,
        /**
         * The object has to be saved and sent to the ground truth manager.
         */
        SAVE_LABEL,
        /**
         * The object or frame attributes were changed. Reload them.
         */
        RELOAD_ATTRIBUTES,
        /**
         * The user wants to see a preview of the application with another DPI
         * value.
         */
        REFRESH_APPLICATION_VIEW,
        /**
         * The window for the edit of attributes should be shown.
         */
        DISPLAY_EDIT_ATTRIBUTES_WIN
    }
}
