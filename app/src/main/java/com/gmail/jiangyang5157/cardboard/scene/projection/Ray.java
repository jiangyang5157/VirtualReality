package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class Ray extends Point {

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.ray_point_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.ray_point_fragment_shader;

    private static final float POINT_SIZE_NORMAL = 18f;
    private static final float POINT_SIZE_FOCUSED = 48f;
    private static final float POINT_SIZE_GRAdiENT_UNIT = (POINT_SIZE_FOCUSED - POINT_SIZE_NORMAL) / 6;

    protected static final float SPACE = (float) (Math.PI * POINT_SIZE_NORMAL);

    private static final int COLOR_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private Intersection intersection;

    public Ray(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
        pointSize = POINT_SIZE_NORMAL;
    }

    public void create() {
        create(AppUtils.getColor(context, COLOR_RES_ID));
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;

        if (intersection.getModel() instanceof Intersection.Clickable) {
            if (pointSize < POINT_SIZE_FOCUSED){
                pointSize += POINT_SIZE_GRAdiENT_UNIT;
            }
        } else {
            if (pointSize > POINT_SIZE_NORMAL){
                pointSize -= POINT_SIZE_GRAdiENT_UNIT;
            }
        }

        Vector i_camera = intersection.getCameraPosVec().minus(intersection.getIntersecttPosVec());

        Vector rayPosVec = new Vector(intersection.getIntersecttPosVec().plus(i_camera.direction().times(Ray.SPACE)));
        double[] rayPosVecData = rayPosVec.getData();

        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0, (float) rayPosVecData[0], (float) rayPosVecData[1], (float) rayPosVecData[2]);
    }

    public Intersection getIntersection() {
        return intersection;
    }
}
