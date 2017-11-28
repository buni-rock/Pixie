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

import java.io.File;
import javax.swing.ImageIcon;

/**
 * The type Icons.
 *
 * @author Olimpia Popica
 */
public class Icons {

    /**
     * The common path for all the icons.
     */
    private static final String BASE_PATH_ICONS = "img" + File.separator + "icons" + File.separator;

    /**
     * The path to the folder containing 16x16 icons.
     */
    private static final String PATH_16x16 = BASE_PATH_ICONS + "16x16" + File.separator;

    /**
     * The path to the folder containing 32x32 icons.
     */
    private static final String PATH_32X32 = BASE_PATH_ICONS + "32x32" + File.separator;

    /**
     * The path to the folder containing 48x48 icons.
     */
    private static final String PATH_48X48 = BASE_PATH_ICONS + "48x48" + File.separator;

    /**
     * The path to the folder containing logos icons.
     */
    public static final String PATH_LOGO = "img" + File.separator + "logos" + File.separator;

    /**
     * The path to the reindeer icon.
     */
    public static final String REINDEER_ICON_PATH = PATH_LOGO + "Reindeer.PNG";

    /**
     * The path to the exclamation/attention icon.
     */
    public static final String EXCLAMATION_ICON_PATH = PATH_48X48 + "Dark_icon_Attention.png";

    /**
     * The path to the splash screen image.
     */
    public static final String SPLASH_SCREEN_PATH = PATH_LOGO + "SplashScreenPixie.png";

    /**
     * The icon used for info.
     */
    public static final ImageIcon INFO_ICON = new ImageIcon(PATH_48X48 + "Dark_icon_I.png");

    /**
     * The icon used for OK.
     */
    public static final ImageIcon OK_ICON = new ImageIcon(PATH_48X48 + "Dark_icon_OK.png");

    /**
     * The icon used for exclamation, attention etc.
     */
    public static final ImageIcon EXCLAMATION_ICON = new ImageIcon(EXCLAMATION_ICON_PATH);

    /**
     * The icon used for error.
     */
    public static final ImageIcon ERROR_ICON = new ImageIcon(PATH_48X48 + "Dark_icon_X.png");

    /**
     * The icon used for question.
     */
    public static final ImageIcon QUESTION_ICON = new ImageIcon(PATH_48X48 + "Dark_icon_Question.png");

    /**
     * The icon used for file loading.
     */
    public static final ImageIcon LOAD_FILE_ICON = new ImageIcon(PATH_32X32 + "Dark_icon_LoadFile.png");

    /**
     * The icon used for previous.
     */
    public static final ImageIcon PREVIOUS_ICON = new ImageIcon(PATH_32X32 + "Dark_icon_Prev.png");

    /**
     * The icon used for play.
     */
    public static final ImageIcon PLAY_ICON = new ImageIcon(PATH_32X32 + "Dark_icon_Play.png");

    /**
     * The icon used for pause.
     */
    public static final ImageIcon PAUSE_ICON = new ImageIcon(PATH_32X32 + "Dark_icon_Pause.png");

    /**
     * The icon used for next.
     */
    public static final ImageIcon NEXT_ICON = new ImageIcon(PATH_32X32 + "Dark_icon_Next.png");

    /**
     * The icon representing the picture of reindeer (400 × 261).
     */
    public static final ImageIcon REINDEER_SMALL_ICON = new ImageIcon(PATH_LOGO + "Reindeer_small.PNG");

    /**
     * The icon representing the picture of reindeer (640 × 417).
     */
    public static final ImageIcon REINDEER_ICON = new ImageIcon(REINDEER_ICON_PATH);

    /**
     * The image representing the splash screen.
     */
    public static final ImageIcon SPLASH_SCREEN = new ImageIcon(SPLASH_SCREEN_PATH);

    /**
     * The image representing a blue tag.
     */
    public static final ImageIcon CLOSED_ICON_16X16 = new ImageIcon(PATH_16x16 + "tag_blue.png");

    /**
     * The image representing a green tag.
     */
    public static final ImageIcon OPEN_ICON_16X16 = new ImageIcon(PATH_16x16 + "tag_green.png");

    /**
     * The image representing a yellow tag.
     */
    public static final ImageIcon LEAF_ICON_16X16 = new ImageIcon(PATH_16x16 + "tag_yellow.png");

    /**
     * The image representing a settings icon.
     */
    public static final ImageIcon SETTINGS_ICON_16X16 = new ImageIcon(PATH_16x16 + "settings.png");

    /**
     * The image representing the exit application icon.
     */
    public static final ImageIcon EXIT_APPLICATION_ICON_16X16 = new ImageIcon(PATH_16x16 + "exit.png");

    /**
     * The image representing the export image icon.
     */
    public static final ImageIcon EXPORT_IMAGE_ICON_16X16 = new ImageIcon(PATH_16x16 + "export_image.png");

    /**
     * The image representing the help icon.
     */
    public static final ImageIcon HELP_BOOK_ICON_16X16 = new ImageIcon(PATH_16x16 + "help_book.png");

    /**
     * The image representing the info icon.
     */
    public static final ImageIcon INFO_ICON_16X16 = new ImageIcon(PATH_16x16 + "info_round.png");

    /**
     * The image representing the about icon.
     */
    public static final ImageIcon ABOUT_ICON_16X16 = new ImageIcon(PATH_16x16 + "info_square.png");

    /**
     * The image representing the key icon.
     */
    public static final ImageIcon KEY_ICON_16X16 = new ImageIcon(PATH_16x16 + "key.png");

    /**
     * The image representing the list icon.
     */
    public static final ImageIcon LIST_ICON_16X16 = new ImageIcon(PATH_16x16 + "list.png");

    /**
     * The image representing the select language icon.
     */
    public static final ImageIcon LANGUAGE_ICON_16X16 = new ImageIcon(PATH_16x16 + "language.png");

    /**
     * The image representing the previous icon.
     */
    public static final ImageIcon PREVIOUS_ICON_16X16 = new ImageIcon(PATH_16x16 + "previous.png");

    /**
     * The image representing the previous icon.
     */
    public static final ImageIcon NEXT_ICON_16X16 = new ImageIcon(PATH_16x16 + "next.png");

    /**
     * The image representing the folder icon.
     */
    public static final ImageIcon FOLDER_ICON_16X16 = new ImageIcon(PATH_16x16 + "folder.png");
}
