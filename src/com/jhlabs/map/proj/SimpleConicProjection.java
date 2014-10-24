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
 * Bernhard Jenny, 17. September 2010:
 * Changed initialize() to use projectionLatitude1 and projectionLatitude2 instead
 * of hard-coded values.
 * Cleaned code in initialize().
 * Fixed bugs in projectInverse().
 * FIXME: This class should be split in multiple classes for a proper object
 * oriented design. This has been done for Euler and Tissot.
 */
package com.jhlabs.map.proj;

import com.jhlabs.map.MapMath;
import java.awt.geom.Point2D;

public class SimpleConicProjection extends ConicProjection {

    private double n;
    private double rho_c;
    private double rho_0;
    private double sig;
    private double c1, c2;
    private int type;
    public final static int MURD1 = 1;
    public final static int MURD2 = 2;
    public final static int MURD3 = 3;
    public final static int PCONIC = 4;
    public final static int VITK1 = 6;
    private final static double EPS = 1e-10;

    public SimpleConicProjection() {
        this(MURD1);
    }

    public SimpleConicProjection(int type) {
        this.type = type;
        projectionLatitude1 = Math.toRadians(50);
        minLatitude = Math.toRadians(0);
        maxLatitude = Math.toRadians(80);
    }

    public String toString() {
        return "Simple Conic";
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double out) {
        double rho;

        switch (type) {
            case MURD2:
                rho = rho_c + Math.tan(sig - lpphi);
                break;
            case PCONIC:
                rho = c2 * (c1 - Math.tan(lpphi));
                break;
            default:
                rho = rho_c - lpphi;
                break;
        }
        out.x = rho * Math.sin(lplam *= n);
        out.y = rho_0 - rho * Math.cos(lplam);
        return out;
    }

    public Point2D.Double projectInverse(double xyx, double xyy, Point2D.Double out) {
        double rho;
        xyy = rho_0 - xyy;
        rho = MapMath.distance(xyx, xyy);
        if (n < 0.) {
            rho = -rho;
            xyx = -xyx;
            xyy = -xyy;
        }
        out.x = Math.atan2(xyx, xyy) / n;
        switch (type) {
            case PCONIC:
                out.y = Math.atan(c1 - rho / c2) + sig;
                break;
            case MURD2:
                out.y = sig - Math.atan(rho - rho_c);
                break;
            default:
                out.y = rho_c - rho;
        }
        return out;
    }

    public boolean hasInverse() {
        return true;
    }

    public void initialize() {
        super.initialize();

        /* get common factors for simple conics */
        double del = 0.5 * (projectionLatitude2 - projectionLatitude1);
        sig = 0.5 * (projectionLatitude2 + projectionLatitude1);
        
        if (Math.abs(del) < EPS || Math.abs(sig) < EPS) {
            throw new ProjectionException("-42");
        }

        double cs;
        switch (type) {
            case MURD1:
                rho_c = Math.sin(del) / (del * Math.tan(sig)) + sig;
                rho_0 = rho_c - projectionLatitude;
                n = Math.sin(sig);
                break;
            case MURD2:
                rho_c = (cs = Math.sqrt(Math.cos(del))) / Math.tan(sig);
                rho_0 = rho_c + Math.tan(sig - projectionLatitude);
                n = Math.sin(sig) * cs;
                break;
            case MURD3:
                rho_c = del / (Math.tan(sig) * Math.tan(del)) + sig;
                rho_0 = rho_c - projectionLatitude;
                n = Math.sin(sig) * Math.sin(del) * Math.tan(del) / (del * del);
                break;
            case PCONIC:
                n = Math.sin(sig);
                c2 = Math.cos(del);
                c1 = 1. / Math.tan(sig);
                if (Math.abs(del = projectionLatitude - sig) - EPS10 >= MapMath.HALFPI) {
                    throw new ProjectionException("-43");
                }
                rho_0 = c2 * (c1 - Math.tan(del));
                maxLatitude = Math.toRadians(60);//FIXME
                break;
            case VITK1:
                n = (cs = Math.tan(del)) * Math.sin(sig) / del;
                rho_c = del / (cs * Math.tan(sig)) + sig;
                rho_0 = rho_c - projectionLatitude;
                break;
        }
    }
}
