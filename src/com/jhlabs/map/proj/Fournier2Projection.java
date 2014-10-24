/*
Copyright 2010 Bernhard Jenny

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

/**
 * Fournier II projection.
 * Code from PROJ.4.
 * 23 September: changed super class to PseudoCylindricalProjection.
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public class Fournier2Projection extends PseudoCylindricalProjection {

    private static final double Cx = 0.5641895835477562869480794515;
    private static final double Cy = 0.8862269254527580136490837416;

    public Fournier2Projection() {
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.x = Cx * lplam * Math.cos(lpphi);
	out.y = Cy * Math.sin(lpphi);
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.y = Math.asin(xyy / Cy);
	out.x = xyx / (Cx * Math.cos(out.y));
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Fournier II";
    }
}
