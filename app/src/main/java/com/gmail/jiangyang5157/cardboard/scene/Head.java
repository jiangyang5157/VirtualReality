package com.gmail.jiangyang5157.cardboard.scene;

import android.opengl.Matrix;
import android.provider.Settings;
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

    private float[] linerAccelerationValues = new float[3];
    private float[] lastLinerAccelerationValues = new float[3];

    private float[] a = new float[3];
    private float[] lastA = new float[3];

    private float[] v = new float[3];
    private float[] lastV = new float[3];

    public Head() {
        camera = new Camera();
    }

    public void adjustPosition(Earth earth){
//
//        a[0] = linerAccelerationValues[0] - lastLinerAccelerationValues[0];
//        a[1] = linerAccelerationValues[1] - lastLinerAccelerationValues[1];
//        a[2] = linerAccelerationValues[2] - lastLinerAccelerationValues[2];
//        System.arraycopy(linerAccelerationValues, 0, lastLinerAccelerationValues, 0, 3);

//        a[0] -= -0.094960876f;
//        a[1] -= 0.47013694f;
//        a[2] -= 0.012373058f;


        float k = 1f;
        if (-k < a[0] && a[0] < k){
            a[0] = 0;
        }
        if (-k < a[1] && a[1] < k){
            a[1] = 0;
        }
        if (-k < a[2] && a[2] < k){
            a[2] = 0;
        }

        Log.i("####", "a: " + a[0] + "," + a[1] + "," + a[2]);

        a[2] = -a[2];
//        rotateZaxis(a, Math.toRadians(90));


        v[0] = lastV[0] + lastA[0] + (a[0] - lastA[0]) / 2;
        v[1] = lastV[1] + lastA[1] + (a[1] - lastA[1]) / 2;
        v[2] = lastV[2] + lastA[2] + (a[2] - lastA[2]) / 2;
//        v[0] = lastV[0] + lastA[0] + (a[0]) / 2;
//        v[1] = lastV[1] + lastA[1] + (a[1]) / 2;
//        v[2] = lastV[2] + lastA[2] + (a[2]) / 2;



        float[] offset = new float[]{
                lastV[0] + (v[0] - lastV[0]) / 2,
                lastV[1] + (v[1] - lastV[1]) / 2,
                lastV[2] + (v[2] - lastV[2]) / 2
        };
        offset[0] *= 20;
        offset[1] *= 20;
        offset[2] *= 20;


        System.arraycopy(a, 0, lastA, 0, 3);
        System.arraycopy(v, 0, lastV, 0, 3);

        checkMovementEnd();
                
        float[] pos = camera.getPosition();
        forward(pos, offset);
        if (earth.contain(pos)) {
            camera.move(offset);
        }
    }

    final int CHECK_MOVEMENT_COUNT = 20;
    int[] checkMovementEndCount = new int[3];
    private void checkMovementEnd() {
        if (a[0] == 0) {
            checkMovementEndCount[0]++;
        } else {
            checkMovementEndCount[0] = 0;
        }

        if (checkMovementEndCount[0] >= CHECK_MOVEMENT_COUNT) {
            lastV[0] = v[0] = 0;
        }

        if (a[0] == 0) {
            checkMovementEndCount[1]++;
        } else {
            checkMovementEndCount[1] = 0;
        }

        if (checkMovementEndCount[1] >= CHECK_MOVEMENT_COUNT) {
            lastV[1] = v[1] = 0;
        }

        if (a[2] == 0) {
            checkMovementEndCount[2]++;
        } else {
            checkMovementEndCount[2] = 0;
        }

        if (checkMovementEndCount[2] >= CHECK_MOVEMENT_COUNT) {
            lastV[2] = v[2] = 0;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public static void forward(float[] src, float[] dir) {
        src[0] += dir[0];
        src[1] += dir[1];
        src[2] += dir[2];
    }

    public void setA(float[] a) {
        this.a = a;
    }

    public void setLinerAccelerationValues(float[] linerAccelerationValues) {
        this.linerAccelerationValues = linerAccelerationValues;
    }

    public void rotateXaxis(float[] data, double radian) {
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        float y = data[1];
        float z = data[2];
        data[1] = (float) (y * cos + z * sin);
        data[2] = (float) (y * -sin + z * cos);
    }

    public void rotateYaxis(float[] data, double radian) {
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        float x = data[0];
        float z = data[2];
        data[0] = (float) (x * cos - z * sin);
        data[2] = (float) (x * sin + z * cos);
    }

    public void rotateZaxis(float[] data, double radian) {
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        float x = data[0];
        float y = data[1];
        data[0] = (float) (x * cos + y * sin);
        data[1] = (float) (x * -sin + y * cos);
    }
}
