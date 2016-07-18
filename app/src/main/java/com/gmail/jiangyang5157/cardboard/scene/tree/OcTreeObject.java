package com.gmail.jiangyang5157.cardboard.scene.tree;

import com.gmail.jiangyang5157.cardboard.scene.Intersectable;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.model.GlModel;
import com.gmail.jiangyang5157.tookit.math.Vector;

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
    public RayIntersection onIntersection(Vector cameraPos_vec, Vector headForward_vec, final float[] headView) {
        return model.onIntersection(cameraPos_vec, headForward_vec, headView);
    }

    public GlModel getModel() {
        return model;
    }
}
