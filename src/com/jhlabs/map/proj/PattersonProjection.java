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

public class PattersonProjection extends CylindricalProjection {
    
    private final static double k1 = 1.0148;
    private final static double k2 = 0.23185;
    private final static double k3 = -0.14499;
    private final static double k4 = 0.02406;
    private final static double c1 = k1;
    private final static double c2 = 5 * k2;
    private final static double c3 = 7 * k3;
    private final static double c4 = 9 * k4;
    private final static double EPS = 1e-11;
    private final static double MAX_Y = 1.790857183;
    
    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double out) {
        final double lat_sq = lat * lat;
        out.x = lon;
        out.y = lat * (k1 + lat_sq * lat_sq * (k2 + lat_sq * (k3 + k4 * lat_sq)));
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
            double f = (yc * (k1 + y2 * y2 * (k2 + y2 * (k3 + k4 * y2)))) - y;
            double fder = c1 + y2 * y2 * (c2 + y2 * (c3 + c4 * y2));
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
        return "Patterson Cylindrical";
    }
}
