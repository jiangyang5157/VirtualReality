package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.tookit.render.GlesUtils;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class GlModel {
    private static final String TAG = "[GlModel]";

    public interface ClickListener {
        void onClick(GlModel model);
    }

    protected interface BindableBuffer {
        void bindBuffers();
    }

    protected interface BindableTextureBuffer {
        void bindTextureBuffers();
    }

    public static final int GLES_VERSION_REQUIRED = 0x00020000;

    protected float[] rotation;
    // The scale for all x, y and z axis
    protected float scale;
    protected float[] translation;

    protected float[] model = new float[16];
    protected float[] view;
    protected float[] perspective;

    protected static final String MODEL_HANDLE = "u_ModelMatrix";
    protected static final String VIEW_HANDLE = "u_ViewMatrix";
    protected static final String PERSPECTIVE_HANDLE = "u_PerspectiveMatrix";

    protected static final String TEXTURE_ID_HANDLE = "u_TexId";
    protected static final String COLOR_HANDLE = "u_Color";
    protected static final String LIGHT_POSITION_HANDLE = "u_LightPos";
    protected static final String VERTEX_HANDLE = "a_Position";
    protected static final String NORMAL_HANDLE = "a_Normal";
    protected static final String TEXTURE_COORDS_HANDLE = "a_TexCoord";

    protected int mModelHandle;
    protected int mViewHandle;
    protected int mPerspectiveHandle;

    protected int texIdHandle;
    protected int colorHandle;
    protected int lightPosHandle;
    protected int vertexHandle;
    protected int normalHandle;
    protected int texCoordHandle;

    protected int verticesBuffHandle;
    protected int normalsBuffHandle;
    protected int indicesBuffHandle;
    protected int texturesBuffHandle;

    protected int indicesBufferCapacity;
    protected float[] color;

    protected int program = 0;

    protected Context context;

    protected Lighting lighting;

    protected Handler handler;
    protected HandlerThread handlerThread;

    protected ClickListener onClickListener;

    private boolean isCreated;
    private boolean isVisible;

    /**
     * For each frame, check this value before mv / mvp.
     * When there is a change made for rotation / scale / translation, we need to set modelRequireUpdate true as well.
     */
    protected boolean modelRequireUpdate = false;

    protected GlModel(Context context) {
        this.context = context;

        rotation = new float[16];
        Matrix.setIdentityM(rotation, 0);
        scale = 1.0f;
        translation = new float[16];
        Matrix.setIdentityM(translation, 0);
        model = new float[16];
        Matrix.setIdentityM(this.model, 0);
    }

    public void create(@NonNull ArrayMap<Integer, Integer> shaders) {
        create(GlesUtils.createProgram(context, shaders));
    }

    public void create(int program) {
        bindProgram(program);
    }

    public void bindProgram(int program) {
        this.program = program;
    }

    protected abstract void bindHandles();

    protected abstract void buildData();

    public void update(float[] view, float[] perspective) {
        if (modelRequireUpdate) {
            // Only update model mat when required, update need to be done in the following order rotation-scale-translation.
            Matrix.setIdentityM(model, 0);
            Matrix.multiplyMM(model, 0, rotation, 0, model, 0);
            Matrix.scaleM(model, 0, scale, scale, scale);
            Matrix.multiplyMM(model, 0, translation, 0, model, 0);
            modelRequireUpdate = false;
        }

        this.view = view;
        this.perspective = perspective;
    }

    public void update() {

    }

    public void draw() {

    }

    public void onFocuse(boolean isFocused) {

    }

    public void setOnClickListener(ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setLighting(Lighting lighting) {
        this.lighting = lighting;
    }

    public void setColor(String hex) {
        setColor(Color.parseColor(hex));
    }

    public void setColor(int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;
        color = new float[]{r / 255f, g / 255f, b / 255f};
    }

    public int getColor() {
        int r = (int) (color[0] * 255);
        int g = (int) (color[1] * 255);
        int b = (int) (color[2] * 255);
        return Color.rgb(r, g, b);
    }

    public int getColorWithAlpha(float alpha) {
        int a = (int) (alpha * 255);
        int r = (int) (color[0] * 255);
        int g = (int) (color[1] * 255);
        int b = (int) (color[2] * 255);
        return Color.argb(a, r, g, b);
    }

    protected Handler getHandler() {
        if (handlerThread == null) {
            handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        } else if (handlerThread.getState() == Thread.State.NEW) {
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        } else if (handlerThread.getState() == Thread.State.WAITING) {
            handler = new Handler(handlerThread.getLooper());
        } else if (handlerThread.getState() == Thread.State.TERMINATED) {
            handlerThread = null;
            handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        return handler;
    }

    public float[] getModel() {
        return this.model;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void destroy() {
        isVisible = false;
        isCreated = false;

        if (program != 0) {
            GLES20.glDeleteProgram(program);
        }

        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    public float[] getPosition() {
        return new float[]{translation[12], translation[13], translation[14]};
    }

    public float getX() {
        return translation[12];
    }

    public float getY() {
        return translation[13];
    }

    public float getZ() {
        return translation[14];
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getTranslation() {
        return translation;
    }
}
