package com.gmail.jiangyang5157.cardboard.scene.tree;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 7/10/2016
 */
public abstract class TreeNode implements TreePhase {
    abstract void split();

    abstract boolean isValid();

    abstract ArrayList<TreeNode> getValidNodes();

    abstract int getDepth();

    abstract int getNodeSize();

    abstract int getLeafSize();
}
