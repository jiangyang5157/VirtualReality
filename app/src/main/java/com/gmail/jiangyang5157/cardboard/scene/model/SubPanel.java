package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 5/8/2016
 */
public abstract class SubPanel extends Panel {
    private static final String TAG = "[SubPanel]";

    private float scaleNormal;
    private float scaleFocused;
    private float scaleGradient;
    private float scaleSelector;

    protected String content;

    public SubPanel(Context context) {
        super(context);
    }

    private void initScaleSelector(float normal) {
        scaleNormal = normal;
        scaleFocused = normal * 0.92f;
        scaleGradient = (scaleFocused - scaleNormal) / 8;
        scaleSelector = scaleNormal;
    }

    @Override
    public void create(int program) {
        initScaleSelector(scale);
        super.create(program);
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
        GLES20.glUniform1i(texIdHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        GlesUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForward_vec) {
        RayIntersection ret = super.getIntersection(cameraPos_vec, headForward_vec);
        boolean isFocused = ret != null && onClickListener != null;
        onFocuse(isFocused);
        return ret;
    }

    @Override
    public void onFocuse(boolean isFocused) {
        if (isFocused) {
            if (scaleSelector > scaleFocused) {
                scaleSelector += scaleGradient;
            }
        } else {
            if (scaleSelector < scaleNormal) {
                scaleSelector -= scaleGradient;
            }
        }
        scale = scaleSelector;
        modelRequireUpdate = true;
    }

    public String getContent() {
        return content;
    }

    public void setCcntent(String content) {
        this.content = content;
    }
}
