package com.gmail.jiangyang5157.cardboard.scene;

/**
 * Created by Yang on 4/9/2016.
 *
 * angle [0, 180] = 180 / PI * radian
 * radian [0, PI] = PI / 180 * angle
 */
interface Geometry {
    //golden ratio: https://en.wikipedia.org/wiki/Regular_icosahedron
    float GOLDEN_RATIO = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

    float PI = (float) Math.PI;
    float PIx2 = PI * 2.0f;

}
