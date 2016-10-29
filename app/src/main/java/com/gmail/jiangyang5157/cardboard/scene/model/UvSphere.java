package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class UvSphere extends Sphere implements GlModel.BindableTextureBuffer {

    private int rings;
    private int segments;

    protected float[] vertices;
    protected float[] normals;
    protected short[] indices;
    protected float[] textures;

    protected UvSphere(Context context, int rings, int segments) {
        super(context);
        this.rings = rings;
        this.segments = segments;
    }

    // eg: (rings, segments = 5, 4) maps to (4 stacks, 4 slices) UV sphere
    public void buildData() {
        final float PI = (float) Math.PI;
        final float PItimes2 = PI * 2.0f;
        final float RINGS_FACTOR = 1.0f / (float) (rings - 1);
        final float SEGMENTS_FACTOR = 1.0f / (float) (segments - 1);
        int vertexIndex = 0;
        int textureIndex = 0;
        int indexIndex = 0;
        vertices = new float[rings * segments * 3];
        indices = new short[rings * segments * 6];
        textures = new float[rings * segments * 2];

        for (int r = 0; r < rings; r++) {
            float v = r * RINGS_FACTOR;
            float phi = v * PI;
            for (int s = 0; s < segments; s++) {
                float u = s * SEGMENTS_FACTOR;
                float theta = u * PItimes2;
                // radius = 1
                float x = (float) (Math.cos(theta) * Math.sin(phi));
                float y = (float) Math.cos(phi);
                float z = (float) (Math.sin(theta) * Math.sin(phi));

                vertices[vertexIndex] = x;
                vertices[vertexIndex + 1] = y;
                vertices[vertexIndex + 2] = z;
                vertexIndex += 3;

                textures[textureIndex] = u;
                textures[textureIndex + 1] = v;
                textureIndex += 2;
            }
        }
        // GL_CCW
        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < segments; s++) {
                int r_ = (r + 1 == rings) ? 0 : r + 1;
                int s_ = (s + 1 == segments) ? 0 : s + 1;
                indices[indexIndex] = (short) (r * segments + s); //tl
                indices[indexIndex + 1] = (short) (r_ * segments + s); //bl
                indices[indexIndex + 2] = (short) (r * segments + s_); //tr

                indices[indexIndex + 3] = (short) (r * segments + s_); //tr
                indices[indexIndex + 4] = (short) (r_ * segments + s); //bl
                indices[indexIndex + 5] = (short) (r_ * segments + s_); //br
                indexIndex += 6;
            }
        }
        normals = vertices.clone();
    }
}
