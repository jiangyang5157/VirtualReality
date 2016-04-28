package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Placemark extends Mark {

    private static final float DEFAULT_RADIUS = 0.5f;
    private static final float[] DEFAULT_COLOR = new float[]{0.8f, 0.0f, 0.0f, 1.0f};

    private static final int DEFAULT_RECURSION_LEVEL = 0;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.color_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.color_fragment;

    private final Earth earth;
    public final String name;
    public final String description;

    public Placemark(Context context, Earth earth, KmlPlacemark kmlPlacemark) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_RECURSION_LEVEL, DEFAULT_RADIUS, DEFAULT_COLOR);
        LatLng latlng = (LatLng) kmlPlacemark.getGeometry().getGeometryObject();
        this.earth = earth;
        this.name = kmlPlacemark.getProperty("name");
        this.description = kmlPlacemark.getProperty("description");
        setCoordinate(new Coordinate(latlng.latitude, latlng.longitude, -this.getRadius(), this.earth.getRadius()));
    }
}
