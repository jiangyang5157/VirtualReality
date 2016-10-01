package com.gmail.jiangyang5157.cardboard.kml;

/**
 * @author Yang
 * @since 9/30/2016
 */

public class KmlLink {

    private String mLinkId;

    private String mHref;

    /* package */ KmlLink() {
        mLinkId = null;
        mHref = null;
    }

    /* package */ String getLinkId() {
        return mLinkId;
    }

    /* package */ void setLinkId(String linkId) {
        mLinkId = linkId;
    }

    /* package */ String getHref() {
        return mHref;
    }

    /* package */ void setHref(String href) {
        mHref = href;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Style").append("{");
        sb.append("\n link id=").append(mLinkId);
        sb.append(",\n href=").append(mHref);
        sb.append("\n}\n");
        return sb.toString();
    }
}
