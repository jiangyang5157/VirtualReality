package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class MarkerDetailView extends Dialog {

    private Event eventListener;
    public interface Event {
        void showObjModel(ObjModel model);
    }

    private static final float WIDTH = 400f;

    private Marker marker;

    public MarkerDetailView(Context context, Marker marker) {
        super(context);
        this.marker = marker;
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void create(int color) {
        createContent();
        adjustBounds();
        create(width, height, color);
    }

    private void createContent() {
        if (marker.getName() != null) {
            TextField tf1 = new TextField(context);
            tf1.create(marker.getName(), WIDTH - PADDING_BOARD * 2, TextField.TEXT_SIZE_LARGE, Layout.Alignment.ALIGN_CENTER);
            addPanel(tf1);
        }
        if (marker.getDescription() != null) {
            TextField tf2 = new TextField(context);
            tf2.create(marker.getDescription(), WIDTH - PADDING_BOARD * 2, TextField.TEXT_SIZE_SMALL, Layout.Alignment.ALIGN_NORMAL);
            addPanel(tf2);
        }
        if (marker.getObjModel() != null) {
            TextField tf3 = new TextField(context);
            tf3.create(marker.getObjModel().getTitle(), WIDTH - PADDING_BOARD * 2, TextField.TEXT_SIZE_SMALL, Layout.Alignment.ALIGN_NORMAL);
            addPanel(tf3);

            tf3.setOnClickListener(new Intersection.Clickable() {
                @Override
                public void onClick(Model model) {
                    if (eventListener != null){
                        eventListener.showObjModel(marker.getObjModel());
                    }
                }
            });
        }
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
    public void setPosition(float[] cameraPos, float[] forward, float[] quaternion, float[] up, float[] right) {
        super.setPosition(cameraPos, forward, quaternion, up, right);

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

        for (Panel panel : panels) {
            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            panel.setPosition(cameraPos, forward, quaternion, up, right);

            cameraPos[0] -= up[0] * panel.height / 2;
            cameraPos[1] -= up[1] * panel.height / 2;
            cameraPos[2] -= up[2] * panel.height / 2;

            cameraPos[0] -= up[0] * PADDING_BOARD;
            cameraPos[1] -= up[1] * PADDING_BOARD;
            cameraPos[2] -= up[2] * PADDING_BOARD;
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
