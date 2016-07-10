package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeNode extends TreeNode {
    private static final String TAG = "[OcTreeNode]";

    protected static final int MIN_OBJECT_SIZE = 4; // size > 0

    protected float[] center; // center position of this node
    protected float step; // half of edge's length of this node

    protected final int depth; // depth of this node
    private ArrayMap<Integer, OcTreeNode> nodes; // the child nodes - <octant code, node>

    private ArrayMap<TreeObject, Integer> objects; // the objects stored at this node - <object, octant code>

    public OcTreeNode(float[] center, float step, int depth) {
        this.center = center;
        this.step = step;
        this.depth = depth;
        objects = new ArrayMap<>();
    }

    @Override
    protected void split() {
        nodes = new ArrayMap<>();
        float halfStep = step * 0.5f;
        for (int i = 0; i < 8; i++) {
             /*
            // Octants numbering
            //
            //                                 +Y                 -Z
            //                                 |                  /
            //                                 |                 /
            //                                 |                /
            //                                 |               /
            //                     o-----------|---o---------------o
            //                    /            |  /               /|
            //                   /       5     | /       4       / |
            //                  /              |/               /  |
            //                 o---------------o---------------o   |
            //                /               /               /|   |
            //               /       1       /       0       / | 4 |
            //              /               /               /  |   o
            //             o---------------o---------------o   |  /|
            //             |               |               |   | / |
            //             |               |               | 0 |/  |
            //             |               |               |   o --|-------------- +X
            //             |       1       |       0       |  /|   |
            //             |               |               | / | 6 |
            //             |               |               |/  |   o
            //             o---------------o---------------o   |  /
            //             |               |               |   | /
            //             |               |               | 2 |/
            //             |               |               |   o
            //             |       3       |       2       |  /
            //             |               |               | /
            //             |               |               |/
            //             o---------------o---------------o
            //
            //
            //
            */
            boolean[] octant = getBooleanOctant(i);
            float offsetX = octant[0] ? halfStep : -halfStep;
            float offsetY = octant[1] ? halfStep : -halfStep;
            float offsetZ = octant[2] ? halfStep : -halfStep;
            OcTreeNode node = new OcTreeNode(new float[]{center[0] + offsetX, center[1] + offsetY, center[2] + offsetZ}, halfStep, depth + 1);
            nodes.put(i, node);
        }
    }

    @Override
    protected boolean isValid() {
        return objects.size() > 0;
    }

    @Override
    protected ArrayList<TreeNode> getValidNodes() {
        ArrayList<TreeNode> ret = new ArrayList<>();

        if (isValid()) {
            ret.add(this);
        }

        if (nodes != null) {
            for (int key : nodes.keySet()) {
                ret.addAll(nodes.get(key).getValidNodes());
            }
        }

        return ret;
    }

    public ArrayMap<TreeObject, Integer> getObjects() {
        return objects;
    }

    private boolean[] getBooleanOctant(int index) {
        return new boolean[]{
                (index & 1) == 0, // 0, 2, 4, 6
                (index & 2) == 0, // 0, 1, 4, 5
                (index & 4) == 0, // 0, 1, 2, 3
        };
    }

    private int getIndex(boolean[] octant) {
        int ret = 0;
        for (int i = 0; i < 3; i++) {
            if (!octant[i]) {
                ret |= (1 << i);
            }
        }
        return ret;
    }

    @Override
    public void insertObject(TreeObject obj) {
        boolean[] octant = new boolean[3];
        for (int i = 0; i < 3; i++) {
            float delta = obj.center[i] - center[i];
            octant[i] = delta >= 0;
        }
        int index = getIndex(octant); // index of the straddled octant

        if (depth < OcTree.MAX_DEPTH) {
            if (nodes == null) {
                if (objects.size() < MIN_OBJECT_SIZE) {
                    objects.put(obj, index);
                } else {
                    split();
                    for (TreeObject key : objects.keySet()) {
                        int code = objects.get(key);
                        nodes.get(code).insertObject(key);
                    }
                    objects.clear();
                    nodes.get(index).insertObject(obj);
                }
            } else {
                nodes.get(index).insertObject(obj);
            }
        } else {
            objects.put(obj, index);
        }
    }

    @Override
    public void clean() {
        if (nodes != null) {
            for (int key : nodes.keySet()) {
                OcTreeNode node = nodes.get(key);
                node.clean();
            }
            nodes.clear();
        }
        objects.clear();
    }


    @Override
    protected int getDepth() {
        return depth;
    }

    @Override
    protected int getNodeSize() {
        int ret = 0;
        if (nodes != null) {
            ret = nodes.size();
            for (int key : nodes.keySet()) {
                OcTreeNode node = nodes.get(key);
                ret += node.getNodeSize();
            }
        }
        return ret;
    }

    @Override
    int getLeafSize() {
        int ret = objects.size();
        if (nodes != null) {
            for (int key : nodes.keySet()) {
                OcTreeNode node = nodes.get(key);
                ret += node.getLeafSize();
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return TAG +
                ": center=" + Arrays.toString(center) +
                ", step=" + step +
                ", depth=" + depth +
                ", hasNodes=" + (nodes != null) +
                ", objects.size=" + objects.size();
    }
}
