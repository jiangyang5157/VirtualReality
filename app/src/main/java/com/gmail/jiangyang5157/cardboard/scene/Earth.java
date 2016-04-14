package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;

import com.gmail.jiangyang5157.cardboard.vr.R;

import java.util.ArrayList;

/**
 * Created by Yang on 4/12/2016.
 */
public class Earth extends TextureSphere {

    private ArrayList<Placemark> placemarks = new ArrayList<>();

    public Earth(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int rings, int sectors, float radius, int textureDrawableResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, rings, sectors, radius, textureDrawableResource);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, 0);
        Matrix.rotateM(model, 0, 180.0f, 0, 1, 0);
    }

    public void addPlacemark(float latitude, float longitude, int recursionLevel, float radius, float[] color) {
        Placemark placemark = new Placemark(context, R.raw.icosphere_vertex, R.raw.icosphere_fragment, recursionLevel, radius, color, this);
        placemark.setCoordinate(new Coordinate(latitude, longitude, -radius));
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
