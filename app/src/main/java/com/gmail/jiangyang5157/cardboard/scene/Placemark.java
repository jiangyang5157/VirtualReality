package com.gmail.jiangyang5157.cardboard.scene;

import android.content.Context;
import android.opengl.Matrix;
import android.util.Log;

public class Placemark extends Icosphere {

    private Earth earth;
    private Coordinate coordinate;

    public Placemark(Context context, int vertexShaderRawResource, int fragmentShaderRawResource, int recursionLevel, float radius, float[] color, Earth earth) {
        super(context, vertexShaderRawResource, fragmentShaderRawResource, recursionLevel, radius, color);
        this.earth = earth;
    }

    //  angle [0, 180] = 180 / PI * radian
    //  radian [0, PI] = PI / 180 * angle
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
        float lat = coordinate.getLatitude();
        float lon = coordinate.getLongitude();
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
        double lat = lla[0];
        double lon = lla[1];
        double alt = lla[2];

        final double E = 8.1819190842622e-2;
        double r = earth.getRadius();

//        float f = 0;
//        float a = 10;
//        float seaLevel = (float) Math.atan((1 - f) * (1 - f) * Math.tan(lat));
//        float seaLevelPoint = (float) Math.sqrt((a * a)/(1 + (1 / (1 - f) * (1 - f) - 1) * Math.sin(seaLevel) * Math.sin(seaLevel)));
//        float x = (float) (a * Math.cos(seaLevel) * Math.cos(lon) + alt * Math.cos(lat) * Math.cos(lon));
//        float y = (float) (a * Math.cos(seaLevel) * Math.sin(lon) + alt * Math.cos(lat) * Math.sin(lon));
//        float z = (float) (a * Math.sin(seaLevel) + alt * Math.sin(lat));


        double asq = Math.pow(r, 2);
        double esq = Math.pow(E, 2);
        double N = r / Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2));

        double x = (N + alt) * Math.cos(lat) * Math.cos(lon);
        double y = (N + alt) * Math.cos(lat) * Math.sin(lon);
        double z = ((1 - esq) * N + alt) * Math.sin(lat);

        double[] ret = {x, y, z};
        return ret;
    }

    private double[] ecef2lla(double[] ecef) {
        final double E = 8.1819190842622e-2;

        double r = earth.getRadius();
        double asq = Math.pow(r, 2);
        double esq = Math.pow(E, 2);

        double x = ecef[0];
        double y = ecef[1];
        double z = ecef[2];

        double b = Math.sqrt(asq * (1 - esq));
        double bsq = Math.pow(b, 2);
        double ep = Math.sqrt((asq - bsq) / bsq);
        double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double th = Math.atan2(r * z, b * p);

        double lon = Math.atan2(y, x);
        double lat = Math.atan2((z + Math.pow(ep, 2) * b * Math.pow(Math.sin(th), 3)), (p - esq * r * Math.pow(Math.cos(th), 3)));
        double N = r / (Math.sqrt(1 - esq * Math.pow(Math.sin(lat), 2)));
        double alt = p / Math.cos(lat) - N;

        // mod lat to 0-2pi
        lon = lon % (2 * Math.PI);

        // correction for altitude near poles left out.
        double[] ret = {lat, lon, alt};

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
}
