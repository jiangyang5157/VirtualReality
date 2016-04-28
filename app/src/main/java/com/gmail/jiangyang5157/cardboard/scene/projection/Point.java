package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;

/**
 * @author Yang
 * @since 4/28/2016
 */
public class Point extends Icosphere {

    private static final int DEFAULT_RECURSION_LEVEL = 0;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.simple_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.simple_fragment;

    private static final float DEFAULT_RADIUS = 0.1f;
    private static final float[] DEFAULT_COLOR = new float[]{1f, 0f, 0f, 1f};

    public Point(Context context) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_RECURSION_LEVEL, DEFAULT_RADIUS, DEFAULT_COLOR);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, -5f, 0f);
    }

    public void update(float[] view, float[] perspective, float[] model, float[] modelView, float[] headView) {

        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];
        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, model, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);
//        Log.i("####", "objPositionVec: " + objPositionVec[0] + "," + objPositionVec[1] + "," + objPositionVec[2]);
//        Matrix.setIdentityM(model, 0);
//        Matrix.translateM(model, 0, objPositionVec[0], objPositionVec[1], objPositionVec[2]);

        //
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    }

    public void update(float[] view, float[] perspective) {
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        if (isVisible == false){
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Icosphere - draw end");
    }
}
