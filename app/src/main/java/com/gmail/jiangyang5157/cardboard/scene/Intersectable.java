package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;

/**
 * @author Yang
 * @since 5/2/2016
 */
public interface Intersectable {
    Intersection intersect(Head head);
}
