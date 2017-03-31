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
 * Changes by Bernhard Jenny, May 2007: added missing toString() and
 * isEqualArea(); this class now derives from CylindricalProjection instead of
 * Projection; removed isRectilinear, which is defined in the new superclass
 * CylindricalProjection; and removed trueScaleLatitude that did hide a field
 * of the Projection class of the same name.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;
import java.time.Year;

public class CylindricalEqualAreaProjection extends CylindricalProjection {

    private double qp;
    private double[] apa;

    public CylindricalEqualAreaProjection() {
        this(0.0, 0.0, 0.0);
    }

    public CylindricalEqualAreaProjection(double projectionLatitude,
            double projectionLongitude,
            double trueScaleLatitude) {
        this.projectionLatitude = projectionLatitude;
        this.projectionLongitude = projectionLongitude;
        this.trueScaleLatitude = trueScaleLatitude;
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        double t = trueScaleLatitude;

        scaleFactor = Math.cos(t);
        if (es != 0) {
            t = Math.sin(t);
            scaleFactor /= Math.sqrt(1. - es * t * t);
            apa = MapMath.authset(es);
            qp = MapMath.qsfn(1., e, one_es);
        }
    }

    @Override
    public Point2D.Double project(double lam, double phi, Point2D.Double xy) {
        if (spherical) {
            xy.x = scaleFactor * lam;
            xy.y = Math.sin(phi) / scaleFactor;
        } else {
            xy.x = scaleFactor * lam;
            xy.y = .5 * MapMath.qsfn(Math.sin(phi), e, one_es) / scaleFactor;
        }
        return xy;
    }

    @Override
    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        if (spherical) {
            y *= scaleFactor;
            double t = Math.abs(y);

            if (t - EPS10 <= 1.) {
                if (t >= 1.) {
                    lp.y = y < 0. ? -MapMath.HALFPI : MapMath.HALFPI;
                } else {
                    lp.y = Math.asin(y);
                }
                lp.x = x / scaleFactor;
            } else {
                throw new ProjectionException();
            }
        } else {
            lp.y = MapMath.authlat(Math.asin(2. * y * scaleFactor / qp), apa);
            lp.x = x / scaleFactor;
        }
        return lp;
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
        return "Cylindrical Equal-Area";
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
