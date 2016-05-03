package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.projection.Point;
import com.gmail.jiangyang5157.cardboard.vr.R;

/**
 * @author Yang
 * @since 5/1/2016
 */
public class AimPoint extends Point {

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.point_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.point_fragment_shader;

    public AimPoint(Context context) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE, COLOR_RED);
        setPointSize(10);
        Matrix.setIdentityM(model, 0);
    }

    public void setPosition(float[] position){
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, position[0], position[1], position[2]);
    }
}
