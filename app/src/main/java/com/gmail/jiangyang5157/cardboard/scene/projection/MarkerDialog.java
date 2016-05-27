package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.tookit.app.AppUtils;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class MarkerDialog extends Dialog{

    private static final float TEXT_SIZE_LARGE = 12f;
    private static final float TEXT_SIZE_MEDIUM = 10f;
    private static final float TEXT_SIZE_SMALL = 8f;
    private static final float TEXT_SIZE_TINY = 6f;

    private static final float WIDTH = 400f;

    private Marker marker;

    public MarkerDialog(Context context) {
        super(context);
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public void destroy() {
        super.destroy();
        setMarker(null);
    }

    @Override
    public void create() {
        createContent();
        adjustBounds();
        create(width, height, AppUtils.getColor(context, COLOR_BACKGROUND_RES_ID));
    }

    private void createContent() {
        if (marker.getName() != null) {
            TextField tf1 = new TextField(context);
            tf1.create(marker.getName(), WIDTH - PADDING_BOARD * 2, TEXT_SIZE_LARGE, Layout.Alignment.ALIGN_CENTER);
            addPanel(tf1);
        }
        if (marker.getDescription() != null) {
            TextField tf2 = new TextField(context);
            tf2.create(marker.getDescription(), WIDTH - PADDING_BOARD * 2, TEXT_SIZE_SMALL, Layout.Alignment.ALIGN_NORMAL);
            addPanel(tf2);
        }
        if (marker.getObj() != null) {
            TextField tf3 = new TextField(context);
            tf3.create(marker.getObj().getTitle(), WIDTH - PADDING_BOARD * 2, TEXT_SIZE_SMALL, Layout.Alignment.ALIGN_NORMAL);
            addPanel(tf3);
        }

        TextField tfTest = new TextField(context);
        tfTest.create("3rdasdfghjklqasd\nasdas\ndasdasdsnadasdasdasdasdhj5h9348huigne-9asd80435tasunnzxbwe]]t,rtyrdyrtybsgpoweir/das/[asd]]1234567890'",
                WIDTH - PADDING_BOARD * 2, TEXT_SIZE_TINY, Layout.Alignment.ALIGN_NORMAL);
        addPanel(tfTest);
    }

    private void adjustBounds() {
        float h = 0;
        h += PADDING_BOARD;
        for (Panel panel : panels) {
            h += panel.height;
            h += PADDING_BOARD;
        }
        width = WIDTH;
        height = h;
    }

    @Override
    public void setPosition(float[] cameraPos, float[] forward, float[] up, float[] right, float[] eulerAngles) {
        super.setPosition(cameraPos, forward, up, right, eulerAngles);

        //
        cameraPos[0] -= forward[0] * PADDING_LAYER;
        cameraPos[1] -= forward[1] * PADDING_LAYER;
        cameraPos[2] -= forward[2] * PADDING_LAYER;

        //
        cameraPos[0] += up[0] * height / 2;
        cameraPos[1] += up[1] * height / 2;
        cameraPos[2] += up[2] * height / 2;

        cameraPos[0] -= up[0] * PADDING_BOARD;
        cameraPos[1] -= up[1] * PADDING_BOARD;
        cameraPos[2] -= up[2] * PADDING_BOARD;

        for (Panel panel : panels){
            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            panel.setPosition(cameraPos, forward, up, right, eulerAngles);

            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            cameraPos[0] -= up[0] * PADDING_BOARD;
            cameraPos[1] -= up[1] * PADDING_BOARD;
            cameraPos[2] -= up[2] * PADDING_BOARD;
        }
    }
}
