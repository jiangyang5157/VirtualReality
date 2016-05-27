package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.graphics.Color;

/**
 * @author Yang
 * @since 5/27/2016
 */
public class Obj extends GLModel {

    private String title;
    private String obj;

    protected Obj(Context context, String obj, String title, String color) {
        super(context);
        this.title = title;
        this.obj = obj;
        setColor(Color.parseColor(color));
    }

    @Override
    protected void initializeHandle() {

    }

    @Override
    protected void buildArrays() {

    }

    @Override
    protected void bindBuffers() {

    }

    @Override
    public void draw() {

    }

    public String getTitle() {
        return title;
    }
}
