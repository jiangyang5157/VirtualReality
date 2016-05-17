package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.tookit.math.Vector;

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
    private float[] a;
    private float[] v;

    public Head() {
        camera = new Camera();
        a = new float[3];
        v = new float[3];
    }

    public void adjustPosition(Earth earth){
        adjustVelocity();

        float[] pos = camera.getPosition().clone();
        forward(pos, v);
        if (earth.contain(pos)) {
            camera.move(v);
        }
    }

    private void adjustVelocity(){


        v[0] = forward[0] * Head.MOVE_UNIT;
        v[1] = forward[1] * Head.MOVE_UNIT;
        v[2] = forward[2] * Head.MOVE_UNIT;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setLinerAccelerationValues(float[] linerAccelerationValues) {
        this.linerAccelerationValues = linerAccelerationValues;
    }

    public static void forward(float[] src, float[] dir) {
        src[0] += dir[0];
        src[1] += dir[1];
        src[2] += dir[2];
    }
}
