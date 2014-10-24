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
 * Apian II Globular projection.
 * Code from PROJ.4.
 * PROJ.4 lists this as miscellaneous projection, but it is pseudo-cylindrical.
 * 
 * @author Bernhard Jenny, Institute of Cartography, ETH Zurich
 */
public class Apian2Projection extends PseudoCylindricalProjection {

    private static final double HLFPI2 = 2.46740110027233965467;
    private static final double RHALFPI = 0.6366197723675813430755350534;

    public Apian2Projection() {
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        out.y = lpphi;
	out.x = lplam * RHALFPI * Math.sqrt(Math.abs(HLFPI2 - lpphi * lpphi));
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
        return "Apian Globular II";
    }
}
