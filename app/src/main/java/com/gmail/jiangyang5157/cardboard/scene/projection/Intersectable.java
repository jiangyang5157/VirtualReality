package com.gmail.jiangyang5157.cardboard.scene.projection;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;

/**
 * @author Yang
 * @since 5/2/2016
 */
public interface Intersectable {
    AimIntersection intersect(Head head);
}
