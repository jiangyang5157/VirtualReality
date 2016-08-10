package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.gmail.jiangyang5157.cardboard.net.Downloader;
import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.base.data.BufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Map;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Earth extends UvSphere implements Creation {
    private static final String TAG = "[Earth]";

    public static final float RADIUS = 4000f;

    protected final int[] buffers = new int[3];
    protected final int[] texBuffers = new int[1];
    protected Bitmap[] textureBitmap = new Bitmap[1];

    private String urlTexture;

    protected int creationState = STATE_BEFORE_PREPARE;

    public Earth(Context context, String urlTexture) {
        super(context, 180, 180);
        this.urlTexture = urlTexture;
        setRadius(RADIUS);

        modelRequireUpdate = true;
    }

    public boolean checkPreparation() {
        File fileTexture = new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture)));
        return fileTexture.exists();
    }

    public void prepare(final Ray ray) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                creationState = STATE_PREPARING;
                ray.addBusy();

                if (checkPreparation()) {
                    buildTextureBuffers();
                    buildData();
                    ray.subtractBusy();
                    creationState = STATE_BEFORE_CREATE;
                } else {
                    File fileTexture = new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture)));
                    if (!fileTexture.exists()) {
                        Log.d(TAG, fileTexture.getAbsolutePath() + " not exist.");
                        new Downloader(urlTexture, fileTexture, new Downloader.ResponseListener() {
                            @Override
                            public boolean onStart(Map<String, String> headers) {
                                return true;
                            }

                            @Override
                            public void onComplete(Map<String, String> headers) {
                                buildTextureBuffers();
                                buildData();
                                ray.subtractBusy();
                                creationState = STATE_BEFORE_CREATE;
                            }

                            @Override
                            public void onError(String url, VolleyError volleyError) {
                                AppUtils.buildToast(context, url + " " + volleyError.toString());
                                ray.subtractBusy();
                                creationState = STATE_BEFORE_PREPARE;
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void create(int program) {
        creationState = STATE_CREATING;

        super.create(program);
        bindHandles();
        bindTextureBuffers();
        bindBuffers();

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
    }

    protected void buildTextureBuffers() {
        InputStream in = null;
        try {
            in = new FileInputStream(new File(Constant.getAbsolutePath(context, Constant.getPath(urlTexture))));
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            textureBitmap[0] = BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void bindHandles() {
        modelHandle = GLES20.glGetUniformLocation(program, MODEL_HANDLE);
        viewHandle = GLES20.glGetUniformLocation(program, VIEW_HANDLE);
        perspectiveHandle = GLES20.glGetUniformLocation(program, PERSPECTIVE_HANDLE);

        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    @Override
    public void bindBuffers() {
        FloatBuffer verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertices).position(0);
        vertices = null;

        ShortBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * BufferUtils.BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
        indicesBuffer.put(indices).position(0);
        indices = null;
        indicesBufferCapacity = indicesBuffer.capacity();

        FloatBuffer texturesBuffer = ByteBuffer.allocateDirect(textures.length * BufferUtils.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texturesBuffer.put(textures).position(0);
        textures = null;

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];
        texturesBuffHandle = buffers[2];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BufferUtils.BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, texturesBuffer.capacity() * BufferUtils.BYTES_PER_FLOAT, texturesBuffer, GLES20.GL_STATIC_DRAW);
        texturesBuffer.limit(0);
    }

    @Override
    public void bindTextureBuffers() {
        GLES20.glGenTextures(1, texBuffers, 0);
        if (texBuffers[0] == 0) {
            throw new RuntimeException("Gl Error - Unable to create texture.");
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, textureBitmap[0], 0);
            textureBitmap[0].recycle();
        }
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
        GLES20.glUniformMatrix4fv(perspectiveHandle, 1, false, perspective, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glUniform1i(texIdHandle, 0); // the 0: GL_TEXTURE0, texBuffers[0]

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        GlesUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteTextures(texBuffers.length, texBuffers, 0);
    }
}
