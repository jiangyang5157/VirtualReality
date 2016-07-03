package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.projection.Dialog;
import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.cardboard.scene.projection.Marker;
import com.gmail.jiangyang5157.cardboard.scene.projection.Ray;

/**
 * @author Yang
 * @since 5/2/2016
 */
public class Camera {

    public static final float ALTITUDE = -1 * (Math.abs(Marker.ALTITUDE) + Marker.RADIUS + Dialog.DISTANCE + Ray.DISTANCE);

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = Earth.RADIUS * 2;

    private float[] matrix;
    private float[] view;

    private final float[] UP;
    private float[] position;
    private float[] lookAt;

    public Camera() {
        matrix = new float[16];
        view = new float[16];

        UP = new float[]{0.0f, 1.0f, 0.0f};
        position = new float[]{0.0f, 0.0f, 0.0f};
        lookAt = new float[]{0.0f, 0.0f, -1.0f};

        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                lookAt[0], lookAt[1], lookAt[2],
                UP[0], UP[1], UP[2]);
    }

    public void move(float[] offset) {
        if (offset == null) {
            return;
        }
        forward(position, offset);
        forward(lookAt, offset);

        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                lookAt[0], lookAt[1], lookAt[2],
                UP[0], UP[1], UP[2]);
    }

    protected static void forward(@NonNull float[] src, float[] dir) {
        if (dir == null) {
            return;
        }
        if (dir.length != src.length) {
            throw new IndexOutOfBoundsException("Array src and dir must have different length.");
        }
        src[0] += dir[0];
        src[1] += dir[1];
        src[2] += dir[2];
    }

    public float[] getPosition() {
        return position.clone();
    }

    public float[] getMatrix() {
        return matrix;
    }

    public float[] getView() {
        return view;
    }
}
