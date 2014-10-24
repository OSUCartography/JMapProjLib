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
 * With the default parameter n, this is identical to Wagner I.
 * 
 * Bernhard Jenny, 19 September 2010: fixed forward projection.
 * 23 September 2010: fixed type in toString, changed super class to
 * PseudoCylindricalProjection.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class URMFPSProjection extends PseudoCylindricalProjection {

    private final static double C_x = 0.8773826753;
    private final static double Cy = 1.139753528477;
    private double n = 0.8660254037844386467637231707;// wag1
    private double C_y;

    public URMFPSProjection() {
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        lpphi = MapMath.asin(n * Math.sin(lpphi));
        out.x = C_x * lplam * Math.cos(lpphi);
        out.y = C_y * lpphi;
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        xyy /= C_y;
        out.y = MapMath.asin(Math.sin(xyy) / n);
        out.x = xyx / (C_x * Math.cos(xyy));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() { // urmfps
        super.initialize();
        if (n <= 0. || n > 1.) {
            throw new ProjectionException("-40");
        }
        C_y = Cy / n;
    }

    // Properties
    public void setN(double n) {
        this.n = n;
    }

    public double getN() {
        return n;
    }

    public String toString() {
        return "Urmayev Flat-Polar Sinusoidal";
    }
}
