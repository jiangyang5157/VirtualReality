package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Rectangle extends GlModel {

    protected final float[] UP;
    protected final float[] RIGHT;

    protected float width;
    protected float height;

    protected Rectangle(Context context) {
        super(context);
        UP = new float[]{0.0f, 1.0f, 0.0f};
        RIGHT = new float[]{1.0f, 0.0f, 0.0f};
    }
}
