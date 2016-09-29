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
public class KmlChooserView extends Dialog {
    private static final String TAG = "[KmlChooserView]";

    private Event eventListener;

    public interface Event {
        void onKmlSelected(String item);
    }

    public KmlChooserView(Context context) {
        super(context);
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));
    }

    public void prepare(final Ray ray) {
        getHandler().post(() -> {
            creationState = STATE_PREPARING;
            ray.addBusy();

            String[] kmlFileNames = getKmlFileNames();
            String lastKmlFileName = AssetUtils.getLastKmlFileName(context);
            for (final String fileName : kmlFileNames) {
                TextField p = new TextField(context);
                float textSize = TextField.TEXT_SIZE_TINY;
                p.setCcntent(fileName);
                p.width = WIDTH;
                p.setScale(SCALE);
                p.modelRequireUpdate = true;
                p.setTextSize(textSize);
                p.setAlignment(Layout.Alignment.ALIGN_CENTER);
                if (!fileName.equals(lastKmlFileName)) {
                    p.setOnClickListener(model1 -> {
                        if (eventListener != null) {
                            eventListener.onKmlSelected(fileName);
                        }
                    });
                }

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

    private String[] getKmlFileNames() {
        String[] ret = null;

        File directory = new File(AssetUtils.getAbsolutePath(context, AssetUtils.getKmlPath("")));
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        } else {
            ret = directory.list((dir, filename) -> filename.endsWith(".kml"));
        }
        return ret;
    }

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
