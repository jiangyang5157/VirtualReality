package com.gmail.jiangyang5157.cardboard.kml;

import java.util.HashMap;


public class KmlPlacemark {

    private final KmlGeometry mGeometry;

    private HashMap<String, String> mProperties;

    /**
     * Creates a new KmlPlacemark object
     *
     * @param geometry   geometry object to store
     * @param properties properties hashmap to store
     */
    public KmlPlacemark(KmlGeometry geometry, HashMap<String, String> properties) {
        mGeometry = geometry;
        mProperties = properties;
    }

    /**
     * Gets the property entry set
     *
     * @return property entry set
     */
    public Iterable getProperties() {
        return mProperties.entrySet();
    }

    /**
     * Gets the property based on property name
     *
     * @param keyValue Property name to retrieve value
     * @return property value, null if not found
     */
    public String getProperty(String keyValue) {
        return mProperties.get(keyValue);
    }

    /**
     * Gets the geometry object
     *
     * @return geometry object
     */
    public KmlGeometry getGeometry() {
        return mGeometry;
    }

    /**
     * Gets whether the basic has a given property
     *
     * @param keyValue key value to check
     * @return true if the key is stored in the properties, false otherwise
     */
    public boolean hasProperty(String keyValue) {
        return mProperties.containsKey(keyValue);
    }

    /**
     * Gets whether the placemark has a properties
     *
     * @return true if there are properties in the properties hashmap, false otherwise
     */
    public boolean hasProperties() {
        return mProperties.size() > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Placemark").append("{");
        sb.append(",\n properties=").append(mProperties);
        sb.append(",\n geometry=").append(mGeometry);
        sb.append("\n}\n");
        return sb.toString();
    }
}
