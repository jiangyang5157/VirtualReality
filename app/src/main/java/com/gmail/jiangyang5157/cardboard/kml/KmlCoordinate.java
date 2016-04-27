package com.gmail.jiangyang5157.cardboard.kml;

/**
 * @author Yang
 * @since 4/27/2016
 */
public class KmlCoordinate {

    public final double latitude;
    public final double longitude;

    public KmlCoordinate(double latitude, double longitude) {
        this.latitude = Math.max(-90.0D, Math.min(90.0D, latitude));
        if (-180.0D <= longitude && longitude < 180.0D) {
            this.longitude = longitude;
        } else {
            this.longitude = ((longitude - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof KmlCoordinate)) {
            return false;
        } else {
            KmlCoordinate that = (KmlCoordinate) o;
            return Double.doubleToLongBits(this.latitude) == Double.doubleToLongBits(that.latitude)
                    && Double.doubleToLongBits(this.longitude) == Double.doubleToLongBits(that.longitude);
        }
    }

    @Override
    public String toString() {
        return "lat/lng: (" + this.latitude + "," + this.longitude + ")";
    }
}
