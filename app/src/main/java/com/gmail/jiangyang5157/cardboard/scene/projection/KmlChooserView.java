package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.tookit.opengl.Model;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Yang
 * @since 6/24/2016
 */
public class KmlChooserView extends Dialog {

    private Event eventListener;

    public interface Event {
        void onKmlSelected(String fileName);
    }

    private static final float WIDTH = 400f;

    public KmlChooserView(Context context) {
        super(context);
    }

    public void create(int color) {
        super.create(WIDTH, color);
    }

    @Override
    protected void createPanels() {
        File directory = new File(Constant.getAbsolutePath(context, Constant.getKmlPath("")));
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
            return;
        }

        String[] fileNames = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".kml");
            }
        });

        for (final String fileName : fileNames) {
            TextField tf = new TextField(context);
            tf.create(fileName, WIDTH - PADDING_BOARD * 2, TextField.TEXT_SIZE_SMALL, Layout.Alignment.ALIGN_CENTER);
            addPanel(tf);
            tf.setOnClickListener(new Intersection.Clickable() {
                @Override
                public void onClick(Model model) {
                    if (eventListener != null) {
                        eventListener.onKmlSelected(fileName);
                    }
                }
            });
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
