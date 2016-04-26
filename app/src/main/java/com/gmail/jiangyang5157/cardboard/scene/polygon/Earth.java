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

    private static final int DEFAULT_STACKS = 50;
    private static final int DEFAULT_SLICES = 50;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.texture_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.texture_fragment;

    private ArrayList<Placemark> placemarks = new ArrayList<>();

    public Earth(Context context, float radius, int textureDrawableResource) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, DEFAULT_STACKS, DEFAULT_SLICES, radius, textureDrawableResource);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, 0);

//        Matrix.rotateM(model, 0, 90, 1, 0, 0);
//        Matrix.rotateM(model, 0, 180, 0, 0, 1);
    }

    public void addPlacemark(double latitude, double longitude, float radius, float[] color, String name) {
        Placemark placemark = new Placemark(context, radius, color);
        Coordinate coordinate = new Coordinate(latitude, longitude, -placemark.getRadius(), this.getRadius(), 0.0);
        placemark.setCoordinate(coordinate);
        placemark.setName(name);
        placemark.create();

        placemarks.add(placemark);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);

        for (Mark mark : placemarks) {
            mark.update(view, perspective);
        }
    }

    @Override
    public void draw(float[] lightPosInEyeSpace) {
        super.draw(lightPosInEyeSpace);

        for (Mark mark : placemarks) {
            mark.draw(lightPosInEyeSpace);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        for (Mark mark : placemarks) {
            mark.destroy();
        }
    }

    public ArrayList<Placemark> getPlacemarks() {
        return placemarks;
    }
}
