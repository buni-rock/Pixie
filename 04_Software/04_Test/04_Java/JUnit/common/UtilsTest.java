/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Benone Aligica
 */
public class UtilsTest{

    public UtilsTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of checkPlausability method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testCheckPlausability1()
    {
        System.out.println("checkPlausability: send null Pointers, expects NullPointerException");
        Dimension comp1 = null;
        Dimension comp2 = null;
        boolean expResult = false;
        boolean result = Utils.checkPlausability(comp1, comp2);
        assertEquals(expResult, result);

    }

    @Test
    public void testCheckPlausability2()
    {
        System.out.println("checkPlausability: send zeroes, expects false");
        Dimension comp1 = new Dimension(0, 0);
        Dimension comp2 = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkPlausability(comp1, comp2);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckPlausability3()
    {
        System.out.println("checkPlausability: send C1(0,0) and C2(0,0), expects false");
        Dimension comp1 = new Dimension(0, 0);
        Dimension comp2 = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkPlausability(comp1, comp2);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckPlausability4()
    {
        System.out.println("checkPlausability: send C1(10,10) and C2(0,0), expects false");
        Dimension comp1 = new Dimension(10, 10);
        Dimension comp2 = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkPlausability(comp1, comp2);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckPlausability5()
    {
        System.out.println("checkPlausability: send C1(10,10) and C2(100,100), expects true");
        Dimension comp1 = new Dimension(10, 10);
        Dimension comp2 = new Dimension(100, 100);
        boolean expResult = true;
        boolean result = Utils.checkPlausability(comp1, comp2);
        assertEquals(expResult, result);
    }

    /**
     * Test of isImageFile method, of class Utils.
     */
    @Test
    public void testIsImageFile1()
    {
        System.out.println("isImageFile: send no file name, expects false");
        String fileName = "";
        boolean expResult = false;
        boolean result = Utils.isImageFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsImageFile2()
    {
        System.out.println("isImageFile: send file name without extension, expects false");
        String fileName = "filename";
        boolean expResult = false;
        boolean result = Utils.isImageFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsImageFile3()
    {
        System.out.println("isImageFile: send file name with wrong extension, expects false");
        String fileName = "filename.abc";
        boolean expResult = false;
        boolean result = Utils.isImageFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsImageFile4()
    {
        System.out.println("isImageFile: send file name with correct extension(lower case), expects true");
        String fileName = "filename.bmp";
        boolean expResult = true;
        boolean result = Utils.isImageFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsImageFile6()
    {
        System.out.println("isImageFile: send file name with correct extension(camel case), expects true");
        String fileName = "filename.JpEg";
        boolean expResult = true;
        boolean result = Utils.isImageFile(fileName);
        assertEquals(expResult, result);
    }

    /**
     * Test of isVideoFile method, of class Utils.
     */
    @Test
    public void testIsVideoFile1()
    {
        System.out.println("isVideoFile: send no file name, expects false");
        String fileName = "";
        boolean expResult = false;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsVideoFile2()
    {
        System.out.println("isVideoFile: send file name without extension, expects false");
        String fileName = "filename";
        boolean expResult = false;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsVideoFile3()
    {
        System.out.println("isVideoFile: send file name with wrong extension, expects false");
        String fileName = "filename.abc";
        boolean expResult = false;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsVideoFile4()
    {
        System.out.println("isVideoFile: send file name with correct extension(lower case), expects true");
        String fileName = "filename.bgr";
        boolean expResult = true;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsVideoFile5()
    {
        System.out.println("isVideoFile: send file name with correct extension(upper case), expects true");
        String fileName = "filename.MP4";
        boolean expResult = true;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testIsVideoFile6()
    {
        System.out.println("isVideoFile: send file name with correct extension(camel case), expects true");
        String fileName = "filename.aVi";
        boolean expResult = true;
        boolean result = Utils.isVideoFile(fileName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtension method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testGetExtension_File1()
    {
        System.out.println("getExtension: send Null Pointer, expects NullPointerException ");
        File f = null;
        String expResult = "";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File2()
    {
        System.out.println("getExtension: send empty String, expects empty String ");
        File f = new File("");
        String expResult = null;
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File3()
    {
        System.out.println("getExtension: send file name without extension, expects empty String ");
        File f = new File("fileName");
        String expResult = null;
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File4()
    {
        System.out.println("getExtension: send filName.extension, expects extension ");
        File f = new File("fileName.extension");
        String expResult = "extension";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File5()
    {
        System.out.println("getExtension: send file.name.extenstion, expects extension ");
        File f = new File("file.name.extension");
        String expResult = "extension";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File6()
    {
        System.out.println("getExtension: send file.name.EXT, expects ext ");
        File f = new File("file.name.EXT");
        String expResult = "ext";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File7()
    {
        System.out.println("getExtension: send file.name.eXtEnSiOn, expects extension ");
        File f = new File("file.name.eXtEnSiOn");
        String expResult = "extension";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File8()
    {
        System.out.println("getExtension: send .ext, expects .ext ");
        File f = new File(".ext");
        String expResult = null;
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File9()
    {
        System.out.println("getExtension: send filename., expects null ");
        File f = new File("fileName.");
        String expResult = null;
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_File10()
    {
        System.out.println("getExtension: send filename.e, expects e ");
        File f = new File("fileName.e");
        String expResult = "e";
        String result = Utils.getExtension(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtension method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testGetExtension_String1()
    {
        System.out.println("getExtension");
        String fileName = null;
        String expResult = null;
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String2()
    {
        System.out.println("getExtension: send empty String, expects empty String ");
        String fileName = "";
        String expResult = null;
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String3()
    {
        System.out.println("getExtension: send file name without extension, expects empty String ");
        String fileName = "fileName";
        String expResult = null;
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String4()
    {
        System.out.println("getExtension: send filName.extension, expects extension ");
        String fileName = "fileName.extension";
        String expResult = "extension";
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String5()
    {
        System.out.println("getExtension: send file.name.extenstion, expects extension ");
        String fileName = "file.name.extension";
        String expResult = "extension";
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String6()
    {
        System.out.println("getExtension: send file.name.EXT, expects ext ");
        String fileName = "file.name.EXT";
        String expResult = "ext";
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String7()
    {
        System.out.println("getExtension: send file.name.eXtEnSiOn, expects extension ");
        String fileName = "file.name.eXtEnSiOn";
        String expResult = "extension";
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String8()
    {
        System.out.println("getExtension: send .ext, expects .ext ");
        String fileName = ".ext";
        String expResult = null;
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String9()
    {
        System.out.println("getExtension: send fileName., expects null ");
        String fileName = "fileName.";
        String expResult = null;
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetExtension_String10()
    {
        System.out.println("getExtension: send fileName.e, expects e ");
        String fileName = "fileName.e";
        String expResult = "e";
        String result = Utils.getExtension(fileName);
        assertEquals(expResult, result);
    }

    /**
     * Test of copySrcIntoDstAt method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testCopySrcIntoDstAt1()
    {
        System.out.println("copySrcIntoDstAt: send null pointers, expects NullPointerException");
        BufferedImage src = null;
        BufferedImage dst = null;
        Utils.copySrcIntoDstAt(src, dst);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCopySrcIntoDstAt2()
    {
        System.out.println("copySrcIntoDstAt: send image with w = 0 and h = 0, expects IllegalArgumentException");
        BufferedImage src = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        BufferedImage dst = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
        Utils.copySrcIntoDstAt(src, dst);
        assertEquals(src.getRaster().getDataBuffer(), dst.getRaster().getDataBuffer());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCopySrcIntoDstAt3()
    {
        System.out.println("copySrcIntoDstAt: send image with w = 1 and h = 1 and no data buffer, expects IllegalArgumentException");
        BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        BufferedImage dst = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Utils.copySrcIntoDstAt(src, dst);
        assertEquals(src.getRaster().getDataBuffer(), dst.getRaster().getDataBuffer());
    }

    @Test ()
    public void testCopySrcIntoDstAt4()
    {
        System.out.println("copySrcIntoDstAt: send image with 1 pixel(RGB), expects 1 pixel(RGB)");

        BufferedImage src = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        src.setRGB(0, 0, 10);

        BufferedImage dst = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);

        Utils.copySrcIntoDstAt(src, dst);

        DataBufferByte expected = (DataBufferByte) src.getRaster().getDataBuffer();
        DataBufferByte result = (DataBufferByte) dst.getRaster().getDataBuffer();

        assertArrayEquals(expected.getData(), result.getData());
    }

    /**
     * Test of histogramEqGrayscale method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testHistogramEqGrayscale()
    {
        System.out.println("histogramEqGrayscale: send null pointers, expects NullPointerException");
        BufferedImage originalImage = null;
        BufferedImage expResult = null;
        BufferedImage result = Utils.histogramEqGrayscale(originalImage);
        assertEquals(expResult, result);
    }

    /**
     * Test of histogramEqColor method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testHistogramEqColor()
    {
        System.out.println("histogramEqColor: send null pointers, expects NullPointerException");
        BufferedImage originalImage = null;
        BufferedImage expResult = null;
        BufferedImage result = Utils.histogramEqColor(originalImage);
        assertEquals(expResult, result);
    }

    /**
     * Test of contrast method, of class Utils.
     */
    @Test (expected = NullPointerException.class)
    public void testContrast()
    {
        System.out.println("contrast: send null pointers, expects NullPointerException");
        BufferedImage workImage = null;
        int min = 0;
        int max = 0;
        BufferedImage expResult = null;
        BufferedImage result = Utils.contrast(workImage, min, max);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRGB method, of class Utils.
     */
    @Test
    public void testGetRGB1()
    {
        System.out.println("getRGB: send zeroes, expects zeroes");
        int pixel = 0;
        int[] expResult = {0,0,0};
        int[] result = Utils.getRGB(pixel);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetRGB2()
    {
        System.out.println("getRGB: send 0xFF 0xFF 0xFF, expects 0xFF 0xFF 0xFF");
        int pixel = 0x00FFFFFF;
        int[] expResult = {0xFF,0xFF,0xFF};
        int[] result = Utils.getRGB(pixel);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetColorOfObjByID2()
    {
        System.out.println ("getColorOfObjByID: send color Id 0, expects red color");
        int id = 0;
        Color expResult = Color.red;
        Color result = Utils.getDrawingColor (id, Color.black);
        assertEquals (expResult, result);
    }
    
    @Test
    public void testGetColorOfObjByID1()
    {
        System.out.println ("getColorOfObjByID: send color Id 1, expects object color");
        int id = 1;
        Color expResult = Color.cyan;
        Color result = Utils.getDrawingColor (id, Color.cyan);
        assertEquals (expResult, result);
    }
    
    
    /**
     * Test of check bounds method, of class Utils.
     */
    @Test(expected = NullPointerException.class)
    public void testCheckBoundsImg1() {
        System.out.println("checkBounds: send null Pointers, expects NullPointerException");
        Point point = null;
        Dimension bounds = new Dimension(1280, 720);
        boolean expResult = false;
        boolean result = Utils.checkBounds(point, bounds);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckBoundsImg2() {
        System.out.println("checkBounds: send zeroes, expects false");
        Point point = new Point(0, 0);
        Dimension bounds = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkBounds(point, bounds);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckBoundsImg3() {
        System.out.println("checkBounds: send P(0,0) and image(0,0), expects false");
        Point point = new Point(0, 0);
        Dimension bounds = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkBounds(point, bounds);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckBoundsImg4() {
        System.out.println("checkBounds: send P(10,10) and image(0,0), expects false");
        Point point = new Point(10, 10);
        Dimension bounds = new Dimension(0, 0);
        boolean expResult = false;
        boolean result = Utils.checkBounds(point, bounds);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckBoundsImg5() {
        System.out.println("checkBounds: send P(10,10) and image(100,100), expects true");
        Point point = new Point(10, 10);
        Dimension bounds = new Dimension(100, 100);
        boolean expResult = true;
        boolean result = Utils.checkBounds(point, bounds);
        assertEquals(expResult, result);
    }
    
}
