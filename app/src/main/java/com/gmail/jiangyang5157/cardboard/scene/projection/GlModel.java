package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class GlModel extends Model {
    public static final int GLES_VERSION_REQUIRED = 0x00020000;

    protected static final String MODEL_HANDLE = "u_ModelMatrix";
    protected static final String MODEL_VIEW_HANDLE = "u_MVMatrix";
    protected static final String MODEL_VIEW_PROJECTION_HANDLE = "u_MVPMatrix";
    protected static final String TEXTURE_ID_HANDLE = "u_TexId";
    protected static final String COLOR_HANDLE = "u_Color";
    protected static final String POINT_SIZE_HANDLE = "u_PointSize";
    protected static final String LIGHT_POSITION_HANDLE = "u_LightPos";
    protected static final String VERTEX_HANDLE = "a_Position";
    protected static final String NORMAL_HANDLE = "a_Normal";
    protected static final String TEXTURE_COORDS_HANDLE = "a_TexCoord";

    protected int mMatrixHandle;
    protected int mvMatrixHandle;
    protected int mvpMatrixHandle;
    protected int texIdHandle;
    protected int colorHandle;
    protected int pointSizeHandle;
    protected int lightPosHandle;
    protected int vertexHandle;
    protected int normalHandle;
    protected int texCoordHandle;

    protected int verticesBuffHandle;
    protected int normalsBuffHandle;
    protected int indicesBuffHandle;
    protected int texturesBuffHandle;

    protected int program;
    protected int indicesBufferCapacity;
    protected float[] color;

    protected Context context;
    protected Lighting lighting;

    private final int vertexShaderRawResource;
    private final int fragmentShaderRawResource;

    protected GlModel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super();
        this.context = context;
        this.vertexShaderRawResource = vertexShaderRawResource;
        this.fragmentShaderRawResource = fragmentShaderRawResource;
    }

    protected void initializeProgram() {
        createProgram();
        bindHandles();
    }

    private int createProgram() {
        int vertexShader = GlUtils.compileShader(context, GLES20.GL_VERTEX_SHADER, vertexShaderRawResource);
        if (vertexShader == 0) {
            return 0;
        }

        int fragmentShader = GlUtils.compileShader(context, GLES20.GL_FRAGMENT_SHADER, fragmentShaderRawResource);
        if (fragmentShader == 0) {
            return 0;
        }

        program = GLES20.glCreateProgram();
        GlUtils.printGlError("glCreateProgram");
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("Gl Error", "Could not link program - " + GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        return program;
    }

    public void update(float[] view, float[] perspective) {
        Matrix.setIdentityM(model, 0);

        Matrix.multiplyMM(model, 0, rotation, 0, model, 0);
        Matrix.scaleM(model, 0, scale[0], scale[1], scale[2]);
        Matrix.multiplyMM(model, 0, translation, 0, model, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    }

    protected abstract void buildArrays();

    protected abstract void bindHandles();

    protected abstract void bindBuffers();

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

    @Override
    public void destroy() {
        super.destroy();
        if (program != 0) {
            GLES20.glDeleteProgram(program);
        }
    }
}
