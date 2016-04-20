package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.scene.projection.TextureSphere;
import com.gmail.jiangyang5157.cardboard.vr.R;

import java.util.ArrayList;

/**
 * Created by Yang on 4/12/2016.
 */
public class Earth extends TextureSphere {

    public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    public static final double WGS84_FLATTENING = 1.0 / 298.257222101;
    public static final double WGS84_ECCENTRICITY = Math.sqrt(1 - Math.pow((1 - WGS84_FLATTENING), 2));

    private ArrayList<Mark> marks = new ArrayList<>();

    public Earth(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int rings, int sectors, float radius, int textureDrawableResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, rings, sectors, radius, textureDrawableResource);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, 0);
    }

    public void addMark(double latitude, double longitude, int recursionLevel, float radius, float[] color, String label) {
        Mark mark = new Mark(context, R.raw.color_vertex, R.raw.color_fragment, recursionLevel, radius, color);
        mark.setLabel(label);
        mark.setCoordinate(new Coordinate(latitude, longitude, -radius, this.getRadius(), 0.0));
        mark.create();

        marks.add(mark);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);

        for (Mark mark : marks) {
            mark.update(view, perspective);
        }
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        super.draw(lightPosInEyeSpace);

        for (Mark mark : marks) {
            mark.draw(lightPosInEyeSpace);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        for (Mark mark : marks) {
            mark.destroy();
        }
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }
}
