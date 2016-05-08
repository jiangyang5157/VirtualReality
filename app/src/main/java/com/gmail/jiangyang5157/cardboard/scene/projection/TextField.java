package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.TextPaint;
import android.util.Log;

import com.gmail.jiangyang5157.tookit.app.AppUtils;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel {

    private final String text;

    public TextField(Context context, int width, float height, float[] position, int color, String text) {
        super(context, width, height, position);
        setColor(color);
        this.text = text;
    }

    @Override
    protected int createTexture() {
        return createTextTexture();
    }

    private int createTextTexture(){
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(getColorInt());

            // The gesture threshold expressed in dp
            final float GESTURE_THRESHOLD_DP = 16.0f;
            // Get the screen's density scale
            final float scale = context.getResources().getDisplayMetrics().density;
            // Convert the dps to pixels, based on density scale
            int mGestureThreshold = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f); //56
            Log.i("####", "mGestureThreshold = " + mGestureThreshold);

            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setStrokeWidth(1);
            textPaint.setTextSize(mGestureThreshold);
            textPaint.setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White));

            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            canvas.drawText(text, 0, Math.abs(fontMetrics.top), textPaint);

//            int baseX = (int) (canvas.getWidth() / 2 - textPaint.measureText(text) / 2);
            int baseY = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));
//            canvas.drawText(text, baseX, baseY, textPaint);
//            canvas.drawLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2, textPaint);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, canvas.getWidth() / 2, baseY, textPaint);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        return textureHandle[0];
    }
}
