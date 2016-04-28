package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.projection.Icosphere;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere {

    private static final int DEFAULT_RECURSION_LEVEL = 0;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.color_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.color_fragment;

    private final Earth earth;

    public final String name;

    private final Coordinate coordinate;

    public Marker(Context context, Earth earth, float radius, float[] color, String name, LatLng latlng) {
        this(context, earth, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_RECURSION_LEVEL, radius, color, name, latlng);
    }

    private Marker(Context context, Earth earth, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color, String name, LatLng latlng) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, color);
        this.earth = earth;
        this.name = name;
        this.coordinate = new Coordinate(latlng.latitude, latlng.longitude, -this.getRadius(), this.earth.getRadius());
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        Matrix.rotateM(model, 0, 1, 0, 1, 1);
        super.update(view, perspective);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void remove() {
        earth.getMarkers().remove(this);
    }
}
