package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.vr.R;

/**
 * @author Yang
 * @date 4/21/2016
 */
public class Placemark extends Mark {

    private static final int RECURSION_LEVEL = 0;
    private static final int DEFAULT_VERTEX_SHADER_RAW_RESOURCE = R.raw.color_vertex;
    private static final int DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE = R.raw.color_fragment;


    private String label;

    public Placemark(Context context, float radius, float[] color) {
        super(context, DEFAULT_VERTEX_SHADER_RAW_RESOURCE, DEFAULT_FRAGMENT_SHADER_RAW_RESOURCE, RECURSION_LEVEL, radius, color);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
