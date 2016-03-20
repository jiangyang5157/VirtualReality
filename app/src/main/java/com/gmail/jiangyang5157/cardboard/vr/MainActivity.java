package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.SceneObject;
import com.gmail.jiangyang5157.cardboard.scene.Sphere;
import com.gmail.jiangyang5157.cardboard.ui.CardboardOverlayView;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity";

    private CardboardOverlayView overlayView;

    private Vibrator vibrator;

    private static final float CAMERA_Z = 0.01f;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private float[] view = new float[16];
    private float[] camera = new float[16];
    private float[] headView = new float[16];



    private Sphere earth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        overlayView = (CardboardOverlayView) findViewById(R.id.cardboard_overlay_view);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");
        overlayView.show3DToast("onCardboardTrigger");
        vibrator.vibrate(50);
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);
        headTransform.getHeadView(headView, 0);
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        SceneObject.checkGLError("MainActivity - onDrawEye");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        // rotate
        Matrix.rotateM(earth.model, 0, 0.5f, 0, 1, 0);
        Matrix.rotateM(earth.model, 0, 0.5f, 1, 0, 0);
        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(earth.modelView, 0, view, 0, earth.model, 0);
        Matrix.multiplyMM(earth.modelViewProjection, 0, perspective, 0, earth.modelView, 0);

        drawScene();
    }

    private void drawScene() {
        earth.draw();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {
        Log.i(TAG, "onSurfaceChanged");
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        earth = new Sphere(this, 222, 222, 2);
        earth.createProgram();

        Matrix.setIdentityM(earth.model, 0);
        Matrix.translateM(earth.model, 0, 0, -5f, -2f);

        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
        // #### why never called? Move cleanup to onDestroy
        // https://developers.google.com/cardboard/android/latest/reference/com/google/vrtoolkit/cardboard/CardboardView.Renderer.html#onRendererShutdown()
        // https://github.com/raasun/cardboard/blob/master/src/com/google/vrtoolkit/cardboard/CardboardView.java
    }
}
