package com.gmail.jiangyang5157.cardboard.scene.model;

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

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.render.GlesUtils;

/**
 * @author Yang
 * @since 5/8/2016
 */
public class TextField extends Panel {
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

    public TextField(Context context) {
        super(context);
    }

    protected final int[] texBuffers = new int[1];

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
    public void bindTextureBuffers() {
        GLES20.glGenTextures(1, texBuffers, 0);
        if (texBuffers[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        } else {
            Bitmap bitmap = buildBitmap();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }
    }

    protected Bitmap buildBitmap() {
        return buildTextBitmap(text);
    }

    protected Bitmap buildTextBitmap(String text) {
        TextPaint textPaint = new TextPaint();
        float textSizePixels = TextField.dp2px(context, textSize);
        textPaint.setTextSize(textSizePixels);
        textPaint.setAntiAlias(true);
        textPaint.setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange, null));

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

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        GLES20.glUniformMatrix4fv(modelHandle, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(viewHandle, 1, false, view, 0);
        GLES20.glUniformMatrix4fv(perspectiveHandle, 1, false, perspective, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, texturesBuffHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texBuffers[0]);
        GLES20.glUniform1i(texIdHandle, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, indicesBufferCapacity, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
        GLES20.glUseProgram(0);

        GlesUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForward_vec) {
        RayIntersection ret = super.getIntersection(cameraPos_vec, headForward_vec);
        boolean isFocused = ret != null && onClickListener != null;
        onFocuse(isFocused);
        return ret;
    }

    @Override
    public void onFocuse(boolean isFocused) {
        if (isFocused) {
            if (scaleSelector > scaleFocused) {
                scaleSelector += scaleGradient;
            }
        } else {
            if (scaleSelector < scaleNormal) {
                scaleSelector -= scaleGradient;
            }
        }
        scale = scaleSelector;
        modelRequireUpdate = true;
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
        GLES20.glDeleteTextures(texBuffers.length, texBuffers, 0);
    }
}
