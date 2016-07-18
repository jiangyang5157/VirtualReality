package com.gmail.jiangyang5157.cardboard.scene.tree;

import android.util.ArrayMap;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.Intersectable;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;

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

    @Override
    public RayIntersection onIntersection(Vector cameraPos_vec, Vector headForward_vec, float[] headView) {
        RayIntersection ret = null;
        ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
        if (isIntersectant(cameraPos_vec, headForward_vec, headView)) {
            Set<OcTreeObject> ocTreeObjects = objects.keySet();
            for (OcTreeObject ocTreeObject : ocTreeObjects) {
                RayIntersection rayIntersection = ocTreeObject.model.onIntersection(cameraPos_vec, headForward_vec, headView);
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

    private boolean isIntersectant(Vector cameraPos_vec, Vector headForward_vec, float[] headView) {
        // ray-intersection-sphere
//        Vector pos_camera_vec = new Vector3d(
//                cameraPos_vec.getData(0) - center[0],
//                cameraPos_vec.getData(1) - center[1],
//                cameraPos_vec.getData(2) - center[2]
//        );
//
//        double stepPower2 = step * step;
//        double twoStepPower2 = stepPower2 + stepPower2;
//        double threeStepPower2 = twoStepPower2 + stepPower2;
//        double radius = Math.sqrt(threeStepPower2);
//        final double b = headForward_vec.dot(pos_camera_vec);
//        final double c = pos_camera_vec.dot(pos_camera_vec) - (radius * radius);
//
//        // solve the quadratic equation
//        final double f = b * b - c;
//        if (f <= Vector.EPSILON) {
//            // ray misses sphere
//            return false;
//        }
//
//        final double sqrtF = Math.sqrt(f);
//        final double t0 = -b + sqrtF;
//        final double t1 = -b - sqrtF;
//
//        // pick the smaller of the two results if both are positive
//        final double t = t0 < 0.0f ? Math.max(t1, 0.0f) : (t1 < 0.0f ? t0 : Math.min(t0, t1));
//        if (t == 0) {
//            // both intersections are behind the matrix
//            return false;
//        }
//        return true;

        //ray-intersection-cube
//        double headForwardFracX = 1.0 / headForward_vec.getData(0);
//        double headForwardFracY = 1.0 / headForward_vec.getData(1);
//        double headForwardFracZ = 1.0 / headForward_vec.getData(2);
//
//        double t1 = (lb[0] - cameraPos_vec.getData(0)) * headForwardFracX;
//        double t2 = (rt[0] - cameraPos_vec.getData(0)) * headForwardFracX;
//        double t3 = (lb[1] - cameraPos_vec.getData(1)) * headForwardFracY;
//        double t4 = (rt[1] - cameraPos_vec.getData(1)) * headForwardFracY;
//        double t5 = (lb[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
//        double t6 = (rt[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
//
//        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
//        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));
//
//        double t;
//        if (tmax < 0) {
//            t = tmax;
//            return false; // ray is intersecting AABB, but whole AABB is behind us
//        }
//        if (tmin > tmax) {
//            t = tmax;
//            return false;  // ray doesn't intersect AABB
//        }
//        t = tmin;
//        return true;

        //ray-intersection-cube
        double headForwardFracX = 1.0 / headForward_vec.getData(0);
        double headForwardFracY = 1.0 / headForward_vec.getData(1);
        double headForwardFracZ = 1.0 / headForward_vec.getData(2);
        double tmin, tmax, tymin, tymax, tzmin, tzmax;

        if (headForwardFracX >= 0) {
            tmin = (lb[0] - cameraPos_vec.getData(0)) * headForwardFracX;
            tmax = (rt[0] - cameraPos_vec.getData(0)) * headForwardFracX;
        } else {
            tmin = (rt[0] - cameraPos_vec.getData(0)) * headForwardFracX;
            tmax = (lb[0] - cameraPos_vec.getData(0)) * headForwardFracX;
        }
        if (headForwardFracY >= 0) {
            tymin = (lb[1] - cameraPos_vec.getData(1)) * headForwardFracY;
            tymax = (rt[1] - cameraPos_vec.getData(1)) * headForwardFracY;
        } else {
            tymin = (rt[1] - cameraPos_vec.getData(1)) * headForwardFracY;
            tymax = (lb[1] - cameraPos_vec.getData(1)) * headForwardFracY;
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

        if (headForwardFracZ >= 0) {
            tzmin = (lb[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
            tzmax = (rt[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
        } else {
            tzmin = (rt[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
            tzmax = (lb[2] - cameraPos_vec.getData(2)) * headForwardFracZ;
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

        Log.d(TAG, "tmin/tmax: " + tmin + ", " + tmax);
        if (tmax < 0) {
            return false; // ray is intersecting AABB, but whole AABB is behind us
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
