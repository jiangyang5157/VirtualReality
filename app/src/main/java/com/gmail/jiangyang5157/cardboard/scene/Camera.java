package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;
import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.model.AtomMarker;
import com.gmail.jiangyang5157.cardboard.scene.model.Dialog;
import com.gmail.jiangyang5157.cardboard.scene.model.Earth;
import com.gmail.jiangyang5157.cardboard.scene.model.Ray;

/**
 * @author Yang
 * @since 5/2/2016
 */
public class Camera {

    public static final float ALTITUDE = -1 * (Math.abs(AtomMarker.ALTITUDE) + AtomMarker.RADIUS + Dialog.DISTANCE + Ray.DISTANCE);

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

    public void move(float offsetX, float offsetY, float offsetZ) {
        position[0] += offsetX;
        position[1] += offsetY;
        position[2] += offsetZ;

        lookAt[0] += offsetX;
        lookAt[1] += offsetY;
        lookAt[2] += offsetZ;

        Matrix.setLookAtM(matrix, 0,
                position[0], position[1], position[2],
                lookAt[0], lookAt[1], lookAt[2],
                UP[0], UP[1], UP[2]);
    }

    public float[] getPosition() {
        return new float[]{position[0], position[1], position[2]};
    }

    public float getX() {
        return position[0];
    }

    public float getY() {
        return position[1];
    }

    public float getZ() {
        return position[2];
    }

    public float[] getMatrix() {
        return matrix;
    }

    public float[] getView() {
        return view;
    }
}
