package com.gmail.jiangyang5157.cardboard.scene.tree;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTree implements TreePhase {
    private static final String TAG = "[OcTree]";

    protected static final int MAX_DEPTH = 16;

    private OcTreeNode node;

    public OcTree(float[] center, float step) {
        node = new OcTreeNode(center, step, 0);
    }

    @Override
    public void clean() {
        node.clean();
    }

    public ArrayList<OcTreeNode> getValidNodes() {
        return node.getValidNodes();
    }

    @Override
    public void insertObject(OcTreeObject obj) {
        node.insertObject(obj);
    }

    @Override
    public String toString() {
        return TAG + "Node Size: " + node.getNodeSize() + ", Leaf Size: " + node.getLeafSize();
    }
}
