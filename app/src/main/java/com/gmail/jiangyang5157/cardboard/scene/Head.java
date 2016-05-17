package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;

/**
 * @author Yang
 * @since 5/12/2016
 */
public class Head {

    public float[] headView = new float[16];
    public float[] forward = new float[3];
    public float[] eulerAngles = new float[3];
    public float[] up = new float[3];
    public float[] right = new float[3];

    private Camera camera;

    public static final float MOVE_UNIT = Earth.RADIUS / 100;
    private float[] linerAccelerationValues;
    private float a;
    private float v;

    public Head() {
        camera = new Camera();
    }

    public void adjustCameraPosition(){

    }

    public Camera getCamera() {
        return camera;
    }

    public void setLinerAccelerationValues(float[] linerAccelerationValues) {
        this.linerAccelerationValues = linerAccelerationValues;
    }
}
