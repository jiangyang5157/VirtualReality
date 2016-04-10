package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Yang on 4/9/2016.
 */
public class TextureSphere extends GlEsModel{

    private int rings;
    private int sectors;
    private float radius;
    private int textureDrawableResource;

    private int indicesBufferCapacity;

    private final int[] buffers = new int[3];
    private final int[] texBuffers = new int[1];
    private final int TEX_ID_OFFSET = 1;

    public TextureSphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int rings, int sectors, float radius, int textureDrawableResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        this.rings = rings;
        this.sectors = sectors;
        this.radius = radius;
        this.textureDrawableResource = textureDrawableResource;
    }

    @Override
    public void create() {
        buildArrays();
        buildBuffers();
        bindBuffers();
    }

    private void buildArrays() {
        vertices = new float[rings * sectors * 3];
        indices = new short[rings * sectors * 6];
        normals = new float[rings * sectors * 3];
        textures = new float[rings * sectors * 2];

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

                textures[textureIndex] = s * S;
                textures[textureIndex + 1] = r * R;
                textureIndex += 2;
            }
        }

        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                int r1 = (r + 1 == rings) ? 0 : r + 1;
                int s1 = (s + 1 == sectors) ? 0 : s + 1;
                indices[indexIndex] = (short) (r * sectors + s);
                indices[indexIndex + 1] = (short) (r * sectors + (s1));
                indices[indexIndex + 2] = (short) ((r1) * sectors + (s1));

                indices[indexIndex + 3] = (short) ((r1) * sectors + s);
                indices[indexIndex + 4] = (short) ((r1) * sectors + (s1));
                indices[indexIndex + 5] = (short) (r * sectors + s);

                indexIndex += 6;
            }
        }
    }

    private void buildBuffers() {
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        texturesBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;
    }

    private void bindBuffers() {
        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
        verticesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
        indicesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);
        texturesBuffer = null;

        GLES20.glGenTextures(texBuffers.length, texBuffers, 0);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureDrawableResource, options);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void draw() {
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform1i(texIdHandle, TEX_ID_OFFSET);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + TEX_ID_OFFSET);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("TextureSphere - draw end");
    }

    @Override
    public void destroy() {
        Log.d("TextureSphere", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteBuffers(texBuffers.length, texBuffers, 0);
    }
}
