package com.gmail.jiangyang5157.cardboard.scene;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

/**
 * Created by Yang on 3/22/2016.
 */
public abstract class ModelLayout {
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_CHAR = 2;

    float[] vertices;
    FloatBuffer verticesBuff;

    float[] normals;
    FloatBuffer normalsBuff;

    float[] textures;
    FloatBuffer texturesBuff;

    char[] indexes;
    CharBuffer indexesBuff;

    public float[] getVertices() {
        return vertices;
    }

    public FloatBuffer getVerticesBuff() {
        return verticesBuff;
    }

    public float[] getNormals() {
        return normals;
    }

    public FloatBuffer getNormalsBuff() {
        return normalsBuff;
    }

    public float[] getTextures() {
        return textures;
    }

    public FloatBuffer getTexturesBuff() {
        return texturesBuff;
    }

    public char[] getIndexes() {
        return indexes;
    }

    public CharBuffer getIndexesBuff() {
        return indexesBuff;
    }
}
