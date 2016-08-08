package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yang
 * @since 7/3/2016
 */
public class AtomMap extends GlModel implements Creation {
    private static final String TAG = "[AtomMap]";

    protected int creationState = STATE_BEFORE_PREPARE;

    private String urlKml;

    private AtomMarkers markers;

    public AtomMap(Context context, String urlKml) {
        super(context);
        this.urlKml = urlKml;
        markers = new AtomMarkers(context);
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

        ArrayMap<Integer, Integer> markerShaders = new ArrayMap<>();
        markerShaders.put(GLES20.GL_VERTEX_SHADER, AtomMarkers.RESOURCE_ID_VERTEX_SHADER);
        markerShaders.put(GLES20.GL_FRAGMENT_SHADER, AtomMarkers.RESOURCE_ID_FRAGMENT_SHADER);
        markers.create(markerShaders);

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
    }

    @Override
    protected void bindHandles() {
        // do nothing
    }

    @Override
    protected void buildData() {
        // do nothing
    }

    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForwardFrac_vec, final float[] headView) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        return markers.getIntersection(cameraPos_vec, headForwardFrac_vec, headView);
    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    public void setMarkerLighting(Lighting lighting) {
        markers.setLighting(lighting);
    }

    public void setOnMarkerClickListener(ClickListener onClickListener) {
        markers.setOnClickListener(onClickListener);
    }

    public AtomMarkers getAtomMarkers() {
        return markers;
    }

    @Override
    public void update(float[] view, float[] perspective) {
//        Log.d(TAG, "update markers"); //
//        Performance.getInstance().addBreakpoint();
        markers.update(view, perspective);
//        Performance.getInstance().addBreakpoint();
//        Performance.getInstance().printEvaluationInMilliseconds();
    }

    @Override
    public void draw() {
//        Log.d(TAG, "draw markers"); // 460-12ms
//        Performance.getInstance().addBreakpoint();
        markers.draw();
//        Performance.getInstance().addBreakpoint();
//        Performance.getInstance().printEvaluationInMilliseconds();
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        markers.destroy();
        super.destroy();
    }
}
