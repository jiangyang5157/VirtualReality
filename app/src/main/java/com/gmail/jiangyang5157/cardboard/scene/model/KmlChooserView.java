package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.text.Layout;
import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.Creation;
import com.gmail.jiangyang5157.cardboard.vr.Constant;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Yang
 * @since 6/24/2016
 */
public class KmlChooserView extends Dialog implements Creation {

    private Event eventListener;
    public interface Event {
        void onKmlSelected(String fileName);
    }

    protected int creationState = STATE_BEFORE_PREPARE;

    public KmlChooserView(Context context) {
        super(context);
    }

    public void prepare(final Ray ray) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                creationState = STATE_PREPARING;
                ray.addBusy();



                ray.subtractBusy();
                creationState = STATE_BEFORE_CREATE;
            }
        });
    }

    @Override
    public void create(int program) {
        creationState = STATE_CREATING;
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));

        createPanels(); // move to prepare
        adjustBounds(WIDTH);// move to prepare

        buildTextureBuffers();
        buildData();

        super.create(program);
        bindHandles();
        bindTextureBuffers();
        bindBuffers();

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
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
        for (int i = 0; i < iSize; i++) {
            final String fileName = fileNames[i];
            TextField p = new TextField(context);
            float textSize = TextField.TEXT_SIZE_TINY;
            p.setCcntent(fileName);
            p.width = WIDTH;
            p.setScale(SCALE);
            p.modelRequireUpdate = true;
            p.setTextSize(textSize);
            p.setAlignment(Layout.Alignment.ALIGN_CENTER);
            p.create(shaders);
            if (!fileName.equals(lastKmlFileName)) {
                p.setOnClickListener(new GlModel.ClickListener() {
                    @Override
                    public void onClick(GlModel model) {
                        if (eventListener != null) {
                            eventListener.onKmlSelected(fileName);
                        }
                    }
                });
            }
            addPanel(p);
        }
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public int getCreationState() {
        return creationState;
    }
}
