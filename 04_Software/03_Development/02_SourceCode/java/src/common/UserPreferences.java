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

import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type User preferences.
 *
 * @author Olimpia Popica
 */
public class UserPreferences implements Serializable {

    /**
     * Serial class version in form of MAJOR_MINOR_BUGFIX_DAY_MONTH_YEAR
     */
    private static final long serialVersionUID = 0x00_06_01_11_09_2017L;

    /**
     * logger instance
     */
    private final transient org.slf4j.Logger log = LoggerFactory.getLogger(UserPreferences.class);

    /**
     * The path to the last used directory.
     */
    private String lastDirectory;

    /**
     * Indicator for knowing if the background should be merged or not in the
     * image.
     */
    private boolean mergeBKG;

    /**
     * Indicator for knowing if the scribbles should be shown on the work panel.
     */
    private boolean showScribbles;

    /**
     * Indicator for knowing if the crops should be shown on the work panel.
     */
    private boolean showCrops;

    /**
     * Indicator for knowing if only the crops and scribbles of the current
     * object have to be displayed on the working panel.
     */
    private boolean showJustCurrentObj;

    /**
     * Indicator for knowing if the objects should be displayed to the user,
     * after pressing next and going to the next file, for review.
     */
    private boolean popUpObjects;

    /**
     * Indicator for knowing if play mode of the video shall be forward or
     * backward.
     */
    private boolean playBackward;

    /**
     * Indicator for knowing if file map shall be saved when the data is saved
     * in the database.
     */
    private boolean saveFrameObjMap;

    /**
     * Indicator for knowing if the user wants to have the object highlighted.
     * True = draws a transparent layer over the object to highlight it.
     */
    private boolean showObjHighlight;

    /**
     * The value of the alpha to be used for the highlight of the object.
     */
    private int objAlphaVal;

    /**
     * Indicator for knowing if the image shall be flipped vertically or not.
     */
    private boolean flipVertically;

    /**
     * Indicator for knowing if the image shall be mirrored or not.
     */
    private boolean mirrorImage;

    /**
     * The preferred value of the DPI for the display of the application.
     */
    private int preferredDPI;

    /**
     * Indicator for knowing if the application is running for the first time.
     */
    private boolean firstRun;

    /**
     * The path where to export images using the Export option.
     */
    private String imgExportPath;

    /**
     * The extension used for the export of images.
     */
    private String imgExportExtension;

    /**
     * Export every frame the selected type of image.
     */
    private boolean imgAutoExportEveryFrame;

    /**
     * The type of image which is to be saved every frame.
     */
    private String imgTypeForExport;

    /**
     * The type of images to be joined for the export of every frame.
     */
    private String imgTypeJoinedExport;

    /**
     * Indicator for knowing if the frame annotations should be checked to
     * insure they are valid.
     */
    private boolean checkFrameAnnotations;

    /**
     * Indicator for knowing if the object attributes should be checked to
     * insure they are valid.
     */
    private boolean checkObjectAttributes;

    /**
     * Instantiate a new user preferences class, which is meant to read the user
     * configuration file and set all its preferences in the application when it
     * starts.
     */
    public UserPreferences() {
        String readLine;

        setDefaults();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.USER_PREF_FILE_PATH), Charset.forName("UTF-8")))) {
            while ((readLine = br.readLine()) != null) {
                String[] wordsList = readLine.split("=");

                // if the words list is not correct, jump over the line
                if ((wordsList == null) || (wordsList.length < 2)) {
                    continue;
                }

                switch (wordsList[0]) {
                    case "lastUsedDirectory":
                        lastDirectory = wordsList[wordsList.length - 1];
                        break;

                    case "mergeBKG":
                        mergeBKG = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "showScribbles":
                        showScribbles = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "showCrops":
                        showCrops = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "showJustCurrentObj":
                        showJustCurrentObj = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "popUpObjects":
                        popUpObjects = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "playBackward":
                        playBackward = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "saveFrameObjMap":
                        saveFrameObjMap = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;


                    case "showObjHighlight":
                        showObjHighlight = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "objAlphaVal":
                        objAlphaVal = Integer.parseInt(wordsList[wordsList.length - 1]);
                        break;

                    case "flipVertically":
                        flipVertically = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "mirrorImage":
                        mirrorImage = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "preferredDPI":
                        preferredDPI = Integer.parseInt(wordsList[wordsList.length - 1]);
                        break;

                    case "firstRun":
                        firstRun = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "imgExportPath":
                        imgExportPath = wordsList[wordsList.length - 1];
                        break;

                    case "imgExportExtension":
                        imgExportExtension = wordsList[wordsList.length - 1];
                        break;

                    case "imgAutoExportEveryFrame":
                        imgAutoExportEveryFrame = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "imgTypeForExport":
                        imgTypeForExport = wordsList[wordsList.length - 1];
                        break;

                    case "imgTypeJoinedExport":
                        imgTypeJoinedExport = wordsList[wordsList.length - 1];
                        break;

                    case "checkFrameAnnotations":
                        checkFrameAnnotations = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    case "checkObjectAttributes":
                        checkObjectAttributes = Boolean.parseBoolean(wordsList[wordsList.length - 1]);
                        break;

                    default:
                        log.info("The userPreferences.txt file contains an unknown key: {}", wordsList[0]);
                        break;
                }
            }
        } catch (IOException ex) {
            log.error("The user preferences file was not loaded! Will load default values!!");
            log.debug("The user preferences file was not loaded! Will load default values!! {}", ex);
        }
    }

    /**
     * Returns the directory of the last opened file.
     *
     * @return - the path to the directory from where the last file was opened
     */
    public String getLastDirectory() {
        return lastDirectory;
    }

    /**
     * Saves the path to the last opened file. This helps the user to not always
     * navigate through folders.
     *
     * @param lastDirectory - the path to the directory from where the last file was opened
     */
    public void setLastDirectory(String lastDirectory) {
        this.lastDirectory = lastDirectory;
    }

    /**
     * Shows if the user wants to have the background merged into the big result
     * or not. This is scribble labeling specific.
     *
     * @return - true if the user used last time the merge background and false otherwise
     */
    public boolean isMergeBKG() {
        return mergeBKG;
    }

    /**
     * Shows if the user wants to have the scribbles of the segmented objects,
     * visible on the drawing panel.
     *
     * @return - true if the user used last time the show scribbles and false otherwise
     */
    public boolean isShowScribbles() {
        return showScribbles;
    }

    /**
     * Shows if the user wants to have just the current object visible in the
     * drawing panel.
     *
     * @return - true if the user used last time the show just current object and false otherwise
     */
    public boolean isShowJustCurrentObj() {
        return showJustCurrentObj;
    }

    /**
     * Sets if the user wants to have the background merged into the big result
     * or not. This is scribble labeling specific.
     *
     * @param mergeBKG - true if the user used last time the merge background and false otherwise
     */
    public void setMergeBKG(boolean mergeBKG) {
        this.mergeBKG = mergeBKG;
    }

    /**
     * Sets if the user wants to have the scribbles of the segmented objects,
     * visible on the drawing panel.
     *
     * @param showScribbles - true if the user used last time the show scribbles and false otherwise
     */
    public void setShowScribbles(boolean showScribbles) {
        this.showScribbles = showScribbles;
    }

    /**
     * Sets if the user wants to have just the current object visible in the
     * drawing panel.
     *
     * @param showJustCurrentObj - true if the user used last time the show just current object and false otherwise
     */
    public void setShowJustCurrentObj(boolean showJustCurrentObj) {
        this.showJustCurrentObj = showJustCurrentObj;
    }

    /**
     * Shows if the user wants to have the crops of the segmented objects,
     * visible on the drawing panel.
     *
     * @return true if the user used last time the show crops and false otherwise
     */
    public boolean isShowCrops() {
        return showCrops;
    }

    /**
     * Sets if the user wants to have the crops of the segmented objects,
     * visible on the drawing panel.
     *
     * @param showCrops true if the user used last time the show crops and false otherwise
     */
    public void setShowCrops(boolean showCrops) {
        this.showCrops = showCrops;
    }

    /**
     * Shows if the user wants to have the objects pop up for review, after the
     * get next frame call.
     *
     * @return - true if the user wants to review the objects, therefore the user wants them to pop up and false otherwise
     */
    public boolean isPopUpObjects() {
        return popUpObjects;
    }

    /**
     * Sets if the user wants to have the objects pop up for review, after the
     * get next frame call.
     *
     * @param popUpObjects - true if the user wants to review the objects, therefore the user wants them to pop up and false otherwise
     */
    public void setPopUpObjects(boolean popUpObjects) {
        this.popUpObjects = popUpObjects;
    }

    /**
     * Shows if the user wants to have the video played forward or backward.
     *
     * @return - true if the user wants the video played backward and false if the user wants the video played forward
     */
    public boolean isPlayBackward() {
        return playBackward;
    }

    /**
     * Sets if the user wants to have the video played forward or backward.
     *
     * @param playBackward - true if the user wants the video played backward and false if the user wants the video played forward
     */
    public void setPlayBackward(boolean playBackward) {
        this.playBackward = playBackward;
    }

    /**
     * Shows if the user wants to save the file object map, when saving data in
     * the database.
     *
     * @return - true if the user wants to save the file object map and false if no file shall be saved
     */
    public boolean isSaveFrameObjMap() {
        return saveFrameObjMap;
    }

    /**
     * Sets if the user wants to save the file object map, when saving data in
     * the database.
     *
     * @param saveFrameObjMap - true if the user wants to save the file object map and false if no file shall be saved
     */
    public void setSaveFrameObjMap(boolean saveFrameObjMap) {
        this.saveFrameObjMap = saveFrameObjMap;
    }

    /**
     * Shows if the user wants to have the object highlighted.
     *
     * @return true if the user wants to have drawn a transparent layer over the object to highlight it and false if the user wants to have just the border of the object and no filling
     */
    public boolean isShowObjHighlight() {
        return showObjHighlight;
    }

    /**
     * Sets if the user wants to have the object highlighted.
     *
     * @param showObjHighlight true if the user wants to have drawn a transparent layer over the object to highlight it and false if the user wants to have just the border of the object and no filling
     */
    public void setShowObjHighlight(boolean showObjHighlight) {
        this.showObjHighlight = showObjHighlight;
    }

    /**
     * Shows the value of the alpha to be used for the highlight of the object.
     *
     * @return the integer value, between 0 and 255, for the alpha to be used for highlighting the object
     */
    public int getObjAlphaVal() {
        return objAlphaVal;
    }

    /**
     * Sets the value of the alpha to be used for the highlight of the object.
     *
     * @param objAlphaVal the integer value, between 0 and 255, for the alpha to be used for highlighting the object
     */
    public void setObjAlphaVal(int objAlphaVal) {
        this.objAlphaVal = objAlphaVal;
    }

    /**
     * Show if the user wants to have the image flipped vertically or not.
     *
     * @return true if the image shall be flipped vertically and false if the image shall not be changed
     */
    public boolean isFlipVertically() {
        return flipVertically;
    }

    /**
     * Sets if the user wants to have the image flipped vertically or not.
     *
     * @param flipVertically true if the image shall be flipped vertically and false if the image shall not be changed
     */
    public void setFlipVertically(boolean flipVertically) {
        this.flipVertically = flipVertically;
    }

    /**
     * Shows if the user wants to have the image mirrored or not.
     *
     * @return true if the image shall be mirrored and false if the image shall not be changed
     */
    public boolean isMirrorImage() {
        return mirrorImage;
    }

    /**
     * Sets if the user wants to have the image mirrored or not.
     *
     * @param mirrorImage true if the image shall be mirrored and false if the image shall not be changed
     */
    public void setMirrorImage(boolean mirrorImage) {
        this.mirrorImage = mirrorImage;
    }

    /**
     * Returns the preferred value of the DPI, used for rescaling the screen
     * image.
     *
     * @return the last used value for the DPI
     */
    public int getPreferredDPI() {
        return preferredDPI;
    }

    /**
     * Sets the preferred value of the DPI, used for rescaling the screen image.
     *
     * @param preferredDPI the user defined value for the DPI
     */
    public void setPreferredDPI(int preferredDPI) {
        this.preferredDPI = preferredDPI;
    }

    /**
     * Shows if the application is running for the first time.
     *
     * @return true if the application is running for the first time and false otherwise
     */
    public boolean isFirstRun() {
        return firstRun;
    }

    /**
     * Sets the flag regarding the application running for the first time.
     *
     * @param firstRun true if the application is running for the first time and false otherwise
     */
    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    /**
     * Get the path where the user prefers to export images.
     *
     * @return the string representing the path where the images should be saved
     */
    public String getImgExportPath() {
        return imgExportPath;
    }

    /**
     * Set the path where the user prefers to export images.
     *
     * @param imgExportPath the string representing the path where the images should be saved
     */
    public void setImgExportPath(String imgExportPath) {
        this.imgExportPath = imgExportPath;
    }

    /**
     * Get the file type the user prefers for the export of images.
     *
     * @return the string representing the file type used for the saved images
     */
    public String getImgExportExtension() {
        return imgExportExtension;
    }

    /**
     * Set the file type the user prefers for the export of images.
     *
     * @param imgExportExtension the string representing the file type used for the saved images
     */
    public void setImgExportExtension(String imgExportExtension) {
        this.imgExportExtension = imgExportExtension;
    }

    /**
     * Set the possibility to export a certain type of image, every frame, till
     * the user changes the option.
     *
     * @return true if every frame should be exported; false if no automatic export shall be done
     */
    public boolean isImgAutoExportEveryFrame() {
        return imgAutoExportEveryFrame;
    }

    /**
     * Find out if the application should export a certain type of image, every
     * frame, till the user changes the option.
     *
     * @param imgAutoExportEveryFrame true if every frame should be exported; false if no automatic export shall be done
     */
    public void setImgAutoExportEveryFrame(boolean imgAutoExportEveryFrame) {
        this.imgAutoExportEveryFrame = imgAutoExportEveryFrame;
    }

    /**
     * Get the type of image to be exported: original, segmented or result
     * image.
     *
     * @return a string representing the type of the image exported every frame
     */
    public String getImgTypeForExport() {
        return imgTypeForExport;
    }

    /**
     * Set the type of image to be exported: original, segmented or result
     * image.
     *
     * @param imgTypeForExport a string representing the type of the image exported every frame
     */
    public void setImgTypeForExport(String imgTypeForExport) {
        this.imgTypeForExport = imgTypeForExport;
    }

    /**
     * Get the types of images to be joined for export: original + segmented +
     * result image.
     *
     * @return string representing the type of the images to be joined for exporting them every frame
     */
    public String getImgTypeJoinedExport() {
        return imgTypeJoinedExport;
    }

    /**
     * Set the types of images to be joined for export: original + segmented +
     * result image.
     *
     * @param imgTypeJoinedExport a string representing the type of the images to be joined for exporting them every frame
     */
    public void setImgTypeJoinedExport(String imgTypeJoinedExport) {
        this.imgTypeJoinedExport = imgTypeJoinedExport;
    }

    /**
     * Get the state of the check of frame attributes option (checks if the
     * frame attributes are set to a different value than the default).
     *
     * @return true if the frame attributes should be checked and false otherwise
     */
    public boolean isCheckFrameAnnotations() {
        return checkFrameAnnotations;
    }

    /**
     * Enables/Disables the check of frame attributes (checks if the frame
     * attributes are set to a different value than the default).
     *
     * @param checkFrameAnnotations true if the frame attributes should be checked and false otherwise
     */
    public void setCheckFrameAnnotations(boolean checkFrameAnnotations) {
        this.checkFrameAnnotations = checkFrameAnnotations;
    }

    /**
     * Get the state of the check of object attributes option (checks if the
     * object attributes are set to a different value than the default).
     *
     * @return true if the object attributes should be checked and false
     * otherwise
     */
    public boolean isCheckObjectAttributes() {
        return checkObjectAttributes;
    }

    /**
     * Enables/Disables the check of object attributes (checks if the object
     * attributes are set to a different value than the default).
     *
     * @param checkObjectAttributes true if the frame attributes should be
     * checked and false otherwise
     */
    public void setCheckObjectAttributes(boolean checkObjectAttributes) {
        this.checkObjectAttributes = checkObjectAttributes;
    }

    /**
     * Saves into the user preferences file the latest wishes of the user.
     */
    public void saveUserPreferences() {
        try {
            String fileContent = "";
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Constants.USER_PREF_FILE_PATH), Charset.forName("UTF-8")))) {
                fileContent += "lastUsedDirectory=" + lastDirectory + "\r\n";
                fileContent += "mergeBKG=" + mergeBKG + "\r\n";
                fileContent += "showScribbles=" + showScribbles + "\r\n";
                fileContent += "showCrops=" + showCrops + "\r\n";
                fileContent += "showJustCurrentObj=" + showJustCurrentObj + "\r\n";
                fileContent += "popUpObjects=" + popUpObjects + "\r\n";
                fileContent += "playBackward=" + playBackward + "\r\n";
                fileContent += "saveFrameObjMap=" + saveFrameObjMap + "\r\n";
                fileContent += "showObjHighlight=" + showObjHighlight + "\r\n";
                fileContent += "objAlphaVal=" + objAlphaVal + "\r\n";
                fileContent += "flipVertically=" + flipVertically + "\r\n";
                fileContent += "mirrorImage=" + mirrorImage + "\r\n";
                fileContent += "preferredDPI=" + preferredDPI + "\r\n";
                fileContent += "firstRun=" + false + "\r\n";
                fileContent += "imgExportPath=" + imgExportPath + "\r\n";
                fileContent += "imgExportExtension=" + imgExportExtension + "\r\n";
                fileContent += "imgAutoExportEveryFrame=" + imgAutoExportEveryFrame + "\r\n";
                fileContent += "imgTypeForExport=" + imgTypeForExport + "\r\n";
                fileContent += "imgTypeJoinedExport=" + imgTypeJoinedExport + "\r\n";
                fileContent += "checkFrameAnnotations=" + checkFrameAnnotations + "\r\n";
                fileContent += "checkObjectAttributes=" + checkObjectAttributes + "\r\n";

                bw.write(fileContent, 0, fileContent.length());
                bw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(UserPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set default values for user configuration parameters.
     */
    private void setDefaults() {
        // set some default initialisation values because the configuration file was not found
        lastDirectory = "";
        mergeBKG = false;
        showScribbles = true;
        showCrops = true;
        showJustCurrentObj = false;
        popUpObjects = true;
        playBackward = false;
        saveFrameObjMap = false;
        showObjHighlight = true;
        objAlphaVal = 60;
        flipVertically = false;
        mirrorImage = false;
        preferredDPI = 144;
        firstRun = true;
        imgExportPath = Constants.EXPORT_IMGS_PATH;
        imgExportExtension = "jpeg";
        imgAutoExportEveryFrame = false;
        imgTypeForExport = Constants.SEGMENTED_IMG;
        imgTypeJoinedExport = Constants.ORIGINAL_IMG + "," + Constants.SEGMENTED_IMG;
        checkFrameAnnotations = true;
        checkObjectAttributes = true;
    }
}
