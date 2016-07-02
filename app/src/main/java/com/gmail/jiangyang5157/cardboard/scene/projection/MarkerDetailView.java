package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.GLES20;
import android.text.Layout;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.vr.R;
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

    private Marker marker;

    public MarkerDetailView(Context context, Marker marker) {
        super(context);
        this.marker = marker;
    }

    @Override
    public void create(ArrayMap<Integer, Integer> shaders) {
        super.create(shaders);

        setCreated(true);
        setVisible(true);
    }

    @Override
    protected void createPanels() {
        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.panel_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.panel_fragment_shader);

        if (marker.getName() != null) {
            TextField tf1 = new TextField(context);
            tf1.setText(marker.getName());
            tf1.width = WIDTH;
            tf1.setScale(SCALE);
            tf1.setTextSize(TextField.TEXT_SIZE_LARGE);
            tf1.setAlignment(Layout.Alignment.ALIGN_CENTER);
            tf1.create(shaders);
            addPanel(tf1);
        }
        if (marker.getDescription() != null) {
            TextField tf2 = new TextField(context);
            tf2.setText(marker.getDescription());
            tf2.width = WIDTH;
            tf2.setScale(SCALE);
            tf2.setTextSize(TextField.TEXT_SIZE_TINY);
            tf2.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            tf2.create(shaders);
            addPanel(tf2);
        }
        if (marker.getObjModel() != null) {
            TextField tf3 = new TextField(context);
            tf3.setText(marker.getObjModel().getTitle());
            tf3.width = WIDTH;
            tf3.setScale(SCALE);
            tf3.setTextSize(TextField.TEXT_SIZE_TINY);
            tf3.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            tf3.create(shaders);
            tf3.setOnClickListener(new GlModel.ClickListener() {
                @Override
                public void onClick(Model model) {
                    if (eventListener != null) {
                        eventListener.showObjModel(marker.getObjModel());
                    }
                }
            });
            addPanel(tf3);
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
