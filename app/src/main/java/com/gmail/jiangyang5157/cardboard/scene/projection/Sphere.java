package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class Sphere extends Model{

    float radius;

    Sphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }

    public float getRadius() {
        return radius;
    }
}
