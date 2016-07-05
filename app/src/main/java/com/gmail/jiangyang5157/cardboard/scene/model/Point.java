package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;

import com.gmail.jiangyang5157.tookit.data.buffer.BufferUtils;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author Yang
 * @since 5/1/2016
 */
public abstract class Point extends GlModel implements GlModel.BindableBuffer {
    private static final String TAG = "[Point]";

    protected static final String POINT_SIZE_HANDLE = "u_PointSize";
    protected int pointSizeHandle;
    protected float pointSize;

    private final int[] buffers = new int[1];

    protected float[] vertices;

    protected Point(Context context) {
        super(context);
    }

    @Override
    public void create(int program) {
        buildData();

        super.create(program);
        bindHandles();
        bindBuffers();
    }

    @Override
    protected void buildData() {
        vertices = new float[0];
    }

    @Override
    protected void bindHandles() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);
        pointSizeHandle = GLES20.glGetUniformLocation(program, POINT_SIZE_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
    }

    @Override
    public void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
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
        super.destroy();
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
