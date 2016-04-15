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
        Log.i("######", "ecef: " + String.format("%.5f", ecef[0]) + ", " + String.format("%.5f", ecef[1]) + ", " + String.format("%.5f", ecef[2]));
        enu = ecef2enu(lla, ecef);
        Log.i("########", "enu: " + String.format("%.5f", enu[0]) + ", " + String.format("%.5f", enu[1]) + ", " + String.format("%.5f", enu[2]));
    }

    public void setLla(double latitude, double longitude, double altitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("latitude [-90, 90], longitude [-180, 180]");
        }
        lla = new double[]{latitude, longitude, altitude};
    }

    private double[] lla2ecef(double[] lla, double a, double e) {
        double phi = Math.toRadians(lla[1]);
        double lam = Math.toRadians(lla[0]);
        double h = lla[2];

        double e2 = Math.pow(e, 2);
        double sinPhi = Math.sin(phi);
        // compute radius of curvature in prime vertical
        double n = a / Math.sqrt(1 - e2 * Math.pow(sinPhi, 2));
        double distanceFromZ = (n + h) * Math.cos(phi);

        double x = distanceFromZ * Math.cos(lam);
        double y = distanceFromZ * Math.sin(lam);
        double z = (n * (1 - e2) + h) * sinPhi;

        double[] ret = {x, y, z};
        return ret;
    }

    private double[] ecef2enu(double[] lla, double[] ecef) {
        double phi = Math.toRadians(lla[1]);
        double lam = Math.toRadians(lla[0]);

        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);
        double sinLam = Math.sin(lam);
        double cosLam = Math.cos(lam);

        double[] rotation = new double[]{
                -sinLam, cosLam, 0,
                (-sinPhi * cosLam), (-sinPhi * sinLam), cosPhi,
                (cosPhi * cosLam), (cosPhi * sinLam), sinPhi
        };

        double enu[] = new double[3];
        for (int i = 0; i < 3; i++) {
            enu[i] = 0.0;
            for (int j = 0; j < 3; j++) {
                enu[i] += rotation[i * 3 + j] * ecef[j];
            }
        }
        return enu;
    }

    @Override
    public String toString() {
        return "lla: " + String.format("%.5f", lla[0]) + ", " + String.format("%.5f", lla[1]) + ", " + String.format("%.5f", lla[2]);
    }
}
