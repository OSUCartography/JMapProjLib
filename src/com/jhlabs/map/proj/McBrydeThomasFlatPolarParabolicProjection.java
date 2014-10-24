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
 * Bernhard Jenny, 19 September 2010: Fixed forward projection.
 * 22 September 2010: changed name from MBTFPPProjection to
 * McBrydeThomasFlatPolarParabolicProjection
 * 23 September 2010: changed super class to PseudoCylindricalProjection, added
 * isEqualArea.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class McBrydeThomasFlatPolarParabolicProjection extends PseudoCylindricalProjection {

    private final static double CS = .95257934441568037152;
    private final static double FXC = .92582009977255146156;
    private final static double FYC = 3.40168025708304504493;
    private final static double C23 = .66666666666666666666;
    private final static double C13 = .33333333333333333333;
    private final static double ONEEPS = 1.0000001;

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        lpphi = Math.asin(CS * Math.sin(lpphi));
        out.x = FXC * lplam * (2. * Math.cos(C23 * lpphi) - 1.);
        out.y = FYC * Math.sin(C13 * lpphi);
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        out.y = xyy / FYC;
        if (Math.abs(out.y) >= 1.) {
            if (Math.abs(out.y) > ONEEPS) {
                throw new ProjectionException("I");
            } else {
                out.y = (out.y < 0.) ? -MapMath.HALFPI : MapMath.HALFPI;
            }
        } else {
            out.y = Math.asin(out.y);
        }
        out.x = xyx / (FXC * (2. * Math.cos(C23 * (out.y *= 3.)) - 1.));
        if (Math.abs(out.y = Math.sin(out.y) / CS) >= 1.) {
            if (Math.abs(out.y) > ONEEPS) {
                throw new ProjectionException("I");
            } else {
                out.y = (out.y < 0.) ? -MapMath.HALFPI : MapMath.HALFPI;
            }
        } else {
            out.y = Math.asin(out.y);
        }
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isEqualArea() {
        return true;
    }
    
    public String toString() {
        return "McBride-Thomas Flat-Pole Parabolic";
    }
}
