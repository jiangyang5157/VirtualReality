package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;

import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class AtomMarker extends Marker3d {
    private static final String TAG = "[AtomMarker]";

    public AtomMarker(Context context) {
        super(context);
    }

    @Override
    public void create(int program) {
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Yellow, null));
        }
        bindProgram(program);
        setCreated(true);
        setVisible(true);
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        // TODO: 7/14/2016 for 2d
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
        GLES20.glUniform3fv(colorHandle, 1, color, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        // TODO: 7/14/2016 for 3d
//        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);
//        GLES20.glUniform3fv(colorHandle, 1, color, 0);
//        GLES20.glUniform1f(pointSizeHandle, pointSize);
//
//        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        GlUtils.printGlError(TAG + " - draw end");
    }
}
