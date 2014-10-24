/*
Copyright 2008 Bernhard Jenny

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
import java.awt.geom.Point2D;

/**
 * Miller's second cylindrical projection, which is not used as often as his
 * first one. Very similar to Braun's cylindrical stereographic projection.
 * Based on proj4.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class MillerCylindrical2Projection extends CylindricalProjection {

    public MillerCylindrical2Projection() {
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.x = lplam;
        out.y = Math.log(Math.tan(MapMath.QUARTERPI + lpphi / 3.)) * 1.5;
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.x = xyx;
        out.y = 2.5 * (Math.atan(Math.exp(.8 * xyy)) - MapMath.QUARTERPI);
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Miller Cylindrical II";
    }
}
