package com.gmail.jiangyang5157.cardboard.scene.projection;

/**
 * @author Yang
 * @since 5/2/2016
 */
public interface Selectable {
    public double[] intersect(float[] cameraPosition, float[] forwardDirection);
}
