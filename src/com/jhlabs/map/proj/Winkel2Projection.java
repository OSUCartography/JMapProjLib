/*
 * Winkel1Projection.java
 *
 * Created on July 17, 2007, 9:04 AM
 *
 */

package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

/**
 * Ported from Proj4 by Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class Winkel2Projection extends PseudoCylindricalProjection {
    
    /**
     * latitude of true scale on central meridian. Default: arccos(2/Pi)
     */
    private double phi1 = Math.acos(2./Math.PI);
    
    /**
     * cosine of latitude of true scale on central meridian
     */
    private double cosphi1 = 2./Math.PI;
    
    private static final int MAX_ITER = 10;
    private static final double LOOP_TOL = 1e-7;
    private static final double TWO_D_PI = 0.636619772367581343;
    
    public Winkel2Projection() {
    }
    
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        
        double k, V;
        int i;
        
        out.y = lpphi * TWO_D_PI;
        k = Math.PI * Math.sin(lpphi);
        lpphi *= 1.8;
        for (i = MAX_ITER; i > 0; --i) {
            lpphi -= V = (lpphi + Math.sin(lpphi) - k) / (1. + Math.cos(lpphi));
            if (Math.abs(V) < LOOP_TOL)
                break;
        }
        if (i == 0)
            lpphi = (lpphi < 0.) ? -MapMath.HALFPI : MapMath.HALFPI;
        else
            lpphi *= 0.5;
        out.x = 0.5 * lplam * (Math.cos(lpphi) + cosphi1);
        out.y = MapMath.QUARTERPI * (Math.sin(lpphi) + out.y);
        
        return out;
        
    }

    public void setLatitudeOfTrueScale(double phi1) {
        if (phi1 < -MapMath.HALFPI || phi1 > MapMath.HALFPI)
            throw new ProjectionException();
        this.phi1 = phi1;
        this.cosphi1 = Math.cos(this.phi1);
    }
    
    public String toString() {
        return "Winkel II";
    }
    
}
