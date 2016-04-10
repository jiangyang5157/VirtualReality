package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.scene.GlEsModel;
import com.gmail.jiangyang5157.cardboard.scene.Icosphere;
import com.gmail.jiangyang5157.cardboard.scene.TextureSphere;
import com.gmail.jiangyang5157.cardboard.ui.CardboardOverlayView;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity";
    private boolean debug = false;

    private static final float CAMERA_Z = 0.01f;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private float[] view = new float[16];
    private float[] camera = new float[16];
    private float[] headView = new float[16];

    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] {5.0f, 0.0f, 0.0f, 1.0f};
    private float[] lightPosInEyeSpace = new float[4];

    private TextureSphere tsEarth;
    private Icosphere icosphere;

    private CardboardOverlayView overlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (!GlEsModel.isSupportGlEsVersion(this)) {
            Toast.makeText(this, getString(R.string.error_gles_version_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }

        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        overlayView = (CardboardOverlayView) findViewById(R.id.cardboard_overlay_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tsEarth.destroy();
        icosphere.destroy();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);
        headTransform.getHeadView(headView, 0);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        if (isLookingAtObject(icosphere.model, icosphere.modelView)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    overlayView.show3DToast("" + icosphere.getClass().getSimpleName() + " - Vertex counts = " + icosphere.getVertexCounts());
                }
            });
        }
    }

    @Override
    public void onCardboardTrigger() {
        test(debug);
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        Matrix.rotateM(tsEarth.model, 0, 0.1f, 0, 1, 0);
        Matrix.multiplyMM(tsEarth.modelView, 0, view, 0, tsEarth.model, 0);
        Matrix.multiplyMM(tsEarth.modelViewProjection, 0, perspective, 0, tsEarth.modelView, 0);

        Matrix.rotateM(icosphere.model, 0, 0.2f, 0, 1, 0);
        Matrix.multiplyMM(icosphere.modelView, 0, view, 0, icosphere.model, 0);
        Matrix.multiplyMM(icosphere.modelViewProjection, 0, perspective, 0, icosphere.modelView, 0);

        drawScene();
    }

    private void drawScene() {
        tsEarth.draw(lightPosInEyeSpace);
        icosphere.draw(lightPosInEyeSpace);
    }

    private boolean isLookingAtObject(float[] model, float[] modelView) {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        tsEarth = new TextureSphere(this, R.raw.earth_vertex, R.raw.earth_fragment, 100, 100, 10, R.drawable.no_ice_clouds_mts_4k);
        tsEarth.create();

        icosphere = new Icosphere(this, R.raw.icosphere_vertex, R.raw.icosphere_fragment, 1, 2, new float[]{1.0f, 0.5f, 0.f, 1.0f});
        icosphere.create();

        test(debug);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
    }

    /**
     * #### why it never get called?
     * https://github.com/raasun/cardboard/blob/master/src/com/google/vrtoolkit/cardboard/CardboardView.java
     */
    @Override
    public void onRendererShutdown() {
        Log.d(TAG, "onRendererShutdown");
    }

    @Deprecated
    private void test(boolean debug) {
        if (debug) {
            Matrix.setIdentityM(tsEarth.model, 0);
            Matrix.translateM(tsEarth.model, 0, 0.0f, 0.0f, -20.0f);
            Matrix.setIdentityM(icosphere.model, 0);
            Matrix.translateM(icosphere.model, 0, 0.0f, -5.0f, -5.0f);
        } else {
            Matrix.setIdentityM(tsEarth.model, 0);
            Matrix.translateM(tsEarth.model, 0, 0.0f, 0.0f, 0.0f);
            Matrix.setIdentityM(icosphere.model, 0);
            Matrix.translateM(icosphere.model, 0, 0.0f, -5.0f, -5.0f);
        }
        this.debug = !this.debug;
    }
}
