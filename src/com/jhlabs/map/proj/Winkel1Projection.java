/*
 * Winkel1Projection.java
 *
 * Created on July 17, 2007, 9:04 AM
 *
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

/**
 * Ported from Proj4 by Bernhard Jenny, Institute of Cartography, ETH Zurich.
 */
public class Winkel1Projection extends PseudoCylindricalProjection {

    /**
     * latitude of true scale on central meridian.
     * Default is 50 degree and 28 minutes. This differs from the default value
     * of proj4, which uses 0 degrees. 0 degrees is not appropriate,
     * as in this case the Winkel I projection is equal to the Eckert V projection.
     */
    private double phi1 = Math.toRadians(50. + 28. / 60.);
    /**
     * cosine of latitude of true scale on central meridian
     */
    private double cosphi1 = Math.cos(phi1);

    public Winkel1Projection() {
    }
    
    public void setLatitudeOfTrueScale(double phi1) {
        if (phi1 < -MapMath.HALFPI || phi1 > MapMath.HALFPI) {
            throw new ProjectionException();
        }
        this.phi1 = phi1;
        this.cosphi1 = Math.cos(this.phi1);
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {

        out.x = .5 * lplam * (cosphi1 + Math.cos(lpphi));
        out.y = lpphi;
        return out;

    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {

        out.y = xyy;
        out.x = 2. * xyx / (this.cosphi1 + Math.cos(xyy));
        return out;

    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Winkel I";
    }
}
