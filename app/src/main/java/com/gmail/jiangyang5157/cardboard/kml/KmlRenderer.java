package com.gmail.jiangyang5157.cardboard.kml;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.model.AtomMap;
import com.gmail.jiangyang5157.cardboard.scene.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Renders all visible KmlPlacemark and KmlGroundOverlay objects onto the Map as Marker,
 * Polyline, Polygon, GroundOverlay objects. Also removes objects from the map.
 *
 * Reference https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/kml
 * Get rid of GoogleMap dependence
 */
/* package */ class KmlRenderer {

    private AtomMap mMap;

    private HashMap<KmlPlacemark, Object> mPlacemarks;

    private HashMap<String, String> mStyleMaps;

    private ArrayList<KmlContainer> mContainers;

    private HashMap<String, KmlStyle> mStyles;

    private HashMap<String, KmlStyle> mStylesRenderer;

    private boolean mLayerVisible;

    private Context mContext;

    /* package */ KmlRenderer(AtomMap map, Context context) {
        mContext = context;
        mMap = map;
        mStylesRenderer = new HashMap<>();
        mLayerVisible = false;
    }

    /**
     * Gets the visibility of the placemark if it is specified. A visibility value of "1"
     * corresponds as "true", a visibility value of "0" corresponds as false. If the
     * visibility is not set, the method returns "true".
     *
     * @param placemark Placemark to obtain visibility from.
     * @return False if a Placemark has a visibility value of "1", true otherwise.
     */
    private static boolean getPlacemarkVisibility(KmlPlacemark placemark) {
        boolean isPlacemarkVisible = true;
        if (placemark.hasProperty("visibility")) {
            String placemarkVisibility = placemark.getProperty("visibility");
            if (Integer.parseInt(placemarkVisibility) == 0) {
                isPlacemarkVisible = false;
            }
        }
        return isPlacemarkVisible;
    }

    /**
     * Removes all given KML placemarks from the map and clears all stored placemarks.
     *
     * @param placemarks placemarks to remove
     */
    private void removePlacemarks(HashMap<KmlPlacemark, Object> placemarks) {
        // Remove map object from the map
        for (Object mapObject : placemarks.values()) {
            if (mapObject instanceof Marker) {
                mMap.getMarkers().removeMarker(((Marker) mapObject));
            }
        }
    }

    /**
     * Gets the visibility of the container
     *
     * @param kmlContainer             container to check visibility of
     * @param isParentContainerVisible true if the parent container is visible, false otherwise
     * @return true if this container is visible, false otherwise
     */
    /*package*/
    static boolean getContainerVisibility(KmlContainer kmlContainer, boolean
            isParentContainerVisible) {
        boolean isChildContainerVisible = true;
        if (kmlContainer.hasProperty("visibility")) {
            String placemarkVisibility = kmlContainer.getProperty("visibility");
            if (Integer.parseInt(placemarkVisibility) == 0) {
                isChildContainerVisible = false;
            }
        }
        return (isParentContainerVisible && isChildContainerVisible);
    }

    /**
     * Removes all the KML data from the map and clears all the stored placemarks of those which
     * are in a container.
     */
    private void removeContainers(Iterable<KmlContainer> containers) {
        for (KmlContainer container : containers) {
            removePlacemarks(container.getPlacemarksHashMap());
            removeContainers(container.getContainers());
        }
    }

    /**
     * Iterates a list of styles and assigns a style
     */
    /*package*/ void assignStyleMap(HashMap<String, String> styleMap,
                                    HashMap<String, KmlStyle> styles) {
        for (String styleMapKey : styleMap.keySet()) {
            String styleMapValue = styleMap.get(styleMapKey);
            if (styles.containsKey(styleMapValue)) {
                styles.put(styleMapKey, styles.get(styleMapValue));
            }
        }
    }

    /**
     * Stores all given data and adds it onto the map
     *
     * @param styles     hashmap of styles
     * @param styleMaps  hashmap of style maps
     * @param placemarks hashmap of placemarks
     * @param folders    array of containers
     */
    /* package */ void storeKmlData(HashMap<String, KmlStyle> styles,
                                    HashMap<String, String> styleMaps,
                                    HashMap<KmlPlacemark, Object> placemarks, ArrayList<KmlContainer> folders) {
        mStyles = styles;
        mStyleMaps = styleMaps;
        mPlacemarks = placemarks;
        mContainers = folders;
    }

    /* package */ void addLayerToMap() {
        mStylesRenderer.putAll(mStyles);
        assignStyleMap(mStyleMaps, mStylesRenderer);
        addContainerGroupToMap(mContainers, true);
        addPlacemarksToMap(mPlacemarks);
        mLayerVisible = true;
    }

    /**
     * Gets the map that objects are being placed on
     *
     * @return map
     */
    /* package */ AtomMap getMap() {
        return mMap;
    }

    /**
     * Sets the map that objects are being placed on
     *
     * @param map map to place placemark, container, style and ground overlays on
     */
    /* package */ void setMap(AtomMap map) {
        removeLayerFromMap();
        mMap = map;
        addLayerToMap();
    }

    /**
     * Checks if the layer contains placemarks
     *
     * @return true if there are placemarks, false otherwise
     */
    boolean hasKmlPlacemarks() {
        return mPlacemarks.size() > 0;
    }

    /**
     * Gets an iterable of KmlPlacemark objects
     *
     * @return iterable of KmlPlacemark objects
     */
    Iterable<KmlPlacemark> getKmlPlacemarks() {
        return mPlacemarks.keySet();
    }

    /**
     * Checks if the layer contains any KmlContainers
     *
     * @return true if there is at least 1 container within the KmlLayer, false otherwise
     */
    boolean hasNestedContainers() {
        return mContainers.size() > 0;
    }

    /**
     * Gets an iterable of KmlContainerInterface objects
     *
     * @return iterable of KmlContainerInterface objects
     */
    Iterable<KmlContainer> getNestedContainers() {
        return mContainers;
    }

    /**
     * Removes all the KML data from the map and clears all the stored placemarks
     */
    /* package */ void removeLayerFromMap() {
        removePlacemarks(mPlacemarks);
        if (hasNestedContainers()) {
            removeContainers(getNestedContainers());
        }
        mLayerVisible = false;
        mStylesRenderer.clear();
    }

    /**
     * Iterates over the placemarks, gets its style or assigns a default one and adds it to the map
     */
    private void addPlacemarksToMap(HashMap<KmlPlacemark, Object> placemarks) {
        for (KmlPlacemark kmlPlacemark : placemarks.keySet()) {
            boolean isPlacemarkVisible = getPlacemarkVisibility(kmlPlacemark);
            Object mapObject = addPlacemarkToMap(kmlPlacemark, isPlacemarkVisible);
            // Placemark stores a KmlPlacemark as a key, and Object as its value
            placemarks.put(kmlPlacemark, mapObject);
        }
    }

    /**
     * Combines style and visibility to apply to a placemark geometry object and adds it to the map
     *
     * @param placemark           Placemark to obtain geometry object to add to the map
     * @param placemarkVisibility boolean value, where true indicates the placemark geometry is
     *                            shown initially on the map, false for not shown initially on the
     *                            map.
     * @return Map Object of the placemark geometry after it has been added to the map.
     */
    private Object addPlacemarkToMap(KmlPlacemark placemark, boolean placemarkVisibility) {
        //If the placemark contains a geometry, then we add it to the map
        //If it doesnt contain a geometry, we do not add anything to the map and just store values
        if (placemark.getGeometry() != null) {
            String placemarkId = placemark.getStyleId();
            KmlGeometry geometry = placemark.getGeometry();
            KmlStyle style = getPlacemarkStyle(placemarkId);
            return addToMap(placemark, geometry, style, placemarkVisibility);
        }
        return null;
    }

    /**
     * Adds placemarks with their corresponding styles onto the map
     *
     * @param kmlContainers An arraylist of folders
     */
    private void addContainerGroupToMap(Iterable<KmlContainer> kmlContainers,
                                        boolean containerVisibility) {
        for (KmlContainer container : kmlContainers) {
            boolean isContainerVisible = getContainerVisibility(container, containerVisibility);
            if (container.getStyles() != null) {
                // Stores all found styles from the container
                mStylesRenderer.putAll(container.getStyles());
            }
            if (container.getStyleMap() != null) {
                // Stores all found style maps from the container
                assignStyleMap(container.getStyleMap(), mStylesRenderer);
            }
            addContainerObjectToMap(container, isContainerVisible);
            if (container.hasContainers()) {
                addContainerGroupToMap(container.getContainers(), isContainerVisible);
            }
        }
    }

    /**
     * Goes through the every placemark, style and properties object within a <Folder> tag
     *
     * @param kmlContainer Folder to obtain placemark and styles from
     */
    private void addContainerObjectToMap(KmlContainer kmlContainer, boolean isContainerVisible) {
        for (KmlPlacemark placemark : kmlContainer.getPlacemarks()) {
            boolean isPlacemarkVisible = getPlacemarkVisibility(placemark);
            boolean isObjectVisible = isContainerVisible && isPlacemarkVisible;
            Object mapObject = addPlacemarkToMap(placemark, isObjectVisible);
            kmlContainer.setPlacemark(placemark, mapObject);
        }
    }

    /**
     * Obtains the styleUrl from a placemark and finds the corresponding style in a list
     *
     * @param styleId StyleUrl from a placemark
     * @return Style which corresponds to an ID
     */
    private KmlStyle getPlacemarkStyle(String styleId) {
        KmlStyle style = mStylesRenderer.get(null);
        if (mStylesRenderer.get(styleId) != null) {
            style = mStylesRenderer.get(styleId);
        }
        return style;
    }

    /**
     * Adds a single geometry object to the map with its specified style
     *
     * @param geometry defines the type of object to add to the map
     * @param style    defines styling properties to add to the object when added to the map
     * @return the object that was added to the map, this is a Marker, Polyline, Polygon or an array
     * of either objects
     */
    private Object addToMap(KmlPlacemark placemark, KmlGeometry geometry, KmlStyle style, boolean isVisible) {

        String geometryType = geometry.getGeometryType();
        if (geometryType.equals("Point")) {
            Marker marker = addPointToMap(placemark, (KmlPoint) geometry, style);
            marker.setVisible(isVisible);
            return marker;
        }
        return null;
    }

    /**
     * Adds a KML Point to the map as a Marker by combining the styling and coordinates
     *
     * @param point contains coordinates for the Marker
     * @param style contains relevant styling properties for the Marker
     * @return Marker object
     */
    private Marker addPointToMap(KmlPlacemark placemark, KmlPoint point, KmlStyle style) {
        MarkerOptions markerUrlStyle = style.getMarkerOptions();
        markerUrlStyle.position(point.getGeometryObject());
        return mMap.getMarkers().addMarker(placemark, markerUrlStyle, style.getMarkerColorInteger());
    }
}
