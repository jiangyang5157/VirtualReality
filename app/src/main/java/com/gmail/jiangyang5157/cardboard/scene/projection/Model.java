package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.opengl.Matrix;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class Model {

    protected static final int BYTES_PER_FLOAT = 4;
    protected static final int BYTES_PER_SHORT = 2;

    public float[] rotation = new float[16];
    public float[] scale = new float[16];
    public float[] translation = new float[16];
    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

    protected boolean isVisible;

    public abstract void draw();

    public Model() {
        Matrix.setIdentityM(rotation, 0);
        Matrix.setIdentityM(scale, 0);
        Matrix.setIdentityM(translation, 0);
        Matrix.setIdentityM(model, 0);
        Matrix.setIdentityM(modelView, 0);
        Matrix.setIdentityM(modelViewProjection, 0);
    }

    public void update(float[] view, float[] perspective) {
        Matrix.setIdentityM(model, 0);

        Matrix.multiplyMM(model, 0, rotation, 0, model, 0);
        Matrix.multiplyMM(model, 0, scale, 0, model, 0);
        Matrix.multiplyMM(model, 0, translation, 0, model, 0);
        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public abstract void destroy();

    public float[] getPosition() {
        return new float[]{translation[12], translation[13], translation[14]};
    }
}
