package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;

import com.gmail.jiangyang5157.tookit.app.AppUtils;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class MarkerDialog extends Dialog{

    public static final int COLOR_BACKGROUND_RES_ID = com.gmail.jiangyang5157.tookit.R.color.DeepOrange;

    private Marker marker;

    public MarkerDialog(Context context, Marker marker) {
        super(context);
        this.marker = marker;
    }

    @Override
    public void create() {
        TextField textField = new TextField(context);
        textField.create(marker.name);
        addPanel(textField);

        width = textField.width + PADDING_BOARD;
        height = textField.height + PADDING_BOARD;

        create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));
    }

    @Override
    public void setPosition(float[] cameraPos, float[] forward, float[] up, float[] right, float[] eulerAngles) {
        super.setPosition(cameraPos, forward, up, right, eulerAngles);

        for (Panel panel : panels) {
            // TODO: 5/14/2016 handle muti-chile-panel
            cameraPos[0] -= forward[0] * PADDING_LAYER;
            cameraPos[1] -= forward[1] * PADDING_LAYER;
            cameraPos[2] -= forward[2] * PADDING_LAYER;
            panel.setPosition(cameraPos, forward, up, right, eulerAngles);
        }
    }
}
