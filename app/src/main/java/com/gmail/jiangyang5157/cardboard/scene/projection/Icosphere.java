package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Icosphere extends Model {

    float radius;

    int recursionLevel;

    int indicesBufferCapacity;

    private final int[] buffers = new int[3];

    boolean isVisible = true;

    public Icosphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        this.recursionLevel = recursionLevel;
        this.radius = radius;
        this.color = color.clone();
    }

    @Override
    protected void buildArrays() {
        IcosphereVertex icosphereVertex = IcosphereBuilder.getInstance().build(recursionLevel);
        indices = icosphereVertex.getArrayCopy(icosphereVertex.indices);
        normals = icosphereVertex.getArrayCopy(icosphereVertex.vertices);
        vertices = icosphereVertex.scale(radius);
    }

    @Override
    protected void bindBuffers() {
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        normalsBuffer = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuffer.put(normals).position(0);
        normals = null;

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        normalsBuffHandle = buffers[1];
        indicesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
        verticesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalsBuffer.capacity() * BYTES_PER_FLOAT, normalsBuffer, GLES20.GL_STATIC_DRAW);
        normalsBuffer.limit(0);
        normalsBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
        indicesBuffer = null;
    }

    @Override
    protected void initializeHandle() {
        mvMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_HANDLE);
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);
        lightPosHandle = GLES20.glGetUniformLocation(program, LIGHT_POSITION_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        normalHandle = GLES20.glGetAttribLocation(program, NORMAL_HANDLE);
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        if (isVisible == false){
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glUniform3fv(lightPosHandle, 1, lightPosInEyeSpace, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBuffHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Icosphere - draw end");
    }

    @Override
    public void destroy() {
        Log.d("Icosphere", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }

    public float getRadius() {
        return radius;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
}
