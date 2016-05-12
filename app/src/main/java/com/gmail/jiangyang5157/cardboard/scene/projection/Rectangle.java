package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Rectangle extends GLModel implements Intersectable {

    protected float width;
    protected float height;

    protected Rectangle(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }
}
