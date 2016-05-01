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

    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.point_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.point_fragment;
    private static final int DEFAULT_POINT_SIZE = 10;
    private static final float[] DEFAULT_COLOR = new float[]{1f, 0, 0, 1f};

    public AimPoint(Context context) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_POINT_SIZE, DEFAULT_COLOR);

        Matrix.setIdentityM(model, 0);
    }

    public void forward(float[] forward){
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, forward[0], forward[1], forward[2]);
    }
}
