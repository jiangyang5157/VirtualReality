package com.gmail.jiangyang5157.cardboard.scene;

import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.model.GlModel;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class RayIntersection implements Comparable<RayIntersection> {

    private final GlModel model;
    private final double t; // indicate the "distance" along the ray to the closest intersection

    public RayIntersection(GlModel model, double t) {
        this.model = model;
        this.t = t;
    }

    public GlModel getModel() {
        return model;
    }

    public double getT() {
        return t;
    }

    /**
     * @return -1; less than; 1; greater than; 0; equals to
     */
    @Override
    public int compareTo(@NonNull RayIntersection that) {
        double ret = this.t - that.t;
        if (ret < 0) {
            return -1;
        } else if (ret > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
