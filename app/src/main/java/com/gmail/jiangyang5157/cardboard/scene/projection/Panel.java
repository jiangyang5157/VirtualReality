package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @author Yang
 * @since 5/5/2016
 */
public class Panel extends Rectangle {

    public Panel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int width, float height, String hex) {
        this(context, vertexShaderRawResource, fragmentShaderRawResource, width, height, hex2color(hex));
    }

    private Panel(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int width, float height, float[] color) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, width, height);
        this.color = color.clone();
    }

    @Override
    protected void initializeHandle() {
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, MODEL_VIEW_PROJECTION_HANDLE);
        colorHandle = GLES20.glGetUniformLocation(program, COLOR_HANDLE);

        vertexHandle = GLES20.glGetAttribLocation(program, VERTEX_HANDLE);
    }

    @Override
    protected void buildArrays() {

    }

    @Override
    protected void bindBuffers() {

    }

    @Override
    public void destroy() {

    }
}
