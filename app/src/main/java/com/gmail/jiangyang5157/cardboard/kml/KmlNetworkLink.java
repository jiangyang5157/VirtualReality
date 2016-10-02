package com.gmail.jiangyang5157.cardboard.kml;

import java.util.HashMap;

/**
 * @author Yang
 * @since 9/30/2016
 */
public class KmlNetworkLink {

    private final KmlLink mLink;

    private HashMap<String, String> mProperties;

    public KmlNetworkLink(KmlLink link, HashMap<String, String> properties) {
        mProperties = new HashMap<>();
        mLink = link;
        mProperties.putAll(properties);
    }

    public KmlLink getLink() {
        return mLink;
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
     * Gets whether the placemark has a properties
     *
     * @return true if there are properties in the properties hashmap, false otherwise
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
        StringBuilder sb = new StringBuilder("NetworkLink").append("{");
        sb.append("\n link=").append(mLink);
        sb.append(",\n properties=").append(mProperties);
        sb.append("\n}\n");
        return sb.toString();
    }
}
