/*
Copyright 2011 Bojan Savric
Revised version of April 6, 2010.


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
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

/**
 * The Natural Earth projection was designed by Tom Patterson, US National Park
 * Service, in 2007, using Flex Projector. The shape of the original projection
 * was defined at every 5 degrees and piece-wise cubic spline interpolation was
 * used to compute the complete graticule.
 * The code here uses polynomial functions instead of cubic splines and
 * is therefore much simpler to program. The polynomial approximation was
 * developed by Bojan Savric, in collaboration with Tom Patterson and Bernhard
 * Jenny, Institute of Cartography, ETH Zurich. It slightly deviates from
 * Patterson's original projection by adding additional curvature to meridians
 * where they meet the horizontal pole line. This improvement is by intention
 * and designed in collaboration with Tom Patterson.
 *
 * @author Bojan Savric
 */
public class NaturalEarthProjection extends PseudoCylindricalProjection {

    private static final double A0 = 0.8707;
    private static final double A1 = -0.131979;
    private static final double A2 = -0.013791;
    private static final double A3 = 0.003971;
    private static final double A4 = -0.001529;
    private static final double B0 = 1.007226;
    private static final double B1 = 0.015085;
    private static final double B2 = -0.044475;
    private static final double B3 = 0.028874;
    private static final double B4 = -0.005916;
    private static final double C0 = B0;
    private static final double C1 = 3 * B1;
    private static final double C2 = 7 * B2;
    private static final double C3 = 9 * B3;
    private static final double C4 = 11 * B4;
    private static final double EPS = 1e-11;
    private static final double MAX_Y = 0.8707 * 0.52 * Math.PI;

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        double phi2 = lpphi * lpphi;
        double phi4 = phi2 * phi2;

        out.x = lplam * (A0 + phi2 * (A1 + phi2 * (A2 + phi4 * phi2 * (A3 + phi2 * A4))));
        out.y = lpphi * (B0 + phi2 * (B1 + phi4 * (B2 + B3 * phi2 + B4 * phi4)));

        return out;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
     
        // make sure y is inside valid range
        if (y > MAX_Y) {
            y = MAX_Y;
        } else if (y < -MAX_Y) {
            y = -MAX_Y;
        }

        // latitude
        double yc = y;
        double tol;
        for (;;) { // Newton-Raphson
            double y2 = yc * yc;
            double y4 = y2 * y2;
            double f = (yc * (B0 + y2 * (B1 + y4 * (B2 + B3 * y2 + B4 * y4)))) - y;
            double fder = C0 + y2 * (C1 + y4 * (C2 + C3 * y2 + C4 * y4));
            yc -= tol = f / fder;
            if (Math.abs(tol) < EPS) {
                break;
            }
        }
        lp.y = yc;

        // longitude
        double y2 = yc * yc;
        double phi = A0 + y2 * (A1 + y2 * (A2 + y2 * y2 * y2 * (A3 + y2 * A4)));
        lp.x = x / phi;

        return lp;
    }

    @Override
    public String toString() {
        return "Natural Earth";
    }
}
