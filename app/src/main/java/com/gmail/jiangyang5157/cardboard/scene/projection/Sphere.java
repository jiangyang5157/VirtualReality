package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class Sphere extends GlModel implements GlModel.BindingBuffers, Ray.IntersectListener {

    protected float radius;

    protected Sphere(Context context) {
        super(context);
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        float[] position = getPosition();
        float[] cameraPos = head.getCamera().getPosition();
        float[] forward = head.getForward();
        Vector positionVec = new Vector3d(position[0], position[1], position[2]);
        Vector cameraPosVec = new Vector3d(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector3d(forward[0], forward[1], forward[2]);
        Vector pos_camera = cameraPosVec.minus(positionVec);

        final double b = forwardVec.dot(pos_camera);
        final double c = pos_camera.dot(pos_camera) - (radius * radius);

        // solve the quadratic equation
        final double f = b * b - c;
        if (f <= Vector.EPSILON) {
            // ray misses sphere
            return null;
        }

        final double sqrtF = Math.sqrt(f);
        final double t0 = -b + sqrtF;
        final double t1 = -b - sqrtF;

        // t is the distance along the ray to the closest intersection with the sphere
        // pick the smaller of the two results if both are positive
        final double t = t0 < 0.0f ? Math.max(t1, 0.0f) : (t1 < 0.0f ? t0 : Math.min(t0, t1));
        if (t == 0) {
            // both intersections are behind the matrix
            return null;
        }

        return new Intersection(this, cameraPosVec, cameraPosVec.plus(forwardVec.times(t)), t);
    }

    public static boolean contain(float radius, @NonNull float[] center, @NonNull float[] point) {
        Vector positionVec = new Vector3d(center[0], center[1], center[2]);
        Vector pointVec = new Vector3d(point[0], point[1], point[2]);
        return pointVec.minus(positionVec).length() < radius;
    }

    public void setRadius(float radius) {
        this.scale = this.radius = radius;
    }
}
