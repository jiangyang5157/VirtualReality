package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.text.Layout;

import com.gmail.jiangyang5157.tookit.android.base.AppUtils;

/**
 * @author Yang
 * @since 5/13/2016
 */
public class MarkerDetailView extends Dialog {
    private static final String TAG = "[MarkerDetailView]";

    private Event eventListener;

    public interface Event {
        void showObjModel(ObjModel model);
    }

    private AtomMarker marker;

    public MarkerDetailView(Context context, AtomMarker marker) {
        super(context);
        this.marker = marker;
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Red, null));
    }

    public void prepare(final Ray ray) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                creationState = STATE_PREPARING;
                ray.addBusy();

                if (marker.getName() != null) {
                    TextField pName = new TextField(context);
                    pName.setCcntent(marker.getName());
                    pName.width = WIDTH;
                    pName.setScale(SCALE);
                    pName.modelRequireUpdate = true;
                    pName.setTextSize(TextField.TEXT_SIZE_LARGE);
                    pName.setAlignment(Layout.Alignment.ALIGN_CENTER);

                    pName.prepare(ray);
                    addPanel(pName);
                }
                if (marker.getObjModel() != null) {
                    TextField pObjModel = new TextField(context);
                    pObjModel.setCcntent(marker.getObjModel().getTitle());
                    pObjModel.width = WIDTH;
                    pObjModel.setScale(SCALE);
                    pObjModel.modelRequireUpdate = true;
                    pObjModel.setTextSize(TextField.TEXT_SIZE_TINY);
                    pObjModel.setAlignment(Layout.Alignment.ALIGN_NORMAL);
                    pObjModel.setOnClickListener(new GlModel.ClickListener() {
                        @Override
                        public void onClick(GlModel model) {
                            if (eventListener != null) {
                                eventListener.showObjModel(marker.getObjModel());
                            }
                        }
                    });

                    pObjModel.prepare(ray);
                    addPanel(pObjModel);
                }
                if (marker.getDescription() != null) {
                    DescriptionField pDescription = new DescriptionField(context);
                    pDescription.setCcntent(marker.getDescription());
                    pDescription.width = WIDTH;
                    pDescription.setScale(SCALE);
                    pDescription.modelRequireUpdate = true;
                    pDescription.setTextSize(TextField.TEXT_SIZE_TINY);
                    pDescription.setAlignment(Layout.Alignment.ALIGN_NORMAL);

                    pDescription.prepare(ray);
                    addPanel(pDescription);
                }

                adjustBounds(WIDTH);

                buildTextureBuffers();
                buildData();

                ray.subtractBusy();
                creationState = STATE_BEFORE_CREATE;
            }
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
