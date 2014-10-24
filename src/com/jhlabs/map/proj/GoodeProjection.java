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
 */
/**
 * Changed superclass from Projection to PseudoCylindricalProjection.
 * Bernhard Jenny, May 25 2010.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class GoodeProjection extends PseudoCylindricalProjection {

    private final static double Y_COR = 0.05280;
    private final static double PHI_LIM = .71093078197902358062;
    private SinusoidalProjection sinu = new SinusoidalProjection();
    private MollweideProjection moll = new MollweideProjection();

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        if (Math.abs(lpphi) <= PHI_LIM) {
            out = sinu.project(lplam, lpphi, out);
        } else {
            out = moll.project(lplam, lpphi, out);
            out.y -= lpphi >= 0.0 ? Y_COR : -Y_COR;
        }
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        if (Math.abs(xyy) <= PHI_LIM) {
            out = sinu.projectInverse(xyx, xyy, out);
        } else {
            xyy += xyy >= 0.0 ? Y_COR : -Y_COR;
            out = moll.projectInverse(xyx, xyy, out);
        }
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Goode Homolosine";
    }
}
