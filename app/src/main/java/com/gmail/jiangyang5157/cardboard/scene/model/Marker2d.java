package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 7/13/2016
 */
public class Marker2d extends Point implements Marker {
    private static final String TAG = "[Marker2d]";

    public static final float RADIUS = 12f;
    public static final float ALTITUDE = -1 * RADIUS;
    public static final float POINT_SIZE = RADIUS * 2;

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

    protected Marker2d(Context context) {
        super(context);
        pointSize = POINT_SIZE;
    }

    @Override
    public void create(int program) {
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Yellow, null));
        }
        super.create(program);

        setCreated(true);
        setVisible(true);
    }

    @Override
    public void setLocation(LatLng latLng, float altitude) {
        this.coordinate = new Coordinate(latLng.latitude, latLng.longitude, altitude, Earth.RADIUS);
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public RayIntersection onIntersection(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        float[] position = getPosition();
        float[] cameraPos = head.getCamera().getPosition();
        float[] camera_pos = new float[]{
                position[0] - cameraPos[0],
                position[1] - cameraPos[1],
                position[2] - cameraPos[2]
        };
        // Convenience vector for extracting the position from a matrix via multiplication.
        float[] posMultiply = new float[]{camera_pos[0], camera_pos[1], camera_pos[2], 0.0f};
        // position in camera space
        float[] posInCameraSpace = new float[4];
        // Convert object space to camera space - Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, head.getHeadView(), 0, model, 0);
        Matrix.multiplyMV(posInCameraSpace, 0, modelView, 0, posMultiply, 0);

        double pitch = Math.atan2(posInCameraSpace[1], -posInCameraSpace[2]);
        double yaw = Math.atan2(posInCameraSpace[0], -posInCameraSpace[2]);
        final double PITCH_LIMIT = 0.02;
        final double YAW_LIMIT = 0.02;
        if (Math.abs(pitch) > PITCH_LIMIT || Math.abs(yaw) > YAW_LIMIT) {
            return null;
        } else {
            Vector camera_pos_vec = new Vector3d(camera_pos[0], camera_pos[1], camera_pos[2]);
            return new RayIntersection(this, camera_pos_vec.length() - RADIUS);
        }
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

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        GlUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setObjModel(ObjModel model) {
        this.objModel = model;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ObjModel getObjModel() {
        return objModel;
    }
}
