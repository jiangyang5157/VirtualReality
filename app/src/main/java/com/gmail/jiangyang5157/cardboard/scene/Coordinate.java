package com.gmail.jiangyang5157.cardboard.scene;

public class Coordinate {

    public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    public static final double WGS84_FLATTENING = 1.0 / 298.257222101;
    public static final double WGS84_ECCENTRICITY = Math.sqrt(1 - Math.pow((1 - WGS84_FLATTENING), 2));
    public static final double DEFAULT_ECCENTRICITY = 0;

    public final double latitude;
    public final double longitude;
    public final double altitude;

    public final double[] ecef;

    public Coordinate(double latitude, double longitude, double altitude, double a) {
        this(latitude, longitude, altitude, a, DEFAULT_ECCENTRICITY);
    }

    public Coordinate(double latitude, double longitude, double altitude, double a, double e) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;

        ecef = lla2ecef(new double[]{latitude, longitude, altitude}, a, e);
    }

    /**
     * a Semi-major axis
     * f Flattening
     * e Eccentricity
     */
    private double[] lla2ecef(double[] lla, double a, double e) {
        double phi = Math.toRadians(lla[0]);
        double lam = Math.toRadians(lla[1]);
        // We put matrix inside the earth, and flipped the texture of the earth in the shader.
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
        return new double[]{-x, z, y};
    }

    @Override
    public String toString() {
        return "lat/lng/alt: (" + this.latitude + "," + this.longitude + "," + this.altitude + ")";
    }
}
