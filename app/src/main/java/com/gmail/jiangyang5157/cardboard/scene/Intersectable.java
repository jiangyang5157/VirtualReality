package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 7/5/2016
 */
public interface Intersectable {
    RayIntersection onIntersection(Vector cameraPos_vec, Vector headForward_vec, final float[] headView);
}