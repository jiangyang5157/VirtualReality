package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class Intersection implements Comparable {

    public interface Intersectable {
        Intersection intersect(Head head);
    }

    public final Intersectable model;
    public final Vector cameraPosVec;
    public final Vector intersecttPosVec;

    private final double t;

    public Intersection(Intersectable model, Vector cameraPosVec, Vector intersecttPosVec, double t) {
        this.model = model;
        this.cameraPosVec = cameraPosVec;
        this.intersecttPosVec = intersecttPosVec;
        this.t = t;
    }


    @Override
    public int compareTo(Object another) {
        Intersection that = (Intersection) another;

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
