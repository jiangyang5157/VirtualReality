package com.gmail.jiangyang5157.cardboard.scene.tree;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTree implements TreePhase {
    private OcTreeNode node;

    public OcTree(float[] center, float step, int depth) {
        node = new OcTreeNode(center, step, depth);
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
