package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;

public class Placemark extends Icosphere {

    private Coordinate coordinate;

    public Placemark(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, color);
    }

    public void setCoordinate(Coordinate coordinate){
        this.coordinate = coordinate;


    }

}
