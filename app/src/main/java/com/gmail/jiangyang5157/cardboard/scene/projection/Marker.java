package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere implements GlModel.ClickListener {
    private static final String TAG = "[Marker]";

    public static final float RADIUS = Earth.RADIUS / 80;
    public static final float ALTITUDE = -1 * RADIUS;

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

    private GlModel.ClickListener onClickListener;

    public Marker(Context context) {
        super(context, 3);
        setRadius(RADIUS);
    }

    @Override
    public void create(int program) {
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White, null));
        }
        super.create(program);

        setCreated(true);
        setVisible(true);
    }

    public void setLocation(LatLng latLng, float altitude) {
        this.coordinate = new Coordinate(latLng.latitude, latLng.longitude, altitude, Earth.RADIUS);
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setOnClickListener(GlModel.ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ClickListener getOnClickListener() {
        return onClickListener;
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

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }
}
