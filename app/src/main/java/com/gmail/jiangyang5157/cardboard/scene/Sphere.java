package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.gmail.jiangyang5157.cardboard.vr.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Yang on 3/20/2016.
 */
public class Sphere extends SceneObject {

    private float[] vertices;
    private float[] normals;
    private float[] textures;

    public char[] indexes;

    private FloatBuffer verticesBuff;
    private FloatBuffer normalsBuff;
    private FloatBuffer texturesBuff;

    private CharBuffer indexesBuff;

    private final int[] buffers = new int[3];
    private final int[] texturesBuffers = new int[1];

    private int positionParam;
    private int normalParam;
    private int textureParam;

    /**
     * @param rings   defines how many circles exists from the bottom to the top of the sphere
     * @param sectors defines how many vertexes define a single ring
     * @param radius  defines the distance of every vertex from the center of the sphere
     */
    public Sphere(Context context, int rings, int sectors, float radius) {
        this.context = context;
        buildArrays(rings, sectors, radius);
        buildBuffs();
    }

    public void draw() {
        GLES20.glUseProgram(program);

        GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
//        GLES20.glEnableVertexAttribArray(normalParam);
//        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, 0);
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glEnableVertexAttribArray(textureParam);
        GLES20.glVertexAttribPointer(textureParam, 2, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesBuffers[0]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexes.length, GLES20.GL_UNSIGNED_SHORT, indexesBuff);
        GLES20.glUseProgram(0);

        checkGLError("Sphere - draw");
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


    @Override
    public void createProgram() {
        //
        program = GLES20.glCreateProgram();
        int sphereVertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.sphere_vertex);
        int sphereFragmentShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.sphere_fragment);
        GLES20.glAttachShader(program, sphereVertexShader);
        GLES20.glAttachShader(program, sphereFragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
        checkGLError("Sphere - createProgram");

        modelParam = GLES20.glGetUniformLocation(program, "u_MMatrix");
        modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVPMatrix");

        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
        textureParam = GLES20.glGetAttribLocation(program, "a_Texture");

        //
        GLES20.glGenBuffers(buffers.length, buffers, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuff.capacity() * BYTES_PER_FLOAT, verticesBuff, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, normalsBuff.capacity() * BYTES_PER_FLOAT, normalsBuff, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuff.capacity() * BYTES_PER_FLOAT, texturesBuff, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        //
        GLES20.glGenTextures(texturesBuffers.length, texturesBuffers, 0);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture_earth_500x250, options);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturesBuffers[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }


    @Override
    void destroyProgram() {
        final int[] buffersToDelete = new int[]{
                buffers[0], buffers[1], buffers[2],
                texturesBuffers[0]
        };
        GLES20.glDeleteBuffers(buffersToDelete.length, buffersToDelete, 0);
    }
}
