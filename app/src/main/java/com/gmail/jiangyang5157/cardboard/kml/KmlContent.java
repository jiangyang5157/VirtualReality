package com.gmail.jiangyang5157.cardboard.kml;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yang
 * @since 4/27/2016
 */
public class KmlContent {

    private HashMap<KmlPlacemark, Object> mPlacemarks;

    private ArrayList<KmlContainer> mContainers;

    /**
     * Stores all given data and adds it onto the map
     *
     * @param placemarks     hashmap of placemarks
     * @param folders        array of containers
     */
    /* package */ void storeKmlData(HashMap<KmlPlacemark, Object> placemarks,
                                    ArrayList<KmlContainer> folders) {
        mPlacemarks = placemarks;
        mContainers = folders;
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
     * @return true if there is at least 1 container within the KmlAgent, false otherwise
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
}
