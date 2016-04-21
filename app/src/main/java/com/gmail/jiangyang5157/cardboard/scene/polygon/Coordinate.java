package com.gmail.jiangyang5157.cardboard.scene.polygon;

public class Coordinate {

//    public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
//    public static final double WGS84_FLATTENING = 1.0 / 298.257222101;
//    public static final double WGS84_ECCENTRICITY = Math.sqrt(1 - Math.pow((1 - WGS84_FLATTENING), 2));

    double[] lla;
    double[] ecef;

    public Coordinate(double latitude, double longitude, double altitude, double a, double e) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("latitude [-90, 90], longitude [-180, 180]");
        }
        lla = new double[]{latitude, longitude, altitude};
        ecef = lla2ecef(lla, a, e);
    }

    /**
     * a Semi-major axis
     * f Flattening
     * e Eccentricity
     */
    private double[] lla2ecef(double[] lla, double a, double e) {
        double phi = Math.toRadians(lla[0]);
        double lam = Math.toRadians(lla[1]);
        // We put camera inside the earth, and flipped the texture of the earth in the shader.
        lam *= -1;
        double h = lla[2];

        double e2 = Math.pow(e, 2);
        double sinPhi = Math.sin(phi);
        double n = a / Math.sqrt(1 - e2 * Math.pow(sinPhi, 2));// radius of curvature in the prime vertical
        double distanceFromZ = (n + h) * Math.cos(phi);

        double x = distanceFromZ * Math.cos(lam);
        double y = distanceFromZ * Math.sin(lam);
        double z = (n * (1 - e2) + h) * sinPhi;

        // Ecef coord system is [y-east z-north(up)], and x points to the 0,0
        // Our coord system is [x-east, y-north(up)], and x points to the 0,180
        // So,we reversal x, and use z as y
        double[] ret = {-x, z, y};
        return ret;
    }

    @Override
    public String toString() {
        return "lla: " + String.format("%.5f", lla[0]) + ", " + String.format("%.5f", lla[1]) + ", " + String.format("%.5f", lla[2]);
    }
}
