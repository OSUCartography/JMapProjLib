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
 * Changed class name from Molleweide to Mollweide, added missing isEqualArea
 * method by Bernhard Jenny, Oct 2007.
 */
package com.jhlabs.map.proj;

import java.awt.geom.Point2D;

public class MollweideProjection extends PseudoCylindricalProjection {

    // Wagner 4 and Wagner 5 should be separated
    // FIXME
    public static final int MOLLWEIDE = 0;
    public static final int WAGNER4 = 1;
    public static final int WAGNER5 = 2;
    private static final int MAX_ITER = 10;
    private static final double TOLERANCE = 1e-7;
    private int type = MOLLWEIDE;
    private double cx, cy, cp;

    public MollweideProjection() {
        this(Math.PI / 2);
    }

    public MollweideProjection(int type) {
        this.type = type;
        switch (type) {
            case MOLLWEIDE:
                init(Math.PI / 2);
                break;
            case WAGNER4:
                init(Math.PI / 3);
                break;
            case WAGNER5:
                init(Math.PI / 2);
                cx = 0.90977;
                cy = 1.65014;
                cp = 3.00896;
                break;
        }
    }

    public MollweideProjection(double p) {
        init(p);
    }

    public void init(double p) {
        double r, sp, p2 = p + p;

        sp = Math.sin(p);
        r = Math.sqrt(Math.PI * 2.0 * sp / (p2 + Math.sin(p2)));
        cx = 2. * r / Math.PI;
        cy = r / sp;
        cp = p2 + Math.sin(p2);
    }

    public MollweideProjection(double cx, double cy, double cp) {
        this.cx = cx;
        this.cy = cy;
        this.cp = cp;
    }

    public Point2D.Double project(double lplam, double lpphi, Point2D.Double xy) {
        double k, v;
        int i;

        k = cp * Math.sin(lpphi);
        for (i = MAX_ITER; i != 0; i--) {
            lpphi -= v = (lpphi + Math.sin(lpphi) - k) / (1. + Math.cos(lpphi));
            if (Math.abs(v) < TOLERANCE) {
                break;
            }
        }
        if (i == 0) {
            lpphi = (lpphi < 0.) ? -Math.PI / 2 : Math.PI / 2;
        } else {
            lpphi *= 0.5;
        }
        xy.x = cx * lplam * Math.cos(lpphi);
        xy.y = cy * Math.sin(lpphi);
        return xy;
    }

    public Point2D.Double projectInverse(double x, double y, Point2D.Double lp) {
        double lat, lon;

        lat = Math.asin(y / cy); // FIXME: test for out of bounds
        lon = x / (cx * Math.cos(lat));
        lat += lat;
        lat = Math.asin((lat + Math.sin(lat)) / cp);// FIXME: test for out of bounds as in Proj4
        lp.x = lon;
        lp.y = lat;
        return lp;
    }

    public boolean hasInverse() {
        return true;
    }

    public boolean isEqualArea() {
        return type == WAGNER4 || type == MOLLWEIDE;
    }

    public String toString() {
        switch (type) {
            case WAGNER4:
                return "Wagner IV";
            case WAGNER5:
                return "Wagner V";
        }
        return "Mollweide";
    }
}
