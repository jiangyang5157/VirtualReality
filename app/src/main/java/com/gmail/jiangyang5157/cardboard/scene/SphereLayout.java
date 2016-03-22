package com.gmail.jiangyang5157.cardboard.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Yang on 3/21/2016.
 */
public class SphereLayout extends ModelLayout {

    /**
     * @param rings   defines how many circles exists from the bottom to the top of the sphere
     * @param sectors defines how many vertexes define a single ring
     * @param radius  defines the distance of every vertex from the center of the sphere
     */
    public SphereLayout(int rings, int sectors, float radius) {
        buildArrays(rings, sectors, radius);
        buildBuffs();
    }

    private void buildArrays(int rings, int sectors, float radius) {
        vertices = new float[rings * sectors * 3];
        normals = new float[rings * sectors * 3];
        textures = new float[rings * sectors * 2];
        indexes = new char[rings * sectors * 6];

        int vertexIndex = 0;
        int normalIndex = 0;
        int textureIndex = 0;
        int indexIndex = 0;

        final float R = 1f / (float) (rings - 1);
        final float S = 1f / (float) (sectors - 1);

        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                float y = (float) Math.sin((-Math.PI / 2f) + Math.PI * r * R);
                float x = (float) Math.cos(2f * Math.PI * s * S) * (float) Math.sin(Math.PI * r * R);
                float z = (float) Math.sin(2f * Math.PI * s * S) * (float) Math.sin(Math.PI * r * R);

                vertices[vertexIndex] = x * radius;
                vertices[vertexIndex + 1] = y * radius;
                vertices[vertexIndex + 2] = z * radius;

                vertexIndex += 3;

                normals[normalIndex] = x;
                normals[normalIndex + 1] = y;
                normals[normalIndex + 2] = z;

                normalIndex += 3;

                if (textures != null) {
                    textures[textureIndex] = s * S;
                    textures[textureIndex + 1] = r * R;

                    textureIndex += 2;
                }
            }
        }

        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                int r1 = (r + 1 == rings) ? 0 : r + 1;
                int s1 = (s + 1 == sectors) ? 0 : s + 1;

                indexes[indexIndex] = (char) (r * sectors + s);
                indexes[indexIndex + 1] = (char) (r * sectors + (s1));
                indexes[indexIndex + 2] = (char) ((r1) * sectors + (s1));

                indexes[indexIndex + 3] = (char) ((r1) * sectors + s);
                indexes[indexIndex + 4] = (char) ((r1) * sectors + (s1));
                indexes[indexIndex + 5] = (char) (r * sectors + s);

                indexIndex += 6;
            }
        }
    }

    private void buildBuffs() {
        verticesBuff = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuff.put(vertices).position(0);

        normalsBuff = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuff.put(normals).position(0);

        texturesBuff = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuff.put(textures).position(0);

        indexesBuff = ByteBuffer.allocateDirect(indexes.length * BYTES_PER_CHAR).order(ByteOrder.nativeOrder()).asCharBuffer();
        indexesBuff.put(indexes).position(0);
    }
}
