package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.ArrayMap;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersectable;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeNode extends TreeNode implements Intersectable {
    private static final String TAG = "[OcTreeNode]";

    protected static final int MIN_OBJECT_SIZE = 5; // size > 0

    protected float[] center; // center position of this node
    protected float step; // half of edge's length of this node

    protected final int depth; // depth of this node
    private ArrayMap<Integer, OcTreeNode> nodes; // the child nodes - <octant code, node>
    private ArrayMap<OcTreeObject, Integer> objects; // the objects stored at this node - <object, octant code>

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
    public RayIntersection onIntersection(Head head) {
        RayIntersection ret = null;
        ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
        if (isIntersectant(head)) {
            Set<OcTreeObject> ocTreeObjects = objects.keySet();
            for (OcTreeObject ocTreeObject : ocTreeObjects) {
                RayIntersection rayIntersection = ocTreeObject.model.onIntersection(head);
                if (rayIntersection != null) {
                    rayIntersections.add(rayIntersection);
                }
            }
        }
        Collections.sort(rayIntersections);
        if (rayIntersections.size() > 0) {
            ret = rayIntersections.get(0);
        }
        return ret;
    }

    /**
     * ray-intersection-cube
     *
     * @param head
     * @return
     */
    private boolean isIntersectant(Head head) {
        float[] cameraPos = head.getCamera().getPosition();
        float[] forward = head.getForward();

        float[] dirfrac = new float[]{
                1.0f / forward[0],
                1.0f / forward[1],
                1.0f / forward[2]
        };

        float[] lb = new float[]{
                center[0] - step,
                center[1] - step,
                center[2] - step
        };
        float[] rt = new float[]{
                center[0] + step,
                center[1] + step,
                center[2] + step
        };

        double t1 = (lb[0] - cameraPos[0]) * dirfrac[0];
        double t2 = (rt[0] - cameraPos[0]) * dirfrac[0];
        double t3 = (lb[1] - cameraPos[1]) * dirfrac[1];
        double t4 = (rt[1] - cameraPos[1]) * dirfrac[1];
        double t5 = (lb[2] - cameraPos[2]) * dirfrac[2];
        double t6 = (rt[2] - cameraPos[2]) * dirfrac[2];

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        double t;
        if (tmax < 0) {
            // ray is intersecting AABB, but whole AABB is behind us
            t = tmax;
            return false;
        }

        if (tmin > tmax) {
            // ray doesn't intersect AABB
            t = tmax;
            return false;
        }

        t = tmin;
        return true;
    }

    @Override
    protected boolean isValid() {
        return objects.size() > 0;
    }

    protected ArrayList<OcTreeNode> getValidNodes() {
        ArrayList<OcTreeNode> ret = new ArrayList<>();

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

    public ArrayMap<OcTreeObject, Integer> getObjects() {
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
    public void insertObject(OcTreeObject obj) {
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
                    for (OcTreeObject key : objects.keySet()) {
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
        int ret = 1;
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
