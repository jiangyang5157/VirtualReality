package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Rectangle extends GlModel implements Intersection.Intersectable {

    protected final float[] UP;
    protected final float[] RIGHT;

    protected float width;
    protected float height;

    protected Rectangle(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        UP = new float[]{0.0f, 1.0f, 0.0f};
        RIGHT = new float[]{1.0f, 0.0f, 0.0f};
    }
}