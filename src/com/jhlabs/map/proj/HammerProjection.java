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
 * split Hammer and Eckert-Greifendorff projections.
 * added inverse, removed m parameter,
 * changed super class to Projection
 * Bernhard Jenny
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class HammerProjection extends Projection {

    private final double w = 0.5;

    public HammerProjection() {
    }

    @Override
    public Point2D.Double project(double lplam, double lpphi, Point2D.Double xy) {
        lplam *= w;
        double cosphi = Math.cos(lpphi);
        double d = Math.sqrt(2. / (1. + cosphi * Math.cos(lplam)));
        xy.x = d * cosphi * Math.sin(lplam) / w;
        xy.y = d * Math.sin(lpphi);
        return xy;
    }

    @Override
    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        final double EPS = 1.0e-10;
        double wx = w * xyx;
        double z = Math.sqrt(1. - 0.25 * (wx * wx + xyy * xyy));
        double zz2_1 = 2 * z * z - 1;
        if (Math.abs(zz2_1) < EPS) {
            out.x = Double.NaN;
            out.y = Double.NaN;
        } else {
            out.x = Math.atan2(wx * z, zz2_1) / w; // FIXME aatan2
            out.y = Math.asin(z * xyy); // FIXME aasin
        }
        return out;
    }

    /**
     * Returns true if this projection is equal area
     */
    @Override
    public boolean isEqualArea() {
        return true;
    }

    @Override
    public String toString() {
        return "Hammer";
    }
}
