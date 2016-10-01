package com.gmail.jiangyang5157.cardboard.kml;

import com.google.android.gms.maps.model.GroundOverlay;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a KML Document or Folder.
 *
 * Reference https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/kml
 */
public class KmlContainer {

    private final HashMap<String, String> mProperties;

    private final HashMap<KmlPlacemark, Object> mPlacemarks;

    private final HashMap<KmlNetworkLink, Object> mNetworkLinks;

    private final ArrayList<KmlContainer> mContainers;

    private final HashMap<KmlGroundOverlay, GroundOverlay> mGroundOverlays;

    private final HashMap<String, String> mStyleMap;

    private HashMap<String, KmlStyle> mStyles;

    private String mContainerId;

    /*package*/ KmlContainer(HashMap<String, String> properties, HashMap<String, KmlStyle> styles,
            HashMap<KmlPlacemark, Object> placemarks, HashMap<KmlNetworkLink, Object> networkLinks, HashMap<String, String> styleMaps,
            ArrayList<KmlContainer> containers, HashMap<KmlGroundOverlay, GroundOverlay>
            groundOverlay, String Id) {
        mProperties = properties;
        mPlacemarks = placemarks;
        mNetworkLinks = networkLinks;
        mStyles = styles;
        mStyleMap = styleMaps;
        mContainers = containers;
        mGroundOverlays = groundOverlay;
        mContainerId = Id;
    }

    /**
     * @return Map of Kml Styles, with key values representing style name (ie, color) and
     * value representing style value (ie #FFFFFF)
     */
    /* package */ HashMap<String, KmlStyle> getStyles() {
        return mStyles;
    }

    /**
     * @param placemark Placemark for the container to contain
     * @param object     Corresponding GoogleMap map object of the basic_placemark (if it has been
     *                   added
     *                   to the map)
     */
    /* package */ void setPlacemark(KmlPlacemark placemark, Object object) {
        mPlacemarks.put(placemark, object);
    }

    /* package */ void setNetworkLink(KmlNetworkLink networkLink, Object object) {
        mNetworkLinks.put(networkLink, object);
    }

    /**
     * @return A map of strings representing a style map, null if no style maps exist
     */
    /* package */ HashMap<String, String> getStyleMap() {
        return mStyleMap;
    }

    /**
     * Gets all of the ground overlays which were set in the container
     *
     * @return A set of ground overlays
     */
    /* package */ HashMap<KmlGroundOverlay, GroundOverlay> getGroundOverlayHashMap() {
        return mGroundOverlays;
    }

    /**
     * Gets the Container ID if it is specified
     *
     * @return Container ID or null if not set
     */
    public String getContainerId() {
        return mContainerId;
    }

    /**
     * Gets a style based on an ID
     */
    public KmlStyle getStyle(String styleID) {
        return mStyles.get(styleID);
    }

    /**
     * @return HashMap of containers
     */
    /*package*/ HashMap<KmlPlacemark, Object> getPlacemarksHashMap() {
        return mPlacemarks;
    }

    /*package*/ HashMap<KmlNetworkLink, Object> getNetworkLinksHashMap() {
        return mNetworkLinks;
    }

    /**
     * Gets the value of a property based on the given key
     *
     * @param propertyName property key to find
     * @return value of property found, null if key doesn't exist
     */
    public String getProperty(String propertyName) {
        return mProperties.get(propertyName);
    }

    /**
     * Gets whether the container has any properties
     *
     * @return true if there are properties, false otherwise
     */
    public boolean hasProperties() {
        return mProperties.size() > 0;
    }

    /**
     * Gets whether the given key exists in the properties
     *
     * @param keyValue property key to find
     * @return true if key was found, false otherwise
     */
    public boolean hasProperty(String keyValue) {
        return mProperties.containsKey(keyValue);
    }

    /**
     * Gets whether the container has containers
     *
     * @return true if there are containers, false otherwise
     */
    public boolean hasContainers() {
        return mContainers.size() > 0;
    }

    /**
     * Gets an iterable of nested KmlContainers
     *
     * @return iterable of KmlContainers
     */
    public Iterable<KmlContainer> getContainers() {
        return mContainers;
    }

    /**
     * Gets an iterable of the properties hashmap entries
     *
     * @return iterable of the properties hashmap entries
     */
    public Iterable<String> getProperties() {
        return mProperties.keySet();
    }

    /**
     * Gets an iterable of KmlPlacemarks
     *
     * @return iterable of KmlPlacemarks
     */
    public Iterable<KmlPlacemark> getPlacemarks() {
        return mPlacemarks.keySet();
    }

    public Iterable<KmlNetworkLink> getNetworkLinks() {
        return mNetworkLinks.keySet();
    }

    /**
     * Gets whether the container has any placemarks
     *
     * @return true if there are placemarks, false otherwise
     */
    public boolean hasPlacemarks() {
        return mPlacemarks.size() > 0;
    }

    public boolean hasNetworkLinks() {
        return mNetworkLinks.size() > 0;
    }

    /**
     * Gets an iterable of KmlGroundOverlay objects
     *
     * @return iterable of KmlGroundOverlay objects
     */
    public Iterable<KmlGroundOverlay> getGroundOverlays() {
        return mGroundOverlays.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Container").append("{");
        sb.append("\n properties=").append(mProperties);
        sb.append(",\n placemarks=").append(mPlacemarks);
        sb.append(",\n networkLinks=").append(mNetworkLinks);
        sb.append(",\n containers=").append(mContainers);
        sb.append(",\n ground overlays=").append(mGroundOverlays);
        sb.append(",\n style maps=").append(mStyleMap);
        sb.append(",\n styles=").append(mStyles);
        sb.append("\n}\n");
        return sb.toString();
    }
}
