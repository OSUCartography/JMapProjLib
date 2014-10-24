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
 * Fixed bug in forward projection and increased precisions of constants.
 * Changed superclass to PseudoCylindricalProjection.
 * Bernhard Jenny, October 28 2008
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class Wagner2Projection extends PseudoCylindricalProjection {

    private final static double C_x = 0.9248327337222211159780313106;
    private final static double C_y = 1.387249100583331673967046966;
    private final static double C_p1 = 0.8855017059025996450524064573;
    private final static double C_p2 = 0.8802234877744129284498330453;

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        lpphi = MapMath.asin(C_p1 * Math.sin(C_p2 * lpphi));
        out.x = C_x * lplam * Math.cos(lpphi);
        out.y = C_y * lpphi;
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.y = xyy / C_y;
        out.x = xyx / (C_x * Math.cos(out.y));
        out.y = MapMath.asin(Math.sin(out.y) / C_p1) / C_p2;
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Wagner II";
    }
}
