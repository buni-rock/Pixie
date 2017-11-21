/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Olimpia Popica
 */
public class ResizeTest {

    public ResizeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of resizeImage method, of class Resize.
     */
    @Test
    public void testResizeImage_01() {
        final String testDescription = "----------resizeImage_01----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check there is no exception when processing null input. Input image is null, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall return null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = null;
        Resize instance = new Resize(1.0, 1.0);
        BufferedImage expResult = null;
        BufferedImage result = instance.resizeImage(origImg);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizeImage_02() {
        final String testDescription = "----------resizeImage_02----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check if an image has the same size when the resize is set to 1.0. Input image has a size of 100x100, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output an image of size 100x100; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        Resize instance = new Resize(1.0, 1.0);
        Dimension expDim = new Dimension(100, 100);
        BufferedImage resultImg = instance.resizeImage(origImg);
        Dimension resultDim = new Dimension(resultImg.getWidth(), resultImg.getHeight());
        assertEquals(expDim, resultDim);
    }

    @Test
    public void testResizeImage_03() {
        final String testDescription = "----------resizeImage_03----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check if the approximation of the downscale works as expected. 66.6667 shall be aproximated to 66. Input image has a size of 100x100, the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output an image of size 66x66; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        Resize instance = new Resize(1.5, 1.5);
        Dimension expDim = new Dimension(66, 66);
        BufferedImage resultImg = instance.resizeImage(origImg);
        Dimension resultDim = new Dimension(resultImg.getWidth(), resultImg.getHeight());
        assertEquals(expDim, resultDim);
    }

    @Test
    public void testResizeImage_04() {
        final String testDescription = "----------resizeImage_04----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check if the approximation of the upscale works as expected. 333.3334 shall be aproximated to 333. Input image has a size of 100x100, the resize is set to (0.3, 0.3).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output an image of size 333x333; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        Resize instance = new Resize(0.3, 0.3);
        Dimension expDim = new Dimension(333, 333);
        BufferedImage resultImg = instance.resizeImage(origImg);
        Dimension resultDim = new Dimension(resultImg.getWidth(), resultImg.getHeight());
        assertEquals(expDim, resultDim);
    }

    @Test
    public void testResizeImage_05() {
        final String testDescription = "----------resizeImage_05----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check if the exact computation (no aproximation required) works as expected. Input image has a size of 100x100, the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output an image of size 200x200; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        Resize instance = new Resize(0.5, 0.5);
        Dimension expDim = new Dimension(200, 200);
        BufferedImage resultImg = instance.resizeImage(origImg);
        Dimension resultDim = new Dimension(resultImg.getWidth(), resultImg.getHeight());
        assertEquals(expDim, resultDim);
    }

    @Test
    public void testResizeImage_06() {
        final String testDescription = "----------resizeImage_06----------\n"
                + " Summary: Test of resizeImage(BufferedImage) method, of class Resize\n"
                + " Description: Check if the resize ratio is not used for division when it is 0.0. Make sure that division by 0 is avoided. Input image has a size of 100x100, the resize is set to (0.0, 0.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        BufferedImage origImg = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);
        Resize instance = new Resize(0.0, 0.0);
        BufferedImage expResponse = null;
        BufferedImage resultImg = instance.resizeImage(origImg);
        assertEquals(expResponse, resultImg);
    }

    /**
     * Test of resizedToOriginal method, of class Resize.
     */
    @Test
    public void testResizedToOriginal_01() {
        final String testDescription = "----------resizedToOriginal_01----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the input of 0 as values for both point and resize rate, generate 0 as output (the tested function contains multiplication). Input values (0, 0), the resize is set to (0.0, 0.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 0;
        int y = 0;
        Resize instance = new Resize(0.0, 0.0);
        Point expResult = new Point(0, 0);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_02() {
        final String testDescription = "----------resizedToOriginal_02----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input values (0, 0), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 0;
        int y = 0;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(0, 0);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_03() {
        final String testDescription = "----------resizedToOriginal_03----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element.  Input values (10, 10), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 10;
        int y = 10;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(10, 10);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_04() {
        final String testDescription = "----------resizedToOriginal_04----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the upscale with exact result value works. Input values (100, 100), the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (150, 150); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 100;
        int y = 100;
        Resize instance = new Resize(1.5, 1.5);
        Point expResult = new Point(150, 150);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_05() {
        final String testDescription = "----------resizedToOriginal_05----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the downscale with exact result value works. Input values (100, 100), the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (50, 50); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 100;
        int y = 100;
        Resize instance = new Resize(0.5, 0.5);
        Point expResult = new Point(50, 50);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_06() {
        final String testDescription = "----------resizedToOriginal_06----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.33 => 10.89 which shall be aproximated to 10. For 22 and 0.33 => 7.26 which shall be aproximated to 7. Input values (33, 22), the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 7); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 33;
        int y = 22;
        Resize instance = new Resize(0.33, 0.33);
        Point expResult = new Point(10, 7);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_07() {
        final String testDescription = "----------resizedToOriginal_07----------\n"
                + " Summary: Test of resizedToOriginal(int, int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.89 => 29.37 which shall be aproximated to 29. For 22 and 0.89 => 19.58 which shall be aproximated to 19. Input values (33, 22), the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (29, 19); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 33;
        int y = 22;
        Resize instance = new Resize(0.89, 0.89);
        Point expResult = new Point(29, 19);
        Point result = instance.resizedToOriginal(x, y);
        assertEquals(expResult, result);
    }

    /**
     * Test of resizedToOriginal method, of class Resize.
     */
    @Test
    public void testResizedToOriginalValue_01() {
        final String testDescription = "----------resizedToOriginalValue_01----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the input of 0 as values for both value and resize rate, generate 0 as output (the tested function contains multiplication). Input value 0, the resize is set to (0.0, 0.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 0; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 0;
        Resize instance = new Resize(0.0, 0.0);
        int expResult = 0;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_02() {
        final String testDescription = "----------resizedToOriginalValue_02----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input value 0, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 0; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 0;
        Resize instance = new Resize(1.0, 1.0);
        int expResult = 0;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_03() {
        final String testDescription = "----------resizedToOriginalValue_03----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input value 10, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 10; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 10;
        Resize instance = new Resize(1.0, 1.0);
        int expResult = 10;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_04() {
        final String testDescription = "----------resizedToOriginalValue_04----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.33 => 10.89 which shall be aproximated to 10. Input value 33, the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 10; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 33;
        Resize instance = new Resize(0.33, 0.33);
        int expResult = 10;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_05() {
        final String testDescription = "----------resizedToOriginalValue_05----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 22 and 0.33 => 7.26 which shall be aproximated to 7. Input value 22, the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 7; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.33, 0.33);
        int expResult = 7;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_06() {
        final String testDescription = "----------resizedToOriginalValue_06----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.89 => 29.37 which shall be aproximated to 29. Input value 33, the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 29; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 33;
        Resize instance = new Resize(0.89, 0.89);
        int expResult = 29;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_07() {
        final String testDescription = "----------resizedToOriginalValue_07----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 22 and 0.89 => 19.58 which shall be aproximated to 19. Input value 22, the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 19; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.89, 0.89);
        int expResult = 19;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_08() {
        final String testDescription = "----------resizedToOriginalValue_08----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the downscale with exact result value works. Input value 22, the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 11; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.5, 0.5);
        int expResult = 11;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginalValue_09() {
        final String testDescription = "----------resizedToOriginalValue_09----------\n"
                + " Summary: Test of resizedToOriginal(int) method, of class Resize\n"
                + " Description: Check if the upscale with exact result value works. Input value 10, the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 15; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 10;
        Resize instance = new Resize(1.5, 1.5);
        int expResult = 15;
        int result = instance.resizedToOriginal(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of resizedToOriginal for polygon method, of class Resize.
     *
     * No further tests are done because the resize of the polygon points is
     * done based on resizedToOriginal for values methods.
     */
    @Test
    public void testResizedToOriginal_Polygon_01() {
        final String testDescription = "----------resizedToOriginalPolygon_01----------\n"
                + " Summary: Test of resizedToOriginal(Polygon) method, of class Resize\n"
                + " Description: Check there is no exception when processing null input. Input value null, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Polygon polyResized = null;
        Resize instance = new Resize(1.0, 1.0);
        Polygon expResult = null;
        Polygon result = instance.resizedToOriginal(polyResized);
        assertEquals(expResult, result);
    }

    /**
     * Test of resizedToOriginal method, of class Resize.
     */
    @Test
    public void testResizedToOriginal_Point_01() {
        final String testDescription = "----------resizedToOriginalPoint_01----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check there is no exception when processing null input. Input point null, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = null;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = null;
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_02() {
        final String testDescription = "----------resizedToOriginalPoint_02----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (0, 0), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(0, 0);
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(0, 0);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_03() {
        final String testDescription = "----------resizedToOriginalPoint_03----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (10, 10), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(10, 10);
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(10, 10);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_04() {
        final String testDescription = "----------resizedToOriginalPoint_04----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the upscale with exact result works. Input point (100, 100), the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (150, 150); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(100, 100);
        Resize instance = new Resize(1.5, 1.5);
        Point expResult = new Point(150, 150);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_05() {
        final String testDescription = "----------resizedToOriginalPoint_05----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the downscale with exact result works. Input point (100, 100), the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (50, 50); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(100, 100);
        Resize instance = new Resize(0.5, 0.5);
        Point expResult = new Point(50, 50);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_06() {
        final String testDescription = "----------resizedToOriginalPoint_06----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.33 => 10.89 which shall be aproximated to 10. For 22 and 0.33 => 7.26 which shall be aproximated to 7. Input point (33, 22), the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 7); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(33, 22);
        Resize instance = new Resize(0.33, 0.33);
        Point expResult = new Point(10, 7);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testResizedToOriginal_Point_07() {
        final String testDescription = "----------resizedToOriginalPoint_07----------\n"
                + " Summary: Test of resizedToOriginal(Point) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.89 => 29.37 which shall be aproximated to 29. For 22 and 0.89 => 19.58 which shall be aproximated to 19. Input point (33, 22), the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (29, 19); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point resizedPoint = new Point(33, 22);
        Resize instance = new Resize(0.89, 0.89);
        Point expResult = new Point(29, 19);
        Point result = instance.resizedToOriginal(resizedPoint);
        assertEquals(expResult, result);
    }

    /**
     * Test of originalToResized method, of class Resize.
     */
    @Test
    public void testOriginalToResized_01() {
        final String testDescription = "----------originalToResized_01----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the input of 0 as values for both point and resize rate, generate 0 as output (the tested function contains division). Input point (0, 0), the resize is set to (0.0, 0.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 0;
        int y = 0;
        Resize instance = new Resize(0.0, 0.0);
        Point expResult = null;
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_02() {
        final String testDescription = "----------originalToResized_02----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (0, 0), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 0;
        int y = 0;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(0, 0);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_03() {
        final String testDescription = "----------originalToResized_03----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (10, 10), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 10;
        int y = 10;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(10, 10);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_04() {
        final String testDescription = "----------originalToResized_04----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the downscale with exact result works. Input point (15, 15), the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 15;
        int y = 15;
        Resize instance = new Resize(1.5, 1.5);
        Point expResult = new Point(10, 10);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_05() {
        final String testDescription = "----------originalToResized_05----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the upscale with exact result works. Input point (100, 100), the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (200, 200); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);
        int x = 100;
        int y = 100;
        Resize instance = new Resize(0.5, 0.5);
        Point expResult = new Point(200, 200);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_06() {
        final String testDescription = "----------originalToResized_06----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 34 and 0.33 => 103.0303 which shall be aproximated to 103. For 22 and 0.33 => 66.6667 which shall be aproximated to 66. Input point (34, 22), the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (103, 66); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 34;
        int y = 22;
        Resize instance = new Resize(0.33, 0.33);
        Point expResult = new Point(103, 66);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_07() {
        System.out.println("originalToResized_07: send (34,22), resize 0.89, expect (38,24)");
        final String testDescription = "----------originalToResized_07----------\n"
                + " Summary: Test of originalToResized(int, int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 34 and 0.89 => 38.2022 which shall be aproximated to 38. For 22 and 0.89 => 24.7191 which shall be aproximated to 24. Input point (34, 22), the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (38, 24); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int x = 34;
        int y = 22;
        Resize instance = new Resize(0.89, 0.89);
        Point expResult = new Point(38, 24);
        Point result = instance.originalToResized(x, y);
        assertEquals(expResult, result);
    }

    /**
     * Test of originalToResized method, of class Resize.
     */
    @Test
    public void testOriginalToResizedValue_01() {
        final String testDescription = "----------originalToResizedValue_01----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the input of 0 as values for both point and resize rate, generate 0 as output (the tested function contains division). Input value 0, the resize is set to (0.0, 0.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 0; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 0;
        Resize instance = new Resize(0.0, 0.0);
        int expResult = 0;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_02() {
        final String testDescription = "----------originalToResizedValue_02----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input value 0, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 0; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);
        int value = 0;
        Resize instance = new Resize(1.0, 1.0);
        int expResult = 0;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_03() {
        final String testDescription = "----------originalToResizedValue_03----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input value 10, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the value 10; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);
        int value = 10;
        Resize instance = new Resize(1.0, 1.0);
        int expResult = 10;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_04() {
        final String testDescription = "----------originalToResizedValue_04----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 34 and 0.33 => 103.0303 which shall be aproximated to 103. Input value 34, the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 103; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 34;
        Resize instance = new Resize(0.33, 0.33);
        int expResult = 103;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_05() {
        final String testDescription = "----------originalToResizedValue_05----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 22 and 0.33 => 66.6667 which shall be aproximated to 66. Input value 22, the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 66; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.33, 0.33);
        int expResult = 66;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_06() {
        final String testDescription = "----------originalToResizedValue_06----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 33 and 0.89 => 37.0786 which shall be aproximated to 37. Input value 33, the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 37; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 33;
        Resize instance = new Resize(0.89, 0.89);
        int expResult = 37;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_07() {
        final String testDescription = "----------originalToResizedValue_07----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 22 and 0.89 => 24.7191 which shall be aproximated to 24. Input value 22, the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 24; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.89, 0.89);
        int expResult = 24;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_08() {
        final String testDescription = "----------originalToResizedValue_08----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the upscale with exact result works. Input value 22, the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 44; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 22;
        Resize instance = new Resize(0.5, 0.5);
        int expResult = 44;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResizedValue_09() {
        final String testDescription = "----------originalToResizedValue_09----------\n"
                + " Summary: Test of originalToResized(int) method, of class Resize\n"
                + " Description: Check if the downscale with exact result works. Input value 22, the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output value 44; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        int value = 15;
        Resize instance = new Resize(1.5, 1.5);
        int expResult = 10;
        int result = instance.originalToResized(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of originalToResized method, of class Resize.
     *
     * No further tests are done because the resize of the polygon points is
     * done based on resizedToOriginal for values methods.
     */
    @Test
    public void testOriginalToResized_Polygon_01() {
        final String testDescription = "----------originalToResized_Polygon_01----------\n"
                + " Summary: Test of originalToResized(Polygon) method, of class Resize\n"
                + " Description: Check there is no exception when processing null input. Input null polygon, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Polygon polyOrig = null;
        Resize instance = new Resize(1.0, 1.0);
        Polygon expResult = null;
        Polygon result = instance.originalToResized(polyOrig);
        assertEquals(expResult, result);
    }

    /**
     * Test of originalToResized method, of class Resize.
     */
    @Test
    public void testOriginalToResized_Point_01() {
        final String testDescription = "----------originalToResized_Point_01----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check there is no exception when processing null input. Input null point, the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output null; no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = null;
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = null;
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_02() {
        final String testDescription = "----------originalToResized_Point_02----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (0, 0), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (0, 0); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(0, 0);
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(0, 0);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_03() {
        final String testDescription = "----------originalToResized_Point_03----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the resize ratio of 1.0 is outputing the same input value. Check if resize ratio 1.0 is neutral element / identity element. Input point (10, 10), the resize is set to (1.0, 1.0).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(10, 10);
        Resize instance = new Resize(1.0, 1.0);
        Point expResult = new Point(10, 10);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_04() {
        final String testDescription = "----------originalToResized_Point_04----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the downscale with exact result works. Input point (15, 15), the resize is set to (1.5, 1.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (10, 10); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(15, 15);
        Resize instance = new Resize(1.5, 1.5);
        Point expResult = new Point(10, 10);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_05() {
        final String testDescription = "----------originalToResized_Point_05----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the upscale with exact result works. Input point (100, 100), the resize is set to (0.5, 0.5).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (200, 200); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(100, 100);
        Resize instance = new Resize(0.5, 0.5);
        Point expResult = new Point(200, 200);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_06() {
        final String testDescription = "----------originalToResized_Point_06----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 34 and 0.33 => 103.0303 which shall be aproximated to 103. For 22 and 0.33 => 66.6667 which shall be aproximated to 66. Input point (34, 22), the resize is set to (0.33, 0.33).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (103, 66); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(34, 22);
        Resize instance = new Resize(0.33, 0.33);
        Point expResult = new Point(103, 66);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

    @Test
    public void testOriginalToResized_Point_07() {
        final String testDescription = "----------originalToResized_Point_07----------\n"
                + " Summary: Test of originalToResized(Point) method, of class Resize\n"
                + " Description: Check if the approximation of values works. For 34 and 0.89 => 38.2022 which shall be aproximated to 38. For 22 and 0.89 => 24.7191 which shall be aproximated to 24. Input point (34, 22), the resize is set to (0.89, 0.89).\n"
                + " Pre-conditions: none\n"
                + " Conditions: none\n"
                + " Expected result: It shall output the point (38, 24); no errors or exceptions shall occur.\n";
        System.out.println(testDescription);

        Point origPoint = new Point(34, 22);
        Resize instance = new Resize(0.89, 0.89);
        Point expResult = new Point(38, 24);
        Point result = instance.originalToResized(origPoint);
        assertEquals(expResult, result);
    }

//
//    /**
//     * Test of getRatioWidth method, of class Resize.
//     */
//    @Test
//    public void testGetRatioWidth() {
//        System.out.println("getRatioWidth");
//        Resize instance = null;
//        double expResult = 0.0;
//        double result = instance.getRatioWidth();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getRatioHeight method, of class Resize.
//     */
//    @Test
//    public void testGetRatioHeight() {
//        System.out.println("getRatioHeight");
//        Resize instance = null;
//        double expResult = 0.0;
//        double result = instance.getRatioHeight();
//        assertEquals(expResult, result, 0.0);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of incrementRatioWidth method, of class Resize.
//     */
//    @Test
//    public void testIncrementRatioWidth() {
//        System.out.println("incrementRatioWidth");
//        double value = 0.0;
//        Resize instance = null;
//        instance.incrementRatioWidth(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of incrementRatioHeight method, of class Resize.
//     */
//    @Test
//    public void testIncrementRatioHeight() {
//        System.out.println("incrementRatioHeight");
//        double value = 0.0;
//        Resize instance = null;
//        instance.incrementRatioHeight(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of incrementRatioWidthHeight method, of class Resize.
//     */
//    @Test
//    public void testIncrementRatioWidthHeight() {
//        System.out.println("incrementRatioWidthHeight");
//        double value = 0.0;
//        Resize instance = null;
//        instance.incrementRatioWidthHeight(value);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of incrementWidthHeight method, of class Resize.
//     */
//    @Test
//    public void testIncrementWidthHeight() {
//        System.out.println("incrementWidthHeight");
//        Dimension crop = null;
//        Resize instance = null;
//        int expResult = 0;
//        int result = instance.incrementWidthHeight(crop);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of equals method, of class Resize.
//     */
//    @Test
//    public void testEquals() {
//        System.out.println("equals");
//        Resize resize = null;
//        Resize instance = null;
//        boolean expResult = false;
//        boolean result = instance.equals(resize);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isSizeIncreaseOK method, of class Resize.
//     */
//    @Test
//    public void testIsSizeIncreaseOK() {
//        System.out.println("isSizeIncreaseOK");
//        Dimension crop = null;
//        Dimension screenRes = null;
//        Resize instance = null;
//        boolean expResult = false;
//        boolean result = instance.isSizeIncreaseOK(crop, screenRes);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of isSizeDecreaseOK method, of class Resize.
//     */
//    @Test
//    public void testIsSizeDecreaseOK() {
//        System.out.println("isSizeDecreaseOK");
//        Dimension crop = null;
//        Resize instance = null;
//        boolean expResult = false;
//        boolean result = instance.isSizeDecreaseOK(crop);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getZoomingFactor method, of class Resize.
//     */
//    @Test
//    public void testGetZoomingFactor() {
//        System.out.println("getZoomingFactor");
//        Resize instance = null;
//        int expResult = 0;
//        int result = instance.getZoomingFactor();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of resizeBox method, of class Resize.
//     */
//    @Test
//    public void testResizeBox() {
//        System.out.println("resizeBox");
//        Rectangle rectangle = null;
//        Resize instance = null;
//        Rectangle expResult = null;
//        Rectangle result = instance.resizeBox(rectangle);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
