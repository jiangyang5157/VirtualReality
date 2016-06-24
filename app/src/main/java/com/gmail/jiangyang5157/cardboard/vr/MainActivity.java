package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.app.VolleyApplication;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.projection.ObjModel;
import com.gmail.jiangyang5157.cardboard.scene.projection.Panel;
import com.gmail.jiangyang5157.cardboard.scene.projection.Ray;
import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.cardboard.scene.projection.Marker;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.scene.projection.GlModel;
import com.gmail.jiangyang5157.cardboard.scene.projection.MarkerDialog;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.app.DeviceUtils;
import com.gmail.jiangyang5157.tookit.data.io.IoUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends GvrActivity implements GvrView.StereoRenderer {
    private static final String TAG = "[MainActivity]";

    private Head head;

    private final float[] LIGHT_POS_IN_CAMERA_SPACE = new float[]{0.0f, Panel.DISTANCE / 10, 0.0f, 1.0f};
    private final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] lightPosInCameraSpace = new float[4];

    private Ray ray;

    private Earth earth;
    private MarkerDialog markerDialog;
    private ObjModel objModel;

    private GvrView gvrView;

    private static final long TIME_DELTA_DOUBLE_CLICK = 200;
    private long lastTimeOnCardboardTrigger = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gvrView = (GvrView) findViewById(R.id.gvr_view);
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);

        gvrView.setRenderer(this);
        if (Constant.DEBUG != 0) {
            // The transition view used to prompt the user to place their phone into a GVR viewer.
            gvrView.setTransitionViewEnabled(true);
        }

        gvrView.setOnCardboardBackButtonListener(
                new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
        setGvrView(gvrView);

        head = new Head(getApplicationContext());
    }

    /**
     * To check if there is a new patch in the server, download if yes.
     */
    private void checkPatch() {
        final File patchFile = new File(Constant.getAbsolutePath(getApplicationContext(), Constant.getPatchPath()));
        new Downloader(Constant.getPatchUrl(), patchFile, new Downloader.ResponseListener() {
            @Override
            public boolean onStart(Map<String, String> headers) {
                try {
                    long lastModifiedTime = Constant.getLastPatchLastModifiedTime(getApplicationContext());
                    long httpDateTime = Constant.getHttpDateTime(headers.get("Last-Modified"));
                    Log.d(TAG, "lastModifiedTime/httpDateTime: " + lastModifiedTime + "," + httpDateTime);
                    if (lastModifiedTime < httpDateTime) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onComplete(Map<String, String> headers) {
                InputStream in = null;
                try {
                    Constant.setLastPatchLastModifiedTime(getApplicationContext(), Constant.getHttpDateTime(headers.get("Last-Modified")));
                    in = new FileInputStream(patchFile);
                    IoUtils.unzip(in, new File(AppUtils.getProfilePath(getApplicationContext())), true);
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
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

            @Override
            public void onError(String url, VolleyError volleyError) {
                Log.d(TAG, "onError:" + url + " " + volleyError.toString());
            }
        });
    }

    /**
     * To check if there is no exist resource, uncompress the default zip file which bundled with the apk
     * Usually this is only execute in the first time app launch
     */
    private void checkResource() {
        File directory = new File(Constant.getAbsolutePath(getApplicationContext(), Constant.DIRECTORY_STATIC));
        if (!directory.exists() || !directory.isDirectory()) {
            InputStream in = null;
            try {
                in = getAssets().open(Constant.getPatchPath());
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

    private Intersection getIntersection() {
        Intersection ret = null;

        if (markerDialog != null) {
            ret = markerDialog.onIntersect(head);
            if (ret == null) {
                if (markerDialog.isCreated()) {
                    markerDialog.destroy();
                    markerDialog = null;
                    if (objModel != null) {
                        objModel.destroy();
                        objModel = null;
                    }
                }
            }
        }

        if (ret == null) {
            if (earth != null) {
                ret = earth.onIntersect(head);
            }
        }

        return ret;
    }

    @Override
    public void onCardboardTrigger() {
        long thisTime = System.currentTimeMillis();
        if (thisTime - lastTimeOnCardboardTrigger < TIME_DELTA_DOUBLE_CLICK) {
            // no interpupillary distance will be applied to the eye transformations
            // automatic distortion correction will not take place
            // field of view and perspective may look off especially if the view is not set to fullscreen
//            gvrView.setVRModeEnabled(!gvrView.getVRMode());

            // TODO: 6/24/2016 change kml test
            String kmlFileName = Constant.getLastKmlFileName(getApplicationContext());
            if (kmlFileName.equals("example.kml")) {
                Constant.setLastKmlFileName(getApplicationContext(), "example2.kml");
            } else {
                Constant.setLastKmlFileName(getApplicationContext(), "example.kml");
            }
            createEarth(Constant.getKmlUrl(Constant.getLastKmlFileName(getApplicationContext())));

            lastTimeOnCardboardTrigger = 0;
            return;
        } else {
            lastTimeOnCardboardTrigger = thisTime;
        }

        if (objModel != null && objModel.isCreated()) {
            objModel.destroy();
            objModel = null;
            return;
        }

        final Intersection intersection = ray.getIntersection();
        if (intersection != null) {
            if (intersection.getModel() instanceof Intersection.Clickable) {
                ((Intersection.Clickable) intersection.getModel()).onClick(intersection.getModel());
            }
        }
    }

    private Intersection.Clickable markerOnClickListener = new Intersection.Clickable() {

        @Override
        public void onClick(Model model) {
            markerDialog = new MarkerDialog(getApplicationContext(), (Marker) model);
            markerDialog.setEventListener(markerDialogEventListener);
        }
    };

    private MarkerDialog.Event markerDialogEventListener = new MarkerDialog.Event() {
        @Override
        public void showObjModel(ObjModel model) {
            objModel = model;
        }
    };

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(head.getHeadView(), 0);
        headTransform.getForwardVector(head.getForward(), 0);
        headTransform.getUpVector(head.getUp(), 0);
        headTransform.getRightVector(head.getRight(), 0);
        headTransform.getQuaternion(head.getQuaternion(), 0);

        head.adjustPosition(earth);

        if (earth != null) {
            if (!earth.isCreated()) {
                if (earth.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                    earth.prepare(ray);
                } else if (earth.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                    earth.create();
                }
            }
        }

        if (markerDialog != null) {
            if (!markerDialog.isCreated()) {
                markerDialog.create();
                markerDialog.setPosition(head.getCamera().getPosition(), head.getForward(), head.getQuaternion(), head.getUp(), head.getRight());
            }

            if (objModel != null) {
                if (!objModel.isCreated()) {
                    if (objModel.getCreationState() == Creation.STATE_BEFORE_PREPARE) {
                        objModel.prepare(ray);
                    } else if (objModel.getCreationState() == Creation.STATE_BEFORE_CREATE) {
                        objModel.create();
                        objModel.setPosition(head.getCamera().getPosition(), head.getForward(), head.getQuaternion());
                    }
                }
            }
        }

        ray.setIntersection(getIntersection());
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the matrix.
        Matrix.multiplyMM(head.getCamera().getView(), 0, eye.getEyeView(), 0, head.getCamera().getMatrix(), 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInCameraSpace, 0, head.getCamera().getView(), 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        float[] perspective = eye.getPerspective(Camera.Z_NEAR, Camera.Z_FAR);

        updateScene(head.getCamera().getView(), perspective);
        drawScene();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    private void updateScene(float[] view, float[] perspective) {
        if (ray != null) {
            ray.update(view, perspective);
        }
        if (earth != null) {
            earth.update(view, perspective);
        }
        if (markerDialog != null) {
            markerDialog.update(view, perspective);
        }
        if (objModel != null) {
            objModel.update(view, perspective);
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
        if (earth != null) {
            earth.draw();
        }

        if (markerDialog != null) {
            GLES20.glDisable(GLES20.GL_CULL_FACE);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            markerDialog.draw();
            GLES20.glDisable(GLES20.GL_BLEND);

            GLES20.glEnable(GLES20.GL_CULL_FACE);
        }

        if (objModel != null) {
            objModel.draw();
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    private float[] getModelPositionInCameraSpace(float[] model, float[] modelView) {
        float[] init = {0, 0, 0, 1.0f};
        float[] objPosition = new float[4];
        // Convert object space to matrix space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, head.getHeadView(), 0, model, 0);
        Matrix.multiplyMV(objPosition, 0, modelView, 0, init, 0);
        return objPosition;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        // Dark background so text shows up well.
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);

        ray = new Ray(getApplicationContext());
        ray.create();

        createEarth(Constant.getKmlUrl(Constant.getLastKmlFileName(getApplicationContext())));
    }

    private void createEarth(String urlKml) {
        destoryEarth();

        earth = new Earth(getApplicationContext(), urlKml,
                Constant.getResourceUrl(Constant.EARTH_TEXTURE_FILE_NAME));
        earth.setOnMarkerClickListener(markerOnClickListener);
        earth.setMarkerLighting(new Lighting() {
            @Override
            public float[] getLightPosInCameraSpace() {
                return lightPosInCameraSpace;
            }
        });
        earth.setMarkerObjModelLighting(new Lighting() {
            @Override
            public float[] getLightPosInCameraSpace() {
                return LIGHT_POS_IN_CAMERA_SPACE;
            }
        });
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
    }

    @Override
    public void onRendererShutdown() {
        Log.d(TAG, "onRendererShutdown");
        // WHY: never get called
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!DeviceUtils.glesValidate(getApplicationContext(), GlModel.GLES_VERSION_REQUIRED)) {
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

    private void destoryEarth() {
        if (objModel != null) {
            objModel.destroy();
            objModel = null;
        }
        if (markerDialog != null) {
            markerDialog.destroy();
            markerDialog = null;
        }
        if (earth != null) {
            earth.destroy();
            earth = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyApplication.getInstance().cancelPendingRequests();

        if (gvrView != null) {
            gvrView.shutdown();
        }

        if (ray != null) {
            ray.destroy();
            ray = null;
        }

        destoryEarth();
    }
}
