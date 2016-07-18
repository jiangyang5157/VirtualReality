package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.ArrayMap;

import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.math.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @author Yang
 * @since 7/10/2016
 */
public class OcTreeNode extends TreeNode {
    private static final String TAG = "[OcTreeNode]";

    protected static final int MIN_OBJECT_SIZE = 5; // size > 0

    protected float[] center; // center position of this node
    protected float step; // half of edge's length of this node
    protected float[] lb; // lb position of this node
    protected float[] rt; // rt position of this node

    protected final int depth; // depth of this node
    private ArrayMap<Integer, OcTreeNode> nodes; // the child nodes - <octant code, node>
    private ArrayMap<OcTreeObject, Integer> objects; // the objects stored at this node - <object, octant code>

    public OcTreeNode(float[] center, float step, int depth) {
        this.center = center;
        this.step = step;
        this.depth = depth;
        objects = new ArrayMap<>();

        lb = new float[]{
                center[0] - step,
                center[1] - step,
                center[2] - step
        };
        rt = new float[]{
                center[0] + step,
                center[1] + step,
                center[2] + step
        };
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

    public RayIntersection getObjectIntersection(Vector cameraPos_vec, Vector headForwardFrac_vec, float[] headView) {
        RayIntersection ret = null;

        if (hasIntersection(cameraPos_vec, headForwardFrac_vec)) {
            ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
            Set<OcTreeObject> ocTreeObjects = objects.keySet();
            for (OcTreeObject ocTreeObject : ocTreeObjects) {
                RayIntersection rayIntersection = ocTreeObject.model.getIntersection(cameraPos_vec, headView);
                if (rayIntersection != null) {
                    rayIntersections.add(rayIntersection);
                }
            }

            int size = rayIntersections.size();
            if (size > 0) {
                if (size > 1) {
                    Collections.sort(rayIntersections);
                }
                ret = rayIntersections.get(0);
            }
        }

        return ret;
    }

    private boolean hasIntersection(Vector cameraPos_vec, Vector headForwardFrac_vec) {
        double tmin, tmax, tymin, tymax, tzmin, tzmax;

        if (headForwardFrac_vec.getData(0) >= 0) {
            tmin = (lb[0] - cameraPos_vec.getData(0)) * headForwardFrac_vec.getData(0);
            tmax = (rt[0] - cameraPos_vec.getData(0)) * headForwardFrac_vec.getData(0);
        } else {
            tmin = (rt[0] - cameraPos_vec.getData(0)) * headForwardFrac_vec.getData(0);
            tmax = (lb[0] - cameraPos_vec.getData(0)) * headForwardFrac_vec.getData(0);
        }
        if (headForwardFrac_vec.getData(1) >= 0) {
            tymin = (lb[1] - cameraPos_vec.getData(1)) * headForwardFrac_vec.getData(1);
            tymax = (rt[1] - cameraPos_vec.getData(1)) * headForwardFrac_vec.getData(1);
        } else {
            tymin = (rt[1] - cameraPos_vec.getData(1)) * headForwardFrac_vec.getData(1);
            tymax = (lb[1] - cameraPos_vec.getData(1)) * headForwardFrac_vec.getData(1);
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }
        if (tymin > tmin) {
            tmin = tymin;
        }
        if (tymax < tmax) {
            tmax = tymax;
        }

        if (headForwardFrac_vec.getData(2) >= 0) {
            tzmin = (lb[2] - cameraPos_vec.getData(2)) * headForwardFrac_vec.getData(2);
            tzmax = (rt[2] - cameraPos_vec.getData(2)) * headForwardFrac_vec.getData(2);
        } else {
            tzmin = (rt[2] - cameraPos_vec.getData(2)) * headForwardFrac_vec.getData(2);
            tzmax = (lb[2] - cameraPos_vec.getData(2)) * headForwardFrac_vec.getData(2);
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }
        if (tzmin > tmin) {
            tmin = tzmin;
        }
        if (tzmax < tmax) {
            tmax = tzmax;
        }

        if (tmax < 0) {
            return false; // ray is intersecting AABB, but whole AABB is behind us
        }
        if (tmin > tmax) {
            return false;  // ray doesn't intersect AABB
        }

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
