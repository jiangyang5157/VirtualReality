package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Yang on 4/12/2016.
 */
public abstract class Model extends GlEsModel {

    static final int BYTES_PER_FLOAT = 4;
    static final int BYTES_PER_SHORT = 2;

    public float[] model = new float[16];
    public float[] modelView = new float[16];
    public float[] modelViewProjection = new float[16];

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

    public abstract void draw(float[] lightPosInEyeSpace);

    public abstract void destroy();
}
