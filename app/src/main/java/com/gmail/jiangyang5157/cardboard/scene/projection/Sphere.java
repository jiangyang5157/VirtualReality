package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class Sphere extends GLModel implements Intersection.Intersectable {

    protected float radius;

    public Sphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }

    protected void create(float radius) {
        this.radius = radius;
        setScale(radius);
        buildArrays();

        initializeProgram();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    @Override
    public Intersection onIntersect(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        float[] position = getPosition();
        float[] cameraPos = head.getCamera().getPosition();
        Vector positionVec = new Vector3d(position[0], position[1], position[2]);
        Vector cameraPosVec = new Vector3d(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardVec = new Vector3d(head.forward[0], head.forward[1], head.forward[2]);
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
}
