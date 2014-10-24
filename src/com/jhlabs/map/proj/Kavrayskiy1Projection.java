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
 * Kavrayskiy I Projection Arithmetic mean of the Mercator and the cylindrical
 * equal-area projections Code from proj4.
 *
 * @author Bernhard Jenny, Oregon State University
 */
public class Kavrayskiy1Projection extends CylindricalProjection {

    final double MERCATOR_MAX_LAT = Math.toRadians(70);
    final double DY = Math.log(Math.tan(0.5 * (MapMath.HALFPI + MERCATOR_MAX_LAT)));
    final double C = 1 / Math.cos(MERCATOR_MAX_LAT);

    public Kavrayskiy1Projection() {
    }

    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double out) {
        out.x = lon;
        // out.y = lat * C; // the scaled equirectangular secant at 70deg
        if (lat > MERCATOR_MAX_LAT) {
            out.y = (lat - MERCATOR_MAX_LAT) * C + DY;
        } else if (lat < -MERCATOR_MAX_LAT) {
            out.y = (lat + MERCATOR_MAX_LAT) * C - DY;
        } else {
            out.y = Math.log(Math.tan(0.5 * (Math.PI / 2 + lat)));
        }
        return out;
    }

    @Override
    public String toString() {
        return "Kavrayskiy I";
    }
}
