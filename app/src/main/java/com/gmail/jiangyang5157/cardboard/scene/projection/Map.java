package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.tookit.opengl.Model;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 7/2/2016
 */
public class Map extends GlModel implements GlModel.ClickListener, Creation {

    protected int creationState = STATE_BEFORE_PREPARE;

    private ArrayList<Marker> markers;

    public Map(Context context) {
        super(context);
    }

    @Override
    protected void bindHandles() {

    }

    @Override
    protected void buildData() {

    }

    @Override
    public void draw() {

    }

    @Override
    public int getCreationState() {
        return creationState;
    }

    @Override
    public void onClick(Model model) {

    }
}
