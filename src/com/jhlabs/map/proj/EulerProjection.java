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

/*
 * This file was semi-automatically converted from the public-domain USGS PROJ source.
 *
 * Bernhard Jenny, 17 September 2010:
 * Euler projection does no longer derive from SimpleConicProjection base class,
 * but from ConicProjection.
 * Fixed bugs in projectInverse().
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class EulerProjection extends ConicProjection {

    private final static double EPS = 1e-10;
    private double rho_c, rho_0, n;

    public EulerProjection() {
        projectionLatitude = Math.toRadians(45);
        projectionLatitude1 = Math.toRadians(35);
        projectionLatitude2 = Math.toRadians(60);
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        double rho = rho_c - lpphi;
        out.x = rho * Math.sin(lplam *= n);
        out.y = rho_0 - rho * Math.cos(lplam);
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        xyy = rho_0 - xyy;
        double rho = MapMath.distance(xyx, xyy);
        if (n < 0.) {
            rho = -rho;
            xyx = -xyx;
            xyy = -xyy;
        }
        out.x = Math.atan2(xyx, xyy) / n;
        out.y = rho_c - rho;
        return out;
    }

    public void initialize() {

        super.initialize();

        double del = 0.5 * (projectionLatitude2 - projectionLatitude1);
        double sig = 0.5 * (projectionLatitude2 + projectionLatitude1);

        if (Math.abs(del) < EPS || Math.abs(sig) < EPS) {
            throw new ProjectionException("-42");
        }
        n = Math.sin(sig) * Math.sin(del) / del;
        del *= 0.5;
        rho_c = del / (Math.tan(del) * Math.tan(sig)) + sig;
        rho_0 = rho_c - projectionLatitude;

    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Euler";
    }
}
