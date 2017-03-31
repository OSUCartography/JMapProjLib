package com.jhlabs.map.proj;

import java.awt.geom.Point2D;
import java.time.Year;

public class LambertAzimuthalEqualAreaProjection extends AzimuthalProjection {

    private static final double lat0 = 0;
    private static final double cosLat0 = 1;
    private static final double sinLat0 = 0;

    public LambertAzimuthalEqualAreaProjection() {
    }

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        /* polar aspect
        double y = 2 * Math.sin(FORTPI - lpphi * 0.5);
        out.x = y * Math.sin(lplam);
        out.y = y * -Math.cos(lplam);
         */
        double sinLat = Math.sin(lpphi);
        double cosLat = Math.cos(lpphi);
        double cosLon = Math.cos(lplam);
        double sinLon = Math.sin(lplam);
        double y = 1 + sinLat0 * sinLat + cosLat0 * cosLat * cosLon;
        // the projection is indeterminate for lon = PI and lat = -lat0
        // this point would have to be plotted as a circle
        // The following Math.sqrt will return NaN in this case.
        y = Math.sqrt(2 / y);
        out.x = y * cosLat * sinLon;
        out.y = y * (cosLat0 * sinLat - sinLat0 * cosLat * cosLon);

        return out;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {

        /* polar aspetc
        double rh = Math.sqrt(x * x + y * y);
        double phi = rh * .5;
        out.x = Math.atan2(x, -y);
        out.y = HALFPI - 2. * Math.asin(phi);
         */
        double dd = x * x + y * y;
        if (dd > 4) {
            out.x = out.y = Double.NaN;
            return out;
        }
        double rh = Math.sqrt(dd);
        double phi = rh * 0.5;
        phi = 2 * Math.asin(phi);
        double sinz = Math.sin(phi);
        double cosz = Math.cos(phi);
        out.y = phi = (rh <= EPS10) ? lat0 : Math.asin(cosz * sinLat0 + y * sinz * cosLat0 / rh);
        x *= sinz * cosLat0;
        y = (cosz - Math.sin(phi) * sinLat0) * rh;
        out.x = (y == 0) ? 0 : Math.atan2(x, y);

        return out;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public String toString() {
        return "Lambert Azimuthal Equal Area";
    }

    @Override
    public boolean isEqualArea() {
        return true;
    }

    @Override
    public Year getYear() {
        return Year.of(1772);
    }

    @Override
    public String getAuthor() {
        return "Johann Heinrich Lambert (1728Ð1777)";
    }
}
