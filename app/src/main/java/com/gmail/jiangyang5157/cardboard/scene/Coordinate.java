package com.gmail.jiangyang5157.cardboard.scene;

public class Coordinate {

    private float latitude;
    private float longitude;
    private float altitude;

    public Coordinate(float latitude, float longitude, float altitude) {
        set(latitude, longitude, altitude);
    }

    public void set(float latitude, float longitude, float altitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("latitude [-90, 90], longitude [-180, 180]");
        }

        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getAltitude() {
        return altitude;
    }

    @Override
    public String toString() {
        return "[" + latitude + ", " + longitude + ", " + altitude + "]";
    }
}
