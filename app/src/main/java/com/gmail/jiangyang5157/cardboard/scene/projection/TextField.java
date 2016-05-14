package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.TextPaint;

import com.gmail.jiangyang5157.tookit.app.AppUtils;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel {

    private String text;

    public static final float ALPHA_BACKGROUND = 0.5f;
    public static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.White;

    public static final int COLOR_TEXT_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    protected TextPaint textPaint;
    protected float textSizePixels;

    public TextField(Context context) {
        super(context);
    }

    public void create(String text) {
        this.text = text;

        final float GESTURE_THRESHOLD_DP = 16.0f; // The gesture threshold expressed in dp
        final float scale = context.getResources().getDisplayMetrics().density;
        textSizePixels = (GESTURE_THRESHOLD_DP * scale + 0.5f); // Convert the dps to pixels, based on density scale

        textPaint = new TextPaint();
        textPaint.setTextSize(textSizePixels);

        // TODO: 5/13/2016 handle muti-lines
        int lineCount = 1;
        width = textPaint.measureText(text);
        height = textSizePixels * (1 + lineCount);

        create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));
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

            textPaint.setAntiAlias(true);
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
}
