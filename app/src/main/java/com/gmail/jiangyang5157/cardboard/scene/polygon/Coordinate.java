package com.gmail.jiangyang5157.cardboard.scene.polygon;

import com.google.vrtoolkit.cardboard.sensors.internal.Matrix3x3d;
import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

public class Coordinate {

    public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    public static final double WGS84_FLATTENING = 1.0 / 298.257222101;
    private static final double WGS84_ECCENTRICITY = Math.sqrt(1 - Math.pow((1 - WGS84_FLATTENING), 2));

    public static final double ECCENTRICITY = 0;

    public final double latitude;
    public final double longitude;
    public final double altitude;

    public final double[] ecef;

    public Coordinate(double latitude, double longitude, double altitude, double a) {
        this(latitude, longitude, altitude, a, ECCENTRICITY);
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

//        Matrix3x3d m = new Matrix3x3d();
//        m.setIdentity();
//        //Matrix3x3d.mult(getRotationMatrixFromU(new double[]{1, 0, 0}, 90), m, m);
//        Vector3d ecefVec = new Vector3d(x, y, z);
//        double length = ecefVec.length();
//        ecefVec.normalize();
//        Matrix3x3d.mult(m, ecefVec, ecefVec);
//        ecefVec = new Vector3d(ecefVec.x * length, ecefVec.y * length, ecefVec.z * length);
//        double[] ret = new double[]{-ecefVec.x, ecefVec.z, ecefVec.y};

        return ret;
    }

    private Matrix3x3d getRotationMatrixFromX(double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        return new Matrix3x3d(
                1, 0, 0,
                0, cos, sin,
                0, -sin, cos
        );
    }

    private Matrix3x3d getRotationMatrixFromY(double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        return new Matrix3x3d(
                cos, 0, -sin,
                0, 1, 0,
                sin, 0, cos
        );
    }

    private Matrix3x3d getRotationMatrixFromZ(double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        return new Matrix3x3d(
                cos, sin, 0,
                -sin, cos, 0,
                0, 0, 1
        );
    }

    private Matrix3x3d getRotationMatrixFromU(double[] u, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        double u0sin = u[0] * sin;
        double u1sin = u[1] * sin;
        double u2sin = u[2] * sin;
        double u0u1 = u[0] * u[1];
        double u0u2 = u[0] * u[2];
        double u1u2 = u[1] * u[2];

        return new Matrix3x3d(
                Math.pow(u[0], 2) * (1.0 - cos) + cos, u0u1 * (1.0 - cos) + u2sin, u0u2 * (1.0 - cos) - u1sin,
                u0u1 * (1.0 - cos) - u2sin, Math.pow(u[1], 2) * (1.0 - cos) + cos, u1u2 * (1.0 - cos) + u0sin,
                u0u2 * (1.0 - cos) + u1sin, u1u2 * (1.0 - cos) - u0sin, Math.pow(u[2], 2) * (1.0 - cos) + cos
        );
    }

    @Override
    public String toString() {
        return "lat/lng/alt: (" + this.latitude + "," + this.longitude + "," + this.altitude + ")";
    }
}
