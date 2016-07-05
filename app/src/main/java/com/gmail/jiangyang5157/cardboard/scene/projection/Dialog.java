package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.app.AppUtils;

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

    public Dialog(Context context) {
        super(context);
        this.scale = SCALE;
        panels = new ArrayList<>();
    }

    protected abstract void createPanels();

    @Override
    public void create(int program) {
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Red, null));
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
        for (Panel panel : panels) {
            panel.update(view, perspective);
        }
        super.update(view, perspective);
    }

    @Override
    public void draw() {
        for (Panel panel : panels) {
            panel.draw();
        }
        super.draw();
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        for (Panel panel : panels) {
            panel.destroy();
        }
        panels.clear();
        super.destroy();
    }

    public void addPanel(Panel panel) {
        panels.add(panel);
    }

    @Override
    public RayIntersection onIntersection(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        RayIntersection ret;

        ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
        for (Panel panel : panels) {
            RayIntersection rayIntersection = panel.onIntersection(head);
            if (rayIntersection != null) {
                rayIntersections.add(rayIntersection);
            }
        }
        Collections.sort(rayIntersections);
        if (rayIntersections.size() > 0) {
            ret = rayIntersections.get(0);
        } else {
            ret = super.onIntersection(head);
        }

        return ret;
    }

    protected void adjustBounds(float width) {
        float h = 0;
        for (Panel panel : panels) {
            h += panel.height;
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
        for (Panel panel : panels) {
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
}
