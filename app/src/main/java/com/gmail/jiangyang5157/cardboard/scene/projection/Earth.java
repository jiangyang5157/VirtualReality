package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends TextureSphere {

    private static final int TEXTURE_DRAWABLE_RESOURCE = R.drawable.no_clouds_2k;
    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.texture_earth_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.texture_earth_fragment_shader;

    private static final int STACKS = 25;
    private static final int SLICES = 25;
    public static final float RADIUS = 1000f;

    public static final float MARKER_RADIUS = RADIUS / 50;
    public static final float MARKER_ALTITUDE = -1 * MARKER_RADIUS;
    public static final float CAMERA_ALTITUDE = (2 * AimRay.SPACE) * (MARKER_ALTITUDE > 0 ? 1 : -1);

    private ArrayList<Marker> markers = new ArrayList<>();

    public Earth(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
    }

    public void create() {
        create(RADIUS, TEXTURE_DRAWABLE_RESOURCE, STACKS, SLICES);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        for (Marker marker : markers) {
            marker.update(view, perspective);
        }
        super.update(view, perspective);
    }

    @Override
    public void draw() {
        for (Marker marker : markers) {
            marker.draw();
        }
        super.draw();
    }

    @Override
    public void destroy() {
        for (Marker marker : markers) {
            marker.destroy();
        }
        super.destroy();
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }


    private void addMarker(Marker marker) {
        markers.add(marker);
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle) {
        String name = kmlPlacemark.getProperty("name");
        LatLng latLng = markerUrlStyle.getPosition();

        Marker marker = new Marker(context, this);
        marker.create(MARKER_RADIUS, name, latLng, MARKER_ALTITUDE);
        marker.setLighting(lighting);
        addMarker(marker);
        return marker;
    }

    public boolean contain(float[] point) {
        float[] position = getPosition();
        Vector positionVec = new Vector3d(position[0], position[1], position[2]);
        Vector pointVec = new Vector3d(point[0], point[1], point[2]);
        return pointVec.minus(positionVec).length() < getRadius() + CAMERA_ALTITUDE;
    }
}
