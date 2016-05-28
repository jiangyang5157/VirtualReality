package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.app.AppUtils;

import java.io.InputStream;

/**
 * @author Yang
 * @since 5/27/2016
 */
public class ObjModel extends GLModel {

    private static final int VERTEX_SHADER_RAW_RESOURCE = R.raw.obj_color_vertex_shader;
    private static final int FRAGMENT_SHADER_RAW_RESOURCE = R.raw.obj_color_fragment_shader;

    private static final int COLOR_NORMAL_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private String title;
    private String obj;

    protected ObjModel(Context context, String title, String obj) {
        super(context, VERTEX_SHADER_RAW_RESOURCE, FRAGMENT_SHADER_RAW_RESOURCE);
        this.title = title;
        this.obj = obj;

    }

    public void create() {
        setColor(COLOR_NORMAL_RES_ID);

        InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier(obj, "raw", context.getPackageName()));

//        initializeProgram();
//
//        buildArrays();
//        bindBuffers();


        program = -1;
//        setVisible(true);
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
