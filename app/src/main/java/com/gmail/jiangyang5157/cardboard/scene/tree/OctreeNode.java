package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeNode extends TreeNode {
    private static final String TAG = "[OcTreeNode]";

    protected float[] center; // center position of this node
    protected float step; // edge length of this node

    private final int depth; // depth of this node
    private OcTreeNode[] nodes; // the child nodes

    private final ArrayList<TreeObject> objects; // the objects stored at this node

    public OcTreeNode(float[] center, float step, int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException(TAG + " - depth should not less than 0.");
        }
        this.center = center;
        this.step = step;
        this.depth = depth;
        this.objects = new ArrayList<>();
        Log.d(TAG, toString());
    }

    @Override
    protected void split() {
        nodes = new OcTreeNode[8];
        float halfStep = step * 0.5f;
        for (int i = 0; i < 8; i++) {
            float offsetX = (((i & 1) == 0) ? halfStep : -halfStep);
            float offsetY = (((i & 2) == 0) ? halfStep : -halfStep);
            float offsetZ = (((i & 4) == 0) ? halfStep : -halfStep);
            nodes[i] = new OcTreeNode(new float[]{center[0] + offsetX, center[1] + offsetY, center[2] + offsetZ}, halfStep, depth - 1);
        }
    }

    @Override
    public void insertObject(TreeObject obj) {
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

        if (depth > 0) {
            if (nodes == null) {
                split();
            }
            nodes[index].insertObject(obj);
        } else {
            if (straddle) {
                objects.add(obj);
                Log.d(TAG, "insertObject on depth: " + depth + ": " + Arrays.toString(obj.center) + " - " + Arrays.toString(obj.center));
            }
        }

//        if (!straddle && depth > 0) {
//            if (nodes == null) {
//                split();
//            }
//            nodes[index].insertObject(obj);
//        } else {
//            objects.add(obj);
//            Log.d(TAG, "insertObject: " + toString());
//        }
    }

    @Override
    public void clean() {
        objects.clear();
        if (nodes != null) {
            for (OcTreeNode node : nodes) {
                node.clean();
            }
        }
    }

    @Override
    public String toString() {
        return "OcTreeNode{" +
                "center=" + Arrays.toString(center) +
                ", step=" + step +
                ", depth=" + depth +
                '}';
    }
}
