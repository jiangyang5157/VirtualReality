package com.gmail.jiangyang5157.cardboard.scene;

/**
 * Created by Yang on 3/21/2016.
 */
public interface Model extends ModelLayout{

    String MODEL_PARAM_NAME = "u_ModelMatrix";
    String MODEL_VIEW_PARAM_NAME = "u_MVMatrix";
    String MODEL_VIEW_PROJECTION_PARAM_NAME = "u_MVPMatrix";
    String TEXTURE_ID_PARAM_NAME = "u_TexId";

    String POSOTION_PARAM_NAME = "a_Position";
    String NORMAL_PARAM_NAME = "a_Normal";
    String TEXTURE_COORDS_PARAM_NAME = "a_TexCoords";
}
