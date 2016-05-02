package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.scene.polygon.AimPoint;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Earth;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Marker;
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

    private final float Z_NEAR = 0.1f;
    private final float Z_FAR = 100.0f;
    private final float[] CAMERA_POSITION = new float[]{0.0f, 0.0f, 0.0f};
    private final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private float[] lightPosInCameraSpace = new float[4];

    private float[] view = new float[16];
    private float[] camera = new float[16];
    private float[] headView = new float[16];
    private float[] forwardDirection = new float[3];

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
        headTransform.getForwardVector(forwardDirection, 0);

        float dis = earth.getRadius() + Earth.LAYER_ALTITUDE_AIMPOINT;
        float[] pos = new float[]{forwardDirection[0] * dis, forwardDirection[1] * dis, forwardDirection[2] * dis};
        aimPoint.setPosition(pos);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onCardboardTrigger() {
//        overlayView.show3DToast("Earth\n" + " stacks/slices: (" + earth.getStacks() + "," + earth.getSlices() + ")");

        int intersectCount = 0;
        for (final Marker mark : earth.getMarkers()) {
            double t = mark.intersect(CAMERA_POSITION, forwardDirection);
            if (t > 0) {
                intersectCount++;
            }
        }
        final String intersectCountStr = String.valueOf(intersectCount);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                overlayView.show3DToast("intersectCount:" + intersectCountStr);
            }
        });
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInCameraSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        updateScene(view, perspective);
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
        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPosition, 0, modelView, 0, init, 0);
        return objPosition;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        Matrix.setLookAtM(camera, 0, CAMERA_POSITION[0], CAMERA_POSITION[1], CAMERA_POSITION[2], 0.0f, 0.0f, -0.1f, 0.0f, 1.0f, 0.0f);

        earth = new Earth(this);
        earth.create();
        earth.setLighting(new Lighting() {
            @Override
            public float[] getLightPosInEyeSpace() {
                return lightPosInCameraSpace;
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
