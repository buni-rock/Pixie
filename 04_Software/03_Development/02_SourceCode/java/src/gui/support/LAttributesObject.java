/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
