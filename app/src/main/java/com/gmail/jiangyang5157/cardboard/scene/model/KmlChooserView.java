package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.text.Layout;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.cardboard.vr.R;

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
    public void create(int program) {
        super.create(program);

        setCreated(true);
        setVisible(true);
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

        ArrayMap<Integer, Integer> shaders = new ArrayMap<>();
        shaders.put(GLES20.GL_VERTEX_SHADER, R.raw.panel_vertex_shader);
        shaders.put(GLES20.GL_FRAGMENT_SHADER, R.raw.panel_fragment_shader);

        String lastKmlFileName = Constant.getLastKmlFileName(context);
        int iSize = fileNames.length;
        for (int i = 0; i < iSize; i++){
            final String fileName = fileNames[i];
            TextField tf = new TextField(context);
            float textSize = TextField.TEXT_SIZE_TINY;
            tf.setText(fileName);
            tf.width = WIDTH;
            tf.setXyzScale(SCALE);
            tf.modelRequireUpdate = true;
            tf.setTextSize(textSize);
            tf.setAlignment(Layout.Alignment.ALIGN_CENTER);
            tf.create(shaders);
            if (!fileName.equals(lastKmlFileName)) {
                tf.setOnClickListener(new GlModel.ClickListener() {
                    @Override
                    public void onClick(GlModel model) {
                        if (eventListener != null) {
                            eventListener.onKmlSelected(fileName);
                        }
                    }
                });
            }
            addPanel(tf);
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
