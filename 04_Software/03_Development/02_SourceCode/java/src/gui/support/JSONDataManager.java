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

import common.ConstantsLabeling;
import common.Utils;
import commonsegmentation.ScribbleInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A data manager for the GUIController. It manages the write and read of ground
 * truth to/from a JSON file.
 *
 * @author Olimpia Popica
 */
public class JSONDataManager {

    /**
     * The file writer used to write the ground truth to file.
     */
    private FileWriter writer;

    /**
     * The JSON parser for reading and writing the ground truth.
     */
    private final JSONParser jsonParser;

    /**
     * A JSON array containing the ground truth of the labeled objects.
     */
    private JSONArray jsonObjectsArray;

    /**
     * A JSON object containing the frame related ground truth.
     */
    private JSONObject jsonFrame;

    /**
     * The path to the chosen directory or the path to directory where the
     * ground truth shall be saved.
     */
    private String gtFilePath;

    private static final String OUTPUT_MAP_PATH = "object_map_path";
    private static final String POSITION_X = "position_x";
    private static final String POSITION_Y = "position_y";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    /**
     * logger instance
     */
    private final Logger log = LoggerFactory.getLogger(JSONDataManager.class);

    /**
     * Instantiates a new Json data manager.
     */
    public JSONDataManager() {
        this.jsonParser = new JSONParser();
    }

    /**
     * Open the file where the ground truth will be written.
     *
     * @param filePath the name and path of the file containing the ground truth
     */
    public void initWriteFile(String filePath) {
        try {
            this.gtFilePath = filePath;
            this.writer = new FileWriter(filePath + "_GT.json");
            this.jsonObjectsArray = new JSONArray();
        } catch (IOException ex) {
            log.error("Write objects to the json file error");
            log.debug("Write objects to the json file error {}", ex);
        }
    }

    /**
     * Add the frame info to the list to be written to file.
     *
     * @param frameInfo the labeling information regarding frame
     * @param frameMap the object map of the frame
     */
    public void addFrame(FrameInfo frameInfo, byte[][] frameMap) {
        // Create a new JSONObject
        jsonFrame = new JSONObject();

        // Add the frame values to the jsonObject
        jsonFrame.put("illumination", frameInfo.getIllumination().toLowerCase(Locale.ENGLISH));
        jsonFrame.put("weather", frameInfo.getWeather().toLowerCase(Locale.ENGLISH));
        jsonFrame.put("road_type", frameInfo.getRoadType().toLowerCase(Locale.ENGLISH));
        jsonFrame.put("road_event", frameInfo.getRoadEvent().toLowerCase(Locale.ENGLISH));
        jsonFrame.put("country", frameInfo.getCountry().toLowerCase(Locale.ENGLISH));
        jsonFrame.put("wipers_visible", frameInfo.isWipersVisible());
        jsonFrame.put("dirt_visible", frameInfo.isDirtVisible());
        jsonFrame.put("image_distorted", frameInfo.isImageDistorted());

        if (frameInfo.isSaveFrameObjMap()) {
            String frameMapPath = gtFilePath + "_frame_map" + ".bin";

            // save the path to the object map
            jsonFrame.put("frame_map_path", frameMapPath);

            // save the frame map to disk
            saveMapFile(frameMapPath, frameMap);
        }
    }

    /**
     * Add a new object to the list of objects to be written to file.
     *
     * @param object the labeling information regarding the object
     */
    public void addObject(Objects object) {
        // Create a new JSONObject
        JSONObject jsonObject = new JSONObject();

        // Add the values to the jsonObject
        jsonObject.put("object_id", object.getObjectId());
        jsonObject.put("segmentation_type", object.getSegmentationType().toLowerCase(Locale.ENGLISH));
        jsonObject.put("type", object.getObjectType().toLowerCase(Locale.ENGLISH));
        jsonObject.put("class", object.getObjectClass().toLowerCase(Locale.ENGLISH));
        jsonObject.put("value", object.getObjectValue().toLowerCase(Locale.ENGLISH));
        jsonObject.put("occluded", object.getOccluded().toLowerCase(Locale.ENGLISH));
        jsonObject.put(POSITION_X, object.getOuterBBox().x);
        jsonObject.put(POSITION_Y, object.getOuterBBox().y);
        jsonObject.put(WIDTH, object.getOuterBBox().width);
        jsonObject.put(HEIGHT, object.getOuterBBox().height);

        if (object instanceof ObjectScribble) {
            String objectMapPath = gtFilePath + "_map_obj_" + object.getObjectId() + ".bin";

            // save the path to the object map
            jsonObject.put(OUTPUT_MAP_PATH, objectMapPath);

            // save the list of crops in files and in the ground truth    
            jsonObject.put("Crops List", saveCropList(((ObjectScribble) object).getCropList(), object.getObjectId()));

            // generate and save object map 
            saveMapFile(objectMapPath, ((ObjectScribble) object).getObjectMap());
        } else if (object instanceof ObjectPolygon) {
            String objectMapPath = gtFilePath + "_map_obj_" + object.getObjectId() + ".ser";

            // save the path to the object map
            jsonObject.put(OUTPUT_MAP_PATH, objectMapPath);

            // save the polygon points in a file
            savePolygonMap(objectMapPath, ((ObjectPolygon) object).getPolygon());

            String xCoords = "", yCoords = "";
            // save data in plain in the json file
            Polygon poly = ((ObjectPolygon) object).getPolygon();
            for (int idx = 0; idx < poly.npoints; idx++) {
                xCoords += poly.xpoints[idx] + " ";
                yCoords += poly.ypoints[idx] + " ";
            }

            jsonObject.put("xCoordinates", xCoords.toLowerCase(Locale.ENGLISH));
            jsonObject.put("yCoordinates", yCoords.toLowerCase(Locale.ENGLISH));
        }

        // add the object to the object array list
        jsonObjectsArray.add(jsonObject);
    }

    /**
     * Write the list of objects in the file containing the ground truth for the
     * current file.
     */
    public void writeFile() {
        // stop writing if the file was not open
        if (writer == null) {
            log.debug("file not found or not open");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            if (jsonFrame != null) {
                jsonObject.put("Frame Info", jsonFrame);
            }

            // Add the jsonArray to jsonObject
            jsonObject.put("Objects List", jsonObjectsArray);

            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            log.error("Write the objects list to the json file error");
            log.debug("Write the objects list to the json file error {}", ex);
        }
    }

    /**
     * Save crop list in the ground truth.
     *
     * @param cropList - the list of crops to be saved
     * @param objId - the id of the object for which the crops are saved
     */
    private JSONArray saveCropList(List<CropObject> cropList, long objId) {
        JSONArray cropsArray = new JSONArray();

        // make sure the input is correct
        if (cropList == null) {
            return cropsArray;
        }

        // save each crop
        for (CropObject cropObject : cropList) {
            String scribbleMapPathCrop = gtFilePath + "_scribb_" + objId + "_" + cropList.indexOf(cropObject) + ".ser";

            // save crop data in files and ground truth            
            // save scribbleMap
            saveCropScribblesFile(scribbleMapPathCrop, cropObject);

            // save data in the ground truth
            Rectangle pos = cropObject.getPositionOrig();

            if (pos != null) {
                JSONObject crop = new JSONObject();

                crop.put("scribble_map_path", scribbleMapPathCrop);
                crop.put(POSITION_X, pos.x);
                crop.put(POSITION_Y, pos.y);
                crop.put(WIDTH, pos.width);
                crop.put(HEIGHT, pos.height);

                cropsArray.add(crop);
            }
        }

        return cropsArray;
    }

    /**
     * Save the list of scribbles, locally, in a file.
     */
    private void saveCropScribblesFile(String scribbleMapPathCrop, CropObject cropObj) {
        if ((cropObj == null) || (cropObj.getScribbleList() == null)) {
            return;
        }

        try (FileOutputStream fout = new FileOutputStream(scribbleMapPathCrop);
                ObjectOutputStream oos = new ObjectOutputStream(fout)) {

            // write the scribble object
            oos.writeObject((ArrayList<ScribbleInfo>) cropObj.getScribbleList());

        } catch (IOException ex) {
            log.error("Write the scribbles list to the file error");
            log.debug("Write the scribbles list to the file error {}", ex);
        }
    }

    /**
     * Save the object map, locally, in a file.
     *
     * @param mapPath the path to the place where the map will be saved
     * @param map the map to be saved on the disk
     */
    private void saveMapFile(String mapPath, byte[][] map) {
        if (map == null) {
            return;
        }

        try (FileOutputStream fout = new FileOutputStream(mapPath)) {
            for (byte[] mapLine : map) {
                fout.write(mapLine);
                fout.flush();
            }
        } catch (IOException ex) {
            log.error("Write the map file error");
            log.debug("Write the map file error {}", ex);
        }
    }

    /**
     * Save the list of points representing the vertices of the polygon,
     * locally, in a file.
     *
     * @param mapPath - the path to the place where the map will be saved
     * @param polygon - the polygon representing the object
     */
    private void savePolygonMap(String mapPath, java.awt.Polygon polygon) {
        if (polygon == null) {
            return;
        }

        try (FileOutputStream fout = new FileOutputStream(mapPath);
                ObjectOutputStream oos = new ObjectOutputStream(fout)) {
            oos.writeObject(polygon);
            oos.flush();
        } catch (IOException ex) {
            log.error("Write the polygon map to the file error");
            log.debug("Write the polygon map to the file error {}", ex);
        }
    }

    /**
     * Read the list of objects from the file containing the ground truth for
     * the current file.
     *
     * @param filePath the name of the file containing the ground truth.
     * @param frameInfo the frame info
     * @param objectList the object list
     */
    public void readFile(String filePath, FrameInfo frameInfo, List<Objects> objectList) {
        // add the file extension
        String jsonFilePath = filePath + "_GT.json";

        File file = new File(jsonFilePath);
        if ((!file.exists()) || (file.length() == 0)) {
            return;
        }

        try (FileReader fileRead = new FileReader(jsonFilePath);) {
            // convert Object to JSONObject
            JSONObject jsonDataList = (JSONObject) jsonParser.parse(fileRead);

            // reading the frame specific information
            readFrameInformation(jsonDataList, frameInfo);

            // reading the array of objects, json format
            JSONArray jsonObjList = (JSONArray) jsonDataList.get("Objects List");

            // go over the list of objects from the json file and convert them to Objects format
            for (Object jsonObj : jsonObjList) {

                JSONObject jsonObject = (JSONObject) jsonObj;
                Objects obj = null;

                long objectId = (long) jsonObject.get("object_id");
                String segmentationType = (String) jsonObject.get("segmentation_type");

                // create the object based on the segmentation type
                if (segmentationType.equalsIgnoreCase(ConstantsLabeling.LABEL_SCRIBBLE)) {
                    obj = new ObjectScribble();

                    // reading the array of objects
                    JSONArray jsonCropList = (JSONArray) jsonObject.get("Crops List");

                    if (!jsonCropList.isEmpty()) {
                        // add the crops to the scribble object
                        for (Object jsonCropObj : jsonCropList) {
                            // parse the JSON crop objects
                            JSONObject jsonCrop = (JSONObject) jsonCropObj;

                            // create a new crop object and fill in the needed data: position, scribble map
                            CropObject cropObj = new CropObject();
                            cropObj.setPositionOrig(new Rectangle((int) ((long) jsonCrop.get(POSITION_X)), (int) ((long) jsonCrop.get(POSITION_Y)),
                                    (int) ((long) jsonCrop.get(WIDTH)), (int) ((long) jsonCrop.get(HEIGHT))));
                            cropObj.setScribbleList(getScribbleList((String) jsonCrop.get("scribble_map_path")));
                            cropObj.setObjectMap(new byte[(int) ((long) jsonCrop.get(WIDTH))][(int) ((long) jsonCrop.get(HEIGHT))]);

                            // add the crop to the object list of crops
                            ((ObjectScribble) obj).addToCropList(cropObj);
                        }
                    }

                } else if (segmentationType.equalsIgnoreCase(ConstantsLabeling.LABEL_2D_BOUNDING_BOX)) {
                    obj = new ObjectBBox();
                } else if (segmentationType.equalsIgnoreCase(ConstantsLabeling.LABEL_POLYGON)) {
                    obj = new ObjectPolygon();
                    // load the saved polygon object
                    String objectMapPath = (String) jsonObject.get(OUTPUT_MAP_PATH);
                    ((ObjectPolygon) obj).setPolygon(getPolygon(objectMapPath));
                }

                // fill in the common data of the object 
                if (obj != null) {
                    obj.setObjectId(objectId);
                    obj.setSegmentationType(segmentationType);
                    obj.setSegmentationSource(ConstantsLabeling.LABEL_SOURCE_MANUAL);
                    obj.setObjectType(Utils.capitalize((String) jsonObject.get("type")));
                    obj.setObjectClass(Utils.capitalize((String) jsonObject.get("class")));
                    obj.setObjectValue(Utils.capitalize((String) jsonObject.get("value")));
                    obj.setOccluded((String) jsonObject.get("occluded"));
                    obj.setOuterBBox(new Rectangle((int) ((long) jsonObject.get(POSITION_X)), (int) ((long) jsonObject.get(POSITION_Y)),
                            (int) ((long) jsonObject.get(WIDTH)), (int) ((long) jsonObject.get(HEIGHT))));
                    obj.setColor(Utils.getColorOfObjByID(objectId));

                    // add the object in the object list if it does not exist yet
                    if (!isObjInObjList(obj, objectList)) {
                        // add the object in the list
                        objectList.add(obj);
                    }
                }
            }

            // printing all the values
            log.trace("Frame: {}", frameInfo);

            log.trace("Objects:");
            jsonObjList.stream().forEach(obj -> log.trace("\t {}", obj));
        } catch (IOException | ParseException ex) {
            log.error("Read objects from the json file error");
            log.debug("Read objects from the json file error {}", ex);
        }
    }

    /**
     * Get the scribble list from the scribble map path. The scribble list is
     * stored in a text file and it has to be loaded and put back in the
     * Scribble Info format.
     *
     * @param scribbleMapPath - the path where the file was saved
     * @return - the array list of scribbles, needed to be displayed
     */
    private List<ScribbleInfo> getScribbleList(String scribbleMapPath) {
        List<ScribbleInfo> scribbleList = new ArrayList<>();
        try (FileInputStream fin = new FileInputStream(scribbleMapPath);
                ObjectInputStream ois = new ObjectInputStream(fin)) {

            // write the scribble object
            scribbleList = (List<ScribbleInfo>) ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            log.error("Read the scribbles list from the file error");
            log.debug("Read the scribbles list from the file error {}", ex);
        }

        return scribbleList;
    }

    /**
     * Get the polygon containing the list of points representing its vertices,
     * from a locally saved file.
     *
     * @param mapPath - the path to the place where the map was saved
     * @param polygon - the polygon representing the object
     */
    private java.awt.Polygon getPolygon(String objectMap) {
        java.awt.Polygon polygon = null;
        try (FileInputStream fin = new FileInputStream(objectMap);
                ObjectInputStream ois = new ObjectInputStream(fin)) {

            // get the polygon object
            polygon = (java.awt.Polygon) ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            log.error("Read the polygon map from the file error");
            log.debug("Read the polygon map from the file error {}", ex);
        }

        return polygon;
    }

    /**
     * Checks if the object exists in the objects list.
     *
     * @param obj - the object to check if it exists in the list
     * @return - true if the object exists and false otherwise
     */
    private static boolean isObjInObjList(Objects obj, List<Objects> objectList) {
        return objectList.stream().anyMatch(object -> (object.getObjectId() == obj.getObjectId()));
    }

    /**
     * Parse the JSON file and retrieve the frame information.
     *
     * @param jsonDataList parser of the JSON file
     * @param frameInfo the data structure where the frame info shall be saved
     */
    private static void readFrameInformation(JSONObject jsonDataList, FrameInfo frameInfo) {
        JSONObject jsonFrameRead = (JSONObject) jsonDataList.get("Frame Info");

        String illumination = (String) jsonFrameRead.get("illumination");
        String weather = (String) jsonFrameRead.get("weather");
        String roadType = (String) jsonFrameRead.get("road_type");
        String roadEvent = (String) jsonFrameRead.get("road_event");
        String country = (String) jsonFrameRead.get("country");
        boolean wipersVisible = (boolean) jsonFrameRead.get("wipers_visible");
        boolean dirtVisible = (boolean) jsonFrameRead.get("dirt_visible");
        boolean imageDistorted = (boolean) jsonFrameRead.get("image_distorted");

        frameInfo.setAttributes(illumination, weather, roadType, roadEvent, country, wipersVisible, dirtVisible, imageDistorted, frameInfo.isSaveFrameObjMap());
    }
}
