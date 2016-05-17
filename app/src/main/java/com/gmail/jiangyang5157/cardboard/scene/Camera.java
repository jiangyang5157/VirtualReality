package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;

/**
 * @author Yang
 * @since 5/2/2016
 */
public class Camera {

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = Earth.RADIUS * 2;

    public float[] matrix;
    public float[] view;

    private float[] position = new float[]{0.0f, 0.0f, 0.0f};
    private float[] lookAt = new float[]{0.0f, 0.0f, -1.0f};
    private static final float[] UP = new float[]{0.0f, 1.0f, 0.0f};

    public Camera() {
        matrix = new float[16];
        view = new float[16];

        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                lookAt[0], lookAt[1], lookAt[2],
                UP[0], UP[1], UP[2]);
    }

    public float[] getPosition() {
        return position.clone();
    }

    public void move(float[] forwardDir, float distance) {
        forward(position, forwardDir, distance);
        forward(lookAt, forwardDir, distance);
        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                lookAt[0], lookAt[1], lookAt[2],
                UP[0], UP[1], UP[2]);
    }

    public static void forward(float[] src, float[] dir, float dis) {
        src[0] += dir[0] * dis;
        src[1] += dir[1] * dis;
        src[2] += dir[2] * dis;
    }
}
