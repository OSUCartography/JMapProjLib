/*
 Copyright 2014 Bojan Savric

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

import java.awt.geom.*;

/**
 * The Natural Earth II projection was designed by Tom Patterson, US National
 * Park Service, in 2012, using Flex Projector. The polynomial equations for the
 * projection were developed by Bojan Savric, Oregon State University, in
 * collaboration with Tom Patterson and Bernhard Jenny, Oregon State University.
 *
 * @author Bojan Savric
 */
public class NaturalEarth2Projection extends PseudoCylindricalProjection {

    private static final double A0 = 0.84719;
    private static final double A1 = -0.13063;
    private static final double A2 = -0.04515;
    private static final double A3 = 0.05494;
    private static final double A4 = -0.02326;
    private static final double A5 = 0.00331;
    private static final double B0 = 1.01183;
    private static final double B1 = -0.02625;
    private static final double B2 = 0.01926;
    private static final double B3 = -0.00396;
    private static final double C0 = B0;
    private static final double C1 = 9 * B1;
    private static final double C2 = 11 * B2;
    private static final double C3 = 13 * B3;
    private static final double EPS = 1e-11;
    private static final double MAX_Y = 0.84719 * 0.5351175 * Math.PI;

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        double phi2 = lpphi * lpphi;
        double phi4 = phi2 * phi2;
        double phi6 = phi4 * phi2;

        out.x = lplam * (A0 + A1 * phi2 + phi6 * phi6 * (A2 + A3 * phi2 + A4 * phi4 + A5 * phi6));
        out.y = lpphi * (B0 + phi4 * phi4 * (B1 + B2 * phi2 + B3 * phi4));

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
            double f = (yc * (B0 + y4 * y4 * (B1 + B2 * y2 + B3 * y4))) - y;
            double fder = C0 + y4 * y4 * (C1 + C2 * y2 + C3 * y4);
            yc -= tol = f / fder;
            if (Math.abs(tol) < EPS) {
                break;
            }
        }
        lp.y = yc;

        // longitude
        double y2 = yc * yc;
        double y4 = y2 * y2;
        double y6 = y4 * y2;

        double phi = A0 + A1 * y2 + y6 * y6 * (A2 + A3 * y2 + A4 * y4 + A5 * y6);
        lp.x = x / phi;

        return lp;
    }

    @Override
    public String toString() {
        return "Natural Earth II";
    }
}
