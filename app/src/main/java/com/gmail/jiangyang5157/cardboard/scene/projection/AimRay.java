package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

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

    public static final float POINT_SIZE = 20.0f;
    public static final float SPACE = POINT_SIZE;

    public static final int COLOR_NOTMAL_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;
    public static final int COLOR_FOCUCED_RES_ID = com.gmail.jiangyang5157.tookit.R.color.Teal;

    private AimIntersection intersection;

    public AimRay(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
        setPointSize(POINT_SIZE);
    }

    public void create() {
        create(AppUtils.getColor(context, COLOR_NOTMAL_RES_ID));
    }

    public void setPosition(float[] position) {
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, position[0], position[1], position[2]);
    }

    public void intersectAt(AimIntersection intersection) {
        this.intersection = intersection;

        if (intersection == null){
            return;
        }

        if (intersection.model instanceof Panel) {
            setColor(AppUtils.getColor(context, COLOR_FOCUCED_RES_ID));
        } else if (intersection.model instanceof Marker) {
            setColor(AppUtils.getColor(context, COLOR_FOCUCED_RES_ID));
        } else {
            setColor(AppUtils.getColor(context, COLOR_NOTMAL_RES_ID));
        }

        Matrix.setIdentityM(model, 0);
        Vector i_camera = intersection.cameraPosVec.minus(intersection.intersecttPosVec);
        Vector newIntersectPosVec = intersection.intersecttPosVec.plus(i_camera.direction().times(SPACE));
        setPosition(new float[]{(float) newIntersectPosVec.getData()[0], (float) newIntersectPosVec.getData()[1], (float) newIntersectPosVec.getData()[2]});
    }

    public AimIntersection getIntersection() {
        return intersection;
    }
}
