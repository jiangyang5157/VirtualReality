package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.gmail.jiangyang5157.cardboard.scene.projection.Earth;

/**
 * @author Yang
 * @since 5/12/2016
 */
public class Head implements SensorEventListener {

    private static final String TAG = "Head ####";

    private Camera camera;

    public float[] headView = new float[16];
    public float[] forward = new float[3];
    public float[] eulerAngles = new float[3];
    public float[] up = new float[3];
    public float[] right = new float[3];
    public float[] quaternion = new float[4];
    public float[] translation = new float[3];

    private SensorManager sensorManager;
    private Sensor stepDetector;
    private int stepsCounter;

    private float[] velocity;

    public Head(Context context) {

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

    public void adjustPosition(Earth earth) {
        float[] a = new float[]{
                forward[0] * stepsCounter,
                forward[1] * stepsCounter,
                forward[2] * stepsCounter
        };
        stepsCounter = 0;

        velocity[0] *= 0.9;
        velocity[1] *= 0.9;
        velocity[2] *= 0.9;
        float[] newVelocity = new float[]{
                velocity[0] + a[0],
                velocity[1] + a[1],
                velocity[2] + a[2]
        };
        final float MOVEMENT_UNIT = 80;
        float[] offset = new float[]{
                newVelocity[0] * MOVEMENT_UNIT,
                newVelocity[1] * MOVEMENT_UNIT,
                newVelocity[2] * MOVEMENT_UNIT
        };

        float[] pos = camera.getPosition();
        camera.forward(pos, offset);
        if (earth.contain(pos)) {
            camera.move(offset);
        } else {
            newVelocity[0] = newVelocity[1] = newVelocity[2] = 0;
        }
        System.arraycopy(newVelocity, 0, velocity, 0, 3);
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
