package com.gmail.jiangyang5157.cardboard.kml;

/**
 * Represents a KML geometry object.
 *
 * @param <T> type of object that the coordinates are stored in
 *
 * Reference https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/kml
 */
public interface KmlGeometry<T> {

    /**
     * Gets the type of geometry
     *
     * @return type of geometry
     */
    public String getGeometryType();

    /**
     * Gets the stored KML Geometry object
     *
     * @return geometry object
     */
    public T getGeometryObject();

}