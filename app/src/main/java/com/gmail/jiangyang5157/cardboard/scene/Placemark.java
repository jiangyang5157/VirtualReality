package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class Placemark extends Icosphere {

    private Earth earth;
    private Coordinate coordinate;

    private String label;

    public Placemark(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color, Earth earth) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, color);
        this.earth = earth;
    }

    //  angle [0, 180] = 180 / PI * radian
    //  radian [0, PI] = PI / 180 * angle
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        double lat = coordinate.getLatitude();
        double lon = coordinate.getLongitude();
        float alt = coordinate.getAltitude();
        Log.i("####", "lla: " + lat + ", " + lon + ", " + alt);

        double[] ecrf = lla2ecef(new double[]{lat, lon, alt});
        float x = (float) ecrf[0];
        float y = (float) ecrf[1];
        float z = (float) ecrf[2];
        Log.i("####", "ecrf: " + x + ", " + y + ", " + z);

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, x, y, z);
    }

    private double[] lla2ecef(double[] lla) {
        double lat = lla[1];
        double lon = lla[0];
        double alt = lla[2];
//        double bearing = 90 - lat;
        lat = Math.PI / 180.0f * lat;
        lon = Math.PI / 180.0f * lon;
//        bearing = Math.PI / 180.0f * bearing;

        final double F = 0;

        final double E = 0; //e = sqrt(2f-f^2)
        double E2 = Math.pow(E, 2);

        double r = earth.getRadius();
        double r2 = Math.pow(r, 2);

        double n = r / Math.sqrt(1 - E2 * Math.pow(Math.sin(lat), 2));

        double x = (n + alt) * Math.cos(lat) * Math.cos(lon);
        double y = (n + alt) * Math.cos(lat) * Math.sin(lon);
        double z = (n * (1 - E2) + alt) * Math.sin(lat);

//        double x = (n + alt) * (Math.sin(lon) * Math.cos(bearing) - Math.sin(lat) * Math.cos(lon) * Math.sin(bearing));
//        double y = (n + alt) * (-Math.cos(lon) * Math.cos(bearing) - Math.sin(lat) * Math.sin(lon) * Math.sin(bearing));
//        double z = (n * (1 - E2) + alt) * Math.cos(lat) * Math.sin(bearing);

        double[] ret = {x, y, z};
        return ret;
    }

    @Override
    public void update(float[] view, float[] perspective) {
        Matrix.rotateM(model, 0, 1f, 0, 1, 1);
        super.update(view, perspective);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
