package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Yang on 4/10/2016.
 */
public class Icosphere extends GlEsModel {

    private float radius;

    /**
     * Faces + Vertices = Edges + 2
     * recursion level 0 vertexCount= 12 faceCount=20 edgeCount=30
     * recursion level 1 vertexCount= 42 faceCount=80 edgeCount=120
     * recursion level 2 vertexCount= 162 faceCount=320 edgeCount=480
     * recursion level 3 vertexCount= 642 faceCount=1280 edgeCount=1920
     * ...
     */
    private static final short[] vertexCounts = new short[]{12, 42, 162, 642, 2562, 10242};
    private int recursionLevel;
    private static final short initialIndices[] = {
            0, 11, 5,
            0, 5, 1,
            0, 1, 7,
            0, 7, 10,
            0, 10, 11,

            1, 5, 9,
            5, 11, 4,
            11, 10, 2,
            10, 7, 6,
            7, 1, 8,

            3, 9, 4,
            3, 4, 2,
            3, 2, 6,
            3, 6, 8,
            3, 8, 9,

            4, 9, 5,
            2, 4, 11,
            6, 2, 10,
            8, 6, 7,
            9, 8, 1,
    };

    private int indicesBufferCapacity;
    private final int[] buffers = new int[2];

    public Icosphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, float radius, int recursionLevel) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        if (recursionLevel > vertexCounts.length - 1) {
            throw new RuntimeException("Icosphere - Unable to create a Icosphere with recursion level: " + recursionLevel);
        }
        this.radius = radius;
        this.recursionLevel = recursionLevel;
    }

    @Override
    public void create() {
        buildArrays();
        fillBuffers();
        bindBuffers();
    }

    private void buildArrays() {
        vertices = new float[vertexCounts[recursionLevel] * 3];
        short vIndex = initializeVertices();
        indices = initialIndices.clone();
        int iLength = indices.length;

        // Each edge of the triangle is split in half.
        // One triangle is formed by the three points sitting in the middle of these edges and three triangles surrounding it
        LongSparseArray<Short> vCache = new LongSparseArray<>();
        for (int level = 0; level < recursionLevel; level++) {
            final int newFaceCount = 20 * (int) Math.pow(4, level + 1);
            short newIndices[] = new short[newFaceCount * 3];
            int iIndex = 0;
            int faceCount = iLength / 3;
            for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
                short v1 = indices[faceIndex * 3];
                short v2 = indices[faceIndex * 3 + 1];
                short v3 = indices[faceIndex * 3 + 2];

                short a = getMiddleVertexIndex(v1, v2, vIndex, vCache);
                if (a == vIndex) {
                    vIndex++;
                }
                short b = getMiddleVertexIndex(v2, v3, vIndex, vCache);
                if (b == vIndex) {
                    vIndex++;
                }
                short c = getMiddleVertexIndex(v3, v1, vIndex, vCache);
                if (c == vIndex) {
                    vIndex++;
                }

                newIndices[iIndex++] = v1;
                newIndices[iIndex++] = a;
                newIndices[iIndex++] = c;

                newIndices[iIndex++] = c;
                newIndices[iIndex++] = b;
                newIndices[iIndex++] = v3;

                newIndices[iIndex++] = a;
                newIndices[iIndex++] = v2;
                newIndices[iIndex++] = b;

                newIndices[iIndex++] = a;
                newIndices[iIndex++] = b;
                newIndices[iIndex++] = c;
            }
            indices = newIndices;
            iLength = iIndex;
        }
    }

    //http://1.bp.blogspot.com/_-FeuT9Vh6rk/Sj1WHbcQwxI/AAAAAAAAABw/xaFDct6AyOI/s400/icopoints.png
    private short initializeVertices() {
        short vIndex = 0;
        addVertex(-1, GOLDEN_RATIO, 0, vIndex++);
        addVertex(1, GOLDEN_RATIO, 0, vIndex++);
        addVertex(-1, -GOLDEN_RATIO, 0, vIndex++);
        addVertex(1, -GOLDEN_RATIO, 0, vIndex++);

        addVertex(0, -1, GOLDEN_RATIO, vIndex++);
        addVertex(0, 1, GOLDEN_RATIO, vIndex++);
        addVertex(0, -1, -GOLDEN_RATIO, vIndex++);
        addVertex(0, 1, -GOLDEN_RATIO, vIndex++);

        addVertex(GOLDEN_RATIO, 0, -1, vIndex++);
        addVertex(GOLDEN_RATIO, 0, 1, vIndex++);
        addVertex(-GOLDEN_RATIO, 0, -1, vIndex++);
        addVertex(-GOLDEN_RATIO, 0, 1, vIndex++);
        return vIndex;
    }

    private short getMiddleVertexIndex(short v1, short v2, short vIndex, LongSparseArray<Short> vCache) {
        if (v1 > v2) {
            short temp = v1;
            v1 = v2;
            v2 = temp;
        }
        //the key always the smaller vIndex is stored as the first
        long key = (long) v1 << 32 | (long) v2;
        Short value = vCache.get(key);
        if (value == null) {
            value = vIndex;
            float x = (vertices[v1 * 3] + vertices[v2 * 3]) / 2f;
            float y = (vertices[v1 * 3 + 1] + vertices[v2 * 3 + 1]) / 2f;
            float z = (vertices[v1 * 3 + 2] + vertices[v2 * 3 + 2]) / 2f;
            addVertex(x, y, z, value);
            vCache.put(key, value);
        }

        return value;
    }

    /**
     * normalize and scale radius
     */
    private void addVertex(float x, float y, float z, int vIndex) {
        float length = Matrix.length(x, y, z);
        vertices[vIndex * 3] = x / length * radius;
        vertices[vIndex * 3 + 1] = y / length * radius;
        vertices[vIndex * 3 + 2] = z / length * radius;
    }

    private void fillBuffers() {
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();
    }

    private void bindBuffers() {
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

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void draw() {
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Icosphere - draw end");
    }

    @Override
    public void destroy() {
        Log.d("Icosphere", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
