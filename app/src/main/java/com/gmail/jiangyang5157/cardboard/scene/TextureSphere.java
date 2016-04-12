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
public class TextureSphere extends Model {

    private int rings;
    private int sectors;
    private float radius;
    private int textureDrawableResource;

    private int indicesBufferCapacity;

    private final int[] buffers = new int[4];
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
    protected void buildArrays() {
        vertices = new float[rings * sectors * 3];
        normals = new float[rings * sectors * 3];
        indices = new short[rings * sectors * 6];
        textures = new float[rings * sectors * 2];

        int vertexIndex = 0;
        int textureIndex = 0;
        int indexIndex = 0;

        final float RING_FACTOR = 1f / (float) (rings - 1);
        final float SECTORS_FACTOR = 1f / (float) (sectors - 1);

        for (int r = 0; r < rings; r++) {
            float v = r * RING_FACTOR;
            float phi = v * PI;

            for (int s = 0; s < sectors; s++) {
                float u = s * SECTORS_FACTOR;
                float theta = u * PIx2;

                float x = (float) (Math.cos(theta) * Math.sin(phi));
                float y = (float) Math.cos(phi);
                float z = (float) (Math.sin(theta) * Math.sin(phi));

                normals[vertexIndex] = x;
                normals[vertexIndex + 1] = y;
                normals[vertexIndex + 2] = z;

                vertices[vertexIndex] = x * radius;
                vertices[vertexIndex + 1] = y * radius;
                vertices[vertexIndex + 2] = z * radius;
                vertexIndex += 3;

                textures[textureIndex] = u;
                textures[textureIndex + 1] = v;
                textureIndex += 2;
            }
        }

        for (int r = 0; r < rings; r++) {
            for (int s = 0; s < sectors; s++) {
                int r_ = (r + 1 == rings) ? 0 : r + 1;
                int s_ = (s + 1 == sectors) ? 0 : s + 1;
                indices[indexIndex] = (short) (r * sectors + s);
                indices[indexIndex + 1] = (short) (r * sectors + s_);
                indices[indexIndex + 2] = (short) (r_ * sectors + s_);

                indices[indexIndex + 3] = (short) (r_ * sectors + s);
                indices[indexIndex + 4] = (short) (r_ * sectors + s_);
                indices[indexIndex + 5] = (short) (r * sectors + s);

                indexIndex += 6;
            }
        }
    }

    @Override
    protected void bindBuffers() {
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

        texturesBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        normalsBuffHandle = buffers[2];
        texturesBuffHandle = buffers[3];

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
    public void draw(float[] lightPosInEyeSpace) {
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform1i(texIdHandle, TEX_ID_OFFSET);
        GLES20.glUniform3fv(lightPosHandle, 1, lightPosInEyeSpace, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBuffHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + TEX_ID_OFFSET);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
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

    public float getRadius() {
        return radius;
    }

    public int getSectors() {
        return sectors;
    }

    public int getRings() {
        return rings;
    }
}
