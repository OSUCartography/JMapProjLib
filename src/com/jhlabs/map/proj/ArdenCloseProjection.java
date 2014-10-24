/*
Copyright 2013 Bernhard Jenny

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
 * Arden-Close Projection
 * Arithmetic mean of the Mercator and the cylindrical equal-area projections
 * Code from proj4.
 * @author Bernhard Jenny, Oregon State University
 */
public class ArdenCloseProjection extends CylindricalProjection {

    public ArdenCloseProjection() {
        minLatitude = MapMath.degToRad(-85);
        maxLatitude = MapMath.degToRad(85);
    }

    @Override
    public Point2D.Double project(double lam, double phi, Point2D.Double out) {
	out.x = lam;
        final double yMercator = Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phi));
        final double yCylEA = Math.sin(phi);
        out.y = 0.5 * (yMercator + yCylEA);
        return out;
    }

    public String toString() {
        return "Arden-Close";
    }
}
