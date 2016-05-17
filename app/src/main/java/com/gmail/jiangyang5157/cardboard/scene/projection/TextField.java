package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel implements Model.Clickable{

    private String text;

    private static final float TEXT_SIZE = 8f;

    private static final float ALPHA_BACKGROUND = 0.5f;
    private static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.White;

    private static final int COLOR_TEXT_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private TextPaint textPaint;

    private Model.Clickable onClickListener;

    public TextField(Context context) {
        super(context);
    }

    public void create(String text) {
        prepare(text);
        create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));
    }

    public void create(float width, float height) {
        if (text == null || textPaint == null){
            throw new RuntimeException("Function prepare() need to be called before create.");
        }
        this.width = width;
        this.height = height;
        create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));
    }

    public void prepare(String text) {
        this.text = text;

        textPaint = new TextPaint();
        float textSizePixels = dp2px(context, TEXT_SIZE);
        textPaint.setTextSize(textSizePixels);

        // TODO: 5/13/2016 handle muti-lines
        int lineCount = 1;
        width = textPaint.measureText(text);
        height = textSizePixels * (1 + lineCount);

        //solution for muti-lines
//        StaticLayout staticLayout = new StaticLayout(text, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//        canvas.save();
//        canvas.translate(x, y);
//        staticLayout.draw(canvas);
//        canvas.restore();
    }

    public float dp2px(Context context, float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public float getMeasureWidth(String text, float size){
        TextPaint tp = new TextPaint();
        tp.setTextSize(size);
        return tp.measureText(text);
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
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
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

    @Override
    public Intersection intersect(Head head) {
        return super.intersect(head);
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null){
            onClickListener.onClick(this);
        }
    }

    public void setOnClickListener(Clickable onClickListener) {
        this.onClickListener = onClickListener;
    }
}
