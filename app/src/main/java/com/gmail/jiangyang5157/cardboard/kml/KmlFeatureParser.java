package com.gmail.jiangyang5157.cardboard.kml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

/**
 * Parses the feature of a given KML file into a KmlPlacemark
 */
class KmlFeatureParser {

    private final static String GEOMETRY_REGEX = "Point";

    private final static int LONGITUDE_INDEX = 0;

    private final static int LATITUDE_INDEX = 1;

    private final static String PROPERTY_REGEX = "name|description";

    private final static String EXTENDED_DATA = "ExtendedData";

    /**
     * Creates a new Placemark object (created if a Placemark start tag is read by the
     * XmlPullParser and if a Geometry tag is contained within the Placemark tag)
     * and assigns specific elements read from the parser to the Placemark.
     */
    /* package */
    static KmlPlacemark createPlacemark(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        HashMap<String, String> properties = new HashMap<String, String>();
        KmlGeometry geometry = null;
        int eventType = parser.getEventType();
        while (!(eventType == END_TAG && parser.getName().equals("Placemark"))) {
            if (eventType == START_TAG) {
                if (parser.getName().matches(GEOMETRY_REGEX)) {
                    geometry = createGeometry(parser, parser.getName());
                } else if (parser.getName().matches(PROPERTY_REGEX)) {
                    properties.put(parser.getName(), parser.nextText());
                } else if (parser.getName().equals(EXTENDED_DATA)) {
                    properties.putAll(setExtendedDataProperties(parser));
                }
            }
            eventType = parser.next();
        }
        return new KmlPlacemark(geometry, properties);
    }

    /**
     * Creates a new KmlGeometry object (Created if "Point", "LineString", "Polygon" or
     * "MultiGeometry" tag is detected by the XmlPullParser)
     *
     * @param geometryType Type of geometry object to create
     */
    private static KmlGeometry createGeometry(XmlPullParser parser, String geometryType)
            throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        while (!(eventType == END_TAG && parser.getName().equals(geometryType))) {
            if (eventType == START_TAG) {
                if (parser.getName().equals("Point")) {
                    return createPoint(parser);
                }
            }
            eventType = parser.next();
        }
        return null;
    }

    /**
     * Adds untyped name value pairs parsed from the ExtendedData
     */
    private static HashMap<String, String> setExtendedDataProperties(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        HashMap<String, String> properties = new HashMap<String, String>();
        String propertyKey = null;
        int eventType = parser.getEventType();
        while (!(eventType == END_TAG && parser.getName().equals(EXTENDED_DATA))) {
            if (eventType == START_TAG) {
                if (parser.getName().equals("Data")) {
                    propertyKey = parser.getAttributeValue(null, "name");
                } else if (parser.getName().equals("value") && propertyKey != null) {
                    properties.put(propertyKey, parser.nextText());
                    propertyKey = null;
                }
            }
            eventType = parser.next();
        }
        return properties;
    }

    /**
     * Creates a new KmlPoint object
     *
     * @return KmlPoint object
     */
    private static KmlPoint createPoint(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        LatLng coordinate = null;
        int eventType = parser.getEventType();
        while (!(eventType == END_TAG && parser.getName().equals("Point"))) {
            if (eventType == START_TAG && parser.getName().equals("coordinates")) {
                coordinate = convertToLatLng(parser.nextText());
            }
            eventType = parser.next();
        }
        return new KmlPoint(coordinate);
    }

    /**
     * Convert a string of coordinates into an array of LatLngs
     *
     * @param coordinatesString coordinates string to convert from
     * @return array of LatLng objects created from the given coordinate string array
     */
    private static ArrayList<LatLng> convertToLatLngArray(String coordinatesString) {
        ArrayList<LatLng> coordinatesArray = new ArrayList<LatLng>();
        // Need to trim to avoid whitespace around the coordinates such as tabs
        String[] coordinates = coordinatesString.trim().split("(\\s+)");
        for (String coordinate : coordinates) {
            coordinatesArray.add(convertToLatLng(coordinate));
        }
        return coordinatesArray;
    }

    /**
     * Convert a string coordinate from a string into a LatLng object
     *
     * @param coordinateString coordinate string to convert from
     * @return LatLng object created from given coordinate string
     */
    private static LatLng convertToLatLng(String coordinateString) {
        // Lat and Lng are separated by a ,
        String[] coordinate = coordinateString.split(",");
        Double lat = Double.parseDouble(coordinate[LATITUDE_INDEX]);
        Double lon = Double.parseDouble(coordinate[LONGITUDE_INDEX]);
        return new LatLng(lat, lon);
    }
}
