package com.gmail.jiangyang5157.cardboard.scene;

import com.gmail.jiangyang5157.cardboard.scene.projection.Ray;

/**
 * @author Yang
 * @since 6/19/2016
 */
public interface Creation {
    public static final int STATE_BEFORE_PREPARE = 0x00000001;
    public static final int STATE_PREPARING = 0x00000010;
    public static final int STATE_BEFORE_CREATE = 0x00000100;
    public static final int STATE_CREATING = 0x00001000;

    public void prepare(final Ray ray);
    public boolean checkPreparation();
    public void create();
    public int getCreationState();
}
