package com.gmail.jiangyang5157.cardboard.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Sphere;
import com.gmail.jiangyang5157.cardboard.scene.SphereLayout;
import com.gmail.jiangyang5157.cardboard.ui.CardboardOverlayView;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.egl.EGLConfig;

public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    private static final String TAG = "MainActivity";

    public static final String MODEL_PARAM_NAME = "u_ModelMatrix";
    public static final String MODEL_VIEW_PARAM_NAME = "u_MVMatrix";
    public static final String MODEL_VIEW_PROJECTION_PARAM_NAME = "u_MVPMatrix";
    public static final String TEXTURE_ID_PARAM_NAME = "u_TexId";

    public static final String POSOTION_PARAM_NAME = "a_Position";
    public static final String NORMAL_PARAM_NAME = "a_Normal";
    public static final String TEXTURE_COORDS_PARAM_NAME = "a_TexCoord";

    private static final float CAMERA_Z = 0.01f;
    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private float[] view = new float[16];
    private float[] camera = new float[16];
    private float[] headView = new float[16];

    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];
    public int modelViewParam;
    public int modelViewProjectionParam;

    private Sphere earth;
    public int earthProgram;
    //public int earthModelParam;
    public int earthPositionParam;
    //public int earthNormalParam;
    public int earthTextureCoordsParam;
    public int earthTextureIdParam;
    private final int EARTH_TEXTURE_ID_OFFSET = 1;
    private final int[] earthBuffers = new int[3];
    private final int[] earthTexturesBuffers = new int[1];

    private CardboardOverlayView overlayView;

    private Vibrator vibrator;

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
        final int[] buffersToDelete = new int[]{
                earthBuffers[0], earthBuffers[1], earthBuffers[2], earthTexturesBuffers[0]
        };
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
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
        checkGLError("MainActivity - onDrawEye");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Build the ModelView and ModelViewProjection matrices for calculating different object's position
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        Matrix.rotateM(earth.model, 0, 0.2f, 0, 1, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, earth.model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);

        drawScene();
    }

    private void drawScene() {
        GLES20.glUseProgram(earthProgram);

        // GLES20.glUniformMatrix4fv(earthModelParam, 1, false, earth.model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glUniform1i(earthTextureIdParam, EARTH_TEXTURE_ID_OFFSET);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[0]);
        GLES20.glEnableVertexAttribArray(earthPositionParam);
        GLES20.glVertexAttribPointer(earthPositionParam, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[1]);
//        GLES20.glEnableVertexAttribArray(earthNormalParam);
//        GLES20.glVertexAttribPointer(earthNormalParam, 3, GLES20.GL_FLOAT, false, 0, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[2]);
        GLES20.glEnableVertexAttribArray(earthTextureCoordsParam);
        GLES20.glVertexAttribPointer(earthTextureCoordsParam, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + EARTH_TEXTURE_ID_OFFSET);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, earthTexturesBuffers[0]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, earth.getModelLayout().getIndexes().length, GLES20.GL_UNSIGNED_SHORT, earth.getModelLayout().getIndexesBuff());


        GLES20.glUseProgram(0);

        checkGLError("Sphere - draw");
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
        earth = new Sphere(new SphereLayout(111, 111, 11));

        //
        earthProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(earthProgram, loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.earth_vertex));
        GLES20.glAttachShader(earthProgram, loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.earth_fragment));
        GLES20.glLinkProgram(earthProgram);
        GLES20.glUseProgram(earthProgram);
        checkGLError("Sphere - createProgram");

        //earthModelParam = GLES20.glGetUniformLocation(earthProgram, MODEL_PARAM_NAME);
        modelViewParam = GLES20.glGetUniformLocation(earthProgram, MODEL_VIEW_PARAM_NAME);
        modelViewProjectionParam = GLES20.glGetUniformLocation(earthProgram, MODEL_VIEW_PROJECTION_PARAM_NAME);
        earthTextureIdParam = GLES20.glGetUniformLocation(earthProgram, TEXTURE_ID_PARAM_NAME);

        earthPositionParam = GLES20.glGetAttribLocation(earthProgram, POSOTION_PARAM_NAME);
        //earthNormalParam = GLES20.glGetAttribLocation(earthProgram, NORMAL_PARAM_NAME);
        earthTextureCoordsParam = GLES20.glGetAttribLocation(earthProgram, TEXTURE_COORDS_PARAM_NAME);

        //
        GLES20.glGenBuffers(earthBuffers.length, earthBuffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, earth.getModelLayout().getVerticesBuff().capacity() * 4, earth.getModelLayout().getVerticesBuff(), GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[1]);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, earth.getModelLayout().getNormalsBuff().capacity() * 4, earth.getModelLayout().getNormalsBuff(), GLES20.GL_STATIC_DRAW);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, earthBuffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, earth.getModelLayout().getTexturesBuff().capacity() * 4, earth.getModelLayout().getTexturesBuff(), GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //
        GLES20.glGenTextures(earthTexturesBuffers.length, earthTexturesBuffers, 0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.texture_earth_500x250, options);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, earthTexturesBuffers[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();

        Matrix.setIdentityM(earth.model, 0);
        //Matrix.translateM(earth.model, 0, 0.0f, 0.0f, -22.0f);
        Matrix.translateM(earth.model, 0, 0.0f, 0.0f, 0.0f);

        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
        // #### why never called? Move cleanup to onDestroy
        // https://developers.google.com/cardboard/android/latest/reference/com/google/vrtoolkit/cardboard/CardboardView.Renderer.html#onRendererShutdown()
        // https://github.com/raasun/cardboard/blob/master/src/com/google/vrtoolkit/cardboard/CardboardView.java
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type  The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e("OpenGL Compiling Error", "shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void checkGLError(String label) {
        for (int error; (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR; ) {
            Log.e("OpenGL Error", "glError: " + error + " - " + label);
        }
    }
}
