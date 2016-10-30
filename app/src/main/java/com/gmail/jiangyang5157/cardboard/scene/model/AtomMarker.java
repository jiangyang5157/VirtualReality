package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.base.time.Performance;

/**
 * This GlModel cannot be created and draw individually. Its a component of AtomMarkers, which is responsible to create / update / draw AtomMarker.
 *
 * @author Yang
 * @since 4/12/2016.
 */
public class AtomMarker extends Marker3d {
    private static final String TAG = "[AtomMarker]";

    public AtomMarker(Context context) {
        super(context);
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
        GLES20.glUniformMatrix4fv(perspectiveHandle, 1, false, perspective, 0);

        GLES20.glUniform3fv(colorHandle, 1, color, 0);

//        Performance.getInstance().addBreakpoint();
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);
//        Performance.getInstance().addBreakpoint();
//        Performance.getInstance().printEvaluationInMilliseconds();

        GlesUtils.printGlError(TAG + " - draw end");
    }
}
