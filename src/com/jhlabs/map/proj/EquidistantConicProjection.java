/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

/**
 * This file was semi-automatically converted from the public-domain USGS PROJ source.
 *
 * Bernhard Jenny, 16 September 2010:
 * Added project and projectInverse, commented out transform and transformInverse
 * and related variables (which are not functional). Only the spherical case is
 * currently supported.
 * 
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

/**
 * The Equidistant Conic projection.
 */
public class EquidistantConicProjection extends ConicProjection {

    /*private double standardLatitude1;
    private double standardLatitude2;
    private double eccentricity = 0.822719;
    private double eccentricity2 = eccentricity * eccentricity;
    private double eccentricity4 = eccentricity2 * eccentricity2;
    private double eccentricity6 = eccentricity2 * eccentricity4;
    private double radius = 1;
    private boolean northPole;
    private double f, n, rho0;*/

    /**
     * pre-computed values derived from projectionLatitude, projectionLatitude1
     * and projectionLatitude2.
     */
    private double rho0, c, n;


    public EquidistantConicProjection() {

        projectionLatitude = Math.toRadians(45);
        projectionLatitude1 = Math.toRadians(35);
        projectionLatitude2 = Math.toRadians(60);

        //initialize(MapMath.degToRad(0), MapMath.degToRad(37.5), standardLatitude1, standardLatitude2);
    }

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        final double rho = c - lpphi;
	out.x = rho * Math.sin( lplam *= n );
	out.y = rho0 - rho * Math.cos(lplam);

        return out;
    }
    
    /*
    public Point2D.Double transform(Point2D.Double in, Point2D.Double out) {
        double lon = MapMath.normalizeLongitude(in.x - projectionLongitude);
        double lat = in.y;
        double rho, theta, hold1, hold2, hold3;

        hold2 = Math.pow(((1.0 - eccentricity * Math.sin(lat)) / (1.0 + eccentricity * Math.sin(lat))), 0.5 * eccentricity);
        hold3 = Math.tan(MapMath.QUARTERPI - 0.5 * lat);
        hold1 = (hold3 == 0.0) ? 0.0 : Math.pow(hold3 / hold2, n);
        rho = radius * f * hold1;
        theta = n * lon;

        out.x = rho * Math.sin(theta);
        out.y = rho0 - rho * Math.cos(theta);
        return out;
    }*/
 
    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double dst) {

        double rho = Math.hypot(x, y = rho0 - y);
        if (rho > 0) {
            if (n < 0.) {
                rho = -rho;
                x = -x;
                y = -y;
            }
            dst.y = c - rho;
            //if (P->ellips)
            //	lp.phi = proj_inv_mdist(lp.phi, P->en);
            dst.x = Math.atan2(x, y) / n;
        } else {
            dst.x = 0.;
            dst.y = n > 0. ? MapMath.HALFPI : -MapMath.HALFPI;
        }

        return dst;
    }

    /*
    public Point2D.Double inverseTransform(Point2D.Double in, Point2D.Double out) {
        double theta, temp, rho, t, tphi, phi = 0, delta;

        theta = Math.atan(in.x / (rho0 - in.y));
        out.x = (theta / n) + projectionLongitude;

        temp = in.x * in.x + (rho0 - in.y) * (rho0 - in.y);
        rho = Math.sqrt(temp);
        if (n < 0) {
            rho = -rho;
        }
        t = Math.pow((rho / (radius * f)), 1. / n);
        tphi = MapMath.HALFPI - 2.0 * Math.atan(t);
        delta = 1.0;
        for (int i = 0; i < 100 && delta > 1.0e-8; i++) {
            temp = (1.0 - eccentricity * Math.sin(tphi)) / (1.0 + eccentricity * Math.sin(tphi));
            phi = MapMath.HALFPI - 2.0 * Math.atan(t * Math.pow(temp, 0.5 * eccentricity));
            delta = Math.abs(Math.abs(tphi) - Math.abs(phi));
            tphi = phi;
        }
        out.y = phi;
        return out;
    }
    */

    /*
    private void initialize(double rlong0, double rlat0, double standardLatitude1, double standardLatitude2) {
        super.initialize();
        double t_standardLatitude1, m_standardLatitude1, t_standardLatitude2, m_standardLatitude2, t_rlat0;

        northPole = rlat0 > 0.0;
        projectionLatitude = northPole ? MapMath.HALFPI : -MapMath.HALFPI;

        t_standardLatitude1 = Math.tan(MapMath.QUARTERPI - 0.5 * standardLatitude1) / Math.pow((1.0 - eccentricity
                * Math.sin(standardLatitude1)) / (1.0 + eccentricity * Math.sin(standardLatitude1)), 0.5 * eccentricity);
        m_standardLatitude1 = Math.cos(standardLatitude1) / Math.sqrt(1.0 - eccentricity2
                * Math.pow(Math.sin(standardLatitude1), 2.0));
        t_standardLatitude2 = Math.tan(MapMath.QUARTERPI - 0.5 * standardLatitude2) / Math.pow((1.0 - eccentricity
                * Math.sin(standardLatitude2)) / (1.0 + eccentricity * Math.sin(standardLatitude2)), 0.5 * eccentricity);
        m_standardLatitude2 = Math.cos(standardLatitude2) / Math.sqrt(1.0 - eccentricity2
                * Math.pow(Math.sin(standardLatitude2), 2.0));
        t_rlat0 = Math.tan(MapMath.QUARTERPI - 0.5 * rlat0)
                / Math.pow((1.0 - eccentricity * Math.sin(rlat0))
                / (1.0 + eccentricity * Math.sin(rlat0)), 0.5 * eccentricity);

        if (standardLatitude1 != standardLatitude2) {
            n = (Math.log(m_standardLatitude1) - Math.log(m_standardLatitude2)) / (Math.log(t_standardLatitude1) - Math.log(t_standardLatitude2));
        } else {
            n = Math.sin(standardLatitude1);
        }

        f = m_standardLatitude1 / (n * Math.pow(t_standardLatitude1, n));
        projectionLongitude = rlong0;
        rho0 = radius * f * Math.pow(t_rlat0, n);
    }
    */

    @Override
    public void initialize() {

        super.initialize();

        if (Math.abs(projectionLatitude1 + projectionLatitude2) < EPS10) {
            throw new ProjectionException("-21");
        }
	double sinphi = Math.sin(projectionLatitude1);
	double cosphi = Math.cos(projectionLatitude1);
	boolean secant = Math.abs(projectionLatitude1 - projectionLatitude2) >= EPS10;

        if (secant) {
            n = (cosphi - Math.cos(projectionLatitude2)) / (projectionLatitude2 - projectionLatitude1);
        } else {
            n = sinphi;
        }
        c = projectionLatitude1 + Math.cos(projectionLatitude1) / n;
        rho0 = c - projectionLatitude /*phi0 */;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public String toString() {
        return "Equidistant Conic";
    }
    
    @Override
    public String getAuthor() {
        return "Claudius Ptolemy about A. D. 100 (rudimentary), improved by Johannes Ruysch in 1506, Gerardus Mercator late 16th century, Nicolas de l'Isle in 1745";
    }
    
    @Override
    public String getHistoryDescription() {
        return super.getHistoryDescription() + "\nVariations of standard parallels by Patrick Murdoch (projections I, III) in 1758, Leonhard Euler in 1777.";
    }
}
