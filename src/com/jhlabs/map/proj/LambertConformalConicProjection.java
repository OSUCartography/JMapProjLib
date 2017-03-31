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
package com.jhlabs.map.proj;

import com.jhlabs.map.Ellipsoid;
import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.time.Year;

public class LambertConformalConicProjection extends ConicProjection {

    private double n;
    private double rho0;
    private double c;

    public LambertConformalConicProjection() {
        minLatitude = Math.toRadians(0);
        maxLatitude = Math.toRadians(80.0);
        projectionLatitude = MapMath.QUARTERPI;
        projectionLatitude1 = 0;
        projectionLatitude2 = 0;
        initialize();
    }

    /**
     * Set up a projection suitable for State Place Coordinates.
     */
    public LambertConformalConicProjection(Ellipsoid ellipsoid, double lon_0, double lat_1, double lat_2, double lat_0, double x_0, double y_0) {
        setEllipsoid(ellipsoid);
        projectionLongitude = lon_0;
        projectionLatitude = lat_0;
        scaleFactor = 1.0;
        falseEasting = x_0;
        falseNorthing = y_0;
        projectionLatitude1 = lat_1;
        projectionLatitude2 = lat_2;
        initialize();
    }

    public Point2D.Double project(double x, double y, Point2D.Double out) {
        double rho;
        if (Math.abs(Math.abs(y) - MapMath.HALFPI) < 1e-10) {
            rho = 0.0;
        } else {
            rho = c * (spherical
                    ? Math.pow(Math.tan(MapMath.QUARTERPI + .5 * y), -n)
                    : Math.pow(MapMath.tsfn(y, Math.sin(y), e), n));
        }
        out.x = scaleFactor * (rho * Math.sin(x *= n));
        out.y = scaleFactor * (rho0 - rho * Math.cos(x));
        return out;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double out) {
        x /= scaleFactor;
        y /= scaleFactor;
        double rho = MapMath.distance(x, y = rho0 - y);
        if (rho != 0) {
            if (n < 0.0) {
                rho = -rho;
                x = -x;
                y = -y;
            }
            if (spherical) {
                out.y = 2.0 * Math.atan(Math.pow(c / rho, 1.0 / n)) - MapMath.HALFPI;
            } else {
                out.y = MapMath.phi2(Math.pow(rho / c, 1.0 / n), e);
            }
            out.x = Math.atan2(x, y) / n;
        } else {
            out.x = 0.0;
            out.y = n > 0.0 ? MapMath.HALFPI : -MapMath.HALFPI;
        }
        return out;
    }

    public void initialize() {
        super.initialize();
        double cosphi, sinphi;
        boolean secant;

        if (projectionLatitude1 == 0) {
            projectionLatitude1 = projectionLatitude2 = projectionLatitude;
        }

        if (Math.abs(projectionLatitude1 + projectionLatitude2) < 1e-10) {
            throw new ProjectionException();
        }
        n = sinphi = Math.sin(projectionLatitude1);
        cosphi = Math.cos(projectionLatitude1);
        secant = Math.abs(projectionLatitude1 - projectionLatitude2) >= 1e-10;
        spherical = (es == 0.0);
        if (!spherical) {
            double ml1, m1;

            m1 = MapMath.msfn(sinphi, cosphi, es);
            ml1 = MapMath.tsfn(projectionLatitude1, sinphi, e);
            if (secant) {
                n = Math.log(m1
                        / MapMath.msfn(sinphi = Math.sin(projectionLatitude2), Math.cos(projectionLatitude2), es));
                n /= Math.log(ml1 / MapMath.tsfn(projectionLatitude2, sinphi, e));
            }
            c = (rho0 = m1 * Math.pow(ml1, -n) / n);
            rho0 *= (Math.abs(Math.abs(projectionLatitude) - MapMath.HALFPI) < 1e-10) ? 0.
                    : Math.pow(MapMath.tsfn(projectionLatitude, Math.sin(projectionLatitude), e), n);
        } else {
            if (secant) {
                n = Math.log(cosphi / Math.cos(projectionLatitude2))
                        / Math.log(Math.tan(MapMath.QUARTERPI + .5 * projectionLatitude2)
                                / Math.tan(MapMath.QUARTERPI + .5 * projectionLatitude1));
            }
            c = cosphi * Math.pow(Math.tan(MapMath.QUARTERPI + .5 * projectionLatitude1), n) / n;
            rho0 = (Math.abs(Math.abs(projectionLatitude) - MapMath.HALFPI) < 1e-10) ? 0.
                    : c * Math.pow(Math.tan(MapMath.QUARTERPI + .5 * projectionLatitude), -n);
        }
    }

    /**
     * Returns true if this projection is conformal
     */
    public boolean isConformal() {
        return true;
    }

    public boolean hasInverse() {
        return true;
    }

    public String toString() {
        return "Lambert Conformal Conic";
    }

    @Override
    public Year getYear() {
        return Year.of(1772);
    }
    
    @Override
    public String getAuthor() {
        return "Johann Heinrich Lambert (1728Ð1777)";
    }

}
