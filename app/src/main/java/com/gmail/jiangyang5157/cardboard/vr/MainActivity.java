package com.gmail.jiangyang5157.cardboard.vr;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.projection.Model;
import com.gmail.jiangyang5157.cardboard.scene.projection.Ray;
import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.cardboard.scene.projection.Marker;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.scene.projection.GLModel;
import com.gmail.jiangyang5157.cardboard.scene.projection.MarkerDialog;
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

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer, SensorEventListener {

    boolean debug_camer_movement;

    private static final String TAG = "MainActivity ####";

    private Head head;

    public static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    public float[] lightPosInCameraSpace = new float[4];

    private Earth earth;
    private Ray ray;
    private MarkerDialog markerDialog;

    private CardboardOverlayView overlayView;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linerAcceleration;
    private Sensor mageticField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DeviceUtils.glesValidate(this, GLModel.GLES_VERSION_REQUIRED)) {
            Toast.makeText(this, getString(R.string.error_gles_version_not_supported), Toast.LENGTH_SHORT).show();
            finish();
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linerAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mageticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRestoreGLStateEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        overlayView = (CardboardOverlayView) findViewById(R.id.cardboard_overlay_view);

        head = new Head();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (markerDialog != null) {
            markerDialog.destroy();
        }
        if (earth != null) {
            earth.destroy();
        }
        if (ray != null) {
            ray.destroy();
        }
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(head.headView, 0);
        headTransform.getForwardVector(head.forward, 0);
        headTransform.getEulerAngles(head.eulerAngles, 0);
        headTransform.getUpVector(head.up, 0);
        headTransform.getRightVector(head.right, 0);

        if (debug_camer_movement) {
            head.adjustPosition(earth);
        }

        checkDialog();

        ray.setIntersection(getIntersection());
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    private void checkDialog() {
        if (markerDialog == null || markerDialog.getMarker() == null || markerDialog.isProgramCreated()) {
            return;
        }
        markerDialog.create();
        markerDialog.setPosition(head.getCamera().getPosition(), head.forward, head.up, head.right, head.eulerAngles);
    }

    private Intersection getIntersection() {
        Intersection ret = null;
        if (markerDialog != null) {
            ret = markerDialog.intersect(head);
            if (ret == null) {
                // TODO: 5/14/2016 earth went black after destroy - texture confuse
//                  markerDialog.destroy();
                markerDialog = null;
            }
        }
        if (ret == null) {
            if (earth != null) {
                ret = earth.intersect(head);
            }
        }
        return ret;
    }

    @Override
    public void onCardboardTrigger() {
        final Intersection intersection = ray.getIntersection();
        if (intersection != null) {
            if (intersection.getModel() instanceof Model.Clickable) {
                ((Model.Clickable) intersection.getModel()).onClick(intersection.getModel());
                debug_camer_movement = false;
            } else {
                debug_camer_movement = !debug_camer_movement;
            }
        }
    }

    private Model.Clickable markerOnClickListener = new Model.Clickable() {
        @Override
        public void onClick(Model model) {
            markerDialog = new MarkerDialog(getApplicationContext());
            markerDialog.setMarker((Marker) model);
        }
    };

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the matrix.
        Matrix.multiplyMM(head.getCamera().view, 0, eye.getEyeView(), 0, head.getCamera().matrix, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInCameraSpace, 0, head.getCamera().view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Camera.Z_NEAR, Camera.Z_FAR);

        updateScene(head.getCamera().view, perspective);
        drawScene();
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

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    private float[] getModelPositionInCameraSpace(float[] model, float[] modelView) {
        float[] init = {0, 0, 0, 1.0f};
        float[] objPosition = new float[4];
        // Convert object space to matrix space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, head.headView, 0, model, 0);
        Matrix.multiplyMV(objPosition, 0, modelView, 0, init, 0);
        return objPosition;
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        ray = new Ray(this);
        ray.create();

        earth = new Earth(this);
        earth.setOnMarkerClickListener(markerOnClickListener);
        earth.setLighting(new Lighting() {
            @Override
            public float[] getLightPosInCameraSpace() {
                return lightPosInCameraSpace;
            }
        });
        earth.create();
        try {
            KmlLayer kmlLayer = new KmlLayer(earth, R.raw.example, getApplicationContext());
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.d(TAG, "onSurfaceChanged");
    }

    @Override
    public void onRendererShutdown() {
        Log.d(TAG, "onRendererShutdown");
        // TODO: 5/14/2016 WHY: this callback is never get called
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)) {
            throw new UnsupportedOperationException("Accelerometer not supported");
        }
        if (!sensorManager.registerListener(this, linerAcceleration, SensorManager.SENSOR_DELAY_NORMAL)) {
            throw new UnsupportedOperationException("LinerAcceleration not supported");
        }
        if (!sensorManager.registerListener(this, mageticField, SensorManager.SENSOR_DELAY_NORMAL)) {
            throw new UnsupportedOperationException("MageticField not supported");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            head.setLinerAccelerationValues(event.values);
            Log.i(TAG, "LinerAcceleration: " + event.values[0] + "," + event.values[1] + "," + event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
