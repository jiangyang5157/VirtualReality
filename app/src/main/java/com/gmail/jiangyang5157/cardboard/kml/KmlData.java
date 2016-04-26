package com.gmail.jiangyang5157.cardboard.kml;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Yang
 * @date 4/27/2016
 */
public class KmlData {

    private HashMap<KmlPlacemark, Object> mPlacemarks;

    private ArrayList<KmlContainer> mContainers;

    private Context mContext;

    /* package */ KmlData(Context context) {
        mContext = context;
    }

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
     * Checks if the layer contains placemarks
     *
     * @return true if there are placemarks, false otherwise
     */
    /* package */ boolean hasKmlPlacemarks() {
        return mPlacemarks.size() > 0;
    }

    /**
     * Gets an iterable of KmlPlacemark objects
     *
     * @return iterable of KmlPlacemark objects
     */
    /* package */ Iterable<KmlPlacemark> getKmlPlacemarks() {
        return mPlacemarks.keySet();
    }

    /**
     * Checks if the layer contains any KmlContainers
     *
     * @return true if there is at least 1 container within the KmlLayer, false otherwise
     */
    /* package */ boolean hasNestedContainers() {
        return mContainers.size() > 0;
    }

    /**
     * Gets an iterable of KmlContainerInterface objects
     *
     * @return iterable of KmlContainerInterface objects
     */
    /* package */ Iterable<KmlContainer> getNestedContainers() {
        return mContainers;
    }
}
