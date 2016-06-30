package com.gmail.jiangyang5157.cardboard.scene;

/**
 * @author Yang
 * @since 6/19/2016
 */
public interface Creation {
    int STATE_BEFORE_PREPARE = 0x00000001;
    int STATE_PREPARING = 0x00000010;
    int STATE_BEFORE_CREATE = 0x00000100;
    int STATE_CREATING = 0x00001000;

    int getCreationState();
}
