package com.gmail.jiangyang5157.cardboard.scene.tree;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTree implements TreePhase {
    protected static final int MAX_DEPTH = 10;
    protected static final int MAX_NODE_OBJECT_SIZE = 4;

    private OcTreeNode node;

    public OcTree(float[] center, float step) {
        node = new OcTreeNode(center, step, 0);
    }

    @Override
    public void clean() {
        node.clean();
    }

    @Override
    public void insertObject(TreeObject obj) {
        node.insertObject(obj);
    }
}
