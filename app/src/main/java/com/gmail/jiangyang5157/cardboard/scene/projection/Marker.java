package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere implements Intersection.Clickable {

    public static final float RADIUS = Earth.RADIUS / 80;
    public static final float ALTITUDE = -1 * RADIUS;

    private String name;
    private String description;
    private Coordinate coordinate;

    private final Earth earth;

    private ObjModel objModel;

    private Intersection.Clickable onClickListener;

    public Marker(Context context, Earth earth) {
        super(context, 3);
        this.earth = earth;
        setRadius(RADIUS);
    }

    public void create() {
        super.create();
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White, null));
        }

        buildArrays();

        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.sphere_color_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.sphere_color_fragment_shader);
        buildProgram(shaders);
        bindHandles();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    public void setLocation(LatLng latLng, float altitude) {
        this.coordinate = new Coordinate(latLng.latitude, latLng.longitude, altitude, this.earth.radius);
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public void update(float[] view, float[] perspective) {
        super.update(view, perspective);
    }

    public void remove() {
        earth.removeMarker(this);
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setOnClickListener(Intersection.Clickable onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjModel getObjModel() {
        return objModel;
    }

    public void setObjModel(ObjModel model) {
        this.objModel = model;
    }
}
