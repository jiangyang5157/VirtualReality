package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.cardboard.scene.Intersection;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.opengl.Model;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Yang
 * @since 6/24/2016
 */
public class KmlChooserView extends Dialog {

    public interface Event {
        void onKmlSelected(String fileName);
    }

    private Event eventListener;

    public KmlChooserView(Context context) {
        super(context);
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

        String lastKmlFileName = Constant.getLastKmlFileName(context);
        for (final String fileName : fileNames) {
            TextField tf = new TextField(context);
            float textSize;
            if (fileName.equals(lastKmlFileName)) {
                textSize = TextField.TEXT_SIZE_MEDIUM;
            } else {
                textSize = TextField.TEXT_SIZE_TINY;
            }
            tf.setText(fileName);
            tf.width = WIDTH;
            tf.setScale(SCALE);
            tf.setTextSize(textSize);
            tf.setAlignment(Layout.Alignment.ALIGN_CENTER);
            tf.create();
            tf.setOnClickListener(new GlModel.ClickListener() {
                @Override
                public void onClick(Model model) {
                    if (eventListener != null) {
                        eventListener.onKmlSelected(fileName);
                    }
                }
            });
            addPanel(tf);
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
