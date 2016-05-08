package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere {

    protected static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.color_sphere_vertex_shader;
    protected static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.color_sphere_fragment_shader;

    protected static final int DEFAULT_RECURSION_LEVEL = 3;

    public static final int COLOR_NORMAL_RES_ID = com.gmail.jiangyang5157.tookit.R.color.Blue;

    private final Earth earth;

    public final String name;

    private final Coordinate coordinate;

    public Marker(Context context, Earth earth, float radius, String name, LatLng latlng, float altitude) {
        this(context, earth, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_RECURSION_LEVEL, radius, name, latlng, altitude);
    }

    private Marker(Context context, Earth earth, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, String name, LatLng latlng, float altitude) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, AppUtils.getColor(context, COLOR_NORMAL_RES_ID));
        this.earth = earth;
        this.name = name;
        this.coordinate = new Coordinate(latlng.latitude, latlng.longitude, altitude, this.earth.getRadius());

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
