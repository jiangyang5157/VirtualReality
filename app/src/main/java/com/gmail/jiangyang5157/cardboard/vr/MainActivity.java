package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.model.AtomMap;
import com.gmail.jiangyang5157.cardboard.scene.model.AtomMarker;
import com.gmail.jiangyang5157.cardboard.scene.model.LayerChooserView;
import com.gmail.jiangyang5157.cardboard.scene.model.Dialog;
import com.gmail.jiangyang5157.cardboard.scene.model.ObjModel;
import com.gmail.jiangyang5157.cardboard.scene.model.Ray;
import com.gmail.jiangyang5157.cardboard.scene.model.Earth;
import com.gmail.jiangyang5157.cardboard.scene.model.GlModel;
import com.gmail.jiangyang5157.cardboard.scene.model.MarkerDetailView;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.android.base.DeviceUtils;
import com.gmail.jiangyang5157.tookit.base.data.IoUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.microedition.khronos.egl.EGLConfig;

// TODO: 8/11/2016 [WHY] E/libEGL: call to OpenGL ES API with no current context (logged once per thread)
public class MainActivity extends GvrActivity implements GvrView.StereoRenderer {
    private static final String TAG = "[MainActivity]";

    private Vibrator vibrator;

    private GvrView gvrView;

    private final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] lightPosInCameraSpace = new float[4];

    private Head head;

    private Ray ray;
    private Earth earth;
    private MarkerDetailView markerDetailView;
    private ObjModel objModel;
    private LayerChooserView layerChooserView;
    private AtomMap atomMap;

    private static final long TIME_DELTA_DOUBLE_CLICK = 200;
    private long lastTimeOnCardboardTrigger = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLContextClientVersion(3);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        if (Settings.DEBUG == 0) {
            // The transition view used to prompt the user to place their phone into a GVR viewer.
            gvrView.setTransitionViewEnabled(true);
        }
        gvrView.setOnCardboardBackButtonListener(
                this::onBackPressed);
        setGvrView(gvrView);

        head = new Head(getApplicationContext());
    }

    /**
     * To check if there is a new patch in the server, download if yes.
     */
    private void checkPatch() {
        File file = new File(AssetUtils.getAbsolutePath(getApplicationContext(), AssetUtils.getPatchPath()));
        AssetFile assetFile = new AssetFile(file, AssetUtils.getPatchUrl());
        new Downloader(assetFile, new Downloader.ResponseListener() {
            @Override
            public boolean onStart(java.util.Map<String, String> headers) {
                try {
                    long lastModifiedTime = AssetUtils.getLastPatchLastModifiedTime(getApplicationContext());
                    Log.d(TAG, "lastModifiedTime: " + lastModifiedTime);
                    String httpDate = headers.get("Last-Modified");
                    long httpDateTime = AssetUtils.getHttpDateTime(httpDate);
                    Log.d(TAG, "httpDateTime: " + httpDateTime);
                    if (lastModifiedTime < httpDateTime) {
                        return true; // continue to download patch
                    } else {
                        return false; // no need to upgrade patch
                    }
                } catch (ParseException e) {
                    Log.e(TAG, e.toString());
                    return false;
                }
            }

            @Override
            public void onComplete(AssetFile assetFile, java.util.Map<String, String> headers) {
                Log.d(TAG, "Patch onComplete: " + assetFile);
                InputStream in = null;
                try {
                    AssetUtils.setLastPatchLastModifiedTime(getApplicationContext(), AssetUtils.getHttpDateTime(headers.get("Last-Modified")));
                    in = new FileInputStream(assetFile.getFile());
                    IoUtils.unzip(in, new File(AppUtils.getProfilePath(getApplicationContext())), true);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(AssetFile assetFile, VolleyError volleyError) {
                Log.d(TAG, "onError:" + assetFile.getUrl() + " " + volleyError.toString());
            }
        }).start();
    }

    /**
     * To check if there is no exist resource, uncompress the default zip file which bundled with the apk
     * Usually this is only execute in the first time app launch
     */
    private void checkResource() {
        File directory = new File(AssetUtils.getAbsolutePath(getApplicationContext(), AssetUtils.DIRECTORY_STATIC));
        if (!directory.exists() || !directory.isDirectory()) {
            InputStream in = null;
            try {
                in = getAssets().open(AssetUtils.getPatchPath());
                IoUtils.unzip(in, new File(AppUtils.getProfilePath(getApplicationContext())), true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
//        Log.d(TAG, "onNewFrame");
        headTransform.getHeadView(head.getHeadView(), 0);
        headTransform.getForwardVector(head.getForward(), 0);
        headTransform.getUpVector(head.getUp(), 0);
        headTransform.getRightVector(head.getRight(), 0);
        headTransform.getQuaternion(head.getQuaternion(), 0);
        head.update();

        if (objModel != null) {
            objModel.update();
        }

        if (ray != null) {
//            Log.d(TAG, "getIntersection()");
//            Performance.getInstance().addBreakpoint();
            ray.setIntersections(getIntersection());
//            Performance.getInstance().addBreakpoint();
//            Performance.getInstance().printEvaluationInMilliseconds();
            ray.update();
        }
    }

    @Override
    public void onDrawEye(Eye eye) {
//        Log.d(TAG, "onDrawEye");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the matrix.
        Matrix.multiplyMM(head.getCamera().getView(), 0, eye.getEyeView(), 0, head.getCamera().getMatrix(), 0);
        // Set the position of the light
        Matrix.multiplyMV(lightPosInCameraSpace, 0, head.getCamera().getView(), 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        head.getCamera().setPerspective(eye.getPerspective(Camera.Z_NEAR, Camera.Z_FAR));
        updateScene(head.getCamera().getView(), head.getCamera().getPerspective());
        drawScene();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
//        Log.d(TAG, "onFinishFrame");
        if (earth != null) {
            if (!earth.isCreated()) {
                if (earth.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                    earth.prepare(ray);
                } else if (earth.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                    ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
                    shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.earth_uv_vertex_shader);
                    shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.earth_uv_fragment_shader);
                    earth.create(shaders);
                }
            }
        }

        if (atomMap != null) {
            if (!atomMap.isCreated()) {
                if (atomMap.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                    atomMap.prepare(ray);
                } else if (atomMap.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                    atomMap.create();
                }
            }
        }

        if (markerDetailView != null) {
            if (!markerDetailView.isCreated()) {
                if (markerDetailView.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                    markerDetailView.prepare(ray);
                } else if (markerDetailView.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                    ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
                    shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.panel_vertex_shader);
                    shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.panel_fragment_shader);
                    markerDetailView.create(shaders);
                    markerDetailView.setPosition(head.getCamera().getPosition(), head.getForward(), Dialog.DISTANCE, head.getQuaternion(), head.getUp(), head.getRight());
                }
            }

            if (objModel != null) {
                if (!objModel.isCreated()) {
                    if (objModel.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                        objModel.prepare(ray);
                    } else if (objModel.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
                        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.obj_color_vertex_shader);
                        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.obj_color_fragment_shader);
                        objModel.create(shaders);
                        objModel.setPosition(head.getCamera().getPosition(), head.getForward(), ObjModel.DISTANCE, head.getQuaternion(), head.getUp(), head.getRight());
                    }
                }
            }
        }

        if (layerChooserView != null) {
            if (!layerChooserView.isCreated()) {
                if (layerChooserView.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                    layerChooserView.prepare(ray);
                } else if (layerChooserView.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                    ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
                    shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.panel_vertex_shader);
                    shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.panel_fragment_shader);
                    layerChooserView.create(shaders);
                    layerChooserView.setPosition(head.getCamera().getPosition(), head.getForward(), Dialog.DISTANCE, head.getQuaternion(), head.getUp(), head.getRight());
                }
            }
        }
    }

    private void updateScene(float[] view, float[] perspective) {
        if (ray != null) {
            ray.update(view, perspective);
        }
        if (atomMap != null) {
            atomMap.update(view, perspective);
        }
        if (earth != null) {
            earth.update(view, perspective);
        }
        if (markerDetailView != null) {
            markerDetailView.update(view, perspective);
        }
        if (objModel != null) {
            objModel.update(view, perspective);
        }
        if (layerChooserView != null) {
            layerChooserView.update(view, perspective);
        }
    }

    private void drawScene() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);

        if (ray != null) {
            ray.draw();
        }

        if (atomMap != null) {
            atomMap.draw();
        }

        if (earth != null) {
            earth.draw();
        }

        if (markerDetailView != null) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            markerDetailView.draw();
            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        if (objModel != null) {
            objModel.draw();
        }

        if (layerChooserView != null) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            layerChooserView.draw();
            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    private RayIntersection getIntersection() {
        float[] headForward = head.getForward();
        Vector cameraPos_vec = new Vector3d(head.getCamera().getX(), head.getCamera().getY(), head.getCamera().getZ());
        Vector headForward_vec = new Vector3d(headForward[0], headForward[1], headForward[2]);
        Vector headForwardFrac_vec = new Vector3d(1.0 / headForward[0], 1.0 / headForward[1], 1.0 / headForward[2]);

        RayIntersection rayIntersection = null;
        if (layerChooserView != null) {
            rayIntersection = layerChooserView.getIntersection(cameraPos_vec, headForward_vec);
            if (rayIntersection == null) {
                if (layerChooserView.isCreated()) {
                    layerChooserView.destroy();
                    layerChooserView = null;
                }
            }
        }
        if (rayIntersection == null) {
            if (markerDetailView != null) {
                rayIntersection = markerDetailView.getIntersection(cameraPos_vec, headForward_vec);
                if (rayIntersection == null) {
                    if (markerDetailView.isCreated()) {
                        markerDetailView.destroy();
                        markerDetailView = null;
                        if (objModel != null) {
                            objModel.destroy();
                            objModel = null;
                        }
                    }
                }
            }
        }
        if (rayIntersection == null) {
            if (atomMap != null) {
                rayIntersection = atomMap.getIntersection(cameraPos_vec, headForwardFrac_vec, head.getHeadView());
            }
        }
        if (rayIntersection == null) {
            if (earth != null) {
                rayIntersection = earth.getIntersection(cameraPos_vec, headForward_vec);
            }
        }

        return rayIntersection;
    }

    private void onCardboardClick() {
        vibrator.vibrate(25);
        ray.resetSpinner();

        if (objModel != null && objModel.isCreated()) {
            destoryObjModel();
            return;
        }

        RayIntersection rayIntersection = ray.getRayIntersection();
        if (rayIntersection != null) {
            GlModel model = rayIntersection.getModel();
            GlModel.ClickListener onClickListener = model.getOnClickListener();
            if (onClickListener != null) {
                onClickListener.onClick(model);
            }
        }
    }

    private void onCardboardDoubleClick() {
        vibrator.vibrate(25);

        if (objModel != null && objModel.isCreated()) {
            destoryObjModel();
        }
        if (markerDetailView != null && markerDetailView.isCreated()) {
            destoryMarkerDetailView();
        }

        if (layerChooserView == null) {
            layerChooserView = new LayerChooserView(getApplicationContext());
            layerChooserView.setEventListener(layerChooserEventListener);
        } else if (layerChooserView.isCreated()) {
            destoryLayerChooserView();
        }
    }

    @Override
    public void onCardboardTrigger() {
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastTimeOnCardboardTrigger < TIME_DELTA_DOUBLE_CLICK) {
            lastTimeOnCardboardTrigger = 0; //  The next quick click time won't trigger double click
            onCardboardDoubleClick();
        } else {
            lastTimeOnCardboardTrigger = thisTime;
            onCardboardClick();
        }
    }

    private boolean newLayer(String urlLayer) {
        destoryMap();
        destoryLayerChooserView();
        destoryObjModel();
        destoryMarkerDetailView();

        atomMap = new AtomMap(getApplicationContext(), urlLayer);
        atomMap.setOnMarkerClickListener(onMarkerClickListener);
        atomMap.setMarkerLighting(() -> lightPosInCameraSpace);
        return true;
    }

    private GlModel.ClickListener onMarkerClickListener = new GlModel.ClickListener() {

        @Override
        public void onClick(GlModel model) {
            markerDetailView = new MarkerDetailView(getApplicationContext(), (AtomMarker) model);
            markerDetailView.setEventListener(markerDetailEventListener);
        }
    };

    private MarkerDetailView.Event markerDetailEventListener = new MarkerDetailView.Event() {
        @Override
        public void showObjModel(ObjModel model) {
            objModel = model;
        }
    };

    private LayerChooserView.Event layerChooserEventListener = new LayerChooserView.Event() {

        @Override
        public void onSelected(File file) {
            String fileName = file.getName();
            Log.d(TAG, "onSelected: " + fileName);
            if (fileName.equals(AssetUtils.getLastLayerFileName(getApplicationContext()))) {
                destoryLayerChooserView();
                return;
            }
            AssetUtils.setLastLayerFileName(getApplicationContext(), fileName);

            newLayer(AssetUtils.getLayerUrl(fileName));

            head.centerCameraPosition();
        }
    };

    private Ray.SpinnerListener spinnerListener = new Ray.SpinnerListener() {
        @Override
        public void onComplete() {
            onCardboardClick();
        }
    };

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        // Dark background so text shows up well.
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);

        ray = new Ray(getApplicationContext(), head);
        ray.setSpinnerListener(spinnerListener);
        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.ray_point_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.ray_point_fragment_shader);
        ray.create(shaders);

        earth = new Earth(getApplicationContext(), AssetUtils.getResourceUrl(AssetUtils.EARTH_TEXTURE_FILE_NAME));

        newLayer(AssetUtils.getLayerUrl(AssetUtils.getLastLayerFileName(getApplicationContext())));
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
    }

    @Override
    public void onRendererShutdown() { // TODO: 7/4/2016 [WHY] never get called
        Log.d(TAG, "onRendererShutdown");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // the integer pass to glesValidate() should be consistent with the glEsVersion in the Manifest
        if (!DeviceUtils.glesValidate(getApplicationContext(), Settings.GL_ES_VERSION)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_gles_version_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }

        checkResource();
        checkPatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (head != null) {
            head.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (head != null) {
            head.onPause();
        }
    }

    private void destoryMap() {
        if (atomMap != null) {
            atomMap.destroy();
            atomMap = null;
        }
    }

    private void destoryLayerChooserView() {
        if (layerChooserView != null) {
            layerChooserView.destroy();
            layerChooserView = null;
        }
    }

    private void destoryObjModel() {
        if (objModel != null) {
            objModel.destroy();
            objModel = null;
        }
    }

    private void destoryMarkerDetailView() {
        if (markerDetailView != null) {
            markerDetailView.destroy();
            markerDetailView = null;
        }
    }

    private void destoryEarth() {
        if (earth != null) {
            earth.destroy();
            earth = null;
        }
    }

    private void destoryRay() {
        if (ray != null) {
            ray.destroy();
            ray = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyApplication.getInstance().cancelPendingRequests();

        if (gvrView != null) {
            gvrView.shutdown();
        }

        destoryMap();
        destoryLayerChooserView();
        destoryObjModel();
        destoryMarkerDetailView();
        destoryEarth();
        destoryRay();
    }
}
