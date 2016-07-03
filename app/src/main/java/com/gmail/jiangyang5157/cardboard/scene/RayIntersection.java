package com.gmail.jiangyang5157.cardboard.scene;

import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.opengl.Model;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class RayIntersection implements Comparable<RayIntersection> {

    private final Model model;
    private final double t; // more smaller t, more closer to the ray

    public RayIntersection(Model model, double t) {
        this.model = model;
        this.t = t;
    }

    public Model getModel() {
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
