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

    private int indicesBufferCapacity;
    private final int[] buffers = new int[3];

    public Icosphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, float radius, int recursionLevel, float[] color) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        if (recursionLevel > vertexCounts.length - 1) {
            throw new RuntimeException("Icosphere - Unable to create a Icosphere with recursion level: " + recursionLevel);
        }
        this.radius = radius;
        this.recursionLevel = recursionLevel;
        this.color = color.clone();
    }

    @Override
    public void create() {
        buildArrays();
        fillBuffers();
        bindBuffers();
    }

    private void buildArrays() {
        vertices = new float[vertexCounts[recursionLevel] * 3];
        normals = new float[vertexCounts[recursionLevel] * 3];

        // create 12 vertices of a icosahedron - http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
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

        indices = new short[]{
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

        // refine triangles - Each edge of the triangle is split in half. One triangle is formed by the three points sitting in the middle of these edges and three triangles surrounding it.
        int iLength = indices.length;
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

        iLength = indices.length;
        int faceCount = iLength / 3;
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            short v1 = indices[faceIndex * 3];
            short v2 = indices[faceIndex * 3 + 1];
            short v3 = indices[faceIndex * 3 + 2];
            float[] normal = createNormal(getVertex(v1), getVertex(v2), getVertex(v3));
            addNormal(normal[0], normal[1], normal[2], v1);
            addNormal(normal[0], normal[1], normal[2], v2);
            addNormal(normal[0], normal[1], normal[2], v3);
        }
    }

    /**
     * return index of vertex in the middle of v1 and v2
     */
    private short getMiddleVertexIndex(short v1, short v2, short vIndex, LongSparseArray<Short> vCache) {
        if (v1 > v2) {
            short temp = v1;
            v1 = v2;
            v2 = temp;
        }
        // first check if we have it already
        boolean firstIsSmaller = v1 < v2;
        long smallerIndex = firstIsSmaller ? v1 : v2;
        long greaterIndex = firstIsSmaller ? v2 : v1;
        //the key always the smaller vIndex is stored as the first
        long key = (smallerIndex << 32) + greaterIndex;

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

    private void addVertex(float x, float y, float z, int index) {
        float length = Matrix.length(x, y, z);
        x = x / length;
        y = y / length;
        z = z / length;
        vertices[index * 3] = x * radius;
        vertices[index * 3 + 1] = y * radius;
        vertices[index * 3 + 2] = z * radius;
    }

    private float[] getVertex(short index) {
        return new float[]{
                vertices[index * 3],
                vertices[index * 3 + 1],
                vertices[index * 3 + 2]
        };
    }

    private float[] createNormal(float[] v1, float[] v2, float[] v3) {
        float[] vU = {v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        float[] vV = {v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]};
        float[] normal = {
                (vU[1] * vV[2]) - (vU[2] * vV[1]),
                (vU[2] * vV[0]) - (vU[0] * vV[2]),
                (vU[0] * vV[1]) - (vU[1] * vV[0])};

        final float length = Matrix.length(normal[0], normal[1], normal[2]);
        normal[0] /= length;
        normal[1] /= length;
        normal[2] /= length;
        return normal;
    }

    private void addNormal(float x, float y, float z, int index) {
        normals[index * 3] = x;
        normals[index * 3 + 1] = y;
        normals[index * 3 + 2] = z;
    }

    private void fillBuffers() {
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
    }

    private void bindBuffers() {
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

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        GLES20.glUniform3fv(lightPosHandle, 1, lightPosInEyeSpace, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBuffHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

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

    public int getVertexCounts() {
        return vertexCounts[recursionLevel];
    }
}
