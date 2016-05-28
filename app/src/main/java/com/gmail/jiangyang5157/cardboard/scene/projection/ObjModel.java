package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.media.UnsupportedSchemeException;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.data.text.IoUtils;

import java.io.InputStream;
import java.nio.charset.UnsupportedCharsetException;

/**
 * @author Yang
 * @since 5/27/2016
 */
public class ObjModel extends GLModel {

    private static final String TAG = "ObjModel ####";

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.obj_color_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.obj_color_fragment_shader;

    private static final int COLOR_NORMAL_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private final int[] buffers = new int[1];

    private String title;
    private String obj;

    protected ObjModel(Context context, String title, String obj) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
        this.title = title;
        this.obj = obj;

    }

    public void create() {
        // TODO: 5/29/2016
        setColor(COLOR_NORMAL_RES_ID);

        initializeProgram();

        buildArrays();
        bindBuffers();

        program = -1;
//        setVisible(true);
    }

    @Override
    protected void buildArrays() {
        InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier(obj, "raw", context.getPackageName()));
        IoUtils.read(ins, new IoUtils.OnReadingListener() {
            @Override
            public boolean onReadLine(String line) {
                if (line == null) {
                    return false;
                } else {
                    // http://paulbourke.net/dataformats/obj/
                    Log.i(TAG, line);
                    if (line.startsWith("v")) {
                        parserGeometricVertices(line);
                    } else if (line.startsWith("f")) {
                        parserFace(line);
                    } else {
                        Log.i(TAG, "Unsupported regex: " + line);
                    }
                    return true;
                }
            }
        });


    }

    private void parserGeometricVertices(String line) {

    }


    private void parserFace(String line) {

    }

    @Override
    protected void bindBuffers() {
//        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
//        verticesBuffer.put(vertices).position(0);
//        vertices = null;
//
//        indicesBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
//        indicesBuffer.put(indices).position(0);
//        indices = null;
//        indicesBufferCapacity = indicesBuffer.capacity();
//
//        GLES20.glGenBuffers(buffers.length, buffers, 0);
//        verticesBuffHandle = buffers[0];
//        indicesBuffHandle = buffers[1];
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
//        verticesBuffer.limit(0);
//        verticesBuffer = null;
//
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
//        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
//        indicesBuffer.limit(0);
//        indicesBuffer = null;
    }


    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
    }

    @Override
    public void draw() {
//        if (!isVisible || !isProgramCreated()) {
//            return;
//        }
//
//        GLES20.glUseProgram(program);
//        GLES20.glEnableVertexAttribArray(vertexHandle);
//
//        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
//        GLES20.glUniform3fv(colorHandle, 1, color, 0);
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
//        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
//
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
//
//        GLES20.glDisableVertexAttribArray(vertexHandle);
//        GLES20.glUseProgram(0);
//
//        checkGlEsError("ObjModel - draw end");
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void destroy() {
        super.destroy();
        Log.d("ObjModel", "destroy");
        GLES20.glDeleteBuffers(buffers.length, buffers, 0);
    }
}
