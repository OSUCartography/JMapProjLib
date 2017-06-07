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

    public static void main(String[] args) {
        TransformedLambertAzimuthalTransverse proj = new TransformedLambertAzimuthalTransverse();
        proj.setW(0.25);
        proj.setCentralLatitude(Math.PI / 4);
        proj.setEllipsoid(com.jhlabs.map.Ellipsoid.SPHERE);
        proj.initialize();
        Point2D.Double pt = new Point2D.Double();
        double lon = -125;
        double lat = -33;
        proj.transform(lon, lat, pt);
        proj.inverseTransform(pt, pt);
        System.out.println(pt.x + " " + pt.y);
    }

    /**
     * Standard parallel for equal-area cylindrical with equatorial aspect in
     * radians. Transverse cylindrical projections commonly use a scale factor k
     * smaller than 1 instead of a standard parallel. There are currently no
     * setter and getter for lat0.
     */
    private final double lat0 = 0;

    /**
     * latitude of the central point in radians
     */
    private double centralLat = 0;

    /**
     * Wagner transformation parameters m and n
     */
    private double m;
    private double n;
    private double sqrt_mn; // sqrt(m * n)

    /**
     * stretching factor to adjust the equator/central meridian ratio
     */
    private double k;

    public TransformedLambertAzimuthalTransverse() {
        setW(0.75);
    }

    /**
     * Weight for transformation between Lambert azimuthal and transverse
     * equal-area cylindrical.
     *
     * @param w 0: transverse cylindrical. 1: Lambert azimuthal.
     */
    final public void setW(double w) {
        setW(w, true);
    }

    /**
     * Weight for transformation between Lambert azimuthal and transverse
     * equal-area cylindrical.
     *
     * @param w 0: transverse cylindrical. 1: Lambert azimuthal
     * @param visuallyContinuous if true, w is mapped non-linearly to bounding
     * meridians and parallels for the Wagner transformation, which results in a
     * visually more continuous transformation.
     */
    final public void setW(double w, boolean visuallyContinuous) {
        if (w < 0 || w > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }

        // bounding meridians and bounding parallels for Wagner transformation
        double lonBound, latBound;

        // compute bounding meridians and bounding parallels from weight w
        if (visuallyContinuous) {
            // non-linear mapping of w to bounding meridians and parallels 
            // results in a visually more continous transition
            lonBound = Math.atan(w) * 4;
            w = 1 - Math.cos(Math.PI / 2 * w);
        } else {
            // linear mapping of w to bounding meridians and parallels
            lonBound = w * Math.PI;
        }
        latBound = w * Math.PI / 2;

        // equator/central meridian ratio for the equal-area cylindrical with 
        // standard parallel lat0
        double cosLat0 = Math.cos(lat0);
        double pCyl = Math.PI * cosLat0 * cosLat0;

        // equator/central meridian ratio for the Lambert azimuthal
        double pAzi = Math.sqrt(2);

        // equator/central meridian ratio by linear blending of the two ratios
        double p = pCyl + (pAzi - pCyl) * w;

        // FIXME
        lonBound = Math.max(lonBound, 0.0000001);
        latBound = Math.max(latBound, 0.0000001);

        // Wagner transformation parameters
        m = Math.sin(latBound);
        n = lonBound / Math.PI;
        sqrt_mn = Math.sqrt(m * n);

        // k: stretching factor to adjust the equator/central meridian ratio 
        k = Math.sqrt(p * Math.sin(latBound / 2) / Math.sin(lonBound / 2));
    }

    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double xy) {

        // transverse rotation
        double cosLat = Math.cos(lat);
        double sinLat = Math.sin(lat);
        double cosLon = Math.cos(lon);
        double sinLon = Math.sin(lon);
        // Synder 1987 Map Projections - A working manual, eq. 5-10b with alpha = 0
        double lon_ = Math.atan2(-sinLat, cosLat * cosLon) + centralLat;
        // Synder 1987 Map Projections - A working manual, eq. 5-9 with alpha = 0
        double sinLat_ = cosLat * sinLon;

        // adjusting longitude value before applying Wagner transformation
        // rotated longitude must be in the range +/- PI
        if (Math.abs(lon_) > Math.PI) {
            lon_ += (lon_ < 0.0) ? Math.PI * 2 : -Math.PI * 2;
        }

        // Wagner transformation applied to Lambert azimuthal
        lon_ *= n;
        double sin_O = m * sinLat_;
        double cos_O = Math.sqrt(1 - sin_O * sin_O);
        double d = Math.sqrt(2 / (1 + cos_O * Math.cos(lon_)));

        // invert x and y and flip y coordinate
        xy.x = d * sin_O / (k * sqrt_mn);
        xy.y = -k / sqrt_mn * d * cos_O * Math.sin(lon_);

        return xy;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {
        double X = x * k * sqrt_mn;
        double Y = -y * sqrt_mn / k;
        double Z = Math.sqrt(1 - (X * X + Y * Y) / 4);
        double lon_ = Math.atan2(Z * Y, 2 * Z * Z - 1) / n;
        double sinLat_ = Z * X / m;
        double lat_ = Math.asin(sinLat_);
        double cosLat_ = Math.cos(lat_);
        out.x = Math.atan2(sinLat_, cosLat_ * Math.cos(lon_ - centralLat));
        out.y = Math.asin(-cosLat_ * Math.sin(lon_ - centralLat));
        return out;
    }

    @Override
    public boolean hasInverse() {
        return true;
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

    /**
     * Get latitude of central point.
     *
     * @return central latitude in radians
     */
    public double getCentralLatitude() {
        return centralLat;
    }

    /**
     * Set latitude of central point.
     *
     * @param centralLat central latitude in radians
     */
    public void setCentralLatitude(double centralLat) {
        assert (centralLat >= -Math.PI / 2 && centralLat <= Math.PI / 2);
        this.centralLat = centralLat;
    }
}
