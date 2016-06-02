package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.opengl.Matrix;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class Model {

    protected float[] rotation;
    protected float[] scale;
    protected float[] translation;
    protected float[] model;
    protected float[] modelView;
    protected float[] modelViewProjection;

    private boolean isCreated;
    private boolean isVisible;

    public Model() {
        rotation = new float[16];
        scale = new float[3];
        translation = new float[16];
        model = new float[16];
        modelView = new float[16];
        modelViewProjection = new float[16];

        Matrix.setIdentityM(rotation, 0);
        setScale(1, 1, 1);
        Matrix.setIdentityM(translation, 0);
        Matrix.setIdentityM(model, 0);
        Matrix.setIdentityM(modelView, 0);
        Matrix.setIdentityM(modelViewProjection, 0);
    }

    public abstract void draw();

    public void destroy() {
        setVisible(false);
        setCreated(false);
    }

    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }

    public void setScale(float x, float y, float z) {
        scale[0] = x;
        scale[1] = y;
        scale[2] = z;
    }

    public void setCreated(boolean created) {
        isCreated = created;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public float[] getPosition() {
        return new float[]{translation[12], translation[13], translation[14]};
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getScale() {
        return scale;
    }

    public float[] getTranslation() {
        return translation;
    }

    public float[] getModel() {
        return model;
    }

    public float[] getModelView() {
        return modelView;
    }

    public float[] getModelViewProjection() {
        return modelViewProjection;
    }
}

