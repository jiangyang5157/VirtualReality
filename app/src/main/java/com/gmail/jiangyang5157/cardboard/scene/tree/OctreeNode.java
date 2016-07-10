package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.ArrayMap;
import android.util.Log;

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
    private ArrayMap<Integer, OcTreeNode> nodes; // the child nodes - key: octant code
    private ArrayMap<Integer, TreeObject> objects; // the objects stored at this node - key: octant code

    public OcTreeNode(float[] center, float step, int depth) {
        this.center = center;
        this.step = step;
        this.depth = depth;
        Log.d(TAG, toString());
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

    private boolean[] getBooleanOctant(int index) {
        return new boolean[]{
                (index & 1) == 0, // 0, 2, 4, 6
                (index & 2) == 0, // 0, 1, 4, 5
                (index & 4) == 0, // 0, 1, 2, 3
        };
    }

    private int getIndex(boolean[] octant) {
//        int a1 = 1 << 0; // 1
//        int a2 = 1 << 1; // 2
//        int a3 = 1 << 2; // 4
//        Log.d("####", "" + a1 + ", " + a2 + ", " + a3);
//        int b = 0;
//        Log.d("####", "b = 0    " + b); // 0
//        b |= a1;
//        Log.d("####", "b |= a1    " + b); // 1
////            b |= a2;
////            Log.d("####", "b |= a2    " + b); // 3
//        b |= a3;
//        Log.d("####", "b |= a3    " + b);  // 7, 5

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
        boolean straddle = false;
        boolean[] octant = new boolean[3];
        for (int i = 0; i < 3; i++) {
            float delta = obj.center[i] - center[i];
            octant[i] = delta >= 0;
            if (Math.abs(delta) <= obj.radius) {
                straddle = true;
            }
        }
        int index = getIndex(octant);

        if (depth < OcTree.MAX_DEPTH) {
            if (nodes == null) {
                split();
            }
            nodes.get(index).insertObject(obj);
        } else {
            addObject(index, obj);
            Log.d(TAG, "insertObject on depth: " + depth + ": " + Arrays.toString(center) + " - " + Arrays.toString(obj.center));
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

    protected void addObject(int octant, TreeObject object) {
        if (objects == null) {
            objects = new ArrayMap<>();
        }
        objects.put(octant, object);
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
        if (objects != null) {
            objects.clear();
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
