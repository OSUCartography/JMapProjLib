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
 * Bernhard Jenny, 19 September 2010: fixed spherical inverse.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class PolyconicProjection extends Projection {

    private double ml0;
    private double[] en;
    private final static double TOL = 1e-10;
    private final static double CONV = 1e-10;
    private final static int N_ITER = 10;
    private final static int I_ITER = 20;
    private final static double ITOL = 1.e-12;

    public PolyconicProjection() {
        minLatitude = MapMath.degToRad(0);
        maxLatitude = MapMath.degToRad(80);
        minLongitude = MapMath.degToRad(-60);
        maxLongitude = MapMath.degToRad(60);
        initialize();
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        if (spherical) {
            double cot, E;

            if (Math.abs(lpphi) <= TOL) {
                out.x = lplam;
                out.y = ml0;
            } else {
                cot = 1. / Math.tan(lpphi);
                out.x = Math.sin(E = lplam * Math.sin(lpphi)) * cot;
                out.y = lpphi - projectionLatitude + cot * (1. - Math.cos(E));
            }
        } else {
            double ms, sp, cp;

            if (Math.abs(lpphi) <= TOL) {
                out.x = lplam;
                out.y = -ml0;
            } else {
                sp = Math.sin(lpphi);
                ms = Math.abs(cp = Math.cos(lpphi)) > TOL ? MapMath.msfn(sp, cp, es) / sp : 0.;
                out.x = ms * Math.sin(out.x *= sp);
                out.y = (MapMath.mlfn(lpphi, sp, cp, en) - ml0) + ms * (1. - Math.cos(lplam));
            }
        }
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        double lpphi;
        if (spherical) {
            double B, dphi, tp;
            int i;

            if (Math.abs(xyy = projectionLatitude + xyy) <= TOL) {
                out.x = xyx;
                out.y = 0.;
            } else {
                lpphi = xyy;
                B = xyx * xyx + xyy * xyy;
                i = N_ITER;
                do {
                    tp = Math.tan(lpphi);
                    lpphi -= (dphi = (xyy * (lpphi * tp + 1.) - lpphi
                            - .5 * (lpphi * lpphi + B) * tp)
                            / ((lpphi - xyy) / tp - 1.));
                } while (Math.abs(dphi) > CONV && --i > 0);
                if (i == 0) {
                    throw new ProjectionException("I");
                }
                out.x = Math.asin(xyx * Math.tan(lpphi)) / Math.sin(lpphi);
                out.y = lpphi;
            }
        } else {
            xyy += ml0;
            if (Math.abs(xyy) <= TOL) {
                out.x = xyx;
                out.y = 0.;
            } else {
                double r, c, sp, cp, s2ph, ml, mlb, mlp, dPhi;
                int i;

                r = xyy * xyy + xyx * xyx;
                for (lpphi = xyy, i = I_ITER; i > 0; --i) {
                    sp = Math.sin(lpphi);
                    s2ph = sp * (cp = Math.cos(lpphi));
                    if (Math.abs(cp) < ITOL) {
                        throw new ProjectionException("I");
                    }
                    c = sp * (mlp = Math.sqrt(1. - es * sp * sp)) / cp;
                    ml = MapMath.mlfn(lpphi, sp, cp, en);
                    mlb = ml * ml + r;
                    mlp = (1.0 / es) / (mlp * mlp * mlp);
                    lpphi += (dPhi =
                            (ml + ml + c * mlb - 2. * xyy * (c * ml + 1.)) / (es * s2ph * (mlb - 2. * xyy * ml) / c
                            + 2. * (xyy - ml) * (c * mlp - 1. / s2ph) - mlp - mlp));
                    if (Math.abs(dPhi) <= ITOL) {
                        break;
                    }
                }
                if (i == 0) {
                    throw new ProjectionException("I");
                }
                c = Math.sin(lpphi);
                out.x = Math.asin(xyx * Math.tan(lpphi) * Math.sqrt(1. - es * c * c)) / Math.sin(lpphi);
                out.y = lpphi;
            }
        }
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();
        spherical = true;//FIXME
        if (!spherical) {
            en = MapMath.enfn(es);
            if (en == null) {
                throw new ProjectionException("E");
            }
            ml0 = MapMath.mlfn(projectionLatitude, Math.sin(projectionLatitude), Math.cos(projectionLatitude), en);
        } else {
            ml0 = -projectionLatitude;
        }
    }

    public String toString() {
        return "Polyconic (American)";
    }
}
