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

import com.jhlabs.map.MapMath;
import java.awt.geom.*;

public class McBrydeThomasFlatPolarSinusoidalProjection extends PseudoCylindricalProjection {

    private static final double m = 0.5;
    private static final double n = 1 + 0.25 * Math.PI;
    private static final double C_y = Math.sqrt((m + 1) / n);
    private static final double C_x = C_y / (m + 1);
    private static final int MAX_ITER = 8;
    private static final double LOOP_TOL = 1e-7;

    public Point2D.Double project(double lam, double phi, Point2D.Double xy) {

        int i;
        double k, V;
        k = n * Math.sin(phi);
        for (i = MAX_ITER; i > 0;) {
            phi -= V = (m * phi + Math.sin(phi) - k) / (m + Math.cos(phi));
            if (Math.abs(V) < LOOP_TOL) {
                break;
            }
            --i;
        }
        if (i == 0) {
            throw new ProjectionException("F_ERROR");
        }

        xy.x = C_x * lam * (m + Math.cos(phi));
        xy.y = C_y * phi;
        return xy;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        y /= C_y;
        lp.y = MapMath.asin((m * y + Math.sin(y)) / n);
        lp.x = x / (C_x * (m + Math.cos(y)));
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isEqualArea() {
        return true;
    }

    public String toString() {
        return "McBryde-Thomas Flat Polar Sinusoidal";
    }
}
