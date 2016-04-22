package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.projection.Icosphere;

public class Mark extends Icosphere {

    private Coordinate coordinate;

    public Mark(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, color);
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]
        );
    }

    @Override
    public void update(float[] view, float[] perspective) {
        Matrix.rotateM(model, 0, 1, 0, 1, 1);
        super.update(view, perspective);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
