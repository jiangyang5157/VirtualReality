package com.gmail.jiangyang5157.cardboard.scene.tree;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersectable;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.model.GlModel;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeObject implements Intersectable {

    protected GlModel model;

    protected float[] center;

    public OcTreeObject(GlModel model) {
        this.model = model;
        this.center = model.getPosition();
    }

    @Override
    public RayIntersection onIntersection(Head head) {
        return model.onIntersection(head);
    }

    public GlModel getModel() {
        return model;
    }
}
