package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.InputStream;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class UvSphere extends Sphere {

    private int stacks;
    private int slices;

    protected float[] vertices;
    protected float[] normals;
    protected short[] indices;
    protected float[] textures;

    public UvSphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }

    protected void create(float radius, int stacks, int slices) {
        this.stacks = stacks;
        this.slices = slices;

        create(radius);
    }

    @Override
    protected void buildArrays() {
        vertices = new float[stacks * slices * 3];
        indices = new short[stacks * slices * 6];
        textures = new float[stacks * slices * 2];

        int vertexIndex = 0;
        int textureIndex = 0;
        int indexIndex = 0;

        float PI = (float) Math.PI;
        float PIx2 = PI * 2.0f;
        final float STACKS_FACTOR = 1f / (float) (stacks - 1);
        final float SLICES_FACTOR = 1f / (float) (slices - 1);
        for (int r = 0; r < stacks; r++) {
            float v = r * STACKS_FACTOR;
            float phi = v * PI;

            for (int s = 0; s < slices; s++) {
                float u = s * SLICES_FACTOR;
                float theta = u * PIx2;

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
        for (int r = 0; r < stacks; r++) {
            for (int s = 0; s < slices; s++) {
                int r_ = (r + 1 == stacks) ? 0 : r + 1;
                int s_ = (s + 1 == slices) ? 0 : s + 1;
                indices[indexIndex] = (short) (r * slices + s); //tlVec
                indices[indexIndex + 1] = (short) (r_ * slices + s); //blVec
                indices[indexIndex + 2] = (short) (r * slices + s_); //trVec

                indices[indexIndex + 3] = (short) (r * slices + s_); //trVec
                indices[indexIndex + 4] = (short) (r_ * slices + s); //blVec
                indices[indexIndex + 5] = (short) (r_ * slices + s_); //brVec

                indexIndex += 6;
            }
        }

        normals = vertices.clone();
    }

    protected static int loadTexture(final InputStream in) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        return textureHandle[0];
    }

    protected static int loadTexture(final Context context, final int resId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        return textureHandle[0];
    }
}
