package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;

/**
 * @author Yang
 * @since 5/5/2016
 */
public abstract class Rectangle extends GLModel implements Intersectable {

    float width;
    float height;

    protected Rectangle(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int width, float height) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public AimIntersection intersect(float[] cameraPosition, float[] forwardDirection) {
        // TODO: 5/5/2016
        return null;
    }
}
