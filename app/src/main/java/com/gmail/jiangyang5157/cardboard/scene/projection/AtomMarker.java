package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class AtomMarker extends Marker {

    public AtomMarker(Context context) {
        super(context);
    }

    @Override
    public void create(int program) {
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.White, null));
        }
        super.create(program);

        setCreated(true);
        setVisible(true);


    }
}
