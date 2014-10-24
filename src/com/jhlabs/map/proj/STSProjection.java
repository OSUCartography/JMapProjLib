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
 * Bernhard Jenny, May 25 2010:
 * Changed superclass from ConicProjection to PseudoCylindricalProjection, and
 * made abstract.
 * Bernhard Jenny, 19 September 2010:
 * Fixed inverse projection.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

/**
 * Sine-Tangent Series
 * Abstract base class for Kavraisky5Projection, McBrydeThomasSine1Projection,
 * FoucautProjection, and QuarticAuthalicProjection.
 */
public abstract class STSProjection extends PseudoCylindricalProjection {

    private double C_x;
    private double C_y;
    private double C_p;
    private boolean tan_mode;

    protected STSProjection(double p, double q, boolean mode) {
        es = 0.;
        C_x = q / p;
        C_y = p;
        C_p = 1 / q;
        tan_mode = mode;
        initialize();
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double xy) {
        double c;

        xy.x = C_x * lplam * Math.cos(lpphi);
        xy.y = C_y;
        lpphi *= C_p;
        c = Math.cos(lpphi);
        if (tan_mode) {
            xy.x *= c * c;
            xy.y *= Math.tan(lpphi);
        } else {
            xy.x /= c;
            xy.y *= Math.sin(lpphi);
        }
        return xy;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double lp) {
        double c;

        xyy /= C_y;
        c = Math.cos(lp.y = tan_mode ? Math.atan(xyy) : MapMath.asin(xyy));
        lp.x = xyx / (C_x * Math.cos(lp.y /= C_p));
        if (tan_mode) {
            lp.x /= c * c;
        } else {
            lp.x *= c;
        }
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }
}
