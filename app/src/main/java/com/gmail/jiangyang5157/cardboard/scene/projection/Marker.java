package com.gmail.jiangyang5157.cardboard.scene.projection;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

import com.gmail.jiangyang5157.cardboard.scene.Coordinate;
import com.gmail.jiangyang5157.cardboard.scene.Head;
import com.gmail.jiangyang5157.cardboard.scene.RayIntersection;
import com.gmail.jiangyang5157.tookit.app.AppUtils;
import com.gmail.jiangyang5157.tookit.math.Vector;
import com.gmail.jiangyang5157.tookit.math.Vector3d;
import com.gmail.jiangyang5157.tookit.opengl.Model;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Yang
 * @since 4/12/2016.
 */
public class Marker extends Icosphere implements GlModel.ClickListener {
    private static final String TAG = "[Marker]";

    //    public static final float RADIUS = 1;
    public static final float RADIUS = 40;
    public static final float ALTITUDE = -1 * RADIUS;

    private String name;
    private String description;
    private Coordinate coordinate;

    private ObjModel objModel;

    private GlModel.ClickListener onClickListener;

    public Marker(Context context) {
        super(context, 3);
        setRadius(RADIUS);
    }

    @Override
    public void create(int program) {
        if (color == null) {
            setColor(AppUtils.getColor(context, com.gmail.jiangyang5157.tookit.R.color.Yellow, null));
        }
        super.create(program);

        setCreated(true);
        setVisible(true);
    }

    public void setLocation(LatLng latLng, float altitude) {
        this.coordinate = new Coordinate(latLng.latitude, latLng.longitude, altitude, Earth.RADIUS);
        Matrix.setIdentityM(translation, 0);
        Matrix.translateM(translation, 0,
                (float) coordinate.ecef[0],
                (float) coordinate.ecef[1],
                (float) coordinate.ecef[2]);
    }

    @Override
    public void onClick(Model model) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    @Override
    public RayIntersection onIntersect(Head head) {
        return super.onIntersect(head);
//        if (!isCreated() || !isVisible()) {
//            return null;
//        }
//
//        // Convenience vector for extracting the position from a matrix via multiplication.
//        float[] posMultiply = new float[]{0, 0, 0, 1.0f};
//        //float[] position = getPosition();
//        //float[] posMultiply = new float[]{position[0], position[1], position[2], 1.0f};
//        float[] objPosition = new float[4];
//        // Convert object space to camera space. Use the headView from onNewFrame.
//        Matrix.multiplyMM(modelView, 0, head.getHeadView(), 0, model, 0);
//        Matrix.multiplyMV(objPosition, 0, modelView, 0, posMultiply, 0);
//
//        float pitch = (float) Math.atan2(objPosition[1], -objPosition[2]);
//        float yaw = (float) Math.atan2(objPosition[0], -objPosition[2]);
//
//        final float YAW_LIMIT = 0.1f;
//        final float PITCH_LIMIT = 0.1f;
//
//        if (Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT) {
//            float[] position = getPosition();
//            float[] cameraPos = head.getCamera().getPosition();
//            Vector pos_camera = new Vector3d(
//                    cameraPos[0] - position[0],
//                    cameraPos[1] - position[1],
//                    cameraPos[2] - position[2]
//            );
//            return new RayIntersection(this, pos_camera.length() - radius);
//        } else {
//            return null;
//        }
    }

    public void setOnClickListener(GlModel.ClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ClickListener getOnClickListener() {
        return onClickListener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjModel getObjModel() {
        return objModel;
    }

    public void setObjModel(ObjModel model) {
        this.objModel = model;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroy");
        super.destroy();
    }
}
