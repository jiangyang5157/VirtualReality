package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.base.data.IoUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yang
 * @since 6/3/2016
 */
public class GlesUtils {
    private final static String TAG = "[GlesUtils]";

    public static int createProgram(Context context, ArrayMap<Integer, Integer> shaders) {
        int ret = GLES20.glCreateProgram();
        if (ret == 0) {
            throw new RuntimeException("Gl Error - Unable to create program.");
        }

        try {
            int length = shaders.size();
            for (int i = 0; i < length; i++) {
                int type = shaders.keyAt(i);
                int resId = shaders.get(type);
                int shader = compileShader(context, type, resId);
                GLES20.glAttachShader(ret, shader);
            }
        } catch (IOException e) {
            e.printStackTrace();
            GLES20.glDeleteProgram(ret);
            return 0;
        }

        GLES20.glLinkProgram(ret);
        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(ret, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e("GL Error", "GL_LINK_STATUS - " + GLES20.glGetProgramInfoLog(ret));
            GLES20.glDeleteProgram(ret);
            return 0;
        }

        printGlError("glCreateProgram");
        return ret;
    }

    public static int compileShader(Context context, int type, int resId) throws IOException {
        InputStream in = context.getResources().openRawResource(resId);
        return compileShader(context, type, IoUtils.read(in));
    }

    public static int compileShader(Context context, int type, String code) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            Log.e("Gl Error", "GL_COMPILE_STATUS - " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        if (shader == 0) {
            throw new RuntimeException("Gl Error - Unable to create shader:\n" + code);
        }
        return shader;
    }

    public static void printInformationLog() {
        Log.d(TAG, "GL Vendor: " + GLES20.glGetString(GLES20.GL_VENDOR));
        Log.d(TAG, "GL Renderer: " + GLES20.glGetString(GLES20.GL_RENDERER));
        Log.d(TAG, "GL Version: " + GLES20.glGetString(GLES20.GL_VERSION));
    }

    public static void printGlError(String label) {
        for (int error; (error = GLES20.glGetError()) != GLES20.GL_NO_ERROR; ) {
            Log.e("GL Error", GLUtils.getEGLErrorString(error) + " - " + label);
        }
    }
}
