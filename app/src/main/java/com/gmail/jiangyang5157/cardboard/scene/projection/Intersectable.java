package com.gmail.jiangyang5157.cardboard.scene.projection;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;

/**
 * @author Yang
 * @since 5/2/2016
 */
public interface Intersectable {
    public AimIntersection intersect(float[] cameraPosition, float[] forwardDirection);
}
