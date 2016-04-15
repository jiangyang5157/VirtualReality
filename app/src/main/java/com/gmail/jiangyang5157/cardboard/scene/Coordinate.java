package com.gmail.jiangyang5157.cardboard.scene;

import android.util.Log;

public class Coordinate {

    private static final double WGS84_A = 6378137.0;
    private static final double WGS84_E = 8.1819190842622e-2;

    double[] lla;
    double[] ecef;
    double[] enu;

    public Coordinate(double latitude, double longitude, double altitude, double a, double e) {
        setLla(latitude, longitude, altitude);
        Log.i("####", "lla: " + String.format("%.5f", lla[0]) + ", " + String.format("%.5f", lla[1]) + ", " + String.format("%.5f", lla[2]));
        ecef = lla2ecef(lla, a, e);
        Log.i("########", "ecef: " + String.format("%.5f", ecef[0]) + ", " + String.format("%.5f", ecef[1]) + ", " + String.format("%.5f", ecef[2]));
//        enu = ecef2enu();
    }

    public void setLla(double latitude, double longitude, double altitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("latitude [-90, 90], longitude [-180, 180]");
        }
        lla = new double[]{latitude, longitude, altitude};
    }

    private double[] lla2ecef(double[] lla, double a, double e) {
        double lat = lla[1];
        double lon = lla[0];
        double alt = lla[2];
        lat = Math.toRadians(lat);
        lon = Math.toRadians(lon);

        double e2 = Math.pow(e, 2);
        double n = a / Math.sqrt(1 - e2 * Math.pow(Math.sin(lat), 2));

        double x = (n + alt) * Math.cos(lat) * Math.cos(lon);
        double y = (n + alt) * Math.cos(lat) * Math.sin(lon);
        double z = (n * (1 - e2) + alt) * Math.sin(lat);

        double[] ret = {x, y, z};
        return ret;
    }

    @Override
    public String toString() {
        return "lla: " + String.format("%.5f", lla[0]) + ", " + String.format("%.5f", lla[1]) + ", " + String.format("%.5f", lla[2]);
    }
}
