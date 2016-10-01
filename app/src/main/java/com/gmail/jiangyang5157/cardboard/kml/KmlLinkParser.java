package com.gmail.jiangyang5157.cardboard.kml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

/**
 * @author Yang
 * @since 9/30/2016
 */

public class KmlLinkParser {

    static KmlLink createLink(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        KmlLink linkProperties = new KmlLink();
        String linkId = parser.getAttributeValue(null, "id");
        linkProperties.setLinkId(linkId);
        int eventType = parser.getEventType();
        while (!(eventType == END_TAG && parser.getName().equals("Link"))) {
            if (eventType == START_TAG) {
                if (parser.getName().equals("href")) {
                    setHref(parser, linkProperties);
                }
            }
            eventType = parser.next();
        }

        return linkProperties;
    }

    private static void setHref(XmlPullParser parser, KmlLink linkProperties)
            throws XmlPullParserException, IOException {
        linkProperties.setHref(parser.nextText());
    }
}
