package com.gmail.jiangyang5157.cardboard.kml;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.polygon.Earth;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Document class allows for users to input their KML data and output it onto the map
 *
 * Reference https://github.com/googlemaps/android-maps-utils/tree/master/library/src/com/google/maps/android/kml
 * Get raid of GoogleMap dependency
 */
public class KmlLayer {

    private final KmlRenderer mRenderer;

    /**
     * Creates a new KmlLayer object - addLayerToMap() must be called to trigger rendering onto a map.
     *
     * @param map        Map object
     * @param resourceId Raw resource KML file
     * @param context    Context object
     * @throws XmlPullParserException if file cannot be parsed
     */
    public KmlLayer(Earth map, int resourceId, Context context)
            throws XmlPullParserException, IOException {
        this(map, context.getResources().openRawResource(resourceId), context);
    }

    /**
     * Creates a new KmlLayer object
     *
     * @param map    Map object
     * @param stream InputStream containing KML file
     * @throws XmlPullParserException if file cannot be parsed
     */
    public KmlLayer(Earth map, InputStream stream, Context context)
            throws XmlPullParserException, IOException {
        if (stream == null) {
            throw new IllegalArgumentException("KML InputStream cannot be null");
        }
        mRenderer = new KmlRenderer(map, context);
        XmlPullParser xmlPullParser = createXmlParser(stream);
        KmlParser parser = new KmlParser(xmlPullParser);
        parser.parseKml();
        stream.close();
        mRenderer.storeKmlData(parser.getStyles(), parser.getStyleMaps(), parser.getPlacemarks(), parser.getContainers());
    }

    /**
     * Creates a new XmlPullParser to allow for the KML file to be parsed
     *
     * @param stream InputStream containing KML file
     * @return XmlPullParser containing the KML file
     * @throws XmlPullParserException if KML file cannot be parsed
     */
    private static XmlPullParser createXmlParser(InputStream stream) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(stream, null);
        return parser;
    }

    /**
     * Adds the KML data to the map
     */
    public void addLayerToMap() throws IOException, XmlPullParserException {
        mRenderer.addLayerToMap();
    }

    /**
     * Removes all the KML data from the map and clears all the stored placemarks
     */
    public void removeLayerFromMap() {
        mRenderer.removeLayerFromMap();
    }

    /**
     * Checks if the layer contains placemarks
     *
     * @return true if there are placemarks, false otherwise
     */

    public boolean hasPlacemarks() {
        return mRenderer.hasKmlPlacemarks();
    }

    /**
     * Gets an iterable of KmlPlacemark objects
     *
     * @return iterable of KmlPlacemark objects
     */
    public Iterable<KmlPlacemark> getPlacemarks() {
        return mRenderer.getKmlPlacemarks();
    }

    /**
     * Checks if the layer contains any KmlContainers
     *
     * @return true if there is at least 1 container within the KmlLayer, false otherwise
     */
    public boolean hasContainers() {
        return mRenderer.hasNestedContainers();
    }

    /**
     * Gets an iterable of KmlContainerInterface objects
     *
     * @return iterable of KmlContainerInterface objects
     */
    public Iterable<KmlContainer> getContainers() {
        return mRenderer.getNestedContainers();
    }

    /**
     * Gets the map that objects are being placed on
     *
     * @return map
     */
    public Earth getMap() {
        return mRenderer.getMap();
    }

    /**
     * Sets the map that objects are being placed on
     *
     * @param map map to place placemark, container, style and ground overlays on
     */
    public void setMap(Earth map) {
        mRenderer.setMap(map);
    }
}
