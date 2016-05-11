package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.text.TextPaint;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel {

    private String text;

    public static final int DEFAULT_WIDTH = 400;
    public static final int DEFAULT_HEIGHT = 100;

    public static final float DEFAULT_DISTANCE = 400;

    public static final float ALPHA_BACKGROUND = 0.4f;
    public static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.BlueGrey;
    public static final int COLOR_TEXT_RES_ID = com.gmail.jiangyang5157.tookit.R.color.White;

    public TextField(Context context) {
        super(context);
    }

    public void create(String text) {
        create(DEFAULT_WIDTH, DEFAULT_HEIGHT, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID), text);
    }

    protected void create(float width, float height, int color, String text) {
        this.text = text;
        create(width, height, color);
    }

    public void translateToFront(float[] cameraPos, float[] forwardDir) {
        Matrix.setIdentityM(model, 0);
        Vector cameraPosVec = new Vector(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardDirVec = new Vector(forwardDir[0], forwardDir[1], forwardDir[2]);
        double[] position = cameraPosVec.plus(forwardDirVec).direction().times(DEFAULT_DISTANCE).getData();

        Matrix.translateM(model, 0, (float) position[0], (float) position[1], (float) position[2]);
    }

    @Override
    protected int createTexture() {
        return createTextTexture();
    }

    private int createTextTexture() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));

            final float GESTURE_THRESHOLD_DP = 16.0f; // The gesture threshold expressed in dp
            final float scale = context.getResources().getDisplayMetrics().density;
            float pixels = (GESTURE_THRESHOLD_DP * scale + 0.5f); // Convert the dps to pixels, based on density scale

            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(pixels);
            textPaint.setColor(AppUtils.getColor(context, COLOR_TEXT_RES_ID));
            int baseY = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
            canvas.drawText(text, 0, baseY, textPaint);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        return textureHandle[0];
    }

    @Override
    public void draw() {
        // blend for rendering alpha
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        super.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}
