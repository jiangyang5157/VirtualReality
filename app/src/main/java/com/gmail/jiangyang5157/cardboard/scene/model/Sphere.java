package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class Sphere extends GlModel implements GlModel.BindableBuffer {

    protected float radius;

    protected Sphere(Context context) {
        super(context);
    }

    @Override
    public RayIntersection onIntersection(Vector cameraPos_vec, Vector headForward_vec, final float[] headView) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        Vector pos_camera_vec = new Vector3d(
                cameraPos_vec.getData(0) - getX(),
                cameraPos_vec.getData(1) - getY(),
                cameraPos_vec.getData(2) - getZ()
        );

        final double b = headForward_vec.dot(pos_camera_vec);
        final double c = pos_camera_vec.dot(pos_camera_vec) - (radius * radius);

        // solve the quadratic equation
        final double f = b * b - c;
        if (f <= Vector.EPSILON) {
            // ray misses sphere
            return null;
        }

        final double sqrtF = Math.sqrt(f);
        final double t0 = -b + sqrtF;
        final double t1 = -b - sqrtF;

        // pick the smaller of the two results if both are positive
        final double t = t0 < 0.0f ? Math.max(t1, 0.0f) : (t1 < 0.0f ? t0 : Math.min(t0, t1));
        if (t == 0) {
            return null; // both intersections are behind the matrix
        }

        return new RayIntersection(this, t);
    }

    public boolean contain(float r, float x, float y, float z) {
        double point_center_x = getX() - x;
        double point_center_y = getY() - y;
        double point_center_z = getZ() - z;
        double dot = point_center_x * point_center_x + point_center_y * point_center_y + point_center_z * point_center_z;
        double squaredRadius = r * r;
        return dot < squaredRadius;
    }

    public void setRadius(float radius) {
        this.scale = this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
