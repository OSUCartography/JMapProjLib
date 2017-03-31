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
 * Added isConformal method, removed isRectilinear (duplicate of super class)
 * by Bernhard Jenny, June 26, 2008.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.time.Year;

public class MercatorProjection extends CylindricalProjection {

    public MercatorProjection() {
        minLatitude = MapMath.degToRad(-85);
        maxLatitude = MapMath.degToRad(85);
    }

    public Point2D.Double project(double lam, double phi, Point2D.Double out) {
        if (spherical) {
            out.x = scaleFactor * lam;
            if (phi > maxLatitude) {
                phi = maxLatitude;
            } else if (phi < minLatitude) {
                phi = minLatitude;
            }
            out.y = scaleFactor * Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phi));
        } else {
            out.x = scaleFactor * lam;
            out.y = -scaleFactor * Math.log(MapMath.tsfn(phi, Math.sin(phi), e));
        }
        return out;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {
        if (spherical) {
            out.y = MapMath.HALFPI - 2. * Math.atan(Math.exp(-y / scaleFactor));
            out.x = x / scaleFactor;
        } else {
            out.y = MapMath.phi2(Math.exp(-y / scaleFactor), e);
            out.x = x / scaleFactor;
        }
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isConformal() {
        return true;
    }

    /**
     * Returns the ESPG code for this projection, or 0 if unknown.
     */
    public int getEPSGCode() {
        return 9804;
    }

    public String toString() {
        return "Mercator";
    }
    
    @Override
    public Year getYear() {
        return Year.of(1569);
    }
    
    @Override
    public String getAuthor() {
        return "Gerardus Mercator (1512-94)";
    }
}
