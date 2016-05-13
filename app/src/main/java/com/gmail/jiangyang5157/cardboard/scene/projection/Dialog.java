package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class Dialog extends Panel{

    protected ArrayList<Panel> panels = new ArrayList<>();

    public static final float ALPHA_BACKGROUND = 0.8f;
    public static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.Yellow;

    public Dialog(Context context) {
        super(context);
    }

    @Override
    protected int createTexture() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
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
        super.destroy();
    }

    public ArrayList<Panel> getPanels() {
        return panels;
    }


    private void addPanel(Panel panel) {
        panels.add(panel);
    }

    @Override
    public AimIntersection intersect(Head head) {
        if (!isVisible) {
            return null;
        }
        AimIntersection ret;

        ArrayList<AimIntersection> intersections = new ArrayList<AimIntersection>();
        for (final Panel panel : panels) {
            AimIntersection intersection = panel.intersect(head);
            if (intersection != null) {
                intersections.add(intersection);
            }
        }
        Collections.sort(intersections);
        if (intersections.size() > 0) {
            ret = intersections.get(0);
        } else {
            ret = super.intersect(head);
        }

        return ret;
    }
}
