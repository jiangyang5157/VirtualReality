package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Yang
 * @since 5/27/2016
 */
public class ObjModel extends GLModel {

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
        setColor(COLOR_NORMAL_RES_ID);

        initializeProgram();

        buildArrays();
//        bindBuffers();

        program = -1;
//        setVisible(true);
    }

    @Override
    protected void initializeHandle() {

    }

    @Override
    protected void buildArrays() {
        InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier(obj, "raw", context.getPackageName()));



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

        GLES20.glGenBuffers(buffers.length, buffers, 0);
        verticesBuffHandle = buffers[0];
        indicesBuffHandle = buffers[1];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);
        verticesBuffer.limit(0);
        verticesBuffer = null;

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * BYTES_PER_SHORT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        indicesBuffer.limit(0);
        indicesBuffer = null;
    }

    @Override
    public void draw() {

    }

    public String getTitle() {
        return title;
    }
}
