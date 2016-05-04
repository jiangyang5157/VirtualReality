package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.vr.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere {

    protected static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.color_vertex_shader;
    protected static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.color_fragment_shader;

    protected static final int DEFAULT_RECURSION_LEVEL = 3;

    private final Earth earth;

    public final String name;

    private final Coordinate coordinate;

    public Marker(Context context, Earth earth, float radius, String name, LatLng latlng, float altitude) {
        this(context, earth, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_RECURSION_LEVEL, radius, name, latlng, altitude);
    }

    private Marker(Context context, Earth earth, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, String name, LatLng latlng, float altitude) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, COLOR_DEEP_ORANGE);
        this.earth = earth;
        this.name = name;
        this.coordinate = new Coordinate(latlng.latitude, latlng.longitude, altitude, this.earth.getRadius());

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);
    }

    public void remove() {
        earth.getMarkers().remove(this);
    }
}
