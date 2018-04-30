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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.LoggerFactory;

/**
 * The type Export image.
 *
 * @author Olimpia Popica
 */
public class ExportImage {

    /**
     * logger instance
     */
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ExportImage.class);

    /**
     * Export the image to a JPEG file format. JPEG is a lossy format.
     *
     * @param image    - the image to be exported
     * @param fileName - the name of the file
     */
    public static void exportAsJPEG(BufferedImage image, String fileName) {
        exportImage(image, fileName, "JPEG");
    }

    /**
     * Export the image to a PNG file format. PNG is a loss-less format.
     *
     * @param image    - the image to be exported
     * @param fileName - the name of the file
     */
    public static void exportAsPNG(BufferedImage image, String fileName) {
        exportImage(image, fileName, "PNG");
    }

    /**
     * Export the image to a BMP file format. BMP is a loss-less format.
     *
     * @param image    - the image to be exported
     * @param fileName - the name of the file
     */
    public static void exportAsBMP(BufferedImage image, String fileName) {
        exportImage(image, fileName, "BMP");
    }

    /**
     * Export the image to a self specified file format.
     *
     * @param image     the image to be exported
     * @param fileName  the name of the file
     * @param extension the file type/extension
     */
    public static void exportImage(BufferedImage image, String fileName, String extension) {
        try {
            Utils.createFolderPath(fileName);

            boolean done = ImageIO.write(image, extension, new File(fileName));
            LOG.info((done ? "Exported the file {}!" : "Export did not succeed; the file type might be unknown!"), fileName);
        } catch (IOException ex) {
            LOG.warn("The file export (from exportImage(..)) was not correctly done! {}", ex);
        }
    }
}
