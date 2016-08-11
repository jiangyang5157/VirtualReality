package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

/**
 * @author Yang
 * @since 8/6/2016
 */
public class TextField extends SubPanel {

    protected static final float ALPHA_BACKGROUND = 0.2f;

    protected static final float TEXT_SIZE_LARGE = 12f;
    protected static final float TEXT_SIZE_MEDIUM = 10f;
    protected static final float TEXT_SIZE_SMALL = 8f;
    protected static final float TEXT_SIZE_TINY = 6f;

    private float textSize;

    private Layout.Alignment alignment;

    public TextField(Context context) {
        super(context);
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.White, null));
    }

    public void prepare(final Ray ray) {
        buildTextureBuffers();
        buildData();
    }

    @Override
    public void create(int program) {
        super.create(program);
        bindHandles();
        bindTextureBuffers();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    @Override
    protected void buildTextureBuffers() {
        textureBitmap[0] = buildTextBitmap(content);
    }

    private Bitmap buildTextBitmap(String text) {
        TextPaint textPaint = new TextPaint();
        float textSizePixels = SubPanel.dp2px(context, textSize);
        textPaint.setTextSize(textSizePixels);
        textPaint.setAntiAlias(true);
        textPaint.setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.DeepOrange, null));

        StaticLayout staticLayout = new StaticLayout(text, textPaint, (int) width, alignment, 1.0f, 0.0f, false);
        int lines = staticLayout.getLineCount();
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        height = fm.descent + lines * (textSizePixels + fm.bottom);

        Bitmap ret = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(ret);
        ret.eraseColor(getColorWithAlpha(ALPHA_BACKGROUND));
        canvas.save();
        canvas.translate(0, fm.descent);
        staticLayout.draw(canvas);
        canvas.restore();

        return ret;
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
}
