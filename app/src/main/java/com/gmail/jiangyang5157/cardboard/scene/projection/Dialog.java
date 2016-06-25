package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 5/13/2016
 */
public abstract class Dialog extends Panel {

    protected static final float ALPHA_BACKGROUND = 0.5f;

    protected static final float PADDING_LAYER = 2.0f;
    protected static final float PADDING_BOARD = 4.0f;

    protected ArrayList<Panel> panels;

    public Dialog(Context context) {
        super(context);
        panels = new ArrayList<>();
    }

    protected abstract void createPanels();

    public void create(float width, int color) {
        createPanels();
        adjustBounds(width);
        create(width, height, color);
    }

    @Override
    protected int createTexture() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
            bitmap.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        return textureHandle[0];
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
    public Intersection onIntersect(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        Intersection ret;

        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        for (final Panel panel : panels) {
            Intersection intersection = panel.onIntersect(head);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }
        Collections.sort(intersections);
        if (intersections.size() > 0) {
            ret = intersections.get(0);
        } else {
            ret = super.onIntersect(head);
        }

        return ret;
    }

    protected void adjustBounds(float width) {
        float h = 0;
        h += PADDING_BOARD;
        for (Panel panel : panels) {
            h += panel.height;
            h += PADDING_BOARD;
        }
        this.width = width;
        height = h;
    }

    @Override
    public void setPosition(float[] cameraPos, float[] forward, float[] quaternion, float[] up, float[] right) {
        super.setPosition(cameraPos, forward, quaternion, up, right);

        //
        cameraPos[0] -= forward[0] * PADDING_LAYER;
        cameraPos[1] -= forward[1] * PADDING_LAYER;
        cameraPos[2] -= forward[2] * PADDING_LAYER;

        //
        cameraPos[0] += up[0] * height / 2;
        cameraPos[1] += up[1] * height / 2;
        cameraPos[2] += up[2] * height / 2;

        cameraPos[0] -= up[0] * PADDING_BOARD;
        cameraPos[1] -= up[1] * PADDING_BOARD;
        cameraPos[2] -= up[2] * PADDING_BOARD;

        for (Panel panel : panels) {
            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            panel.setPosition(cameraPos, forward, quaternion, up, right);

            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            cameraPos[0] -= up[0] * PADDING_BOARD;
            cameraPos[1] -= up[1] * PADDING_BOARD;
            cameraPos[2] -= up[2] * PADDING_BOARD;
        }
    }
}
