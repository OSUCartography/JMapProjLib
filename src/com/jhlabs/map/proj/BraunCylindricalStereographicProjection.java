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

import java.awt.geom.Point2D;

public class BraunCylindricalStereographicProjection extends CylindricalProjection {

    private double cosTrueScaleLatitude = 1.0;

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.x = lplam * cosTrueScaleLatitude;
        out.y = (1 + cosTrueScaleLatitude) * Math.tan(lpphi / 2.);
        return out;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        lp.x = x / cosTrueScaleLatitude;
        lp.y = 2.0 * Math.atan(y / (1 + cosTrueScaleLatitude));
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Cylindrical Stereographic (Braun)";
    }
}
