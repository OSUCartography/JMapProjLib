package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

/**
 * Wagner transformation between Lambert azimuthal and transverse equal-area
 * cylindrical. Useful for portrait-format maps using adaptive composite map
 * projections.
 *
 * @author Bojan Savric, Oregon State University and Esri Inc.
 * @author Bernhard Jenny, Oregon State University and Monash University,
 * Australia
 */
public class TransformedLambertAzimuthalTransverse extends Projection {

    // FIXME missing documentation for fields
    private double lat0;
    private double m;
    private double n;
    private double CA;
    private double CB;

    public TransformedLambertAzimuthalTransverse() {
        setW(0.5);
    }

    /**
     * Weight for transformation between Lambert azimuthal and transverse
     * equal-area cylindrical.
     *
     * @param w 0: transverse cylindrical. 1: Lambert azimuthal.
     */
    final public void setW(double w) {
        if (w < 0 || w > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        lat0 = 0;// FIXME

        double lam1, phi1, p, k, d, pCyl, w_;

//        linear blending
//        lam1 = w * Math.PI;
//        phi1 = w * Math.PI / 2;
//
//        // convert standard parallel to aspect ratio p
//        pCyl = Math.PI * Math.cos(lat0) * Math.cos(lat0);
//        p = pCyl + (1.4142135623730950488016887242097 - pCyl) * w;
        // non-linear blending
        w_ = 1 - Math.cos(Math.PI / 2 * w);
        phi1 = w_ * Math.PI / 2;
        lam1 = Math.atan(w) * 4;

        // convert standard parallel to aspect ratio p
        pCyl = Math.PI * Math.cos(lat0) * Math.cos(lat0);
        p = pCyl + (Math.sqrt(2) - pCyl) * w_;

        lam1 = Math.max(lam1, 0.0000001);
        phi1 = Math.max(phi1, 0.0000001);

        m = Math.sin(phi1);
        n = lam1 / Math.PI;
        k = Math.sqrt(p * Math.sin(phi1 / 2) / Math.sin(lam1 / 2));
        d = Math.sqrt(m * n);
        CA = k / d;
        CB = 1 / (k * d);
    }

    @Override
    public Point2D.Double project(double lam, double phi, Point2D.Double xy) {

        double sin_O, cos_O, d, cosLon, cosLat, sinLat;

        // transverse rotation
        lam += Math.PI / 2;
        cosLon = Math.cos(lam);
        cosLat = Math.cos(phi);
        // Synder 1987 Map Projections - A working manual, eq. 5-10b with alpha = 0
        lam = Math.atan2(cosLat * Math.sin(lam), Math.sin(phi));
        // Synder 1987 Map Projections - A working manual, eq. 5-9 with alpha = 0
        sinLat = -cosLat * cosLon;

        // transformed Lambert azimuthal
        lam *= n;
        sin_O = m * sinLat;
        cos_O = Math.sqrt(1 - sin_O * sin_O);
        d = Math.sqrt(2 / (1 + cos_O * Math.cos(lam)));
        // invert x and y and flip y coordinate
        xy.y = -CA * d * cos_O * Math.sin(lam);
        xy.x = CB * d * sin_O;

        return xy;
    }

    @Override
    public boolean hasInverse() {
        return false;
    }

    @Override
    public boolean isEqualArea() {
        return true;
    }

    @Override
    public String toString() {
        return "Transverse Wagner Transformation between Lambert Azimuthal and Equal-area Cylindrical";
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " Transition for portrait-format maps using adaptive composite map projections.";
    }
}
