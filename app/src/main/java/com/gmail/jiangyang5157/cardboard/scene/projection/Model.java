package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.opengl.Matrix;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class Model {

    protected static final int BYTES_PER_FLOAT = 4;
    protected static final int BYTES_PER_SHORT = 2;

    public float[] model;
    public float[] modelView;
    public float[] modelViewProjection;

    protected boolean isVisible;

    public Model() {
        model = new float[16];
        modelView = new float[16];
        modelViewProjection = new float[16];
    }

    public void draw() {
        if (isVisible == false) {
            return;
        }
    }

    public void update(float[] view, float[] perspective) {
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
        return new float[]{model[12], model[13], model[14]};
    }
}
