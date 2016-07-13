package com.gmail.jiangyang5157.cardboard.scene.model;

/**
 * @author Yang
 * @since 7/13/2016
 */
public interface Marker {
    float RADIUS = 1;
    float ALTITUDE = -1 * RADIUS;

    void setName(String name);

    void setDescription(String description);

    void setObjModel(ObjModel model);

    String getName();

    String getDescription();

    ObjModel getObjModel();
}
