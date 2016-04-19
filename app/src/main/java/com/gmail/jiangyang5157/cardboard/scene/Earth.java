package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.vr.R;

import java.util.ArrayList;

/**
 * Created by Yang on 4/12/2016.
 */
public class Earth extends TextureSphere {

    public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    public static final double WGS84_FLATTENING = 1.0 / 298.257222101;
    public static final double WGS84_ECCENTRICITY = Math.sqrt(1 - Math.pow((1 - WGS84_FLATTENING), 2));

    private ArrayList<Placemark> placemarks = new ArrayList<>();

    public Earth(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int rings, int sectors, float radius, int textureDrawableResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, rings, sectors, radius, textureDrawableResource);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, 0);
    }

    public void addPlacemark(double latitude, double longitude, int recursionLevel, float radius, float[] color, String label) {
        Placemark placemark = new Placemark(context, R.raw.icosphere_vertex, R.raw.icosphere_fragment, recursionLevel, radius, color);
        placemark.setLabel(label);
        placemark.setCoordinate(new Coordinate(latitude, longitude, -radius, this.getRadius(), 0.0));
        placemark.create();

        placemarks.add(placemark);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);

        for (Placemark placemark : placemarks) {
            placemark.update(view, perspective);
        }
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        super.draw(lightPosInEyeSpace);

        for (Placemark placemark : placemarks) {
            placemark.draw(lightPosInEyeSpace);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        for (Placemark placemark : placemarks) {
            placemark.destroy();
        }
    }

    public ArrayList<Placemark> getPlacemarks() {
        return placemarks;
    }
}
