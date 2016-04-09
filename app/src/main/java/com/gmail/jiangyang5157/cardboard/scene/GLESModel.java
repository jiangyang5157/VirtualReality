package com.gmail.jiangyang5157.cardboard.scene;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Yang on 4/9/2016.
 */
public abstract class GlEsModel implements Geometry {

    public static final int GLES_VERSION_REQUIRED = 0x00020000;

    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;

    static final String MODEL_HANDLE = "u_ModelMatrix";
    static final String MODEL_VIEW_HANDLE = "u_MVMatrix";
    static final String MODEL_VIEW_PROJECTION_HANDLE = "u_MVPMatrix";
    static final String TEXTURE_ID_HANDLE = "u_TexId";

    static final String POSOTION_HANDLE = "a_Position";
    static final String NORMAL_HANDLE = "a_Normal";
    static final String COLOR_HANDLE = "a_Color";
    static final String TEXTURE_COORDS_HANDLE = "a_TexCoord";

    int mMatrixHandle;
    int mvMatrixHandle;
    int mvpMatrixHandle;
    int texIdHandle;

    int positionHandle;
    int normalHandle;
    int colorHandle;
    int texCoordHandle;

    final int program;

    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

    float[] vertices;
    short[] indices;
    float[] normals;
    float[] colors;
    float[] textures;

    FloatBuffer verticesBuffer;
    ShortBuffer indicesBuffer;
    FloatBuffer normalsBuffer;
    FloatBuffer colorsBuffer;
    FloatBuffer texturesBuffer;

    int verticesBuffHandle;
    int indicesBuffHandle;
    int normalsBuffHandle;
    int colorsBuffHandle;
    int texturesBuffHandle;

    Context context;

    GlEsModel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource){
        this.context = context;

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderRawResource));
        GLES20.glAttachShader(program, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderRawResource));
        GLES20.glLinkProgram(program);

        //handles
        mMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_HANDLE);
        mvMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_HANDLE);
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        positionHandle = GLES20.glGetAttribLocation(program, POSOTION_HANDLE);
        normalHandle = GLES20.glGetAttribLocation(program, NORMAL_HANDLE);
        colorHandle = GLES20.glGetAttribLocation(program, COLOR_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    private int loadShader(int type, int resId) {
        String code = readRawResource(resId);
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
            throw new RuntimeException("GlEsError - Unable to create shader.");
        }
        return shader;
    }

    private String readRawResource(int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
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

    public static boolean isSupportGlEsVersion(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo().reqGlEsVersion >= GLES_VERSION_REQUIRED;
    }

    public static void checkGlEsError(String label) {
        for (int error; (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR; ) {
            Log.e("GlEsError", error + " - " + label);
        }
    }

    abstract void create();

    abstract void draw();

    abstract void destroy();
}
