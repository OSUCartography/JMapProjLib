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
 * Bernhard Jenny, May 25 2010:
 * Changed super class from Projection to PseudoCylindricalProjection.
 * Bernhard Jenny, 19 September 2010:
 * Fixed inverse projection.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class FaheyProjection extends PseudoCylindricalProjection {

    private final static double TOL = 1e-6;

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.y = 1.819152 * (out.x = Math.tan(0.5 * lpphi));
        out.x = 0.819152 * lplam * asqrt(1 - out.x * out.x);
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.y = 2. * Math.atan(xyy /= 1.819152);
        out.x = Math.abs(xyy = 1. - xyy * xyy) < TOL ? 0.
                : xyx / (0.819152 * Math.sqrt(xyy));
        return out;
    }

    private double asqrt(double v) {
        return (v <= 0) ? 0. : Math.sqrt(v);
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Fahey";
    }
}
