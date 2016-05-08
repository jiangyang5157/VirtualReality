package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

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

    private int createTextTexture(){
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(getColorInt());

            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(10);
            textPaint.setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White));
            canvas.drawText(text, 0, height, textPaint);

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
        return textureHandle[0];
    }

    @Override
    protected int createTexture() {
        return createTextTexture();
    }
}
