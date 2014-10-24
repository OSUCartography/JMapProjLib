/**
 *
Copyright 2007 Bernhard Jenny

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 * HolzelProjection.java
 *
 * Created  October 3, 2008
 *
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;
/**
 * Hoelzel Projection. Based on Proj4.
 */
public class HolzelProjection extends PseudoCylindricalProjection {

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        double aphi = Math.abs(lpphi);
        out.y = lpphi;
        if (aphi <= 1.39634) {
            out.x = lplam * 0.441013 * (1. + Math.cos(aphi));
        } else {
            double t = (aphi - 0.40928) / 1.161517;
            out.x = lplam * (0.322673 + 0.369722 * Math.sqrt(Math.abs(1. - t * t)));
        }
        return out;

    }

    @Override
    public String toString() {
        return "H\u00F6lzel";
    }
}
