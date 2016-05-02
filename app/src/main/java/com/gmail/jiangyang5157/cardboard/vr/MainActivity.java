package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.polygon.AimPoint;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Earth;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Marker;
import com.gmail.jiangyang5157.cardboard.scene.projection.Light;
import com.gmail.jiangyang5157.cardboard.scene.projection.Lighting;
import com.gmail.jiangyang5157.cardboard.scene.projection.GLModel;
import com.gmail.jiangyang5157.cardboard.ui.CardboardOverlayView;
import com.gmail.jiangyang5157.tookit.app.DeviceUtils;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity ####";

    private float[] headView = new float[16];
    private float[] forwardDir = new float[3];

    private Camera camera;
    private Light light;

    private Earth earth;
    private AimPoint aimPoint;

    private CardboardOverlayView overlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DeviceUtils.glesValidate(this, GLModel.GLES_VERSION_REQUIRED)) {
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
        earth.destroy();
        aimPoint.destroy();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);
        headTransform.getHeadView(headView, 0);
        headTransform.getForwardVector(forwardDir, 0);

        float[] pos = camera.getPosition();
        float distance = earth.getRadius() + Earth.DEFAULT_LAYER_ALTITUDE_AIMPOINT;
        Camera.forward(pos, forwardDir, distance);
        aimPoint.setPosition(pos);

        if (go) {
            camera.move(forwardDir, 0.7f);
        } else {
            camera.move(forwardDir, -0.7f);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onCardboardTrigger() {
//        overlayView.show3DToast("Earth\n" + " stacks/slices: (" + earth.getStacks() + "," + earth.getSlices() + ")");

//        int intersectCount = 0;
//        for (final Marker mark : earth.getMarkers()) {
//            double t = mark.intersect(camera.getPosition(), forwardDir);
//            if (t > 0) {
//                intersectCount++;
//            }
//        }
//        if (intersectCount > 0) {
//            final String intersectCountStr = String.valueOf(intersectCount);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    overlayView.show3DToast("intersectCount:" + intersectCountStr);
//                }
//            });
//        }

        go = !go;
    }

    boolean go = false;

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the matrix.
        Matrix.multiplyMM(camera.view, 0, eye.getEyeView(), 0, camera.matrix, 0);

        // Set the position of the light
        Matrix.multiplyMV(light.lightPosInCameraSpace, 0, camera.view, 0, light.LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Camera.Z_NEAR, Camera.Z_FAR);

        updateScene(camera.view, perspective);
        drawScene();
    }

    private void updateScene(float[] view, float[] perspective) {
        earth.update(view, perspective);
        aimPoint.update(view, perspective);
    }

    private void drawScene() {
        earth.draw();
        aimPoint.draw();
    }

    private float[] getModelPositionInEyeSpace(float[] model, float[] modelView) {
        float[] init = {0, 0, 0, 1.0f};
        float[] objPosition = new float[4];
        // Convert object space to matrix space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPosition, 0, modelView, 0, init, 0);
        return objPosition;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        camera = new Camera();
        light = new Light();

        earth = new Earth(this);
        earth.create();
        earth.setLighting(new Lighting() {
            @Override
            public float[] getLightPosInEyeSpace() {
                return light.lightPosInCameraSpace;
            }
        });

        try {
            KmlLayer kmlLayer = new KmlLayer(earth, R.raw.example, getApplicationContext());
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        aimPoint = new AimPoint(this);
        aimPoint.create();
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
}
