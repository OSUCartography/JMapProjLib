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
 * Changed super class from Projection to PseudoCylindricalProjection.
 * Bernhard Jenny, May 25 2010.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class Eckert1Projection extends PseudoCylindricalProjection {

    private final static double FC = .92131773192356127802;
    private final static double RP = .31830988618379067154;

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.x = FC * lplam * (1. - RP * Math.abs(lpphi));
        out.y = FC * lpphi;
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.y = xyy / FC;
        out.x = xyx / (FC * (1. - RP * Math.abs(out.y)));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Eckert I";
    }
}
