/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
