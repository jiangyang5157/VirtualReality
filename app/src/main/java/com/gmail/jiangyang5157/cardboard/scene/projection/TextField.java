package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel implements Intersection.Clickable {

    private static final float ALPHA_BACKGROUND = 0.5f;
    private static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.White;
    private static final int COLOR_TEXT_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_FOCUSED = 0.9f;
    private static final float SCALE_GRADIENT_UNIT = (SCALE_FOCUSED - SCALE_NORMAL) / 6;
    private float xyzScale = 1.0f;

    protected static final float TEXT_SIZE_LARGE = 12f;
    protected static final float TEXT_SIZE_MEDIUM = 10f;
    protected static final float TEXT_SIZE_SMALL = 8f;
    protected static final float TEXT_SIZE_TINY = 6f;

    private TextPaint textPaint;

    private Intersection.Clickable onClickListener;

    private final int[] texBuffers = new int[1];

    public TextField(Context context) {
        super(context);
    }

    protected void create(String text, float width, float textSize, Layout.Alignment align) {
        GLES20.glGenTextures(1, texBuffers, 0);

        if (texBuffers[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            this.width = width;

            textPaint = new TextPaint();
            float textSizePixels = dp2px(context, textSize);
            textPaint.setTextSize(textSizePixels);
            textPaint.setAntiAlias(true);
            textPaint.setColor(AppUtils.getColor(context, COLOR_TEXT_RES_ID));

            StaticLayout staticLayout = new StaticLayout(text, textPaint, (int) width, align, 1.0f, 0.0f, false);
            int lines = staticLayout.getLineCount();
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            height = fm.descent + lines * (textSizePixels + fm.bottom);
            create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));

            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(bitmap);
            bitmap.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));
            canvas.save();
            canvas.translate(0, fm.descent);
            staticLayout.draw(canvas);
            canvas.restore();

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    private float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    protected int createTexture() {
        return texBuffers[0];
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setOnClickListener(Intersection.Clickable onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public Intersection onIntersect(Head head) {
        Intersection ret = super.onIntersect(head);

        if (ret == null) {
            if (xyzScale < SCALE_NORMAL){
                xyzScale -= SCALE_GRADIENT_UNIT;
            }
        } else {
            if (xyzScale > SCALE_FOCUSED){
                xyzScale += SCALE_GRADIENT_UNIT;
            }
        }
        setScale(xyzScale);

        return ret;
    }
}