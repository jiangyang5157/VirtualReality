package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.tookit.data.text.IoUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class GLModel extends Model {
    public static final int GLES_VERSION_REQUIRED = 0x00020000;

    protected static final String MODEL_HANDLE = "u_ModelMatrix";
    protected static final String MODEL_VIEW_HANDLE = "u_MVMatrix";
    protected static final String MODEL_VIEW_PROJECTION_HANDLE = "u_MVPMatrix";
    protected static final String TEXTURE_ID_HANDLE = "u_TexId";
    protected static final String COLOR_HANDLE = "u_Color";
    protected static final String POINT_SIZE_HANDLE = "u_PointSize";
    protected static final String RADIUS_HANDLE = "u_Radius";
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
    protected int radiusHandle;
    protected int lightPosHandle;
    protected int vertexHandle;
    protected int normalHandle;
    protected int texCoordHandle;

    protected float[] vertices;
    protected float[] normals;
    protected short[] indices;
    protected float[] textures;

    protected FloatBuffer verticesBuffer;
    protected FloatBuffer normalsBuffer;
    protected ShortBuffer indicesBuffer;
    protected FloatBuffer texturesBuffer;

    protected int verticesBuffHandle;
    protected int normalsBuffHandle;
    protected int indicesBuffHandle;
    protected int texturesBuffHandle;

    protected float[] color;

    protected int indicesBufferCapacity;

    protected final int program;

    protected Lighting lighting;

    protected Context context;

    protected GLModel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        this.context = context;

        Matrix.setIdentityM(model, 0);
        setVisible(true);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderRawResource));
        GLES20.glAttachShader(program, compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderRawResource));
        GLES20.glLinkProgram(program);

        initializeHandle();
    }

    private int compileShader(int type, String code) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("GlEsError", "Unable to compile shader - " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        if (shader == 0) {
            throw new RuntimeException("GlEsError - Unable to create shader:\n" + code);
        }
        return shader;
    }

    private int compileShader(int type, int resId) {
        return compileShader(type, IoUtils.readTextFile(context, resId));
    }

    public static void checkGlEsError(String label) {
        for (int error; (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR; ) {
            Log.e("GlEsError", error + " - " + label);
        }
    }

    protected abstract void initializeHandle();

    public void create() {
        buildArrays();
        bindBuffers();
    }

    public void setLighting(Lighting lighting) {
        this.lighting = lighting;
    }

    protected abstract void buildArrays();

    protected abstract void bindBuffers();

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
}
