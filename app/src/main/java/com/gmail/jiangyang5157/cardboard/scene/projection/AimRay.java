package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.AimIntersection;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class AimRay extends Point {

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.point_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.point_fragment_shader;

    public static final float FACTOR_POINT_SIZE = 0.05f;
    public static final float SPACE = Earth.MARKER_RADIUS;

    private final Earth earth;

    private AimIntersection intersection;

    public AimRay(Context context, Earth earth) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange));
        this.earth = earth;

        adjustPointSize(this.earth.getRadius());
    }

    public void setPosition(float[] position) {
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, position[0], position[1], position[2]);
    }

    private void adjustPointSize(double distance) {
        setPointSize((float) (distance * FACTOR_POINT_SIZE));
    }

    public void intersectAt(AimIntersection intersection) {
        this.intersection = intersection;

        if (intersection.model instanceof Marker) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Black));
        } else {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.DeepOrange));
        }

        Vector intersectToCameraVec = intersection.cameraPosVec.minus(intersection.intersecttPosVec);

        Vector newIntersectPosVec = intersection.intersecttPosVec.plus(intersectToCameraVec.direction().times(SPACE));
        double[] newPosition = newIntersectPosVec.getData();
        setPosition(new float[]{(float) newPosition[0], (float) newPosition[1], (float) newPosition[2]});

        double distance = newIntersectPosVec.length();
        adjustPointSize(distance);
    }

    public AimIntersection getIntersection() {
        return intersection;
    }
}
