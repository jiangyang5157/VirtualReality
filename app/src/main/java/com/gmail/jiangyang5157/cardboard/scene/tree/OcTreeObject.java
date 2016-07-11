package com.gmail.jiangyang5157.cardboard.scene.tree;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersectable;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.model.Sphere;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeObject implements Intersectable {

    protected Sphere sphere;

    protected float[] center;

    public OcTreeObject(Sphere sphere) {
        this.sphere = sphere;
        this.center = sphere.getPosition();
    }

    @Override
    public RayIntersection onIntersection(Head head) {
        return sphere.onIntersection(head);
    }
}
