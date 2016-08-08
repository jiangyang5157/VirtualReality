package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 5/13/2016
 */
public abstract class Dialog extends Panel {
    private static final String TAG = "[Dialog]";

    protected static final float ALPHA_BACKGROUND = 0.0f;

    protected static final float PADDING_LAYER = 1.0f;

    public static final float WIDTH = 500f;
    public static final float DISTANCE = 120f;
    public static final float SCALE = 0.3f;

    protected ArrayList<Panel> panels;

    protected final int[] texBuffers = new int[1];

    public Dialog(Context context) {
        super(context);
        scale = SCALE;
        modelRequireUpdate = true;
        panels = new ArrayList<>();
    }

    protected abstract void createPanels();

    @Override
    public void create(int program) {
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));
        createPanels();
        adjustBounds(WIDTH);
        super.create(program);
    }

    @Override
    public void bindTextureBuffers() {
        GLES20.glGenTextures(1, texBuffers, 0);
        if (texBuffers[0] == 0) {
            throw new RuntimeException("Gl Error - Unable to create texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
            bitmap.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    @Override
    public void update(float[] view, float[] perspective) {
        int iSize = panels.size();
        for(int i = 0; i < iSize; i++){
            panels.get(i).update(view, perspective);
        }
        super.update(view, perspective);
    }

    @Override
    public void draw() {
        int iSize = panels.size();
        for(int i = 0; i < iSize; i++){
            panels.get(i).draw();
        }

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

    public void addPanel(Panel panel) {
        panels.add(panel);
    }

    @Override
    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForward_vec) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        RayIntersection ret = null;
        ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
        int iSize = panels.size();
        for(int i = 0; i < iSize; i++) {
            RayIntersection rayIntersection = panels.get(i).getIntersection(cameraPos_vec, headForward_vec);
            if (rayIntersection != null) {
                rayIntersections.add(rayIntersection);
            }
        }

        int size = rayIntersections.size();
        if (size > 0) {
            if (size > 1) {
                Collections.sort(rayIntersections);
            }
            ret = rayIntersections.get(0);
        } else {
            ret = super.getIntersection(cameraPos_vec, headForward_vec);
        }

        return ret;
    }

    protected void adjustBounds(float width) {
        float h = 0;
        int iSize = panels.size();
        for(int i = 0; i < iSize; i++){
            h += panels.get(i).height;
        }
        this.width = width;
        this.height = h;
    }

    @Override
    public void setPosition(float[] cameraPos, float[] forward, float distance, float[] quaternion, float[] up, float[] right) {
        super.setPosition(cameraPos, forward, distance, quaternion, up, right);

        //
        final float SCALED_HALF_HEIGHT = height / 2 * scale;
        cameraPos[0] += up[0] * SCALED_HALF_HEIGHT;
        cameraPos[1] += up[1] * SCALED_HALF_HEIGHT;
        cameraPos[2] += up[2] * SCALED_HALF_HEIGHT;

        int iSize = panels.size();
        for(int i = 0; i < iSize; i++){
            Panel panel = panels.get(i);

            final float SCALED_PANEL_HALF_HEIGHT = panel.height / 2 * scale;
            cameraPos[0] -= up[0] * SCALED_PANEL_HALF_HEIGHT;
            cameraPos[1] -= up[1] * SCALED_PANEL_HALF_HEIGHT;
            cameraPos[2] -= up[2] * SCALED_PANEL_HALF_HEIGHT;

            panel.setPosition(cameraPos, forward, distance - PADDING_LAYER, quaternion, up, right);

            cameraPos[0] -= up[0] * SCALED_PANEL_HALF_HEIGHT;
            cameraPos[1] -= up[1] * SCALED_PANEL_HALF_HEIGHT;
            cameraPos[2] -= up[2] * SCALED_PANEL_HALF_HEIGHT;
        }
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        int iSize = panels.size();
        for(int i = 0; i < iSize; i++){
            panels.get(i).destroy();
        }
        panels.clear();
        super.destroy();
        GLES20.glDeleteTextures(texBuffers.length, texBuffers, 0);
    }
}
