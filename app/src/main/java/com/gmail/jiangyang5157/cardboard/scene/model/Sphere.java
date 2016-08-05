package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
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

    /*
      ################################################################
      The original algorithm as follow:
      Ray: R(t) = Ro + Rd * t
      Xr = Xro + Xrd * t
      Yr = Yro + Yrd * t
      Zr = Zro + Zrd * t
      Sphere: (Xs - Xc)^2 + (Yx - Yc)^2 + (Zs - Zc)^2 = r^2

      a * t^2 + b * t + c = 0
      a = Xrd^2 + Yrd^2 + Zrd^2
      b = 2 * (Xrd * (Xro - Xrc) + Yrd * (Yro - Yrc) + Zrd * (Zro - Zrc))
      c = (Xro - Xc)^2 + (Yro - Yc)^2 + (Zro - Zc)^2 - r^2

      Because Rd.normal = 1, so Rd.dot = 1
      a = vec_rd * vec_rd = dot(vec_rd, vec_rd)
      b = 2 * vec_rd * vec_c-ro = dot(vec_rd, vec_c-ro)
      c = vec_c-ro * vec_c-ro - r^2 = dot(vec_c-ro, vec_c-ro) - r^2

      f = b^2 - 4 * a * c
      if (f > 0) {
        t1 = (-b + sqrt(f)) / (2 * a);
        t2 = (-b - sqrt(f)) / (2 * a);
      }
      if (f == 0) {
        t1 = t2 = -b / (2 * a)
      }
      if (f < 0) {
        t1 and t2 are complex numbers -- consider there is no solution for t
      }

     ################################################################
     Improvement:
     + get rid of "/" op
     + reduce "*" op

     Because vec_rd is ray direction vec which has to be 1, so, transform equation f to F won't change positive or negative
     B = b / (2 * a)
     F = B^2 - c
     if (F > 0) {
         t1 = -B + sqrt(f);
         t2 = -B - sqrt(f);
     }
     */
    public RayIntersection getIntersection(Vector cameraPos_vec, Vector headForward_vec) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        Vector pos_camera_vec = new Vector3d(
                cameraPos_vec.getData(0) - getTranslationX(),
                cameraPos_vec.getData(1) - getTranslationY(),
                cameraPos_vec.getData(2) - getTranslationZ()
        );

        final double B = headForward_vec.dot(pos_camera_vec);
        final double c = pos_camera_vec.dot(pos_camera_vec) - (radius * radius);

        final double F = B * B - c;
        if (F < 0) {
            return null; // ray misses sphere
        }

        final double sqrtF = Math.sqrt(F);
        final double t0 = -B + sqrtF;
        final double t1 = -B - sqrtF;

        // pick the smaller of the two results if both are positive
        // pick the only one is positive
        // use 0 if both are negative
        final double t = t0 < 0.0f ? Math.max(t1, 0.0f) : (t1 < 0.0f ? t0 : Math.min(t0, t1));
        if (t == 0) {
            return null; // both intersections are behind the matrix
        }

        return new RayIntersection(this, t);
    }

    public static boolean contain(float r, float centerX, float centerY, float centerZ, float pointX, float pointY, float pointZ) {
        double point_center_x = centerX - pointX;
        double point_center_y = centerY - pointY;
        double point_center_z = centerZ - pointZ;
        double dot = point_center_x * point_center_x + point_center_y * point_center_y + point_center_z * point_center_z;
        double squaredRadius = r * r;
        return dot < squaredRadius;
    }

    public void setRadius(float radius) {
        scale = this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
