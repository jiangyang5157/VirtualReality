package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Yang
 * @since 5/5/2016
 */
public class Panel extends Rectangle {

    protected static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.panel_vertex_shader;
    protected static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.panel_fragment_shader;

    private final int[] buffers = new int[2];

    public Panel(Context context, int width, float height, String hex, float[] position, float[] normal) {
        this(context, width, height, hex2color(hex), position, normal);
    }

    private Panel(Context context, int width, float height, float[] color, float[] position, float[] normal) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, width, height);
        this.color = color.clone();

        // TODO: 5/6/2016
        Matrix.translateM(model, 0, position[0], position[1], position[2]);
    }

    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
    }

    @Override
    protected void buildArrays() {
        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;

        vertices = new float[]{
                -1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f, // tl
                -1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f, // bl
                1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f, // tr
                1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f, // br
        };

        // GL_CCW
        indices = new short[]{
                0, 1, 2, 3
        };
    }

    @Override
    protected void bindBuffers() {
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
        verticesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
        indicesBuffer = null;
    }

    @Override
    public void draw() {
        super.draw();

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Point - draw end");
    }

    @Override
    public void destroy() {
        Log.d("Panel", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
