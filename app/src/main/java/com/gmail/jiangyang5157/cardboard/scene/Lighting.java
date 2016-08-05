package com.gmail.jiangyang5157.cardboard.scene;

/**
 * @author Yang
 * @since 4/30/2016
 */
public interface Lighting {
    // w component: 1 for points and 0 for vectors
    float[] LIGHT_POS_IN_CAMERA_SPACE_CENTER = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    float[] getLightPosInCameraSpace();
}
