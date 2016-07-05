package com.gmail.jiangyang5157.cardboard.scene;

import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.projection.GlModel;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class RayIntersection implements Comparable<RayIntersection> {

    private final GlModel model;
    // t is the distance along the ray to the closest intersection
    private final double t;

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

    @Override
    public int compareTo(@NonNull RayIntersection that) {
        double ret = this.t - that.t;
        if (ret < 0) {
            return -1; // this less than that
        } else if (ret > 0) {
            return 1; // this greater than that
        } else {
            return 0; // this equals to that
        }
    }
}
