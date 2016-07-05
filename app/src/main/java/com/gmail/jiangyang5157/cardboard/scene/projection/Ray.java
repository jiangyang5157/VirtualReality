package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class Ray extends Point {
    private static final String TAG = "[Ray]";

    private static final float POINT_SIZE_NORMAL = 18f;
    private static final float POINT_SIZE_FOCUSED = 36f;
    private static final float POINT_SIZE_GRADIENT_UNIT = (POINT_SIZE_FOCUSED - POINT_SIZE_NORMAL) / 8;

    public static final float DISTANCE = 5;

    private static final String BUSY_HANDLE = "u_Busy";
    private int busyHandle;
    private int busy = 0;

    private Head head;
    private RayIntersection rayIntersection;

    public Ray(Context context, Head head) {
        super(context);
        this.head = head;
        pointSize = POINT_SIZE_NORMAL;
    }

    @Override
    public void create(int program) {
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange, null));
        super.create(program);

        setCreated(true);
        setVisible(true);
    }

    @Override
    public void update() {
        if (rayIntersection == null) {
            if (pointSize > POINT_SIZE_NORMAL) {
                pointSize -= POINT_SIZE_GRADIENT_UNIT;
            }
            return;
        }

        // TODO: 7/5/2016
        if (rayIntersection.getModel().onClickListener == null) {
            if (pointSize > POINT_SIZE_NORMAL) {
                pointSize -= POINT_SIZE_GRADIENT_UNIT;
            }
        } else {
            if (pointSize < POINT_SIZE_FOCUSED) {
                pointSize += POINT_SIZE_GRADIENT_UNIT;
            }
        }

        float[] cameraPos = head.getCamera().getPosition();
        float[] forward = head.getForward();
        double t = rayIntersection.getT();
        // intersectPos = cameraPos + forward * t
        // position = intersectPos + forward * -DISTANCE
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0,
                (float) (cameraPos[0] + forward[0] * (t - DISTANCE)),
                (float) (cameraPos[1] + forward[1] * (t - DISTANCE)),
                (float) (cameraPos[2] + forward[2] * (t - DISTANCE))
        );
    }

    @Override
    protected void bindHandles() {
        super.bindHandles();
        busyHandle = GLES20.glGetUniformLocation(program, BUSY_HANDLE);
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform3fv(colorHandle, 1, color, 0);
        GLES20.glUniform1f(pointSizeHandle, pointSize);
        GLES20.glUniform1i(busyHandle, busy);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        GlUtils.printGlError(TAG + " - draw end");
    }

    public void addBusy() {
        busy++;
    }

    public void subtractBusy() {
        busy--;
        busy = busy < 0 ? 0 : busy;
    }

    public void setIntersections(RayIntersection rayIntersection) {
        this.rayIntersection = rayIntersection;
    }

    public RayIntersection getRayIntersection() {
        return rayIntersection;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }
}
