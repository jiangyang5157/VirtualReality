package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;

/**
 * @author Yang
 * @since 5/12/2016
 */
public class Head implements SensorEventListener {

    private static final String TAG = "Head ####";

    public float[] headView = new float[16];
    public float[] forward = new float[3];
    public float[] eulerAngles = new float[3];
    public float[] up = new float[3];
    public float[] right = new float[3];
    public float[] quaternion = new float[4];
    public float[] translation = new float[3];

    private Camera camera;

    private float[] linearAcceleration = new float[3];

    private float[] a = new float[3];
    private float[] last_a = new float[3];

    private float[] v = new float[3];
    private float[] last_v = new float[3];

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor linerAcceleration;

    public Head(Context context) {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linerAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        camera = new Camera();
    }

    public void onResume() {
        if (!sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)) {
            throw new UnsupportedOperationException("Accelerometer not supported");
        }
        if (!sensorManager.registerListener(this, linerAcceleration, SensorManager.SENSOR_DELAY_GAME)) { //dt ~= 0.02
            throw new UnsupportedOperationException("LinerAcceleration not supported");
        }
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    long last_timestamp;
    final float NS2S = 1.0f / 1000000000.0f;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            System.arraycopy(event.values, 0, linearAcceleration, 0, 3);

            float dt = (event.timestamp - last_timestamp) * NS2S;
//            Log.i(TAG, "dt: " + event.timestamp + " - " + last_timestamp + " = " + dt);
            last_timestamp = event.timestamp;

//            Log.i(TAG, "LinerA: "
//                    + event.values[0]
//                    + "," + event.values[1]
//                    + "," + event.values[2]
//            );
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            Log.i(TAG, "Accele: "
//                    + event.values[0]
//                    + "," + event.values[1]
//                    + "," + event.values[2]
//            );
        }
    }

//    private final int CALIBRATION_COUNT = 1024;
//    private int calibrationCount = 0;
//    private float[] linearAccelerationCalibration = new float[]{
//            -0.094960876f, 0.47013694f, 0.012373058f
//    };
//    private void checkLinearAccelerationCalibration(float[] values) {
//        if (calibrationCount < CALIBRATION_COUNT) {
//            linearAccelerationCalibration[0] += values[0];
//            linearAccelerationCalibration[1] += values[1];
//            linearAccelerationCalibration[2] += values[2];
//            calibrationCount++;
//            Log.i(TAG, "calibrationCount: " + calibrationCount);
//        } else if (calibrationCount == CALIBRATION_COUNT) {
//            linearAccelerationCalibration[0] /= CALIBRATION_COUNT;
//            linearAccelerationCalibration[1] /= CALIBRATION_COUNT;
//            linearAccelerationCalibration[2] /= CALIBRATION_COUNT;
//            calibrationCount++;
//            Log.i(TAG, "linearAccelerationCalibration: " + linearAccelerationCalibration[0] + ", " + linearAccelerationCalibration[1] + ", " + linearAccelerationCalibration[2]);
//        } else {
//            calibrationCount = 0;
//            linearAccelerationCalibration[0] = 0;
//            linearAccelerationCalibration[1] = 0;
//            linearAccelerationCalibration[2] = 0;
//        }
//    }

    public void adjustPosition(Earth earth) {
        System.arraycopy(linearAcceleration, 0, a, 0, 3);
//        Log.i(TAG, "original a: " + a[0] + "," + a[1] + "," + a[2]);

        Log.i(TAG, "original a: "
//                + a[0] + ","
//                + a[1] + ","
                + a[2]
        );

        float k = 1f;
//        if (-k < a[0] && a[0] < k) {
            a[0] = 0;
//        }
//        if (-k < a[1] && a[1] < k) {
            a[1] = 0;
//        }
        if (-k < a[2] && a[2] < k) {
            a[2] = 0;
        }
//        Log.i(TAG, "a: " + a[0] + "," + a[1] + "," + a[2]);

//        Vector aVec = new Vector(a[0], a[1], a[2]);
//        double aVecLength = aVec.length();
//        Log.i(TAG, "aVecLength: " + aVecLength);

//        a[0] = -a[0];
//        a[1] = -a[1];
//        a[2] = -a[2];

        float[] fixedUp = new float[]{
                up[0] * -a[0],
                up[1] * -a[0],
                up[2] * -a[0],
        };
        float[] fixedRight = new float[]{
                right[0] * a[1],
                right[1] * a[1],
                right[2] * a[1],
        };
        float[] fixedForward = new float[]{
                forward[0] * a[2],
                forward[1] * a[2],
                forward[2] * a[2],
        };
        a[0] = fixedRight[0] + fixedUp[0] + fixedForward[0];
        a[1] = fixedRight[1] + fixedUp[1] + fixedForward[1];
        a[2] = fixedRight[2] + fixedUp[2] + fixedForward[2];
//        Log.i(TAG, "head a: " + a[0] + "," + a[1] + "," + a[2]);

        last_v[0] *= 0.9;
        last_v[1] *= 0.9;
        last_v[2] *= 0.9;
        v[0] = last_v[0] + (last_a[0] + a[0]) / 2;
        v[1] = last_v[1] + (last_a[1] + a[1]) / 2;
        v[2] = last_v[2] + (last_a[2] + a[2]) / 2;
//        v[0] = (last_a[0] + a[0]) / 2;
//        v[1] = (last_a[1] + a[1]) / 2;
//        v[2] = (last_a[2] + a[2]) / 2;
//        v[0] = (last_a[0] + a[0]);
//        v[1] = (last_a[1] + a[1]);
//        v[2] = (last_a[2] + a[2]);
//        v[0] = last_v[0] + (last_a[0] + a[0]);
//        v[1] = last_v[1] + (last_a[1] + a[1]);
//        v[2] = last_v[2] + (last_a[2] + a[2]);
//        v[0] = last_v[0] + (a[0]);
//        v[1] = last_v[1] + (a[1]);
//        v[2] = last_v[2] + (a[2]);
        Log.i(TAG, "v: "
//                + v[0] + ","
//                + v[1] + ","
                + v[2]
        );

        float[] offset = new float[]{
//                (last_v[0] + v[0]) / 2,
//                (last_v[1] + v[1]) / 2,
//                (last_v[2] + v[2]) / 2,
//                (last_v[0] + v[0]),
//                (last_v[1] + v[1]),
//                (last_v[2] + v[2]),
                v[0],
                v[1],
                v[2]
        };

        final float MOVEMENT_UNIT = 5;
        offset[0] *= MOVEMENT_UNIT;
        offset[1] *= MOVEMENT_UNIT;
        offset[2] *= MOVEMENT_UNIT;

        System.arraycopy(a, 0, last_a, 0, 3);
        System.arraycopy(v, 0, last_v, 0, 3);

//        checkMovementEnd();

        float[] pos = camera.getPosition();
        camera.forward(pos, offset);
        if (earth.contain(pos)) {
            camera.move(offset);
        }
    }

    final int CHECK_MOVEMENT_COUNT = 10;
    int[] checkMovementEndCount = new int[3];
    private void checkMovementEnd() {
        if (a[0] == 0) {
            checkMovementEndCount[0]++;
        } else {
            checkMovementEndCount[0] = 0;
        }

        if (checkMovementEndCount[0] >= CHECK_MOVEMENT_COUNT) {
            last_v[0] = v[0] = 0;
        }

        if (a[1] == 0) {
            checkMovementEndCount[1]++;
        } else {
            checkMovementEndCount[1] = 0;
        }

        if (checkMovementEndCount[1] >= CHECK_MOVEMENT_COUNT) {
            last_v[1] = v[1] = 0;
        }

        if (a[2] == 0) {
            checkMovementEndCount[2]++;
        } else {
            checkMovementEndCount[2] = 0;
        }

        if (checkMovementEndCount[2] >= CHECK_MOVEMENT_COUNT) {
            last_v[2] = v[2] = 0;
        }
    }

    public Camera getCamera() {
        return camera;
    }

//    public void rotateXaxis(float[] data, double radian) {
//        double sin = Math.sin(radian);
//        double cos = Math.cos(radian);
//        float y = data[1];
//        float z = data[2];
//        data[1] = (float) (y * cos + z * sin);
//        data[2] = (float) (y * -sin + z * cos);
//    }
//
//    public void rotateYaxis(float[] data, double radian) {
//        double sin = Math.sin(radian);
//        double cos = Math.cos(radian);
//        float x = data[0];
//        float z = data[2];
//        data[0] = (float) (x * cos - z * sin);
//        data[2] = (float) (x * sin + z * cos);
//    }
//
//    public void rotateZaxis(float[] data, double radian) {
//        double sin = Math.sin(radian);
//        double cos = Math.cos(radian);
//        float x = data[0];
//        float y = data[1];
//        data[0] = (float) (x * cos + y * sin);
//        data[1] = (float) (x * -sin + y * cos);
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
