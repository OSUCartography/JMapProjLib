package com.jhlabs.map;

public class GeodeticPosition {

    public double lat;
    public double lon;
    public double h;

    public GeodeticPosition() {
        lat = 0;
        lon = 0;
        h = 0;
    }

    public GeodeticPosition(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
        this.h = 0;
    }

    public GeodeticPosition(double lat, double lon, double h) {
        this.lat = lat;
        this.lon = lon;
        this.h = h;
    }
}
