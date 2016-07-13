package com.gmail.jiangyang5157.cardboard.scene.model;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;

/**
 * @author Yang
 * @since 7/13/2016
 */
public class Marker2d implements Marker {

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

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
