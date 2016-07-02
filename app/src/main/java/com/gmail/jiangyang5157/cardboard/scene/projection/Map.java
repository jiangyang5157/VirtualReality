package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 7/2/2016
 */
public class Map extends GlModel implements Creation, GlModel.IntersectListener {
    private static final String TAG = "[Map]";

    protected int creationState = STATE_BEFORE_PREPARE;

    private String urlKml;

    private ArrayList<Marker> markers;

    protected Lighting markerLighting;
    protected Lighting markerObjModelLighting;

    private GlModel.ClickListener onMarkerClickListener;

    public Map(Context context, String urlKml) {
        super(context);
        this.urlKml = urlKml;
        markers = new ArrayList<>();
    }

    public boolean checkPreparation() {
        File fileKml = new File(Constant.getAbsolutePath(context, Constant.getPath(urlKml)));
        return fileKml.exists();
    }

    public void prepare(final Ray ray) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                creationState = STATE_PREPARING;
                ray.addBusy();

                if (checkPreparation()) {
                    final File fileKml = new File(Constant.getAbsolutePath(context, Constant.getPath(urlKml)));
                    prepareKml(fileKml);
                    ray.subtractBusy();
                    creationState = STATE_BEFORE_CREATE;
                } else {
                    final File fileKml = new File(Constant.getAbsolutePath(context, Constant.getPath(urlKml)));
                    if (!fileKml.exists()) {
                        Log.d(TAG, fileKml.getAbsolutePath() + " not exist.");
                        new Downloader(urlKml, fileKml, new Downloader.ResponseListener() {
                            @Override
                            public boolean onStart(java.util.Map<String, String> headers) {
                                return true;
                            }

                            @Override
                            public void onComplete(java.util.Map<String, String> headers) {
                                prepareKml(fileKml);

                                if (checkPreparation()) {
                                    ray.subtractBusy();
                                    creationState = STATE_BEFORE_CREATE;
                                }
                            }

                            @Override
                            public void onError(String url, VolleyError volleyError) {
                                AppUtils.buildToast(context, url + " " + volleyError.toString());
                                ray.subtractBusy();
                                creationState = STATE_BEFORE_PREPARE;
                            }
                        });
                    }
                }
            }
        });
    }

    private void prepareKml(File fileKml) {
        InputStream in = null;
        try {
            in = new FileInputStream(fileKml);
            KmlLayer kmlLayer = new KmlLayer(this, in, context);
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void create() {
        creationState = STATE_CREATING;

        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.sphere_color_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.sphere_color_fragment_shader);
        for (Marker marker : markers) {
            marker.create(shaders);
        }

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
    }

    @Override
    protected void bindHandles() {

    }

    @Override
    protected void buildData() {

    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        for (Marker marker : markers) {
            marker.draw();
        }

        GlUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public void update(float[] view, float[] perspective) {
        for (Marker marker : markers) {
            marker.update(view, perspective);
        }
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
    }

    private void addMarker(Marker marker) {
        markers.add(marker);
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle, int markerColorInteger) {
        LatLng latLng = markerUrlStyle.getPosition();
        AtomMarker marker = new AtomMarker(context);
        if (markerColorInteger != 0) {
            marker.setColor(markerColorInteger);
        }
        marker.setOnClickListener(onMarkerClickListener);
        marker.setLocation(latLng, Marker.ALTITUDE);
        marker.setName(kmlPlacemark.getProperty("name"));
        marker.setDescription(kmlPlacemark.getProperty("description"));
        marker.setLighting(markerLighting);

        String objProperty = kmlPlacemark.getProperty("obj");
        if (objProperty != null) {
            ObjModel objModel = new ObjModel(context, kmlPlacemark.getProperty("title"), objProperty);
            objModel.setLighting(markerObjModelLighting);
            marker.setObjModel(objModel);
        }

        addMarker(marker);
        return marker;
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        Intersection ret = null;

        ArrayList<Intersection> intersections = new ArrayList<>();
        for (Marker marker : markers) {
            Intersection intersection = marker.onIntersect(head);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }
        Collections.sort(intersections);
        if (intersections.size() > 0) {
            ret = intersections.get(0);
        }

        return ret;
    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    public void setOnMarkerClickListener(GlModel.ClickListener onClickListener) {
        this.onMarkerClickListener = onClickListener;
    }

    public void setMarkerObjModelLighting(Lighting markerObjModelLighting) {
        this.markerObjModelLighting = markerObjModelLighting;
    }

    public void setMarkerLighting(Lighting markerLighting) {
        this.markerLighting = markerLighting;
    }

    public void destoryMarks() {
        for (Marker marker : markers) {
            marker.destroy();
        }
        markers.clear();
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        destoryMarks();
        super.destroy();
    }
}
