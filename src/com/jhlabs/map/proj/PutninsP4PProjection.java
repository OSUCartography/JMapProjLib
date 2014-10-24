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
 * Was confounded with Putnins P4. Corrected name of projection, changed
 * name of class from PutninsP4Projection to PutninsP4PProjection, changed
 * superclass to PseudoCylindricalProjection.
 * Bernhard Jenny, October 28 2008.
 */
package com.jhlabs.map.proj;

import java.awt.geom.*;
import com.jhlabs.map.*;

public class PutninsP4PProjection extends PseudoCylindricalProjection {

    protected double C_x;
    protected double C_y;

    public PutninsP4PProjection() {
        C_x = 0.874038744;
        C_y = 3.883251825;
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double xy) {
        lpphi = MapMath.asin(0.883883476 * Math.sin(lpphi));
        xy.x = C_x * lplam * Math.cos(lpphi);
        xy.x /= Math.cos(lpphi *= 0.333333333333333);
        xy.y = C_y * Math.sin(lpphi);
        return xy;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double lp) {
        lp.y = MapMath.asin(xyy / C_y);
        lp.x = xyx * Math.cos(lp.y) / C_x;
        lp.y *= 3.;
        lp.x /= Math.cos(lp.y);
        lp.y = MapMath.asin(1.13137085 * Math.sin(lp.y));
        return lp;
    }

    public boolean isEqualArea() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Putnins P4'";
    }
}
