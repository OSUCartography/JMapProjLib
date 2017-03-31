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
 * Added initialization for minLongitude and maxLongitude.
 * Bernhard Jenny, 15 July 2010.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.time.Year;

public class NicolosiProjection extends Projection {

    private final static double EPS = 1e-10;

    public NicolosiProjection() {
        minLongitude = Math.toRadians(-90);
        maxLongitude = Math.toRadians(90);
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        if (Math.abs(lplam) < EPS) {
            out.x = 0;
            out.y = lpphi;
        } else if (Math.abs(lpphi) < EPS) {
            out.x = lplam;
            out.y = 0.;
        } else if (Math.abs(Math.abs(lplam) - MapMath.HALFPI) < EPS) {
            out.x = lplam * Math.cos(lpphi);
            out.y = MapMath.HALFPI * Math.sin(lpphi);
        } else if (Math.abs(Math.abs(lpphi) - MapMath.HALFPI) < EPS) {
            out.x = 0;
            out.y = lpphi;
        } else {
            double tb, c, d, m, n, r2, sp;

            tb = MapMath.HALFPI / lplam - lplam / MapMath.HALFPI;
            c = lpphi / MapMath.HALFPI;
            d = (1 - c * c) / ((sp = Math.sin(lpphi)) - c);
            r2 = tb / d;
            r2 *= r2;
            m = (tb * sp / d - 0.5 * tb) / (1. + r2);
            n = (sp / r2 + 0.5 * d) / (1. + 1. / r2);
            double x = Math.cos(lpphi);
            x = Math.sqrt(m * m + x * x / (1. + r2));
            out.x = MapMath.HALFPI * (m + (lplam < 0. ? -x : x));
            double y = Math.sqrt(n * n - (sp * sp / r2 + d * sp - 1.)
                    / (1. + 1. / r2));
            out.y = MapMath.HALFPI * (n + (lpphi < 0. ? y : -y));
        }
        return out;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {
        binarySearchInverse(x, y, out);
        return out;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Nicolosi Globular";
    }
    
    @Override
    public Year getYear() {
        return Year.of(1660);
    }
    
    @Override
    public String getAuthor() {
        return "Giambattista Nicolosi (1610-1670)";
    }
}
