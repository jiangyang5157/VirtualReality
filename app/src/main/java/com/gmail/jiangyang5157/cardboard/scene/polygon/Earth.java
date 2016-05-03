package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.scene.projection.TextureSphere;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends TextureSphere {

    private static final int TEXTURE_DRAWABLE_RESOURCE = R.drawable.no_clouds_2k;
    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.texture_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.texture_fragment_shader;

    private static final int STACKS = 50;
    private static final int SLICES = 50;
    public static final float RADIUS = 100f;

    public static final float LAYER_ALTITUDE_ACCESSABLE = -10f;
    public static final float LAYER_ALTITUDE_MARKER = -5f;
    public static final float LAYER_ALTITUDE_AIMPOINT = -10f;

    private ArrayList<Marker> markers = new ArrayList<>();

    public Earth(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, STACKS, SLICES, RADIUS, TEXTURE_DRAWABLE_RESOURCE);

        Matrix.setIdentityM(model, 0);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);

        for (Marker marker : markers) {
            marker.update(view, perspective);
        }
    }

    @Override
    public void draw() {
        super.draw();

        for (Marker marker : markers) {
            marker.draw();
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        for (Marker marker : markers) {
            marker.destroy();
        }
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }


    private void addMarker(Marker marker) {
        markers.add(marker);
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle) {
        int recursionLevel = 2;
        float radius = 4f;
        float[] color = new float[]{0.8f, 0.0f, 0.0f, 1.0f};
        String name = kmlPlacemark.getProperty("name");
        LatLng latLng = markerUrlStyle.getPosition();

        Marker marker = new Marker(context, this, recursionLevel, radius, color, name, latLng, LAYER_ALTITUDE_MARKER);
        marker.create();
        marker.setLighting(lighting);
        addMarker(marker);
        return marker;
    }

    public boolean contain(float[] point) {
        float[] position = getPosition();
        Vector positionVec = new Vector(position[0], position[1], position[2]);
        Vector pointVec = new Vector(point[0], point[1], point[2]);
        return pointVec.minus(positionVec).length() < getRadius() + LAYER_ALTITUDE_ACCESSABLE;
    }
}
