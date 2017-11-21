/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.support;

/**
 * The type Frame info.
 *
 * @author Olimpia Popica
 */
public class FrameInfo {

    private String illumination;
    private String weather;
    private String roadType;
    private String roadEvent;
    private String country;
    private boolean wipersVisible;
    private boolean dirtVisible;
    private boolean imageDistorted;
    private String frameDuration;
    private boolean saveFrameObjMap;

    /**
     * Instantiates a new Frame info.
     *
     * @param illumination    - the scene illumination
     * @param weather         - the scene weather
     * @param roadType        - the type of road in the scene
     * @param roadEvent       - the road event in the scene
     * @param country         - the country where the scene is
     * @param wipersVisible   - the wipers are visible in the image
     * @param dirtVisible     - there is dirt visible in the image
     * @param imageDistorted  - the image is distorted in some way
     * @param saveFrameObjMap - shows that the frame object should be saved while saving data in the database
     */
    public FrameInfo(String illumination, String weather, String roadType, String roadEvent, String country, boolean wipersVisible, boolean dirtVisible, boolean imageDistorted, boolean saveFrameObjMap) {
        this.illumination = illumination;
        this.weather = weather;
        this.roadType = roadType;
        this.roadEvent = roadEvent;
        this.country = country;
        this.wipersVisible = wipersVisible;
        this.dirtVisible = dirtVisible;
        this.imageDistorted = imageDistorted;
        this.saveFrameObjMap = saveFrameObjMap;
        this.frameDuration = "";
    }

    /**
     * Instantiates a new Frame info.
     */
    public FrameInfo() {
    }

    /**
     * Gets illumination.
     *
     * @return the illumination
     */
    public String getIllumination() {
        return illumination;
    }

    /**
     * Gets weather.
     *
     * @return the weather
     */
    public String getWeather() {
        return weather;
    }

    /**
     * Gets road type.
     *
     * @return the road type
     */
    public String getRoadType() {
        return roadType;
    }

    /**
     * Gets road event.
     *
     * @return the road event
     */
    public String getRoadEvent() {
        return roadEvent;
    }

    /**
     * Gets country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Is wipers visible boolean.
     *
     * @return the boolean
     */
    public boolean isWipersVisible() {
        return wipersVisible;
    }

    /**
     * Is dirt visible boolean.
     *
     * @return the boolean
     */
    public boolean isDirtVisible() {
        return dirtVisible;
    }

    /**
     * Is image distorted boolean.
     *
     * @return the boolean
     */
    public boolean isImageDistorted() {
        return imageDistorted;
    }

    /**
     * Set a new frame duration. This initializes completely the frame duration,
     * it does not add the value to the existing ones.
     *
     * @param frameDuration the new value for the frame duration
     */
    public void setFrameDuration(String frameDuration) {
        this.frameDuration = frameDuration;
    }

    /**
     * Gets frame duration.
     *
     * @return the frame duration
     */
    public String getFrameDuration() {
        return frameDuration;
    }

    /**
     * Is save frame obj map boolean.
     *
     * @return the boolean
     */
    public boolean isSaveFrameObjMap() {
        return saveFrameObjMap;
    }

    /**
     * Sets illumination.
     *
     * @param illumination the illumination
     */
    public void setIllumination(String illumination) {
        this.illumination = illumination;
    }

    /**
     * Sets weather.
     *
     * @param weather the weather
     */
    public void setWeather(String weather) {
        this.weather = weather;
    }

    /**
     * Sets road type.
     *
     * @param roadType the road type
     */
    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    /**
     * Sets road event.
     *
     * @param roadEvent the road event
     */
    public void setRoadEvent(String roadEvent) {
        this.roadEvent = roadEvent;
    }

    /**
     * Sets country.
     *
     * @param country the country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Sets wipers visible.
     *
     * @param wipersVisible the wipers visible
     */
    public void setWipersVisible(boolean wipersVisible) {
        this.wipersVisible = wipersVisible;
    }

    /**
     * Sets dirt visible.
     *
     * @param dirtVisible the dirt visible
     */
    public void setDirtVisible(boolean dirtVisible) {
        this.dirtVisible = dirtVisible;
    }

    /**
     * Sets image distorted.
     *
     * @param imageDistorted the image distorted
     */
    public void setImageDistorted(boolean imageDistorted) {
        this.imageDistorted = imageDistorted;
    }

    /**
     * Sets save frame obj map.
     *
     * @param saveFrameObjMap the save frame obj map
     */
    public void setSaveFrameObjMap(boolean saveFrameObjMap) {
        this.saveFrameObjMap = saveFrameObjMap;
    }

    /**
     * Sets attributes.
     *
     * @param illumination    the illumination
     * @param weather         the weather
     * @param roadType        the road type
     * @param roadEvent       the road event
     * @param country         the country
     * @param wipersVisible   the wipers visible
     * @param dirtVisible     the dirt visible
     * @param imageDistorted  the image distorted
     * @param saveFrameObjMap the save frame obj map
     */
    public void setAttributes(String illumination, String weather, String roadType, String roadEvent, String country, boolean wipersVisible, boolean dirtVisible, boolean imageDistorted, boolean saveFrameObjMap) {
        this.illumination = illumination;
        this.weather = weather;
        this.roadType = roadType;
        this.roadEvent = roadEvent;
        this.country = country;
        this.wipersVisible = wipersVisible;
        this.dirtVisible = dirtVisible;
        this.imageDistorted = imageDistorted;
        this.saveFrameObjMap = saveFrameObjMap;
    }

    @Override
    public String toString() {
        return "FrameInfo{" +
                "illumination='" + illumination + '\'' +
                ", weather='" + weather + '\'' +
                ", roadType='" + roadType + '\'' +
                ", roadEvent='" + roadEvent + '\'' +
                ", country='" + country + '\'' +
                ", wipersVisible=" + wipersVisible +
                ", dirtVisible=" + dirtVisible +
                ", imageDistorted=" + imageDistorted +
                ", frameDuration='" + frameDuration + '\'' +
                ", saveFrameObjMap=" + saveFrameObjMap +
                '}';
    }
}
