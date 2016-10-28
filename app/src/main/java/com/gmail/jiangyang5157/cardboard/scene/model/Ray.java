package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class Ray extends Point {
    private static final String TAG = "[Ray]";

    public interface SpinnerListener {
        void onComplete();
    }

    private static final float POINT_SIZE_NORMAL = 48f;
    private static final float POINT_SIZE_FOCUSED = 64f;
    private static final float POINT_SIZE_GRADIENT_UNIT = (POINT_SIZE_FOCUSED - POINT_SIZE_NORMAL) / 8;

    public static final float DISTANCE = 5;

    // Track unfinished background thread.
    private static final String BUSY_HANDLE = "u_Busy";
    private int busyHandle;
    private int busy = 0;

    //  Spinner on Ray when user staring at the target, trigger click event when it reaches to 100.
    private static final float SPINNER_HEAD = (float) -Math.PI;
    private static final float SPINNER_TAIL = (float) Math.PI;
    private static final float SPINNER_GRADIENT_UNIT = (SPINNER_TAIL - SPINNER_HEAD) / 80;
    private static final String SPINNER_HANDLE = "u_Spinner";
    private int spinnerHandle;
    private float spinner = SPINNER_HEAD;
    private SpinnerListener spinnerListener;

    private Head head;
    private RayIntersection rayIntersection;

    public Ray(Context context, Head head) {
        super(context);
        this.head = head;
        pointSize = POINT_SIZE_NORMAL;
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.DeepOrange, null));
    }

    @Override
    public void create(int program) {
        buildData();

        super.create(program);
        bindHandles();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    @Override
    public void update() {
        super.update();
        boolean isFocused = rayIntersection != null && rayIntersection.getModel().onClickListener != null;
        onFocuse(isFocused);

        if (rayIntersection != null) {
            float[] forward = head.getForward();
            float t = (float) rayIntersection.getT();
            // intersectPos = cameraPos + forward * t;  position = intersectPos + forward * -DISTANCE
            Matrix.setIdentityM(translationMatrix, 0);
            Matrix.translateM(translationMatrix, 0,
                    head.getCamera().getX() + forward[0] * (t - DISTANCE),
                    head.getCamera().getY() + forward[1] * (t - DISTANCE),
                    head.getCamera().getZ() + forward[2] * (t - DISTANCE)
            );
            modelRequireUpdate = true;
        }
    }

    @Override
    public void onFocuse(boolean isFocused) {
        if (isFocused) {
            addPointSize();
            if (busy > 0) {
                resetSpinner();
            } else {
                updateSpinner();
            }
        } else {
            subtractPointSize();
            resetSpinner();
        }
    }

    private void addPointSize() {
        pointSize += POINT_SIZE_GRADIENT_UNIT;
        pointSize = pointSize > POINT_SIZE_FOCUSED ? POINT_SIZE_FOCUSED : pointSize;
    }

    private void subtractPointSize() {
        pointSize -= POINT_SIZE_GRADIENT_UNIT;
        pointSize = pointSize < POINT_SIZE_NORMAL ? POINT_SIZE_NORMAL : pointSize;
    }

    public void updateSpinner() {
        spinner += SPINNER_GRADIENT_UNIT;
        spinner = spinner > SPINNER_TAIL ? SPINNER_TAIL : spinner;

        if (spinner == SPINNER_TAIL) {
            if (spinnerListener != null) {
                spinnerListener.onComplete();
            }
        }
    }

    public void resetSpinner() {
        spinner = SPINNER_HEAD;
    }

    public void addBusy() {
        busy++;
    }

    public void subtractBusy() {
        busy--;
        busy = busy < 0 ? 0 : busy;
    }

    @Override
    protected void bindHandles() {
        super.bindHandles();
        busyHandle = GLES20.glGetUniformLocation(program, BUSY_HANDLE);
        spinnerHandle = GLES20.glGetUniformLocation(program, SPINNER_HANDLE);
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
        GLES20.glUniformMatrix4fv(perspectiveHandle, 1, false, perspective, 0);

        GLES20.glUniform3fv(colorHandle, 1, color, 0);
        GLES20.glUniform1f(pointSizeHandle, pointSize);
        GLES20.glUniform1i(busyHandle, busy);
        GLES20.glUniform1f(spinnerHandle, spinner);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_POINTS, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        // TODO: [WHY] 0x501
        GlesUtils.printGlError(TAG + " - draw end");
    }

    public void setIntersections(RayIntersection rayIntersection) {
        if (rayIntersection != null && this.rayIntersection != null
                && rayIntersection.getModel() != this.rayIntersection.getModel()) {
            resetSpinner();
        }

        this.rayIntersection = rayIntersection;
    }

    public RayIntersection getRayIntersection() {
        return rayIntersection;
    }

    public void setSpinnerListener(SpinnerListener spinnerListener) {
        this.spinnerListener = spinnerListener;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }
}
