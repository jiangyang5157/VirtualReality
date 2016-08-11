package com.gmail.jiangyang5157.cardboard.scene.model;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.cardboard.vr.R;
import com.gmail.jiangyang5157.tookit.android.base.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.google.android.gms.maps.model.LatLng;

/**
 * This is a independent Marker GlModel.
 *
 * @author Yang
 * @since 7/13/2016
 */
public class Marker3d extends Icosphere {
    private static final String TAG = "[Marker3d]";

    protected static final int RESOURCE_ID_VERTEX_SHADER = R.raw.marker_icosphere_color_vertex_shader;
    protected static final int RESOURCE_ID_FRAGMENT_SHADER = R.raw.marker_icosphere_color_fragment_shader;

    public static final float RADIUS = 1;
    public static final float ALTITUDE = -1 * RADIUS;

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

    public Marker3d(Context context) {
        super(context, 3);
        setRadius(RADIUS);
        setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.android.base.R.color.Yellow, null));
    }

    @Override
    public void create(int program) {
        buildData();

        super.create(program);
        bindHandles();
        bindBuffers();

        setCreated(true);
        setVisible(true);
    }

    public void setLocation(LatLng latLng, float altitude) {
        this.coordinate = new Coordinate(latLng.latitude, latLng.longitude, altitude, Earth.RADIUS);
        Matrix.setIdentityM(translationMatrix, 0);
        Matrix.translateM(translationMatrix, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
        modelRequireUpdate = true;
    }

    public RayIntersection getIntersection(Vector cameraPos_vec, final float[] headView) {
        if (!isCreated() || !isVisible()) {
            return null;
        }

        double[] camera_pos = new double[]{
                getTranslationX() - cameraPos_vec.getData(0),
                getTranslationY() - cameraPos_vec.getData(1),
                getTranslationZ() - cameraPos_vec.getData(2)
        };
        // Convenience vector for extracting the position from a matrix via multiplication.
        float[] posMultiply = new float[]{(float) camera_pos[0], (float) camera_pos[1], (float) camera_pos[2], 0.0f};
        // position in camera space
        float[] posInCameraSpace = new float[4];
        // Convert object space to camera space - Use the headView from onNewFrame.
        float[] mv = new float[16];
        Matrix.multiplyMM(mv, 0, headView, 0, model, 0);
        Matrix.multiplyMV(posInCameraSpace, 0, mv, 0, posMultiply, 0);

        double pitch = Math.atan2(posInCameraSpace[1], -posInCameraSpace[2]);
        double yaw = Math.atan2(posInCameraSpace[0], -posInCameraSpace[2]);
        final double PITCH_LIMIT = 0.02;
        final double YAW_LIMIT = 0.02;
        if (Math.abs(pitch) > PITCH_LIMIT || Math.abs(yaw) > YAW_LIMIT) {
            return null;
        } else {
            double dot = camera_pos[0] * camera_pos[0] + camera_pos[1] * camera_pos[1] + camera_pos[2] * camera_pos[2];
            double t = Math.sqrt(dot) - radius;
            return new RayIntersection(this, t);
        }
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setObjModel(ObjModel model) {
        this.objModel = model;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ObjModel getObjModel() {
        return objModel;
    }
}
