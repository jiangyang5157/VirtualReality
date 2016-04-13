package com.gmail.jiangyang5157.cardboard.scene;

public class Coordinate {

    private static final int DECIMAL_PLACE_COUNT = 6;
    private static final int PRECISION_FACTOR = 10 ^ DECIMAL_PLACE_COUNT;

    private float longitude;
    private float latitude;
    private float altitude;

    public Coordinate(float longitude, float latitude, float altitude) {
        set(longitude, latitude, altitude);
    }

    public void set(float longitude, float latitude, float altitude) {
        if (longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("longitude [-180, 180], latitude [-90, 90]");
        }

        this.longitude = Math.round(longitude * PRECISION_FACTOR) / PRECISION_FACTOR;
        this.latitude = Math.round(latitude * PRECISION_FACTOR) / PRECISION_FACTOR;
        this.altitude = altitude;
    }
}
