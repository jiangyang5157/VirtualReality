package com.gmail.jiangyang5157.cardboard.scene;

/**
 * @author Yang
 * @since 7/5/2016
 */
public interface Intersectable {
    RayIntersection onIntersect(Head head);
}