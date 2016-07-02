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
import android.util.Log;
import android.util.TypedValue;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel implements GlModel.ClickListener {
    private static final String TAG = "[TextField]";

    private static final float ALPHA_BACKGROUND = 0.2f;

    protected static final float TEXT_SIZE_LARGE = 12f;
    protected static final float TEXT_SIZE_MEDIUM = 10f;
    protected static final float TEXT_SIZE_SMALL = 8f;
    protected static final float TEXT_SIZE_TINY = 6f;

    private float scaleNormal;
    private float scaleFocused;
    private float scaleGradient;
    private float scaleSelector;

    private String text;

    private float textSize;

    private Layout.Alignment alignment;

    private GlModel.ClickListener onClickListener;

    public TextField(Context context) {
        super(context);
    }

    private void initScaleSelector(float normal) {
        scaleNormal = normal;
        scaleFocused = normal * 0.92f;
        scaleGradient = (scaleFocused - scaleNormal) / 8;
        scaleSelector = scaleNormal;
    }

    @Override
    public void create(int program) {
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White, null));
        initScaleSelector(scale);
        super.create(program);

        setCreated(true);
        setVisible(true);
    }

    @Override
    public void bindTexBuffers() {
        GLES20.glGenTextures(1, texBuffers, 0);
        if (texBuffers[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            TextPaint textPaint = new TextPaint();
            float textSizePixels = dp2px(context, textSize);
            textPaint.setTextSize(textSizePixels);
            textPaint.setAntiAlias(true);
            textPaint.setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange, null));

            StaticLayout staticLayout = new StaticLayout(text, textPaint, (int) width, alignment, 1.0f, 0.0f, false);
            int lines = staticLayout.getLineCount();
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            height = fm.descent + lines * (textSizePixels + fm.bottom);

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
    public void draw() {
        super.draw();
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setOnClickListener(GlModel.ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public Intersection onIntersect(Head head) {
        Intersection ret = super.onIntersect(head);

        if (ret == null) {
            if (scaleSelector < scaleNormal) {
                scaleSelector -= scaleGradient;
            }
        } else {
            if (scaleSelector > scaleFocused) {
                scaleSelector += scaleGradient;
            }
        }
        this.scale = scaleSelector;

        return ret;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public Layout.Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Layout.Alignment alignment) {
        this.alignment = alignment;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }
}
