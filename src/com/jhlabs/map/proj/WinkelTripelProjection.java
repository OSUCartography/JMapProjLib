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
 * Bernhard Jenny, May 2007: Separated Aitoff from Winkel Tripel.
 * 23 September 2010: Changed super class to ModifiedAzimuthalProjection.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class WinkelTripelProjection extends ModifiedAzimuthalProjection {

    public WinkelTripelProjection() {
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        double c = 0.5 * lplam;
        double d = Math.acos(Math.cos(lpphi) * Math.cos(c));

        if (d != 0) {
            out.x = 2. * d * Math.cos(lpphi) * Math.sin(c) * (out.y = 1. / Math.sin(d));
            out.y *= d * Math.sin(lpphi);
    } else {
            out.x = out.y = 0.0;
        }
        out.x = (out.x + lplam * 0.636619772367581343) * 0.5;
        out.y = (out.y + lpphi) * 0.5;
        return out;
    }

    public String toString() {
        return "Winkel Tripel";
    }
}
