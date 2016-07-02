package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.data.buffer.BufferUtils;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends UvSphere implements Creation {
    private static final String TAG = "[Earth]";

    public static final float RADIUS = 4000f;

    protected final int[] buffers = new int[3];
    protected final int[] texBuffers = new int[1];

    private String urlTexture;
    private String urlKml;
    private ArrayList<Marker> markers;

    protected int creationState = STATE_BEFORE_PREPARE;

    protected Lighting markerLighting;
    protected Lighting markerObjModelLighting;

    private GlModel.ClickListener onMarkerClickListener;

    public Earth(Context context, String urlKml, String urlTexture) {
        super(context, 180, 180);
        this.urlKml = urlKml;
        this.urlTexture = urlTexture;
        markers = new ArrayList<>();
        setRadius(RADIUS);
    }

    public boolean checkPreparation() {
        File fileKml = new File(Constant.getAbsolutePath(context, Constant.getPath(urlKml)));
        File fileTexture = new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture)));
        return fileKml.exists() && fileTexture.exists();
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
                            public boolean onStart(Map<String, String> headers) {
                                return true;
                            }

                            @Override
                            public void onComplete(Map<String, String> headers) {
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

                    File fileTexture = new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture)));
                    if (!fileTexture.exists()) {
                        Log.d(TAG, fileTexture.getAbsolutePath() + " not exist.");
                        new Downloader(urlTexture, fileTexture, new Downloader.ResponseListener() {
                            @Override
                            public boolean onStart(Map<String, String> headers) {
                                return true;
                            }

                            @Override
                            public void onComplete(Map<String, String> headers) {
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

    @Override
    public void create(ArrayMap<Integer, Integer> shaders) {
        creationState = STATE_CREATING;
        super.create(shaders);

        ArrayMap<Integer, Integer> markerShaders = new ArrayMap<>();
        markerShaders.put(GLES20.GL_VERTEX_SHADER, R.raw.sphere_color_vertex_shader);
        markerShaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.sphere_color_fragment_shader);

        setCreated(true);
        setVisible(true);

        for (Marker marker : markers) {
            marker.create(markerShaders);
        }

        creationState = STATE_BEFORE_CREATE;
    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    @Override
    public void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * BufferUtils.BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        FloatBuffer texturesBuffer = ByteBuffer.allocateDirect(textures.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BufferUtils.BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);
    }

    @Override
    public void bindTexBuffers() {
        InputStream in = null;
        try {
            in = new FileInputStream(new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture))));

            GLES20.glGenTextures(1, texBuffers, 0);
            if (texBuffers[0] == 0) {
                throw new RuntimeException("Gl Error - Unable to create texture.");
            } else {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                final Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                bitmap.recycle();
            }
        } catch (IOException e) {
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

    @Override
    protected void bindHandles() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
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

        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glUniform1i(texIdHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        GlUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteTextures(texBuffers.length, texBuffers, 0);

        destoryMarks();
    }

    public void destoryMarks() {
        for (Marker marker : markers) {
            marker.destroy();
        }
        markers.clear();
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
    }

    private void addMarker(Marker marker) {
        markers.add(marker);
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle, int markerColorInteger) {
        LatLng latLng = markerUrlStyle.getPosition();
        Marker marker = new Marker(context);
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

    public boolean contain(float[] point) {
        return Sphere.contain(radius + Camera.ALTITUDE, getPosition(), point);
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        Intersection ret;

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
        } else {
            ret = super.onIntersect(head);
        }

        return ret;
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
}
