package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.opengl.Matrix;
import android.support.v4.util.LongSparseArray;

import com.gmail.jiangyang5157.tookit.math.Geometry;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class IcosphereVertex implements Geometry {
    /**
     * Faces + Vertices = Edges + 2
     * recursion level 0 vertexCount= 12 faceCount=20 edgeCount=30
     * recursion level 1 vertexCount= 42 faceCount=80 edgeCount=120
     * recursion level 2 vertexCount= 162 faceCount=320 edgeCount=480
     * recursion level 3 vertexCount= 642 faceCount=1280 edgeCount=1920
     * ...
     */
    protected static final int[] VERTEX_COUNTS = new int[]{12, 42, 162, 642, 2562, 10242};

    private float[] vertices;
    private short[] indices;

    protected IcosphereVertex(int recursionLevel) {
        build(recursionLevel);
    }

    private void build(int recursionLevel) {
        final int VERTEX_ARRAY_LENGTH = VERTEX_COUNTS[recursionLevel] * 3;
        vertices = new float[VERTEX_ARRAY_LENGTH];

        // create 12 vertices of a icosahedron - http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
        short vIndex = 0;
        float ratio = (float) GOLDEN_RATIO;
        addVertex(-1, ratio, 0, vIndex++);
        addVertex(1, ratio, 0, vIndex++);
        addVertex(-1, -ratio, 0, vIndex++);
        addVertex(1, -ratio, 0, vIndex++);

        addVertex(0, -1, ratio, vIndex++);
        addVertex(0, 1, ratio, vIndex++);
        addVertex(0, -1, -ratio, vIndex++);
        addVertex(0, 1, -ratio, vIndex++);

        addVertex(ratio, 0, -1, vIndex++);
        addVertex(ratio, 0, 1, vIndex++);
        addVertex(-ratio, 0, -1, vIndex++);
        addVertex(-ratio, 0, 1, vIndex++);

        // GL_CCW
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
                int indexOffset = faceIndex * 3;
                short v1 = indices[indexOffset];
                short v2 = indices[indexOffset + 1];
                short v3 = indices[indexOffset + 2];

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
            int indexOffset1 = v1 * 3;
            int indexOffset2 = v2 * 3;
            float x = (vertices[indexOffset1] + vertices[indexOffset2]) / 2f;
            float y = (vertices[indexOffset1 + 1] + vertices[indexOffset2 + 1]) / 2f;
            float z = (vertices[indexOffset1 + 2] + vertices[indexOffset2 + 2]) / 2f;
            addVertex(x, y, z, value);
            vCache.put(key, value);
        }

        return value;
    }

    private void addVertex(float x, float y, float z, int index) {
        float length = Matrix.length(x, y, z);
        int indexOffset = index * 3;

        vertices[indexOffset] = x / length;
        vertices[indexOffset + 1] = y / length;
        vertices[indexOffset + 2] = z / length;
    }

    public float[] getVertices() {
        return vertices.clone();
    }

    public short[] getIndices() {
        return indices.clone();
    }
}
