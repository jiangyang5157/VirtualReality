package com.gmail.jiangyang5157.cardboard.vr;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.projection.AimRay;
import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.cardboard.scene.projection.Marker;
import com.gmail.jiangyang5157.cardboard.scene.Light;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.scene.projection.GLModel;
import com.gmail.jiangyang5157.cardboard.scene.projection.Panel;
import com.gmail.jiangyang5157.cardboard.scene.projection.TextField;
import com.gmail.jiangyang5157.cardboard.ui.CardboardOverlayView;
import com.gmail.jiangyang5157.tookit.app.DeviceUtils;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    boolean debug_camer_movement;

    private static final String TAG = "MainActivity ####";

    private Head head;
    private Light light;

    private Earth earth;
    private AimRay aimRay;
    private TextField textField;

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
        aimRay.destroy();
        textField.destroy();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glFrontFace(GLES20.GL_CCW);
        GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);
        headTransform.getHeadView(head.headView, 0);
        headTransform.getForwardVector(head.forward, 0);
        headTransform.getEulerAngles(head.eulerAngles, 0);
        headTransform.getRightVector(head.right, 0);
        headTransform.getUpVector(head.up, 0);

        if (debug_camer_movement) {
            float[] point = head.getCamera().getPosition().clone();
            Camera.forward(point, head.forward, Camera.MOVE_UNIT);
            if (earth.contain(point)) {
                head.getCamera().move(head.forward, Camera.MOVE_UNIT);
            }
        }

        AimIntersection intersection = null;
        float[] cameraPos = head.getCamera().getPosition();

        //
        if (intersection == null) {
            intersection = textField.intersect(head);
        }

        //
        if (intersection == null) {
            ArrayList<AimIntersection> markerIntersections = new ArrayList<AimIntersection>();
            for (final Marker mark : earth.getMarkers()) {
                AimIntersection markIntersection = mark.intersect(head);
                if (markIntersection != null) {
                    markerIntersections.add(markIntersection);
                }
            }
            Collections.sort(markerIntersections);
            if (markerIntersections.size() > 0) {
                intersection = markerIntersections.get(0);
            }
        }

        //
        if (intersection == null) {
            intersection = earth.intersect(head);
        }

        aimRay.intersectAt(intersection);
    }
    @Override
    public void onFinishFrame(Viewport viewport) {
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onCardboardTrigger() {
        final AimIntersection intersection = aimRay.getIntersection();
        if (intersection != null) {
            if (intersection.model instanceof Marker) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlayView.show3DToast(((Marker) intersection.model).name);
                    }
                });
                //test textfield movement
                textField.setVisible(true);
                textField.setPosition(head);
                debug_camer_movement = false;
            } else if (intersection.model instanceof Panel) {


                debug_camer_movement = false;
            } else {
                debug_camer_movement = !debug_camer_movement;
            }
        } else {
            debug_camer_movement = !debug_camer_movement;
        }
    }

    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Apply the eye transformation to the matrix.
        Matrix.multiplyMM(head.getCamera().view, 0, eye.getEyeView(), 0, head.getCamera().matrix, 0);

        // Set the position of the light
        Matrix.multiplyMV(light.lightPosInCameraSpace, 0, head.getCamera().view, 0, Light.LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Camera.Z_NEAR, Camera.Z_FAR);

        updateScene(head.getCamera().view, perspective);
        drawScene();
    }

    private void updateScene(float[] view, float[] perspective) {
        earth.update(view, perspective);
        aimRay.update(view, perspective);
        textField.update(view, perspective);
    }

    private void drawScene() {
        aimRay.draw();
        earth.draw();
        textField.draw();
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
        head = new Head();
        light = new Light();

        earth = new Earth(this);
        earth.setLighting(new Lighting() {
            @Override
            public float[] getLightPosInCameraSpace() {
                return light.lightPosInCameraSpace;
            }
        });
        earth.create();

        try {
            KmlLayer kmlLayer = new KmlLayer(earth, R.raw.example, getApplicationContext());
            kmlLayer.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        aimRay = new AimRay(this, earth);
        aimRay.create();

        textField = new TextField(this);
        textField.create("asdasdsdas123123232");
        textField.setVisible(false);
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
