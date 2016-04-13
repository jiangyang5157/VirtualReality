package com.gmail.jiangyang5157.cardboard.scene;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.data.text.IoUtils;
import com.gmail.jiangyang5157.tookit.math.Geometry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Yang on 4/9/2016.
 */
public abstract class GlEsModel implements Geometry {

    public static final int GLES_VERSION_REQUIRED = 0x00020000;

    static final String MODEL_HANDLE = "u_ModelMatrix";
    static final String MODEL_VIEW_HANDLE = "u_MVMatrix";
    static final String MODEL_VIEW_PROJECTION_HANDLE = "u_MVPMatrix";
    static final String TEXTURE_ID_HANDLE = "u_TexId";
    static final String COLOR_HANDLE = "u_Color";
    static final String LIGHT_POSITION_HANDLE = "u_LightPos";

    static final String VERTEX_HANDLE = "a_Position";
    static final String NORMAL_HANDLE = "a_Normal";
    static final String TEXTURE_COORDS_HANDLE = "a_TexCoord";

    int mMatrixHandle;
    int mvMatrixHandle;
    int mvpMatrixHandle;
    int texIdHandle;
    int colorHandle;
    int lightPosHandle;

    int vertexHandle;
    int normalHandle;
    int texCoordHandle;

    final int program;

    Context context;

    GlEsModel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        this.context = context;

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderRawResource));
        GLES20.glAttachShader(program, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderRawResource));
        GLES20.glLinkProgram(program);

        mMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_HANDLE);
        mvMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_HANDLE);
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);
        lightPosHandle = GLES20.glGetUniformLocation(program, LIGHT_POSITION_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        normalHandle = GLES20.glGetAttribLocation(program, NORMAL_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    private int loadShader(int type, String code) {
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

    private int loadShader(int type, int resId) {
        return loadShader(type, IoUtils.readTextFile(context, resId));
    }

    public static void checkGlEsError(String label) {
        for (int error; (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR; ) {
            Log.e("GlEsError", error + " - " + label);
        }
    }
}
