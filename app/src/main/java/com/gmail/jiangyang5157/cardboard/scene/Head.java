package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.model.Earth;

/**
 * @author Yang
 * @since 5/12/2016
 */
public class Head implements SensorEventListener {

    private final float MOVEMENT_UNIT = 80;

    private Camera camera;

    private float[] headView;
    private float[] forward;
    private float[] up;
    private float[] right;
    private float[] quaternion;

    private SensorManager sensorManager;
    private Sensor stepDetector;
    private int stepsCounter;

    private float[] velocity;

    public Head(Context context) {
        headView = new float[16];
        forward = new float[3];
        up = new float[3];
        right = new float[3];
        quaternion = new float[4];

        camera = new Camera();

        velocity = new float[3];

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    public void onResume() {
        if (!sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_GAME)) {
            throw new UnsupportedOperationException("stepDetector not supported");
        }
    }

    public void onPause() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepsCounter += event.values[0];
        }
    }

    public void adjustPosition() {
        float[] a = new float[]{
                forward[0] * stepsCounter,
                forward[1] * stepsCounter,
                forward[2] * stepsCounter
        };
        stepsCounter = 0;

        final float D = 0.9f;
        float[] newVelocity = new float[]{
                velocity[0] * D + a[0],
                velocity[1] * D + a[1],
                velocity[2] * D + a[2]
        };
        float[] offset = new float[]{
                newVelocity[0] * MOVEMENT_UNIT,
                newVelocity[1] * MOVEMENT_UNIT,
                newVelocity[2] * MOVEMENT_UNIT
        };

        float[] pos = camera.getPosition();
        Camera.forward(pos, offset);
        if (Earth.contain(pos)) {
            camera.move(offset);
        } else {
            newVelocity[0] = newVelocity[1] = newVelocity[2] = 0;
        }
        velocity = newVelocity;
    }

    public static float[] getQquaternionMatrix(@NonNull float[] quaternion) {
        float x = quaternion[0];
        float y = quaternion[1];
        float z = quaternion[2];
        float w = quaternion[3];

        float xx = x * x;
        float xy = x * y;
        float xz = x * z;
        float xw = x * w;
        float yy = y * y;
        float yz = y * z;
        float yw = y * w;
        float zz = z * z;
        float zw = z * w;

        float xx2 = xx * 2.0f;
        float xy2 = xy * 2.0f;
        float xz2 = xz * 2.0f;
        float xw2 = xw * 2.0f;
        float yy2 = yy * 2.0f;
        float yz2 = yz * 2.0f;
        float yw2 = yw * 2.0f;
        float zz2 = zz * 2.0f;
        float zw2 = zw * 2.0f;

        float[] ret = new float[16];
        ret[0] = 1.0f - yy2 - zz2;
        ret[1] = xy2 - zw2;
        ret[2] = xz2 + yw2;
        ret[3] = 0.0f;

        ret[4] = xy2 + zw2;
        ret[5] = 1.0f - xx2 - zz2;
        ret[6] = yz2 - xw2;
        ret[7] = 0.0f;

        ret[8] = xz2 - yw2;
        ret[9] = yz2 + xw2;
        ret[10] = 1.0f - xx2 - yy2;
        ret[11] = 0.0f;

        ret[12] = 0.0f;
        ret[13] = 0.0f;
        ret[14] = 0.0f;
        ret[15] = 1.0f;
        return ret;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public float[] getHeadView() {
        return headView;
    }

    public float[] getForward() {
        return forward;
    }

    public float[] getUp() {
        return up;
    }

    public float[] getRight() {
        return right;
    }

    public float[] getQuaternion() {
        return quaternion;
    }
}
