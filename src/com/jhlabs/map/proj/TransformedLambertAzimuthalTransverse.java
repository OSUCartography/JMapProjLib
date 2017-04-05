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

    /**
     * standard parallel for transverse equal-area cylindrical
     */
    private double lat0 = 0;
    
    private double m;
    private double n;
    
    /**
     * scale factor for longitude
     */
    private double C_LON;
    
    /**
     * scale factor for latitude
     */
    private double C_LAT;

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

        // bounding meridians and bounding parallels for Wagner transformation
        // linear mapping of w to bounding meridian and parallels would be 
        // lonBound = w * Math.PI;
        // latBound = w * Math.PI / 2;
        // A non-linear mapping results in a visually more continous transition.
        double w_ = 1 - Math.cos(Math.PI / 2 * w);
        double lonBound = Math.atan(w) * 4;
        double latBound = w_ * Math.PI / 2;
        
        // equator/central meridian ratio for the equal-area cylindrical with 
        // standard parallel lat0
        double cosLat0 = Math.cos(lat0);
        double pCyl = Math.PI * cosLat0 * cosLat0;
        
        // equator/central meridian ratio for the Lambert azimuthal
        double pAzi = Math.sqrt(2);
        
        // equator/central meridian ratio by linear blending of the two ratios
        double p = pCyl + (pAzi - pCyl) * w_;

        // FIXME
        lonBound = Math.max(lonBound, 0.0000001);
        latBound = Math.max(latBound, 0.0000001);

        //
        m = Math.sin(latBound);
        n = lonBound / Math.PI;
        
        // k: stretching factor to adjust the equator/central meridian ratio 
        double k = Math.sqrt(p * Math.sin(latBound / 2) / Math.sin(lonBound / 2));
        
        // scale factors for longitude and latitude
        double d = Math.sqrt(m * n);
        C_LON = k / d;
        C_LAT = 1 / (k * d);
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

        // Wagner transformation applied to Lambert azimuthal
        lam *= n;
        sin_O = m * sinLat;
        cos_O = Math.sqrt(1 - sin_O * sin_O);
        d = Math.sqrt(2 / (1 + cos_O * Math.cos(lam)));
        
        // invert x and y and flip y coordinate
        xy.y = -C_LON * d * cos_O * Math.sin(lam);
        xy.x = C_LAT * d * sin_O;

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
