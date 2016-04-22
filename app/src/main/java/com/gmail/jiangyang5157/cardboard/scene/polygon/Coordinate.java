package com.gmail.jiangyang5157.cardboard.scene.polygon;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.sensors.internal.Matrix3x3d;
import com.google.vrtoolkit.cardboard.sensors.internal.Vector3d;

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






//        Vector3d ecefVec = new Vector3d(x, y, z);
//        Vector3d retVec = rotateVector3d(ecefVec, new Vector3d(0, 0, 1), 90);
//        retVec = rotateVector3d(retVec, new Vector3d(0, 1, 0), 90);
//        double[] ret = new double[]{retVec.x, retVec.y, retVec.z};



//        Vector3d ecefVec = new Vector3d(x, y, z);
//        Vector3d retzec = new Vector3d();
//        Vector3d retyVec = new Vector3d();
//        float[] ecef = new float[]{(float) x, (float) y, (float) z};
//        double sin90 = Math.sin(Math.PI);
//        double cos90 = Math.cos(Math.PI);
//        Matrix3x3d zRotation = new Matrix3x3d(
//                cos90, sin90, 0,
//                -sin90, cos90, 0,
//                0, 0, 1);
//        Matrix3x3d yRotation = new Matrix3x3d(
//                cos90, 0, -sin90,
//                0, 1, 0,
//                sin90, 0, cos90);
//        Matrix3x3d.mult(zRotation, ecefVec, retzec);
//        Matrix3x3d.mult(yRotation, retyVec, retzec);
//        double[] ret = new double[]{retyVec.x, retyVec.y, retyVec.z};







        // Ecef coord system is [y-east z-north(up)], and x points to the 0,0
        // Our coord system is [x-east, y-north(up)], and x points to the 0,180
        // So,we reversal x, and use z as y
        double[] ret = {-x, z, y};

        return ret;
    }

    public static Vector3d rotateVector3d(Vector3d vec, Vector3d axis, double theta) {
        double x, y, z;
        double u, v, w;
        x = vec.x;
        y = vec.y;
        z = vec.z;
        u = axis.x;
        v = axis.y;
        w = axis.z;
        double xPrime = u * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + x * Math.cos(theta)
                + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + y * Math.cos(theta)
                + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + z * Math.cos(theta)
                + (-v * x + u * y) * Math.sin(theta);
        return new Vector3d(xPrime, yPrime, zPrime);
    }

    @Override
    public String toString() {
        return "lla: " + String.format("%.5f", lla[0]) + ", " + String.format("%.5f", lla[1]) + ", " + String.format("%.5f", lla[2]);
    }
}
