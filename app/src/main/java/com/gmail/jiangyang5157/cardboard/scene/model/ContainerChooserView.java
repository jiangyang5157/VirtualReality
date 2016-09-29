package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.text.Layout;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlLayer;
import com.gmail.jiangyang5157.cardboard.vr.KmlLayerCache;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang
 * @since 6/24/2016
 */
public class ContainerChooserView extends Dialog {
    private static final String TAG = "[ContainerChooserView]";

    private KmlLayerCache kmlLayerCache;

    private Event eventListener;

    public interface Event {
        void onSelected(String name);
    }

    public ContainerChooserView(Context context, KmlLayerCache kmlLayerCache) {
        super(context);
        this.kmlLayerCache = kmlLayerCache;
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));
    }

    public void prepare(final Ray ray) {
        getHandler().post(() -> {
            creationState = STATE_PREPARING;
            ray.addBusy();

            HashMap<String, HashSet<KmlLayer>> containerMap = kmlLayerCache.getContainerMap();

            Set<String> keys = containerMap.keySet();
            for (String key : keys) {
                TextField p = new TextField(context);
                float textSize = TextField.TEXT_SIZE_TINY;
                String[] names = key.split(KmlLayerCache.KEY_SEPARATOR);
                p.setCcntent(names[names.length - 1]);
                p.width = WIDTH;
                p.setScale(SCALE);
                p.modelRequireUpdate = true;
                p.setTextSize(textSize);
                p.setAlignment(Layout.Alignment.ALIGN_CENTER);
                p.setOnClickListener(model1 -> {
                    if (eventListener != null) {
                        eventListener.onSelected(key);
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

    public void setEventListener(Event eventListener) {
        this.eventListener = eventListener;
    }
}
