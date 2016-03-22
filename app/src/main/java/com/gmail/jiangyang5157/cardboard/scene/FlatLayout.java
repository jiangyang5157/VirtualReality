package com.gmail.jiangyang5157.cardboard.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Yang on 3/22/2016.
 */
public class FlatLayout extends ModelLayout {

    public FlatLayout() {

        vertices = new float[]{
                200f, 0, -200f,
                -200f, 0, -200f,
                -200f, 0, 200f,
                200f, 0, -200f,
                -200f, 0, 200f,
                200f, 0, 200f,
        };

        normals = new float[]{
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
        };

        colors = new float[]{
                0.0f, 0.5882f, 0.5333f, 1.0f,
                0.0f, 0.5882f, 0.5333f, 1.0f,
                0.0f, 0.5882f, 0.5333f, 1.0f,
                0.0f, 0.5882f, 0.5333f, 1.0f,
                0.0f, 0.5882f, 0.5333f, 1.0f,
                0.0f, 0.5882f, 0.5333f, 1.0f,
        };

        verticesBuff = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuff.put(vertices).position(0);

        normalsBuff = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalsBuff.put(normals).position(0);

        colorsBuff = ByteBuffer.allocateDirect(colors.length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorsBuff.put(colors).position(0);
    }
}
