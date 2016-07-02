package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.Camera;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class Ray extends Point {
    private static final String TAG = "[Ray]";

    private static final float POINT_SIZE_NORMAL = 18f;
    private static final float POINT_SIZE_FOCUSED = 36f;
    private static final float POINT_SIZE_GRADIENT_UNIT = (POINT_SIZE_FOCUSED - POINT_SIZE_NORMAL) / 8;

    public static final float DISTANCE = Camera.Z_NEAR * 2;

    private static final String BUSY_HANDLE = "u_Busy";
    private int busyHandle;
    private int busy = 0;

    private ArrayList<Intersection> intersections;

    public Ray(Context context) {
        super(context);
        pointSize = POINT_SIZE_NORMAL;
        intersections = new ArrayList<>();
    }

    public void create() {
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange, null));
        buildArrays();

        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.ray_point_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.ray_point_fragment_shader);
        buildProgram(shaders);
        bindHandles();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    public void clearIntersections() {
        intersections.clear();
    }

    public void addIntersections(Intersection intersection) {
        if (intersection == null) {
            return;
        }
        intersections.add(intersection);
    }

    public void sortIntersections() {
        Collections.sort(intersections);
    }

    public Intersection getIntersection() {
        Collections.sort(intersections);
        return intersections.size() > 0 ? intersections.get(0) : null;
    }

    public void intersect(Intersection intersection) {
        if (intersection == null) {
            if (pointSize > POINT_SIZE_NORMAL) {
                pointSize -= POINT_SIZE_GRADIENT_UNIT;
            }
            return;
        }

        if (intersection.getModel() instanceof Intersection.Clickable) {
            if (pointSize < POINT_SIZE_FOCUSED) {
                pointSize += POINT_SIZE_GRADIENT_UNIT;
            }
        } else {
            if (pointSize > POINT_SIZE_NORMAL) {
                pointSize -= POINT_SIZE_GRADIENT_UNIT;
            }
        }

        Vector camera_intersection = intersection.getIntersecttPosVec().minus(intersection.getCameraPosVec());
        Vector rayPosVec = new Vector(intersection.getIntersecttPosVec().plus(camera_intersection.direction().times(DISTANCE)));
        double[] rayPosVecData = rayPosVec.getData();

        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0, (float) rayPosVecData[0], (float) rayPosVecData[1], (float) rayPosVecData[2]);
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

        GlUtils.printGlError("Point - draw end");
    }

    public void addBusy() {
        busy++;
    }

    public void subtractBusy() {
        busy--;
        busy = busy < 0 ? 0 : busy;
    }

    @Override
    public void destroy() {
        super.destroy();
        intersections.clear();
    }
}
