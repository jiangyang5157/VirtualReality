package com.gmail.jiangyang5157.cardboard.scene.tree;

/**
 * @author Yang
 * @since 7/10/2016
 */
public abstract class TreeNode implements TreePhase {
    abstract void split();

    abstract boolean isValid();

    abstract int getDepth();

    abstract int getNodeSize();

    abstract int getLeafSize();
}
