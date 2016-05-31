package com.gmail.jiangyang5157.cardboard.vr;

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
import com.gmail.jiangyang5157.cardboard.scene.projection.ObjModel;
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

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final String TAG = "MainActivity ####";

    private Head head;

    public static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    public float[] lightPosInCameraSpace = new float[4];

    private Earth earth;
    private Ray ray;
    private MarkerDialog markerDialog;
    private ObjModel objModel;

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

        head = new Head(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (objModel != null) {
            objModel.destroy();
        }
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
        headTransform.getQuaternion(head.quaternion, 0);
        headTransform.getTranslation(head.translation, 0);

        head.adjustPosition(earth);

        if (markerDialog != null) {
            if (!markerDialog.isCreated()) {
                markerDialog.create(head.getCamera().getPosition(), head.forward, head.up, head.right, head.eulerAngles, head.quaternion);
            }

            if (objModel != null) {
                if (!objModel.isCreated()) {
                    objModel.create(head.getCamera().getPosition(), head.forward, head.up, head.right, head.eulerAngles);
                }
            }
        }

        ray.setIntersection(getIntersection());
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    private Intersection getIntersection() {
        Intersection ret = null;

        if (markerDialog != null) {
            ret = markerDialog.onIntersect(head);
            if (ret == null) {
                if (markerDialog.isCreated()) {
                    markerDialog.destroy();
                    markerDialog = null;
                    objModel.destroy();
                    objModel = null;
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
        head.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        head.onPause();
    }
}
