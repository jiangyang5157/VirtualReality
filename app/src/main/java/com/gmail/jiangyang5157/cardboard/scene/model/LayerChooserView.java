package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.cardboard.vr.AssetUtils;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

import java.io.File;

/**
 * @author Yang
 * @since 6/24/2016
 */
public class LayerChooserView extends Dialog {
    private static final String TAG = "[LayerChooserView]";

    private Event eventListener;

    public interface Event {
        void onSelected(File kml);
    }

    public LayerChooserView(Context context) {
        super(context);
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));
    }

    public void prepare(final Ray ray) {
        getHandler().post(() -> {
            creationState = STATE_PREPARING;
            ray.addBusy();

            File[] files = getKmlLayerFiles(context);
            for (final File file : files) {
                TextField p = new TextField(context);
                float textSize = TextField.TEXT_SIZE_TINY;
                p.setCcntent(AssetUtils.getKmlSimpleFileName(file));
                p.width = WIDTH;
                p.setScale(SCALE);
                p.modelRequireUpdate = true;
                p.setTextSize(textSize);
                p.setAlignment(Layout.Alignment.ALIGN_CENTER);
                p.setOnClickListener(model1 -> {
                    if (eventListener != null) {
                        eventListener.onSelected(file);
                    }
                });

                p.prepare(ray);
                addPanel(p);
            }

            adjustBounds(WIDTH);

            buildTextureBuffers();
            buildData();

            ray.subtractBusy();
            creationState = STATE_BEFORE_CREATE;
        });
    }

    @Override
    public void create(int program) {
        creationState = STATE_CREATING;
        super.create(program);
        bindHandles();
        bindTextureBuffers();
        bindBuffers();

        int iSize = panels.size();
        for (int i = 0; i < iSize; i++) {
            panels.get(i).create(program);
        }

        setCreated(true);
        setVisible(true);
        creationState = STATE_BEFORE_CREATE;
    }

    private File[] getKmlLayerFiles(Context context) {
        File[] ret = null;

        File directory = new File(AssetUtils.getAbsolutePath(context, AssetUtils.getLayerPath("")));
        if (!directory.exists() || !directory.isDirectory()) {
            final boolean mkdirs = directory.mkdirs();
        } else {
            ret = directory.listFiles((dir, fileName) -> fileName.endsWith(AssetUtils.KML_FILE_ENDS));
        }
        return ret;
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
