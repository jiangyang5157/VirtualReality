package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends TextureSphere {

    private static final int TEXTURE_DRAWABLE_RESOURCE = R.drawable.no_clouds_2k;
    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.earth_texture_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.earth_texture_fragment_shader;

    public static final float RADIUS = 4000f;
    private static final int STACKS = 32;
    private static final int SLICES = 32;

    private static final float MARKER_RADIUS = RADIUS / 50;
    private static final float MARKER_ALTITUDE = -1 * MARKER_RADIUS;
    private static final float CAMERA_ALTITUDE = (Math.abs(MARKER_ALTITUDE) + MARKER_RADIUS + Panel.DISTANCE + Ray.SPACE) * (MARKER_ALTITUDE > 0 ? 1 : -1);

    private ArrayList<Marker> markers;

    private Intersection.Clickable onMarkerClickListener;

    public Earth(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
        markers = new ArrayList<>();
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
        markers.clear();
        super.destroy();
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
    }

    private void addMarker(Marker marker) {
        markers.add(marker);
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle) {
        Log.i("####", kmlPlacemark.toString());
        LatLng latLng = markerUrlStyle.getPosition();
        Marker marker = new Marker(context, this);
        marker.setOnClickListener(onMarkerClickListener);
        marker.create(MARKER_RADIUS, latLng, MARKER_ALTITUDE);
        marker.setName(kmlPlacemark.getProperty("name"));
        marker.setDescription(kmlPlacemark.getProperty("description"));
        marker.setLighting(lighting);

        String objProperty = kmlPlacemark.getProperty("obj");
        if (objProperty != null) {
            ObjModel objModel = new ObjModel(context,
                    kmlPlacemark.getProperty("title"),
                    objProperty);
            objModel.setLighting(lighting);
            marker.setObjModel(objModel);
        }

        addMarker(marker);
        return marker;
    }

    public boolean contain(float[] point) {
        float[] position = getPosition();
        Vector positionVec = new Vector3d(position[0], position[1], position[2]);
        Vector pointVec = new Vector3d(point[0], point[1], point[2]);
        return pointVec.minus(positionVec).length() < getRadius() + CAMERA_ALTITUDE;
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isVisible) {
            return null;
        }
        Intersection ret = null;

        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        for (final Marker mark : markers) {
            Intersection intersection = mark.onIntersect(head);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }
        Collections.sort(intersections);
        if (intersections.size() > 0) {
            ret = intersections.get(0);
        } else {
            ret = super.onIntersect(head);
        }

        return ret;
    }

    public void setOnMarkerClickListener(Intersection.Clickable onClickListener) {
        this.onMarkerClickListener = onClickListener;
    }
}
