package com.gmail.jiangyang5157.cardboard.kml;

import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlay;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses a given KML file into KmlStyle, KmlPlacemark, KmlGroundOverlay and KmlContainer objects
 * <p>
 * Reference https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/kml
 */
/* package */ class KmlParser {

    private final static String STYLE = "Style";

    private final static String STYLE_MAP = "StyleMap";

    private final static String PLACEMARK = "Placemark";

    private final static String NETWORK_LINK_REGEX = "NetworkLink";

    private final static String GROUND_OVERLAY = "GroundOverlay";

    private final static String CONTAINER_REGEX = "Folder|Document";

    private final XmlPullParser mParser;

    private final HashMap<KmlPlacemark, Object> mPlacemarks;

    private final HashMap<KmlNetworkLink, Object> mNetworkLinks;

    private final ArrayList<KmlContainer> mContainers;

    private final HashMap<String, KmlStyle> mStyles;

    private final HashMap<String, String> mStyleMaps;

    private final HashMap<KmlGroundOverlay, GroundOverlay> mGroundOverlays;

    private final static String UNSUPPORTED_REGEX = "altitude|altitudeModeGroup|altitudeMode|" +
            "begin|bottomFov|cookie|displayName|displayMode|displayMode|end|expires|extrude|" +
            "flyToView|gridOrigin|httpQuery|leftFov|linkDescription|linkName|linkSnippet|" +
            "listItemType|maxSnippetLines|maxSessionLength|message|minAltitude|minFadeExtent|" +
            "minLodPixels|minRefreshPeriod|maxAltitude|maxFadeExtent|maxLodPixels|maxHeight|" +
            "maxWidth|near|NetworkLinkControl|overlayXY|range|refreshMode|" +
            "refreshInterval|refreshVisibility|rightFov|roll|rotationXY|screenXY|shape|sourceHref|" +
            "state|targetHref|tessellate|tileSize|topFov|viewBoundScale|viewFormat|viewRefreshMode|" +
            "viewRefreshTime|when";

    /**
     * Creates a new KmlParser object
     *
     * @param parser parser containing the KML file to parse
     */
    /* package */ KmlParser(XmlPullParser parser) {
        mParser = parser;
        mPlacemarks = new HashMap<KmlPlacemark, Object>();
        mNetworkLinks = new HashMap<KmlNetworkLink, Object>();
        mContainers = new ArrayList<KmlContainer>();
        mStyles = new HashMap<String, KmlStyle>();
        mStyleMaps = new HashMap<String, String>();
        mGroundOverlays = new HashMap<KmlGroundOverlay, GroundOverlay>();
    }

    /**
     * Parses the KML file and stores the created KmlStyle and KmlPlacemark
     */
    /* package */ void parseKml() throws XmlPullParserException, IOException {
        int eventType = mParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (mParser.getName().matches(UNSUPPORTED_REGEX)) {
                    skip(mParser);
                }
                if (mParser.getName().matches(CONTAINER_REGEX)) {
                    mContainers.add(KmlContainerParser.createContainer(mParser));
                }
                if (mParser.getName().equals(STYLE)) {
                    KmlStyle style = KmlStyleParser.createStyle(mParser);
                    mStyles.put(style.getStyleId(), style);
                }
                if (mParser.getName().equals(STYLE_MAP)) {
                    mStyleMaps.putAll(KmlStyleParser.createStyleMap(mParser));
                }
                if (mParser.getName().equals(PLACEMARK)) {
                    mPlacemarks.put(KmlFeatureParser.createPlacemark(mParser), null);
                }
                if (mParser.getName().matches(NETWORK_LINK_REGEX)) {
                    mNetworkLinks.put(KmlFeatureParser.createNetworkLink(mParser), null);
                }
                if (mParser.getName().equals(GROUND_OVERLAY)) {
                    mGroundOverlays.put(KmlFeatureParser.createGroundOverlay(mParser), null);
                }
            }
            eventType = mParser.next();
        }
        //Need to put an empty new style
        mStyles.put(null, new KmlStyle());
    }

    /**
     * @return List of styles created by the parser
     */
    /* package */ HashMap<String, KmlStyle> getStyles() {
        return mStyles;
    }

    /**
     * @return A list of Kml Placemark objects
     */
    /* package */ HashMap<KmlPlacemark, Object> getPlacemarks() {
        return mPlacemarks;
    }

    /**
     * @return A list of Kml NetworkLink objects
     */
    /* package */ HashMap<KmlNetworkLink, Object> getNetworkLinks() {
        return mNetworkLinks;
    }

    /**
     * @return A list of Kml Style Maps
     */
    /* package */ HashMap<String, String> getStyleMaps() {
        return mStyleMaps;
    }

    /**
     * @return A list of Kml Folders
     */
    /* package */ ArrayList<KmlContainer> getContainers() {
        return mContainers;
    }

    /**
     * @return A list of Ground Overlays
     */
    /* package */ HashMap<KmlGroundOverlay, GroundOverlay> getGroundOverlays() {
        return mGroundOverlays;
    }

    /**
     * Skips tags from START TAG to END TAG
     *
     * @param parser XmlPullParser
     */
    /*package*/
    static void skip(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
