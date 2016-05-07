package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Yang
 * @since 5/5/2016
 */
public class Panel extends Rectangle {

    protected static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.panel_vertex_shader;
    protected static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.panel_fragment_shader;

    private final int[] buffers = new int[3];
    private final int[] texBuffers = new int[1];

    public Panel(Context context, int width, float height, float[] position, float[] normal, int colorInt) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, width, height);
        setColor(colorInt);

        // TODO: 5/6/2016
        Matrix.translateM(model, 0, position[0], position[1], position[2]);
    }

    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        texIdHandle = GLES20.glGetUniformLocation(program, TEXTURE_ID_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
        texCoordHandle = GLES20.glGetAttribLocation(program, TEXTURE_COORDS_HANDLE);
    }

    @Override
    protected void buildArrays() {
        final float HALF_WIDTH = width / 2.0f;
        final float HALF_HEIGHT = height / 2.0f;

        vertices = new float[]{
                -1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f, // tl
                -1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f, // bl
                1.0f * HALF_WIDTH, 1.0f * HALF_HEIGHT, 0.0f, // tr
                1.0f * HALF_WIDTH, -1.0f * HALF_HEIGHT, 0.0f, // br
        };

        // GL_CCW
        indices = new short[]{
                0, 1, 2, 3
        };

        textures = new float[]{
                0.0f, 0.0f, // tl
                0.0f, 1.0f, // bl
                1.0f, 0.0f, // tr
                1.0f, 1.0f // br
        };
    }

    @Override
    protected void bindBuffers() {
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

        texBuffers[0] = createTexture();
    }

    private int createTexture() {
        String text = "asd";

        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);

            bitmap.eraseColor(getColorInt());

            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(40);
            textPaint.setColor(Color.WHITE);
            canvas.drawText(text, 0, height, textPaint);

//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inScaled = false;
//            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher, options);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        return textureHandle[0];
    }

    @Override
    public void draw() {
        super.draw();

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glUniform1i(texIdHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        checkGlEsError("Panel - draw end");
    }

    @Override
    public void destroy() {
        Log.d("Panel", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
        GLES20.glDeleteBuffers(texBuffers.length, texBuffers, 0);
    }
}
