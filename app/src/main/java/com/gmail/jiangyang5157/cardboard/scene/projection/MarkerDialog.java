package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class MarkerDialog extends Dialog{
    private Marker marker;

    public MarkerDialog(Context context) {
        this(context, null);
    }

    public MarkerDialog(Context context, Marker marker) {
        super(context);
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
