package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;

/**
 * Created by Yang on 4/12/2016.
 */
public class Earth extends TextureSphere {

    public Earth(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int rings, int sectors, float radius, int textureDrawableResource) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, rings, sectors, radius, textureDrawableResource);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, 0);
    }

}
