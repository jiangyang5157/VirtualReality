package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Earth;
import com.gmail.jiangyang5157.cardboard.scene.polygon.Placemark;
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

        for (final Placemark mark : earth.getPlacemarks()) {
            if (isLookingAtObject(mark.model, mark.modelView)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlayView.show3DToast(mark.getName() + "\n" + "r=" + mark.getRadius() + "\n" + mark.getCoordinate().toString());
                    }
                });
            }
        }
    }

    @Override
    public void onCardboardTrigger() {
        overlayView.show3DToast("Earth\n" + "r=" + earth.getRadius() + "\n" + " stacks=" + earth.getStacks() + " slices=" + earth.getSlices());
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
        earth.update(view, perspective);
    }

    private void drawScene() {
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

        earth = new Earth(this, 10f, R.drawable.no_clouds_2k);
        earth.create();

        earth.addPlacemark(0, 0, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "");
        earth.addPlacemark(0, 90, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "");
        earth.addPlacemark(0, 180, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "");
        earth.addPlacemark(0, -90, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "");
        earth.addPlacemark(90, 0, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "North Pole");
        earth.addPlacemark(-90, 0, 0.7f, new float[]{0.0f, 0.8f, 0.0f, 1.0f}, "South Pole");

        earth.addPlacemark(-36.84845f, 174.76192f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Auckland");
        earth.addPlacemark(-41.28646f, 174.77623f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Wellington");
        earth.addPlacemark(-33.86748f, 151.20699f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Sydney");
        earth.addPlacemark(52.52000f, 13.40495f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Berlin");
        earth.addPlacemark(38.90719f, -77.03687f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Washington");
        earth.addPlacemark(39.90421f, 116.40739f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Beijing");
        earth.addPlacemark(55.75582f, 37.6173f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Moscow");
        earth.addPlacemark(51.50735f, -0.12775f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "London");
        earth.addPlacemark(48.85661f, 2.35222f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Paris");
        earth.addPlacemark(37.56653f, 126.97796f, 0.15f, new float[]{0.8f, 0.0f, 0.0f, 1.0f}, "Seoul");


//        KmlLayer kmlLayer = null;
//        try {
//            kmlLayer = new KmlLayer(R.raw.simple, getApplicationContext());
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Iterable<KmlPlacemark> placemarks = kmlLayer.getPlacemarks();
//        Log.i("####", "" + placemarks.iterator().next().toString());
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
