package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

    float[] color;

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

    public abstract void draw(float[] lightPosInEyeSpace);

    public abstract void destroy();
}
