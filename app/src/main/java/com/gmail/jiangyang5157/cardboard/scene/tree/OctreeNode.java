package com.gmail.jiangyang5157.cardboard.scene.tree;

import java.util.ArrayList;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeNode extends TreeNode {

    protected float[] center; // center position of this node
    protected float step; // edge length of this node
    private final int depth; // depth of this node
    private final OcTreeNode[] nodes; // the child nodes

    private final ArrayList<TreeObject> objects; // the objects stored at this node

    public OcTreeNode(float[] center, float step, int depth) {
        this.center = center;
        this.step = step;
        this.depth = depth;
        this.objects = new ArrayList<>();

        if (depth > 0) {
            this.nodes = new OcTreeNode[8];
            float halfStep = step * 0.5f;
            for (int i = 0; i < 8; i++) {
                float offsetX = (((i & 1) == 0) ? halfStep : -halfStep);
                float offsetY = (((i & 2) == 0) ? halfStep : -halfStep);
                float offsetZ = (((i & 4) == 0) ? halfStep : -halfStep);
                nodes[i] = new OcTreeNode(new float[]{center[0] + offsetX, center[1] + offsetY, center[2] + offsetZ}, step, depth - 1);
            }
        } else {
            this.nodes = null;
        }
    }

    @Override
    public void insertObject(final TreeObject obj) {
        boolean straddle = false;
        int index = 0;
        for (int i = 0; i < 3; i++) {
            float delta = center[i] - obj.center[i];
            if (Math.abs(delta) <= obj.radius) {
                straddle = true;
                break;
            }

            // calculate the child index
            if (delta > 0) {
                index |= (1 << i);
            }
        }

        if (straddle) {
            objects.add(obj);
        } else {
            if (depth > 0) {
                nodes[index].insertObject(obj);
            }
        }
    }

    @Override
    public void clean() {
        objects.clear();
        if (depth > 0) {
            for (OcTreeNode node : nodes) {
                node.clean();
            }
        }
    }
}
