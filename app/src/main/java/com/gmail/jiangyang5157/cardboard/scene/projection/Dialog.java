package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.app.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Yang
 * @since 5/13/2016
 */
public abstract class Dialog extends Panel {

    protected static final float ALPHA_BACKGROUND = 0.5f;

    protected static final float PADDING_LAYER = 1.0f;

    public static final float WIDTH = 400f;
    public static final float DISTANCE = 400f;

    protected List<Panel> panels;

    public Dialog(Context context) {
        super(context);
        panels = new ArrayList<>();
    }

    protected abstract void createPanels();

    public void create() {
        createPanels();
        adjustBounds(width);
        create(WIDTH, height, AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Red));
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
        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
            panel.update(view, perspective);
        }
        super.update(view, perspective);
    }

    @Override
    public void draw() {
        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
            panel.draw();
        }
        super.draw();
    }

    @Override
    public void destroy() {
        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
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
        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
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
        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
            h += panel.height;
        }
        this.width = width;
        height = h;
    }

    @Override
    public void setPosition(float[] cameraPos, float[] forward, float distance, float[] quaternion, float[] up, float[] right) {
        super.setPosition(cameraPos, forward, distance, quaternion, up, right);

        //
        cameraPos[0] -= forward[0] * PADDING_LAYER;
        cameraPos[1] -= forward[1] * PADDING_LAYER;
        cameraPos[2] -= forward[2] * PADDING_LAYER;

        //
        cameraPos[0] += up[0] * height / 2;
        cameraPos[1] += up[1] * height / 2;
        cameraPos[2] += up[2] * height / 2;

        for (Iterator<Panel> it = panels.iterator(); it.hasNext(); ) {
            Panel panel = it.next();
            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            panel.setPosition(cameraPos, forward, distance, quaternion, up, right);

            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;
        }

    }
}
