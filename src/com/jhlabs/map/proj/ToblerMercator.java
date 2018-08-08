/**
 * Copyright 2018 Bernie Jenny, Monash University, Melbourne, Australia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * An equal-area version of the Mercator projection by Waldo Tobler.
 *
 * Tobler, W. (2018). A new companion for Mercator. Cartography and Geographic
 * Information Science, 45 (3), p. 284-285.
 *
 * @author Bernie Jenny
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.time.Year;

public class ToblerMercator extends CylindricalProjection {

    public ToblerMercator() {
        minLatitude = MapMath.degToRad(-85);
        maxLatitude = MapMath.degToRad(85);
    }

    @Override
    public Point2D.Double project(double lon, double lat, Point2D.Double out) {
        if (lat > maxLatitude) {
            lat = maxLatitude;
        } else if (lat < minLatitude) {
            lat = minLatitude;
        }
        double cosLat = Math.cos(lat);
        out.x = scaleFactor * lon * cosLat * cosLat;
        out.y = scaleFactor * Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * lat));

        return out;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {
        double lat = MapMath.HALFPI - 2. * Math.atan(Math.exp(-y / scaleFactor));
        double cosLat = Math.cos(lat);
        double lon = x / scaleFactor / (cosLat * cosLat);
        out.x = lon;
        out.y = lat;
        return out;
    }

    @Override
    public boolean hasInverse() {
        return true;
    }

    @Override
    public boolean isEqualArea() {
        return true;
    }

    @Override
    public String toString() {
        return "Tobler-Mercator";
    }

    @Override
    public Year getYear() {
        return Year.of(2018);
    }

    @Override
    public String getAuthor() {
        return "Waldo R. Tobler";
    }
}
