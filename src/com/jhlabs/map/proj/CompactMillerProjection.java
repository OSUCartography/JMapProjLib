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

public class CompactMillerProjection extends CylindricalProjection {
    
    private final static double K1 = 0.9902;
    private final static double K2 = 0.1604;
    private final static double K3 = -0.03054;
    private final static double C1 = K1;
    private final static double C2 = 3 * K2;
    private final static double C3 = 5 * K3;
    private final static double EPS = 1e-11;
    private final static double MAX_Y = 0.6000207669862655 * Math.PI;
    
    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double out) {
        final double lat_sq = lat * lat;
        out.x = lon;
        out.y = lat * (K1 + lat_sq * (K2 + K3 * lat_sq));
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
            double f = (yc * (K1 + y2 * (K2 + K3 * y2))) - y;
            double fder = C1 + y2 * (C2 + C3 * y2);
            yc -= tol = f / fder;
            if (Math.abs(tol) < EPS) {
                break;
            }
        }
        lp.y = yc;

        // longitude
        lp.x = x;

        return lp;
    }

    @Override
    public String toString() {
        return "Compact Miller";
    }
}
