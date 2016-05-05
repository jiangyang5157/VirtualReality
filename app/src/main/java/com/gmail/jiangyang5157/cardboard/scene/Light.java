package com.gmail.jiangyang5157.cardboard.scene;

/**
 * @author Yang
 * @since 5/3/2016
 */
public class Light {

    public static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    public float[] lightPosInCameraSpace;

    public Light(){
        lightPosInCameraSpace = new float[4];
    }
}
