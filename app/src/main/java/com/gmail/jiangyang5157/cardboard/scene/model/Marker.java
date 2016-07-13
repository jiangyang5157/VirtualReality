package com.gmail.jiangyang5157.cardboard.scene.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 7/13/2016
 */
public interface Marker {

    void setLocation(LatLng latLng, float altitude);

    void setName(String name);

    void setDescription(String description);

    void setObjModel(ObjModel model);

    String getName();

    String getDescription();

    ObjModel getObjModel();
}
