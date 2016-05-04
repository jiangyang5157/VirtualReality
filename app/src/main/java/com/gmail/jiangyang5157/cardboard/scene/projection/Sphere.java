package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 4/30/2016
 */
public abstract class Sphere extends GLModel implements Intersectable {

    float radius;

    public Sphere(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public AimIntersection intersect(float[] cameraPos, float[] forwardDir) {
        Vector cameraPosVec = new Vector(cameraPos[0], cameraPos[1], cameraPos[2]);
        Vector forwardDirVec = new Vector(forwardDir[0], forwardDir[1], forwardDir[2]);
        float[] position = getPosition();
        Vector positionVec = new Vector(position[0], position[1], position[2]);
        Vector posToCameraVec = cameraPosVec.minus(positionVec);

        final double b = forwardDirVec.dot(posToCameraVec);
        final double c = posToCameraVec.dot(posToCameraVec) - (radius * radius);

        // solve the quadratic equation
        final double f = b * b - c;
        if (f < 0) {
            return null; // ray misses sphere
        }

        final double sqrtF = Math.sqrt(f);
        final double t0 = -b + sqrtF;
        final double t1 = -b - sqrtF;

        // t is the distance along the ray to the closest intersection with the sphere
        // pick the smaller of the two results if both are positive
        final double t = t0 < 0.0f ? Math.max(t1, 0.0f) : (t1 < 0.0f ? t0 : Math.min(t0, t1));
        if (t == 0) {
            return null;//both intersections are behind the matrix
        }

        return new AimIntersection(this, cameraPosVec, cameraPosVec.plus(forwardDirVec.times(t)), t);
    }
}
