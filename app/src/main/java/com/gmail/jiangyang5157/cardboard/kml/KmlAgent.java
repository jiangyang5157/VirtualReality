package com.gmail.jiangyang5157.cardboard.kml;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * "KmlLayer"
 *
 * Document class allows for users to input their KML data and output it onto the map
 */
public class KmlAgent {

    private final KmlContent mContent;

    private Context mContext;

    public KmlAgent(int resourceId, Context context)
            throws XmlPullParserException, IOException {
        this(context.getResources().openRawResource(resourceId), context);
    }

    public KmlAgent(InputStream stream, Context context)
            throws XmlPullParserException, IOException {
        if (stream == null) {
            throw new IllegalArgumentException("KML InputStream cannot be null");
        }
        mContext = context;
        mContent = new KmlContent();
        XmlPullParser xmlPullParser = createXmlParser(stream);
        KmlParser parser = new KmlParser(xmlPullParser);
        parser.parseKml();
        stream.close();
        mContent.storeKmlData(parser.getPlacemarks(), parser.getContainers());
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
     * Checks if the layer contains placemarks
     *
     * @return true if there are placemarks, false otherwise
     */

    public boolean hasPlacemarks() {
        return mContent.hasKmlPlacemarks();
    }

    /**
     * Gets an iterable of KmlPlacemark objects
     *
     * @return iterable of KmlPlacemark objects
     */
    public Iterable<KmlPlacemark> getPlacemarks() {
        return mContent.getKmlPlacemarks();
    }

    /**
     * Checks if the layer contains any KmlContainers
     *
     * @return true if there is at least 1 container within the KmlAgent, false otherwise
     */
    public boolean hasContainers() {
        return mContent.hasNestedContainers();
    }

    /**
     * Gets an iterable of KmlContainerInterface objects
     *
     * @return iterable of KmlContainerInterface objects
     */
    public Iterable<KmlContainer> getContainers() {
        return mContent.getNestedContainers();
    }
}
