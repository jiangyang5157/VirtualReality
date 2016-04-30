package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public abstract class Model extends ShaderHandle {

    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;

    float[] vertices;
    float[] normals;
    short[] indices;
    float[] textures;

    FloatBuffer verticesBuffer;
    FloatBuffer normalsBuffer;
    ShortBuffer indicesBuffer;
    FloatBuffer texturesBuffer;

    int verticesBuffHandle;
    int normalsBuffHandle;
    int indicesBuffHandle;
    int texturesBuffHandle;

    int indicesBufferCapacity;

    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

    float[] color;

    boolean isVisible;

    Model(Context context, int vertexShaderRawResource, int fragmentShaderRawResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource);
    }

    protected abstract void buildArrays();

    protected abstract void bindBuffers();

    public void create() {
        buildArrays();
        bindBuffers();
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
}
