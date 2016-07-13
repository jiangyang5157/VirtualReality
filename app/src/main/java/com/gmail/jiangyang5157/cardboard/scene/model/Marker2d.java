package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;

/**
 * @author Yang
 * @since 7/13/2016
 */
public class Marker2d extends Point implements Marker {

    public static final float RADIUS = 24f;
    public static final float ALTITUDE = -1 * RADIUS;

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

    protected Marker2d(Context context) {
        super(context);
        pointSize = RADIUS;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setObjModel(ObjModel model) {
        this.objModel = model;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ObjModel getObjModel() {
        return objModel;
    }
}
