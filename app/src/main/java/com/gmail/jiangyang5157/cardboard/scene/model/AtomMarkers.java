package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.kml.KmlPlacemark;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.scene.Lighting;
import com.gmail.jiangyang5157.cardboard.scene.tree.OcTree;
import com.gmail.jiangyang5157.cardboard.scene.tree.SphereObj;
import com.gmail.jiangyang5157.cardboard.scene.tree.TreeNode;
import com.gmail.jiangyang5157.tookit.opengl.GlUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Yang
 * @since 7/2/2016
 */
public class AtomMarkers extends Marker {
    private static final String TAG = "[AtomMarkers]";

    private OcTree ocTree;
    private ArrayList<TreeNode> ocTreeNodes;
    private ArrayList<Marker> markers;

    public AtomMarkers(Context context) {
        super(context);
        markers = new ArrayList<>();
        ocTree = new OcTree(new float[3], Earth.RADIUS);
    }

    @Override
    public void create(int program) {
        super.create(program);

        ocTreeNodes = ocTree.getValidNodes();
        for (Marker marker : markers) {
            marker.create(program);
            marker.mvMatrixHandle = mvMatrixHandle;
            marker.mvpMatrixHandle = mvpMatrixHandle;
            marker.colorHandle = colorHandle;
            marker.indicesBufferCapacity = indicesBufferCapacity;
        }

        setCreated(true);
        setVisible(true);
    }

    @Override
    public void draw() {
        if (!isCreated() || !isVisible()) {
            return;
        }

        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(vertexHandle);
        GLES20.glEnableVertexAttribArray(normalHandle);
        if (lighting != null) {
            GLES20.glUniform3fv(lightPosHandle, 1, lighting.getLightPosInCameraSpace(), 0);
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, verticesBuffHandle);
        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, normalsBuffHandle);
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffHandle);

        for (Marker marker : markers) {
            marker.draw();
        }

        GLES20.glDisableVertexAttribArray(vertexHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glUseProgram(0);

        GlUtils.printGlError(TAG + " - draw end");
    }

    @Override
    public void update(float[] view, float[] perspective) {
        for (Marker marker : markers) {
            marker.update(view, perspective);
        }
    }

    private void addMarker(Marker marker) {
        markers.add(marker);
        ocTree.insertObject(new SphereObj(marker));
    }

    public Marker addMarker(KmlPlacemark kmlPlacemark, MarkerOptions markerUrlStyle, int markerColorInteger) {
        LatLng latLng = markerUrlStyle.getPosition();
        AtomMarker marker = new AtomMarker(context);
        if (markerColorInteger != 0) {
            marker.setColor(markerColorInteger);
        }
        marker.setOnClickListener(onClickListener);
        marker.setLocation(latLng, Marker.ALTITUDE);
        marker.setName(kmlPlacemark.getProperty("name"));
        marker.setDescription(kmlPlacemark.getProperty("description"));

        String objProperty = kmlPlacemark.getProperty("obj");
        if (objProperty != null) {
            ObjModel objModel = new ObjModel(context, kmlPlacemark.getProperty("title"), objProperty);
            objModel.setLighting(new Lighting() {
                @Override
                public float[] getLightPosInCameraSpace() {
                    return Lighting.LIGHT_POS_IN_CAMERA_SPACE_CENTER;
                }
            });
            marker.setObjModel(objModel);
        }

        addMarker(marker);
        return marker;
    }

    @Override
    public RayIntersection onIntersection(Head head) {
        if (!isCreated() || !isVisible()) {
            return null;
        }
        RayIntersection ret = null;

        // TODO: 7/3/2016 performance
        Log.d(TAG, "ocTreeNodes.size = " + ocTreeNodes.size());
//        for (TreeNode node : ocTreeNodes) {
//            OcTreeNode ocTreeNode = (OcTreeNode) node;
//        }

//        ArrayList<RayIntersection> rayIntersections = new ArrayList<>();
//        for (Marker marker : markers) {
//            RayIntersection rayIntersection = marker.onIntersection(head);
//            if (rayIntersection != null) {
//                rayIntersections.add(rayIntersection);
//            }
//        }
//        Collections.sort(rayIntersections);
//        if (rayIntersections.size() > 0) {
//            ret = rayIntersections.get(0);
//        }

        return ret;
    }

    public OcTree getOcTree() {
        return ocTree;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public void destoryOcTree() {
        ocTree.clean();
    }

    public void destoryMarks() {
        for (Marker marker : markers) {
            marker.destroy();
        }
        markers.clear();
    }

    @Override
    public void destroy() {
        destoryOcTree();
        destoryMarks();
        super.destroy();
    }
}
