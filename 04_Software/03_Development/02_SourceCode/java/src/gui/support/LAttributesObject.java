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
package gui.support;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class containing the attributes specific to the object.
 *
 * @author Olimpia Popica
 */
public class LAttributesObject implements Serializable {

    /**
     * The list of attributes for the object type.
     */
    private ArrayList<String> objTypeList;

    /**
     * The list of attributes for the object class.
     */
    private ArrayList<String> objClassList;

    /**
     * The list of attributes for the object value.
     */
    private ArrayList<String> objValueList;

    /**
     * Serial class version in form of MAJOR_MINOR_BUGFIX_DAY_MONTH_YEAR
     */
    private static final long serialVersionUID = 0x00_01_01_05_06_2017L;

    /**
     * Instantiates a new L attributes object.
     */
    public LAttributesObject() {
        objTypeList = new ArrayList<>();
        objClassList = new ArrayList<>();
        objValueList = new ArrayList<>();
    }

    /**
     * Gets obj type list.
     *
     * @return the obj type list
     */
    public ArrayList<String> getObjTypeList() {
        return objTypeList;
    }

    /**
     * Sets obj type list.
     *
     * @param objTypeList the obj type list
     */
    public void setObjTypeList(ArrayList<String> objTypeList) {
        this.objTypeList = objTypeList;
    }

    /**
     * Gets obj class list.
     *
     * @return the obj class list
     */
    public ArrayList<String> getObjClassList() {
        return objClassList;
    }

    /**
     * Sets obj class list.
     *
     * @param objClassList the obj class list
     */
    public void setObjClassList(ArrayList<String> objClassList) {
        this.objClassList = objClassList;
    }

    /**
     * Gets obj value list.
     *
     * @return the obj value list
     */
    public ArrayList<String> getObjValueList() {
        return objValueList;
    }

    /**
     * Sets obj value list.
     *
     * @param objValueList the obj value list
     */
    public void setObjValueList(ArrayList<String> objValueList) {
        this.objValueList = objValueList;
    }
}
