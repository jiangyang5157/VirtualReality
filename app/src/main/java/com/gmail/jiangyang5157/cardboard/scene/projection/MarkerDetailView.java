package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
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

    public void create(int color) {
        super.create(WIDTH, color);
    }

    @Override
    protected void createPanels() {
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
                    if (eventListener != null) {
                        eventListener.showObjModel(marker.getObjModel());
                    }
                }
            });
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
