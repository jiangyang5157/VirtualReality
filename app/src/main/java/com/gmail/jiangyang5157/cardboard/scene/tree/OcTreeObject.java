package com.gmail.jiangyang5157.cardboard.scene.tree;

import com.gmail.jiangyang5157.cardboard.scene.model.GlModel;
import com.gmail.jiangyang5157.cardboard.scene.model.Marker3d;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeObject {

    protected Marker3d model;

    protected float[] center;

    public OcTreeObject(Marker3d model) {
        this.model = model;
        this.center = model.getTranslation();
    }

    public GlModel getModel() {
        return model;
    }
}
