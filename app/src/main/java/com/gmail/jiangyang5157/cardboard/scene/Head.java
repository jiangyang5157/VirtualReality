package com.gmail.jiangyang5157.cardboard.scene;

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

    public float[] accelerometerValues;
    public float[] linerAccelerationValues;
    public float[] magneticFieldValues;

    private Camera camera;

    public Head() {
        camera = new Camera();
    }

    public Camera getCamera() {
        return camera;
    }
}
