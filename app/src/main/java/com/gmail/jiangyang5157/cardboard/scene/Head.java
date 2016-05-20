package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

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
    public float[] quaternion = new float[4];
    public float[] translation = new float[3];

    private Camera camera;

    public static final float MOVE_UNIT = Earth.RADIUS / 50;

    private float[] accelerometerValues;
    private float[] linerAccelerationValues;
    private float[] lastLinerAccelerationValues = new float[3];
    private float[] magneticFieldValues;

    private float[] v;
    private float[] a;

    public Head() {
        camera = new Camera();
        a = new float[3];
        v = new float[3];
    }

    public void adjustPosition(Earth earth){
        adjustVelocity();

        float[] pos = camera.getPosition();
        forward(pos, v);
        if (earth.contain(pos)) {
            camera.move(v);
        }
    }

    private void adjustVelocity(){

//        Log.i("####", "la: " + linerAccelerationValues[0] + "," + linerAccelerationValues[1] + "," + linerAccelerationValues[2]);
//        Log.i("####", "last la: " + lastLinerAccelerationValues[0] + "," + lastLinerAccelerationValues[1] + "," + lastLinerAccelerationValues[2]);
        if (lastLinerAccelerationValues == null){
            a[0] = 0;
            a[1] = 0;
            a[2] = 0;
        }else{
            a[0] = linerAccelerationValues[0] - lastLinerAccelerationValues[0];
            a[1] = linerAccelerationValues[1] - lastLinerAccelerationValues[1];
            a[2] = linerAccelerationValues[2] - lastLinerAccelerationValues[2];
        }
        lastLinerAccelerationValues[0] = linerAccelerationValues[0];
        lastLinerAccelerationValues[1] = linerAccelerationValues[1];
        lastLinerAccelerationValues[2] = linerAccelerationValues[2];

//        Log.i("####", "a: " + a[0] + "," + a[1] + "," + a[2]);

//        Log.i("####", "linerA: " + linerAccelerationValues[0] + "," + linerAccelerationValues[1] + "," + linerAccelerationValues[2]);
//        float[] laF = new float[]{Math.round(linerAccelerationValues[0]), Math.round(linerAccelerationValues[0]), Math.round(linerAccelerationValues[2])};
//        Log.i("####", "laF: " + laF[0] + "," + laF[1] + "," + laF[2]);

//        float eulerAnglesDegree0 = (float) Math.toDegrees(eulerAngles[0]);
//        float eulerAnglesDegree1 = (float) Math.toDegrees(eulerAngles[1]);
//        float eulerAnglesDegree2 = (float) Math.toDegrees(eulerAngles[2]);
//        Log.i("####", "eulerD: " + eulerAnglesDegree0 + "," + eulerAnglesDegree1 + "," + eulerAnglesDegree2);
//        Vector3d la = new Vector3d(linerAccelerationValues[0], linerAccelerationValues[1], linerAccelerationValues[2]);
//        la.rotateZaxis(Math.toRadians(90));
//        double[] laD = la.getData();
//        laD[2] = -laD[2];
//        Log.i("####", "laD: " + laD[0] + "," + laD[1] + "," + laD[2]);


//        laD[0] *= laD[0];
//        laD[1] *= laD[1];
//        laD[2] *= laD[2];

//        a[0] = Math.round(a[0]);
//        a[1] = Math.round(a[1]);
//        a[2] = Math.round(a[2]);

        float[] la = new float[3];
        float k = 0.5f;
        if (a[0] > k || a[0] < -k){
            la[0] = a[0];
        }
        if (a[1] > k || a[1] < -k){
            la[1] = a[1];
        }
        if (a[2] > k || a[2] < -k){
            la[2] = a[2];
        }

        Vector3d laV = new Vector3d(la[0], la[1], la[2]);
        laV.rotateZaxis(Math.toRadians(90));
//        laV.rotateXaxis(eulerAnglesDegree0);
//        laV.rotateZaxis(eulerAnglesDegree1);
//        laV.rotateZaxis(eulerAnglesDegree2);
        double[] laD = laV.getData();
        laD[2] = -laD[2];
//        Log.i("####", "laD: " + laD[0] + ", " + laD[1] + ", " + laD[2]);

        v[0] *= 0.9;
        v[1] *= 0.9;
        v[2] *= 0.9;

        v[0] += (float) ((laD[0]) * Head.MOVE_UNIT);
        v[1] += (float) ((laD[1]) * Head.MOVE_UNIT);
        v[2] += (float) ((laD[2]) * Head.MOVE_UNIT);



//        v[0] = forward[0] * Head.MOVE_UNIT;
//        v[1] = forward[1] * Head.MOVE_UNIT;
//        v[2] = forward[2] * Head.MOVE_UNIT;
//        Log.i("####", "v: " + v[0] + ", " + v[1] + ", " + v[2]);
    }

    public Camera getCamera() {
        return camera;
    }

    public static void forward(float[] src, float[] dir) {
        src[0] += dir[0];
        src[1] += dir[1];
        src[2] += dir[2];
    }

    public void setAccelerometerValues(float[] accelerometerValues) {
        this.accelerometerValues = accelerometerValues;
    }

    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    public void setLinerAccelerationValues(float[] linerAccelerationValues) {
        this.linerAccelerationValues = linerAccelerationValues;
    }

    public float[] getLinerAccelerationValues() {
        return linerAccelerationValues;
    }

    public void setMagneticFieldValues(float[] magneticFieldValues) {
        this.magneticFieldValues = magneticFieldValues;
    }

    public float[] getMagneticFieldValues() {
        return magneticFieldValues;
    }
}
