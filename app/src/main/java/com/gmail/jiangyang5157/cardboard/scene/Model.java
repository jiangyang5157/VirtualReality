package com.gmail.jiangyang5157.cardboard.scene;

/**
 * Created by Yang on 3/21/2016.
 */
public abstract class Model {

    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

    private ModelLayout modelLayout;

    Model(ModelLayout theModelLayout) {
        modelLayout = theModelLayout;
    }

    public ModelLayout getModelLayout() {
        return modelLayout;
    }
}
