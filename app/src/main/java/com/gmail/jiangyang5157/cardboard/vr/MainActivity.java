package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Earth;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Marker;
import com.gmail.jiangyang5157.cardboard.scene.projection.Point;
import com.gmail.jiangyang5157.cardboard.scene.projection.ShaderHandle;
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
    private static final String TAG = "MainActivity";

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private final float Z_NEAR = 0.1f;
    private final float Z_FAR = 100.0f;

    private final float[] CAMERA_POS = new float[]{0.0f, 0.0f, 0.01f};

    private final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] lightPosInEyeSpace = new float[4];

    private float[] view = new float[16];
    private float[] camera = new float[16];
    private float[] headView = new float[16];

    private Point aimPoint;
    private Earth earth;

    private CardboardOverlayView overlayView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DeviceUtils.glesValidate(this, ShaderHandle.GLES_VERSION_REQUIRED)) {
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
        aimPoint.destroy();
        earth.destroy();
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

        for (final Marker mark : earth.getMarkers()) {
            if (isLookingAtObject(mark.model, mark.modelView)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlayView.show3DToast(mark.name + "\n" + mark.getCoordinate().toString());
                    }
                });
            }
        }
    }

    @Override
    public void onCardboardTrigger() {
        overlayView.show3DToast("Earth\n" + " stacks/slices: (" + earth.getStacks() + "," + earth.getSlices() + ")");
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

        updateScene(view, perspective);
        drawScene();
    }

    private void updateScene(float[] view, float[] perspective) {
        aimPoint.update(view, perspective, aimPoint.model, aimPoint.modelView, headView);//test
        earth.update(view, perspective);
    }

    private void drawScene() {
        aimPoint.draw(lightPosInEyeSpace);
        earth.draw(lightPosInEyeSpace);
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
        Matrix.setLookAtM(camera, 0, CAMERA_POS[0], CAMERA_POS[1], CAMERA_POS[2], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        aimPoint = new Point(this);
        aimPoint.create();

        earth = new Earth(this);
        earth.create();

        try {
            KmlLayer kmlLayer = new KmlLayer(earth, R.raw.simple, getApplicationContext());
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
