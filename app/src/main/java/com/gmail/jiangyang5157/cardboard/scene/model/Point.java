package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;

import com.gmail.jiangyang5157.tookit.base.data.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Yang
 * @since 5/1/2016
 */
public abstract class Point extends GlModel implements GlModel.BindableBuffer {
    private static final String TAG = "[Point]";

    protected static final String POINT_SIZE_HANDLE = "u_PointSize";
    protected int pointSizeHandle;
    protected float pointSize;

    protected final int[] buffers = new int[2];

    protected float[] vertices;
    protected short[] indices;

    protected Point(Context context) {
        super(context);
    }

    protected void buildData() {
        vertices = new float[3];
        indices = new short[1];
    }

    @Override
    protected void bindHandles() {
        modelHandle = GLES20.glGetUniformLocation(program, MODEL_HANDLE);
        viewHandle = GLES20.glGetUniformLocation(program, VIEW_HANDLE);
        perspectiveHandle = GLES20.glGetUniformLocation(program, PERSPECTIVE_HANDLE);

        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);
        pointSizeHandle = GLES20.glGetUniformLocation(program, POINT_SIZE_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
    }

    @Override
    public void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * BufferUtils.BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BufferUtils.BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
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

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_POINTS, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        GlesUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public void destroy() {
        super.destroy();
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
