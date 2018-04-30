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
 * A class containing the attributes specific to the frame.
 *
 * @author Olimpia Popica
 */
public class LAttributesFrame implements Serializable {

    /**
     * The list of attributes for the frame illumination.
     */
    private ArrayList<String> illuminationList;

    /**
     * The list of attributes for the frame weather.
     */
    private ArrayList<String> weatherList;

    /**
     * The list of attributes for the frame road type.
     */
    private ArrayList<String> roadTypeList;

    /**
     * The list of attributes for the frame road event.
     */
    private ArrayList<String> roadEventList;

    /**
     * The list of attributes for the country.
     */
    private ArrayList<String> countryList;
    
    /**
     * Serial class version in form of MAJOR_MINOR_BUGFIX_DAY_MONTH_YEAR
     */
    private static final long serialVersionUID = 0x00_01_01_05_06_2017L;

    /**
     * Instantiates a new L attributes frame.
     */
    public LAttributesFrame() {
        illuminationList = new ArrayList<>();
        weatherList = new ArrayList<>();
        roadTypeList = new ArrayList<>();
        roadEventList = new ArrayList<>();
        countryList = new ArrayList<>();
    }

    /**
     * Gets illumination list.
     *
     * @return the illumination list
     */
    public ArrayList<String> getIlluminationList() {
        return illuminationList;
    }

    /**
     * Sets illumination list.
     *
     * @param illuminationList the illumination list
     */
    public void setIlluminationList(ArrayList<String> illuminationList) {
        this.illuminationList = illuminationList;
    }

    /**
     * Gets weather list.
     *
     * @return the weather list
     */
    public ArrayList<String> getWeatherList() {
        return weatherList;
    }

    /**
     * Sets weather list.
     *
     * @param weatherList the weather list
     */
    public void setWeatherList(ArrayList<String> weatherList) {
        this.weatherList = weatherList;
    }

    /**
     * Gets road type list.
     *
     * @return the road type list
     */
    public ArrayList<String> getRoadTypeList() {
        return roadTypeList;
    }

    /**
     * Sets road type list.
     *
     * @param roadTypeList the road type list
     */
    public void setRoadTypeList(ArrayList<String> roadTypeList) {
        this.roadTypeList = roadTypeList;
    }

    /**
     * Gets road event list.
     *
     * @return the road event list
     */
    public ArrayList<String> getRoadEventList() {
        return roadEventList;
    }

    /**
     * Sets road event list.
     *
     * @param roadEventList the road event list
     */
    public void setRoadEventList(ArrayList<String> roadEventList) {
        this.roadEventList = roadEventList;
    }

    /**
     * Gets country list.
     *
     * @return the country list
     */
    public ArrayList<String> getCountryList() {
        return countryList;
    }

    /**
     * Sets country list.
     *
     * @param countryList the country list
     */
    public void setCountryList(ArrayList<String> countryList) {
        this.countryList = countryList;
    }
}
