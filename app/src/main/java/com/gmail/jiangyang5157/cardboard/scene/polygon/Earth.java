package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.scene.projection.TextureSphere;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends TextureSphere {

    private static final float DEFAULT_RADIUS = 100f;
    private static final int DEFAULT_TEXTURE_DRAWABLE_RESOURCE = R.drawable.no_clouds_2k;

    private static final int DEFAULT_STACKS = 50;
    private static final int DEFAULT_SLICES = 50;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.texture_vertex_shader;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.texture_fragment_shader;

    private ArrayList<Marker> markers = new ArrayList<>();

    public Earth(Context context) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_STACKS, DEFAULT_SLICES, DEFAULT_RADIUS, DEFAULT_TEXTURE_DRAWABLE_RESOURCE);

        Matrix.setIdentityM(model, 0);
//        Matrix.translateM(model, 0, 0, 0, 0);
//        Matrix.rotateM(model, 0, 90, 1, 0, 0);
//        Matrix.rotateM(model, 0, 180, 0, 0, 1);
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
        float radius = 4f;
        float[] color = new float[]{0.8f, 0.0f, 0.0f, 1.0f};
        String name = kmlPlacemark.getProperty("name");
        LatLng latLng = markerUrlStyle.getPosition();

        Marker marker = new Marker(context, this, radius, color, name, latLng);
        marker.create();
        marker.setLighting(lighting);
        addMarker(marker);
        return marker;
    }
}
