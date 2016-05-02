package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;

/**
 * @author Yang
 * @since 5/2/2016
 */
public class Camera {

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 100.0f;

    public static final float[] INITIALIZED_EYE = new float[]{0.0f, 0.0f, 0.0f};
    public static final float[] INITIALIZED_CENTER = new float[]{0.0f, 0.0f, -1.0f};
    public static final float[] INITIALIZED_UP = new float[]{0.0f, 1.0f, 0.0f};

    public float[] matrix;
    public float[] view;

    private float[] position;

    public Camera() {
        matrix = new float[16];
        view = new float[16];
        position = INITIALIZED_EYE.clone();

        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                INITIALIZED_CENTER[0], INITIALIZED_CENTER[1], INITIALIZED_CENTER[2],
                INITIALIZED_UP[0], INITIALIZED_UP[1], INITIALIZED_UP[2]);
    }

    public float[] getPosition(){
        return position;
    }
}
