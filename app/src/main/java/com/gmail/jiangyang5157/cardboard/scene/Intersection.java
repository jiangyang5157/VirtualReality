package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.opengl.Model;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class Intersection implements Comparable {

    public interface Intersectable {
        Intersection onIntersect(Head head);
    }

    public interface Clickable {
        void onClick(Model model);
    }

    private final Model model;
    private final Vector cameraPosVec;
    private final Vector intersecttPosVec;
    private final double t;

    public Intersection(Model model, Vector cameraPosVec, Vector intersecttPosVec, double t) {
        this.model = model;
        this.cameraPosVec = cameraPosVec;
        this.intersecttPosVec = intersecttPosVec;
        this.t = t;
    }

    public Model getModel() {
        return model;
    }

    public Vector getIntersecttPosVec() {
        return intersecttPosVec;
    }

    public Vector getCameraPosVec() {
        return cameraPosVec;
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
